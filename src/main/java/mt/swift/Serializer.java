/**
 *  Copyright 2008 Martin Traverso
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package mt.swift;

import mt.swift.model.*;
import mt.swift.model.Type;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TProtocol;
import org.objectweb.asm.*;
import static org.objectweb.asm.Opcodes.*;
import org.objectweb.asm.util.CheckClassAdapter;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A dynamic serializer for Thrift structures.
 * <p/>
 * This class is capable of serializing a Map or Javabean via a Thrift protocol object according to a
 * user-specified Thrift schema definition.
 * <p/>
 * Instances of this class are not thread-safe, but they are immutable after they are constructed and all bindings
 * taken care of. As long as references are published in a thread-safe manner, serialize() can be called without
 * additional synchronization:
 *
 */
public class Serializer
{
	private final Map<String, Class<?>> classes = new HashMap<String, Class<?>>();
	private final Map<String, StructureSerializer> serializers = new HashMap<String, StructureSerializer>();

	private int sequence = 0;

	private final static boolean debug = false;

	public void bind(StructureType type, Class clazz)
	{
		classes.put(type.getName(), clazz);

        StructureSerializer serializer = compileSerializer(type, clazz);
        serializers.put(type.getName(), serializer);
	}

	public void bindToMap(StructureType type)
	{
        bind(type, HashMap.class);
	}


	public void serialize(Object object, String name, TProtocol protocol)
		throws TException
	{
		StructureSerializer serializer = serializers.get(name);
		Class clazz = classes.get(name);

		if (clazz == null) {
			throw new IllegalStateException(String.format("Type '%s' not bound to a class", name));
		}

		serializer.serialize(object, this, protocol);
	}

	private StructureSerializer compileSerializer(StructureType type, Class clazz)
	{
		ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES); // TODO: compute this ourselves?
//		ClassWriter classWriter = new ClassWriter(0); // TODO: compute this ourselves?
		ClassVisitor writer = classWriter;

		if (debug) {
			writer = new CheckClassAdapter(classWriter);
			writer = new TraceClassVisitor(writer, new PrintWriter(System.out));
		}

		String targetClassName = Util.getInternalName(clazz);
		String serializerClassName =
			"mt/swift/generated/Serializer" + clazz.getSimpleName() + "_" + sequence++;

		// class metadata
		writer.visit(V1_6, ACC_PUBLIC + ACC_SUPER, serializerClassName, null, "java/lang/Object",
		             new String[] { Util.getInternalName(StructureSerializer.class) });

		compileConstructor(writer);
		compileSerializeMethod(type, writer, targetClassName, clazz);

		writer.visitEnd();

		if (debug) {
			ClassReader reader = new ClassReader(classWriter.toByteArray());
			reader.accept(new TraceClassVisitor(new PrintWriter(System.out)), 0);
		}

		ByteArrayClassLoader loader = new ByteArrayClassLoader();
		try {
			return (StructureSerializer) loader.defineClass(serializerClassName.replace('/', '.'),
			                                                classWriter.toByteArray()).newInstance();
		}
		catch (InstantiationException e) {
			throw new RuntimeException(e);
		}
		catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	private void compileSerializeMethod(StructureType type, ClassVisitor writer, String targetClassName, Class clazz)
	{
		MethodVisitor methodVisitor = writer.visitMethod(ACC_PUBLIC, "serialize",
		                                                 "(Ljava/lang/Object;L" +
		                                                 Util.getInternalName(Serializer.class)
		                                                 + ";Lorg/apache/thrift/protocol/TProtocol;)V",
		                                                 null, new String[] { "org/apache/thrift/TException" });

		FrameRegisterManager context = new FrameRegisterManager();
		context.bindSlot("this", 0);
		context.bindSlot("object", 1);
		context.bindSlot("serializer", 2);
		context.bindSlot("protocol", 3);

		methodVisitor.visitCode();

		// protocol.writeStructBegin(new TStruct("name"))
		methodVisitor.visitVarInsn(ALOAD, context.getSlot("protocol"));

		methodVisitor.visitTypeInsn(NEW, "org/apache/thrift/protocol/TStruct");
		methodVisitor.visitInsn(DUP);
		methodVisitor.visitLdcInsn(type.getName());
		methodVisitor.visitMethodInsn(INVOKESPECIAL, "org/apache/thrift/protocol/TStruct", "<init>", "(Ljava/lang/String;)V");

		methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "org/apache/thrift/protocol/TProtocol", "writeStructBegin", "(Lorg/apache/thrift/protocol/TStruct;)V");

		for (Field field : type.getFields()) {
            // TODO: TField is immutable, so pre-create them as final static class variables in the generated class
            // TField tfield = new TField(name, type, id)
            int fieldSlot = context.newAnonymousSlot();

            methodVisitor.visitTypeInsn(NEW, "org/apache/thrift/protocol/TField");
            methodVisitor.visitInsn(DUP);
            methodVisitor.visitLdcInsn(field.getName());
            pushValue(methodVisitor, field.getType().getTType());
            pushValue(methodVisitor, (short) field.getId()); // TODO: field.getId() should return short
            methodVisitor.visitMethodInsn(INVOKESPECIAL, "org/apache/thrift/protocol/TField", "<init>", "(Ljava/lang/String;BS)V");

			methodVisitor.visitVarInsn(ALOAD, context.getSlot("protocol"));
            methodVisitor.visitInsn(SWAP);
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "org/apache/thrift/protocol/TProtocol", "writeFieldBegin", "(Lorg/apache/thrift/protocol/TField;)V");

			if (Map.class.isAssignableFrom(clazz)) {
				generateGetFromMap(methodVisitor, context, field);
			}
			else {
				generateGetField(targetClassName, methodVisitor, context, field);
			}
			// protocol.writeXXX(element)
			generateWriteElement(methodVisitor, context, field.getType());

			methodVisitor.visitVarInsn(ALOAD, context.getSlot("protocol"));
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "org/apache/thrift/protocol/TProtocol", "writeFieldEnd", "()V");
            context.release(fieldSlot);
		}

		methodVisitor.visitVarInsn(ALOAD, context.getSlot("protocol"));
		methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "org/apache/thrift/protocol/TProtocol", "writeFieldStop", "()V");

		methodVisitor.visitVarInsn(ALOAD, context.getSlot("protocol"));
		methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "org/apache/thrift/protocol/TProtocol", "writeStructEnd", "()V");

		methodVisitor.visitInsn(RETURN);
		methodVisitor.visitMaxs(1, 1); // TODO: compute these
		methodVisitor.visitEnd();
	}

	/**
	 * Generates bytecode to write element at top of stack
	 *
	 * @param methodVisitor
	 * @param context
	 * @param type
	 */
	private void generateWriteElement(MethodVisitor methodVisitor, FrameRegisterManager context, Type type)
	{
		if (type == BasicType.BOOLEAN) {
			methodVisitor.visitVarInsn(ALOAD, context.getSlot("protocol"));
			methodVisitor.visitInsn(SWAP);
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "org/apache/thrift/protocol/TProtocol", "writeBool", "(Z)V");
		}
		else if (type == BasicType.BYTE) {
			methodVisitor.visitVarInsn(ALOAD, context.getSlot("protocol"));
			methodVisitor.visitInsn(SWAP);
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "org/apache/thrift/protocol/TProtocol", "writeByte", "(B)V");
		}
		else if (type == BasicType.I16) {
			methodVisitor.visitVarInsn(ALOAD, context.getSlot("protocol"));
			methodVisitor.visitInsn(SWAP);
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "org/apache/thrift/protocol/TProtocol", "writeI16", "(S)V");
		}
		else if (type == BasicType.I32) {
			methodVisitor.visitVarInsn(ALOAD, context.getSlot("protocol"));
			methodVisitor.visitInsn(SWAP);
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "org/apache/thrift/protocol/TProtocol", "writeI32", "(I)V");
		}
		else if (type == BasicType.I64) {
			// can't use swap for double... use a temp variable instead
			int slot = context.newAnonymousSlot();
			methodVisitor.visitVarInsn(LSTORE, slot);
			methodVisitor.visitVarInsn(ALOAD, context.getSlot("protocol"));
			methodVisitor.visitVarInsn(LLOAD, slot);
			context.release(slot);
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "org/apache/thrift/protocol/TProtocol", "writeI64", "(J)V");
		}
		else if (type == BasicType.DOUBLE) {
			// can't use swap for double... use a temp variable instead
			int slot = context.newAnonymousSlot();
			methodVisitor.visitVarInsn(DSTORE, slot);
			methodVisitor.visitVarInsn(ALOAD, context.getSlot("protocol"));
			methodVisitor.visitVarInsn(DLOAD, slot);
			context.release(slot);
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "org/apache/thrift/protocol/TProtocol", "writeDouble", "(D)V");
		}
		else if (type == BasicType.BINARY) {
			methodVisitor.visitVarInsn(ALOAD, context.getSlot("protocol"));
			methodVisitor.visitInsn(SWAP);
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "org/apache/thrift/protocol/TProtocol", "writeBinary", "([B)V");
		}
		else if (type == BasicType.STRING) {
			methodVisitor.visitVarInsn(ALOAD, context.getSlot("protocol"));
			methodVisitor.visitInsn(SWAP);
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "org/apache/thrift/protocol/TProtocol", "writeString",
			                              "(Ljava/lang/String;)V");
		}
		else if (type instanceof StructureType) {
			StructureType structureType = (StructureType) type;

			methodVisitor.visitVarInsn(ALOAD, context.getSlot("serializer"));
			methodVisitor.visitInsn(SWAP); // element, serializer => serializer, element
			methodVisitor.visitLdcInsn(structureType.getName());
			methodVisitor.visitVarInsn(ALOAD, context.getSlot("protocol"));
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, Util.getInternalName(Serializer.class), "serialize",
			                              "(Ljava/lang/Object;Ljava/lang/String;Lorg/apache/thrift/protocol/TProtocol;)V");
				
		}
		else if (type instanceof ListType) {
			ListType listType = (ListType) type;
			generateWriteList(methodVisitor, context, listType);
		}
		else if (type instanceof SetType) {
			SetType setType = (SetType) type;
			generateWriteSet(methodVisitor, context, setType);
		}
		else if (type instanceof MapType) {
			MapType mapType = (MapType) type;
			generateWriteMap(methodVisitor, context, mapType);
		}
	}

	
	private void generateWriteList(MethodVisitor methodVisitor, FrameRegisterManager context, ListType listType)
	{
		// top of stack is list we're serializing
		int listSlot = context.newAnonymousSlot();
		methodVisitor.visitVarInsn(ASTORE, listSlot);

		// protocol.writeListBegin(new TList(ttype, object.size))
		methodVisitor.visitVarInsn(ALOAD, context.getSlot("protocol"));

		methodVisitor.visitTypeInsn(NEW, "org/apache/thrift/protocol/TList");
		methodVisitor.visitInsn(DUP);
		pushValue(methodVisitor, listType.getValueType().getTType());
		methodVisitor.visitVarInsn(ALOAD, listSlot);
		methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "size", "()I");
		methodVisitor.visitMethodInsn(INVOKESPECIAL, "org/apache/thrift/protocol/TList", "<init>", "(BI)V");

		methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "org/apache/thrift/protocol/TProtocol", "writeListBegin", "(Lorg/apache/thrift/protocol/TList;)V");

		// at this point, stack is empty
		
		// for (element : value), using a while (iterator.hasNext()) { ... } loop
		methodVisitor.visitVarInsn(ALOAD, listSlot);
		methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "iterator", "()Ljava/util/Iterator;");

		generateWriteIteratorElements(methodVisitor, context, listType.getValueType());
		
		// protocol.writeListEnd()
		methodVisitor.visitVarInsn(ALOAD, context.getSlot("protocol"));
		methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "org/apache/thrift/protocol/TProtocol", "writeListEnd", "()V");

		context.release(listSlot);
	}

	private void generateWriteSet(MethodVisitor methodVisitor, FrameRegisterManager context, SetType setType)
	{
		// top of stack is list we're serializing
		int setSlot = context.newAnonymousSlot();
		methodVisitor.visitVarInsn(ASTORE, setSlot);

		// protocol.writeListBegin(new TList(ttype, object.size))
		methodVisitor.visitVarInsn(ALOAD, context.getSlot("protocol"));

		methodVisitor.visitTypeInsn(NEW, "org/apache/thrift/protocol/TSet");
		methodVisitor.visitInsn(DUP);
		pushValue(methodVisitor, setType.getValueType().getTType());
		methodVisitor.visitVarInsn(ALOAD, setSlot);
		methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/Set", "size", "()I");
		methodVisitor.visitMethodInsn(INVOKESPECIAL, "org/apache/thrift/protocol/TSet", "<init>", "(BI)V");

		methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "org/apache/thrift/protocol/TProtocol", "writeSetBegin", "(Lorg/apache/thrift/protocol/TSet;)V");

		// at this point, stack is empty

		// for (element : value), using a while (iterator.hasNext()) { ... } loop
		methodVisitor.visitVarInsn(ALOAD, setSlot);
		methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/Set", "iterator", "()Ljava/util/Iterator;");

		generateWriteIteratorElements(methodVisitor, context, setType.getValueType());

		// protocol.writeSetEnd()
		methodVisitor.visitVarInsn(ALOAD, context.getSlot("protocol"));
		methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "org/apache/thrift/protocol/TProtocol", "writeSetEnd", "()V");

		context.release(setSlot);
	}


	private void generateWriteMap(MethodVisitor methodVisitor, FrameRegisterManager context, MapType mapType)
	{
		// top of stack is list we're serializing
		int mapSlot = context.newAnonymousSlot();
		methodVisitor.visitVarInsn(ASTORE, mapSlot);

		// protocol.writeListBegin(new TList(ttype, object.size))
		methodVisitor.visitVarInsn(ALOAD, context.getSlot("protocol"));

		methodVisitor.visitTypeInsn(NEW, "org/apache/thrift/protocol/TMap");
		methodVisitor.visitInsn(DUP);
		pushValue(methodVisitor, mapType.getKeyType().getTType());
		pushValue(methodVisitor, mapType.getValueType().getTType());
		methodVisitor.visitVarInsn(ALOAD, mapSlot);
		methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "size", "()I");
		methodVisitor.visitMethodInsn(INVOKESPECIAL, "org/apache/thrift/protocol/TMap", "<init>", "(BBI)V");

		methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "org/apache/thrift/protocol/TProtocol", "writeMapBegin", "(Lorg/apache/thrift/protocol/TMap;)V");

		// at this point, stack is empty

		// for (element : value), using a while (iterator.hasNext()) { ... } loop
		methodVisitor.visitVarInsn(ALOAD, mapSlot);
		methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "entrySet", "()Ljava/util/Set;");
		methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/Set", "iterator", "()Ljava/util/Iterator;");

		// TODO: generalize generateIterator method to take a callback to generate the code for processing each
		// element
		Label loopLabel = new Label();
		Label doneLabel = new Label();

		methodVisitor.visitLabel(loopLabel);
		methodVisitor.visitInsn(DUP); // for iterator.hasNext()
		methodVisitor.visitInsn(DUP); // for iterator.next()

		// iterator.hasNext?
		methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/Iterator", "hasNext", "()Z");

		methodVisitor.visitJumpInsn(IFEQ, doneLabel); // if hasNext returned false (0), we're done

		methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/Iterator", "next", "()Ljava/lang/Object;");
		// element is Map.Entry

		methodVisitor.visitInsn(DUP); // for entry.getKey

		int entrySlot = context.newAnonymousSlot();
		methodVisitor.visitVarInsn(ASTORE, entrySlot); // for entry.getValue

		methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/Map$Entry", "getKey", "()Ljava/lang/Object;");
		generateCast(methodVisitor, mapType.getKeyType());
		generateWriteElement(methodVisitor, context, mapType.getKeyType());

		methodVisitor.visitVarInsn(ALOAD, entrySlot);
		methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/Map$Entry", "getValue", "()Ljava/lang/Object;");
		generateCast(methodVisitor, mapType.getValueType());
		generateWriteElement(methodVisitor, context, mapType.getValueType());

		context.release(entrySlot);
		
		methodVisitor.visitJumpInsn(GOTO, loopLabel);

		methodVisitor.visitLabel(doneLabel);
		methodVisitor.visitInsn(POP); // lingering reference to iterator

		// protocol.writeSetEnd()
		methodVisitor.visitVarInsn(ALOAD, context.getSlot("protocol"));
		methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "org/apache/thrift/protocol/TProtocol", "writeMapEnd", "()V");
		
		context.release(mapSlot);
	}

	/**
	 * Generates code to write the elements of an iterator, one after another
	 *
	 * Assumes iterator is at top of stack
	 *
	 * @param methodVisitor
	 * @param context
	 * @param elementType
	 */
	private void generateWriteIteratorElements(MethodVisitor methodVisitor, FrameRegisterManager context,
	                                           Type elementType)
	{
		Label loopLabel = new Label();
		Label doneLabel = new Label();

		methodVisitor.visitLabel(loopLabel);
		methodVisitor.visitInsn(DUP); // for iterator.hasNext()
		methodVisitor.visitInsn(DUP); // for iterator.next()

		// iterator.hasNext?
		methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/Iterator", "hasNext", "()Z");

		methodVisitor.visitJumpInsn(IFEQ, doneLabel); // if hasNext returned false (0), we're done

		methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/Iterator", "next", "()Ljava/lang/Object;");

		generateCast(methodVisitor, elementType);
		generateWriteElement(methodVisitor, context, elementType);

		methodVisitor.visitJumpInsn(GOTO, loopLabel);

		methodVisitor.visitLabel(doneLabel);
		methodVisitor.visitInsn(POP); // lingering reference to iterator
		methodVisitor.visitInsn(POP); // lingering reference to iterator
	}


	private void generateSystemErrPrintlnBoolean(MethodVisitor visitor)
	{
		visitor.visitInsn(DUP);
		visitor.visitFieldInsn(GETSTATIC, "java/lang/System", "err", "Ljava/io/PrintStream;");
		visitor.visitInsn(SWAP);
		visitor.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Z)V");
	}


	// Prints whatever's at the top of the stack to System.err
	private void generateSystemErrPrintlnConstant(MethodVisitor visitor, String value)
	{
		visitor.visitFieldInsn(GETSTATIC, "java/lang/System", "err", "Ljava/io/PrintStream;");
		visitor.visitLdcInsn(value);
		visitor.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V");
	}

	// Prints whatever's at the top of the stack to System.err
	private void generateSystemErrPrintln(MethodVisitor visitor)
	{
		visitor.visitInsn(DUP);
		visitor.visitFieldInsn(GETSTATIC, "java/lang/System", "err", "Ljava/io/PrintStream;");
		visitor.visitInsn(SWAP);
		visitor.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/Object;)V");
	}

	/**
	 * value for given field is read from the map and left at the top of the stack
	 * 
	 * @param methodVisitor
	 * @param context
	 * @param field
	 */
	private void generateGetFromMap(MethodVisitor methodVisitor, FrameRegisterManager context,
	                                Field field)
	{
		// ((Map) object).get("field name")
		methodVisitor.visitVarInsn(ALOAD, context.getSlot("object"));
		methodVisitor.visitTypeInsn(CHECKCAST, "java/util/Map");
		methodVisitor.visitLdcInsn(field.getName());
		methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "get", "(Ljava/lang/Object;)Ljava/lang/Object;");
		generateCast(methodVisitor, field.getType());
	}

	/**
	 * Downcast from Object -> concrete type, depending on the type passed to the method. Unboxes boxed versions
	 * of primitive types
 	 */
	private void generateCast(MethodVisitor methodVisitor, Type type)
	{
		if (type == BasicType.BOOLEAN) {
			methodVisitor.visitTypeInsn(CHECKCAST, "java/lang/Boolean");
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Boolean", "booleanValue", "()Z");
		}
		else if (type == BasicType.BYTE) {
			methodVisitor.visitTypeInsn(CHECKCAST, "java/lang/Byte");
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Byte", "byteValue", "()B");
		}
		else if (type == BasicType.I16) {
			methodVisitor.visitTypeInsn(CHECKCAST, "java/lang/Short");
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Short", "shortValue", "()S");
		}
		else if (type == BasicType.I32) {
			methodVisitor.visitTypeInsn(CHECKCAST, "java/lang/Integer");
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Integer", "intValue", "()I");
		}
		else if (type == BasicType.I64) {
			methodVisitor.visitTypeInsn(CHECKCAST, "java/lang/Long");
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Long", "longValue", "()J");
		}
		else if (type == BasicType.DOUBLE) {
			methodVisitor.visitTypeInsn(CHECKCAST, "java/lang/Double");
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Double", "doubleValue", "()D");
		}
		else if (type == BasicType.STRING) {
			methodVisitor.visitTypeInsn(CHECKCAST, "java/lang/String");
		}
		else if (type == BasicType.BINARY) {
			methodVisitor.visitTypeInsn(CHECKCAST, "[B");
		}
//		else if (type instanceof ListType) {
//			methodVisitor.visitTypeInsn(CHECKCAST, "java/util/List");
//		}
	}

	/**
	 * Generates code to get the corresponding field from the bean to serialize.
	 *
	 * @param targetClassName
	 * @param methodVisitor
	 * @param context
	 * @param field
	 */
	private void generateGetField(String targetClassName, MethodVisitor methodVisitor, FrameRegisterManager context, Field field)
	{
		methodVisitor.visitVarInsn(ALOAD, context.getSlot("object"));
		methodVisitor.visitTypeInsn(CHECKCAST, targetClassName);

		String getter;
		if (field.getType() == BasicType.BOOLEAN) {
			getter = "is" + Util.toCamelCase(field.getName());
		}
		else {
			getter = "get" + Util.toCamelCase(field.getName());
		}

		if (field.getType() == BasicType.BOOLEAN) {
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, targetClassName, getter, "()Z");
		}
		else if (field.getType() == BasicType.BYTE) {
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, targetClassName, getter, "()B");
		}
		else if (field.getType() == BasicType.I16) {
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, targetClassName, getter, "()S");
		}
		else if (field.getType() == BasicType.I32) {
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, targetClassName, getter, "()I");
		}
		else if (field.getType() == BasicType.I64) {
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, targetClassName, getter, "()J");
		}
		else if (field.getType() == BasicType.DOUBLE) {
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, targetClassName, getter, "()D");
		}
		else if (field.getType() == BasicType.BINARY) {
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, targetClassName, getter, "()[B");
		}
		else if (field.getType() == BasicType.STRING) {
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, targetClassName, getter, "()Ljava/lang/String;");
		}
		else if (field.getType() instanceof StructureType) {
			final String name = ((StructureType) field.getType()).getName();
            Class childClass = classes.get(name);
			if (childClass == null) {
		        throw new IllegalStateException(String.format("Type '%s' not bound to a class", name));
		    }
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, targetClassName,
			                              getter, "()L" + Util.getInternalName(childClass) + ";");
		}
		else if (field.getType() instanceof ListType) {
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, targetClassName,
			                              getter,
			                              "()L" + Util.getInternalName(List.class) + ";");
		}
		else if (field.getType() instanceof SetType) {
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, targetClassName,
			                              getter,
			                              "()L" + Util.getInternalName(Set.class) + ";");

		}
		else if (field.getType() instanceof MapType) {
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, targetClassName,
			                              getter,
			                              "()L" + Util.getInternalName(java.util.Map.class) + ";");
		}

	}

	private void pushValue(MethodVisitor methodVisitor, int value)
	{
		if (value <= Byte.MAX_VALUE) {
			pushValue(methodVisitor, (byte) value);
		}
		else {
			methodVisitor.visitIntInsn(SIPUSH, value);
		}
	}

	private void pushValue(MethodVisitor methodVisitor, byte value)
	{
		switch (value) {
			case -1:
				methodVisitor.visitInsn(ICONST_M1);
				break;
			case 0:
				methodVisitor.visitInsn(ICONST_0);
				break;
			case 1:
				methodVisitor.visitInsn(ICONST_1);
				break;
			case 2:
				methodVisitor.visitInsn(ICONST_2);
				break;
			case 3:
				methodVisitor.visitInsn(ICONST_3);
				break;
			case 4:
				methodVisitor.visitInsn(ICONST_4);
				break;
			case 5:
				methodVisitor.visitInsn(ICONST_5);
				break;
			default:
				methodVisitor.visitIntInsn(BIPUSH, value);
		}
	}

	private void compileConstructor(ClassVisitor writer)
	{
		// constructor
		MethodVisitor constructorVisitor = writer.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
		constructorVisitor.visitCode();
		constructorVisitor.visitVarInsn(ALOAD, 0);
		constructorVisitor.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V");
		constructorVisitor.visitInsn(RETURN);
		constructorVisitor.visitMaxs(1, 1);
		constructorVisitor.visitEnd();
	}
}
