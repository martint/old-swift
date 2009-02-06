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

import com.facebook.thrift.TException;
import com.facebook.thrift.protocol.TProtocol;
import mt.swift.model.BasicType;
import mt.swift.model.Field;
import mt.swift.model.ListType;
import mt.swift.model.MapType;
import mt.swift.model.SetType;
import mt.swift.model.StructureType;
import mt.swift.model.Type;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import static org.objectweb.asm.Opcodes.*;
import org.objectweb.asm.util.CheckClassAdapter;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A dynamic deserializer for Thrift structures.
 *
 * This class is capable of deserializing a Thrift-serialized stream into a Map or Javabean, according to a
 * user-specified Thrift schema definition.
 *
 */
public class Deserializer
{
	private Map<String, StructureType> types = new ConcurrentHashMap<String, StructureType>();
	private Map<String, Class<?>> classes = new ConcurrentHashMap<String, Class<?>>();

	private Map<String, StructureDeserializer<?>> deserializers = new ConcurrentHashMap<String, StructureDeserializer<?>>();

	private boolean debug = false;

	public void setDebug(boolean debug)
	{
		this.debug = debug;
	}

	/**
	 * Bind the given Thrift schema to the provided Javabean-compatible class
	 *
	 * @param type the thrift schema
	 * @param clazz the class of the Javabean
	 */
	public void bind(StructureType type, Class clazz)
	{
		types.put(type.getName(), type);
		classes.put(type.getName(), clazz);
	}

	/**
	 * Bind the given Thrift schema to a generic Map.
	 *
	 * @param type the thrift schema
	 */
	public void bindToMap(StructureType type)
	{
		types.put(type.getName(), type);
		classes.put(type.getName(), HashMap.class);
	}

	/**
	 * Deserialize the specified structure from the given protocol
	 *
	 * @param name the name of the structure type to read from the protocol
	 * @param protocol the protocol
	 * @return a map or javabean corresponding to the deserialized object
	 * @throws TException if an error occurs during deserialization
	 */
	public <T> T deserialize(String name, TProtocol protocol)
		throws TException
	{
		StructureDeserializer<T> deserializer = (StructureDeserializer<T>) deserializers.get(name);
		Class<T> clazz = (Class<T>) classes.get(name);

		if (clazz == null) {
			// TODO: deserialize into Map<Integer, ?> as a last resort
			throw new IllegalStateException(String.format("Type '%s' not bound to a class", name));
		}
		
		StructureType type = types.get(name);

		// construct deserializer
		if (deserializer == null) {
			deserializer = compileDeserializer(type, clazz);
			deserializers.put(name, deserializer);
		}

		return deserializer.deserialize(this, protocol);
	}


	private AtomicInteger sequence = new AtomicInteger();

	private <T> StructureDeserializer<T> compileDeserializer(StructureType type, Class<T> clazz)
	{
		ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES); // TODO: compute this ourselves?
		ClassVisitor writer = classWriter;

		if (debug) {
			writer = new CheckClassAdapter(classWriter);
//			writer = new TraceClassVisitor(writer, new PrintWriter(System.out));
		}

		String targetClassName = Util.getInternalName(clazz);
		String deserializerClassName =
			"mt/swift/generated/Deserializer" + clazz.getSimpleName() + "_" + sequence.incrementAndGet();

		// class metadata
		writer.visit(V1_6, ACC_PUBLIC + ACC_SUPER, deserializerClassName,
		             "Ljava/lang/Object;L" + Util.getInternalName(StructureDeserializer.class) +
		             "<L" + targetClassName + ";>;",
		             "java/lang/Object",
		             new String[] { Util.getInternalName(StructureDeserializer.class) });

		compileConstructor(writer);
		compileDeserializeMethod(type, writer, targetClassName, clazz);
		compileBridgeMethod(writer, targetClassName, deserializerClassName);

		writer.visitEnd();

		if (debug) {
			ClassReader reader = new ClassReader(classWriter.toByteArray());
			reader.accept(new TraceClassVisitor(new PrintWriter(System.out)), 0);
		}

		ByteArrayClassLoader loader = new ByteArrayClassLoader();
		try {
			return (StructureDeserializer<T>) loader.defineClass(deserializerClassName.replace('/', '.'),
			                                                     classWriter.toByteArray()).newInstance();
		}
		catch (InstantiationException e) {
			throw new RuntimeException(e);
		}
		catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	private void compileBridgeMethod(ClassVisitor writer, String targetClassName, String deserializerClassName)
	{
		// this method is needed to support the generics-based call 
		MethodVisitor syntheticMethodVisitor = writer.visitMethod(ACC_PUBLIC + ACC_BRIDGE + ACC_SYNTHETIC,
		                                                          "deserialize",
		                                                          "(L" + org.objectweb.asm.Type
			                                                          .getInternalName(Deserializer.class)
		                                                          +
		                                                          ";Lcom/facebook/thrift/protocol/TProtocol;)Ljava/lang/Object;",
		                                                          null,
		                                                          new String[] { "com/facebook/thrift/TException" });
		syntheticMethodVisitor.visitCode();
		syntheticMethodVisitor.visitVarInsn(ALOAD, 0);
		syntheticMethodVisitor.visitVarInsn(ALOAD, 1);
		syntheticMethodVisitor.visitVarInsn(ALOAD, 2);
		syntheticMethodVisitor.visitMethodInsn(INVOKEVIRTUAL, deserializerClassName,
		                                       "deserialize",
		                                       "(L" + Util.getInternalName(Deserializer.class)
		                                       + ";Lcom/facebook/thrift/protocol/TProtocol;)L" + targetClassName + ";");
		syntheticMethodVisitor.visitInsn(ARETURN);
		syntheticMethodVisitor.visitMaxs(3, 3);
		syntheticMethodVisitor.visitEnd();
	}

	private void compileDeserializeMethod(StructureType type, ClassVisitor writer, String targetClassName, Class targetClass)
	{
		MethodVisitor methodVisitor = writer.visitMethod(ACC_PUBLIC, "deserialize",
		                                                 "(L" +
		                                                 Util.getInternalName(Deserializer.class)
		                                                 + ";Lcom/facebook/thrift/protocol/TProtocol;)L"
		                                                 + targetClassName + ";",
		                                                 null, new String[] { "com/facebook/thrift/TException" });

		FrameRegisterManager context = new FrameRegisterManager();
		context.bindSlot("this", 0);
		context.bindSlot("deserializer", 1);
		context.bindSlot("protocol", 2);
		context.newSlot("target");
		context.newSlot("tfield");

		methodVisitor.visitCode();

		// <target> result = new <target>()
		methodVisitor.visitTypeInsn(NEW, targetClassName);
		methodVisitor.visitInsn(DUP);
		methodVisitor.visitMethodInsn(INVOKESPECIAL, targetClassName, "<init>", "()V");
		methodVisitor.visitVarInsn(ASTORE, context.getSlot("target"));

		// protocol.readStructBegin()
		methodVisitor.visitVarInsn(ALOAD, context.getSlot("protocol"));
		methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/facebook/thrift/protocol/TProtocol", "readStructBegin",
		                              "()Lcom/facebook/thrift/protocol/TStruct;");
		methodVisitor.visitInsn(POP); // discard return value

		// while (true)
		Label whileLabel = new Label();
		methodVisitor.visitLabel(whileLabel);

		// TField tfield = protocol.readFieldBegin()
		methodVisitor.visitVarInsn(ALOAD, context.getSlot("protocol"));
		methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/facebook/thrift/protocol/TProtocol", "readFieldBegin",
		                              "()Lcom/facebook/thrift/protocol/TField;");
		methodVisitor.visitVarInsn(ASTORE, context.getSlot("tfield"));

		// tfield.type
		methodVisitor.visitVarInsn(ALOAD, context.getSlot("tfield"));
		methodVisitor.visitFieldInsn(GETFIELD, "com/facebook/thrift/protocol/TField", "type", "B");

		methodVisitor.visitFieldInsn(GETSTATIC, "com/facebook/thrift/protocol/TType", "STOP", "B");

		// if (tfield.type == TType.STOP) { break; }
		Label endWhile = new Label();
		methodVisitor.visitJumpInsn(IF_ICMPEQ, endWhile);

		// tfield.id
		methodVisitor.visitVarInsn(ALOAD, context.getSlot("tfield"));
		methodVisitor.visitFieldInsn(GETFIELD, "com/facebook/thrift/protocol/TField", "id", "S");

		List<Field> fields = new ArrayList<Field>(type.getFields());
		int[] ids = new int[fields.size()];
		Label[] labels = new Label[fields.size()];
		for (int i = 0; i < fields.size(); ++i) {
			ids[i] = fields.get(i).getId();
			labels[i] = new Label();
		}

		Label fieldSkipped = new Label();

		methodVisitor.visitLookupSwitchInsn(fieldSkipped, ids, labels);

		for (int i = 0; i < fields.size(); ++i) {
			Field field = fields.get(i);

			methodVisitor.visitLabel(labels[i]);

			// if (tfield.type == ###)
			methodVisitor.visitVarInsn(ALOAD, context.getSlot("tfield"));
			methodVisitor.visitFieldInsn(GETFIELD, "com/facebook/thrift/protocol/TField", "type", "B");
			methodVisitor.visitIntInsn(BIPUSH, field.getType().getTType());
			methodVisitor.visitJumpInsn(IF_ICMPNE, fieldSkipped);

			methodVisitor.visitVarInsn(ALOAD, context.getSlot("target"));
			if (Map.class.isAssignableFrom(targetClass)) {
				methodVisitor.visitLdcInsn(field.getName());
				generateReadElement(methodVisitor, context, field.getType());
				generateAddToMap(targetClassName, methodVisitor, context, field);
			}
			else {
				generateReadElement(methodVisitor, context, field.getType());
				generateSetTargetField(targetClassName, methodVisitor, context, field);
			}
			
			methodVisitor.visitJumpInsn(GOTO, whileLabel);
		}

		methodVisitor.visitLabel(fieldSkipped);
		methodVisitor.visitVarInsn(ALOAD, context.getSlot("protocol"));
		methodVisitor.visitVarInsn(ALOAD, context.getSlot("tfield"));
		methodVisitor.visitFieldInsn(GETFIELD, "com/facebook/thrift/protocol/TField", "type", "B");
		methodVisitor.visitMethodInsn(INVOKESTATIC, "com/facebook/thrift/protocol/TProtocolUtil", "skip",
		                              "(Lcom/facebook/thrift/protocol/TProtocol;B)V");

		// end while
		methodVisitor.visitJumpInsn(GOTO, whileLabel);

		methodVisitor.visitLabel(endWhile);

		// protocol.readStructEnd()
		methodVisitor.visitVarInsn(ALOAD, context.getSlot("protocol"));
		methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/facebook/thrift/protocol/TProtocol", "readStructEnd", "()V");

		// return result
		methodVisitor.visitVarInsn(ALOAD, context.getSlot("target"));
		methodVisitor.visitInsn(ARETURN);

		methodVisitor.visitMaxs(1, 1); // TODO: what should these be?
		methodVisitor.visitEnd();
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


	private void generateAddToMap(String targetClassName, MethodVisitor visitor, FrameRegisterManager context, Field field)
	{
		generateConvertToObject(visitor, field.getType());
		visitor.visitMethodInsn(INVOKEVIRTUAL, targetClassName, "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");
		visitor.visitInsn(POP);
	}

	// TODO: autoboxing support: setXXX(Integer) vs setXXX(int)
	private void generateSetTargetField(String targetClassName, MethodVisitor methodVisitor, FrameRegisterManager context, Field field)
	{
		String setter = "set" + Util.toCamelCase(field.getName());

		if (field.getType() == BasicType.BOOLEAN) {
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, targetClassName, setter, "(Z)V");
		}
		else if (field.getType() == BasicType.BYTE) {
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, targetClassName, setter, "(B)V");
		}
		else if (field.getType() == BasicType.I16) {
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, targetClassName, setter, "(S)V");
		}
		else if (field.getType() == BasicType.I32) {
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, targetClassName, setter, "(I)V");
		}
		else if (field.getType() == BasicType.I64) {
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, targetClassName, setter, "(J)V");
		}
		else if (field.getType() == BasicType.DOUBLE) {
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, targetClassName, setter, "(D)V");
		}
		else if (field.getType() == BasicType.BINARY) {
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, targetClassName, setter, "([B)V");
		}
		else if (field.getType() == BasicType.STRING) {
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, targetClassName, setter, "(Ljava/lang/String;)V");
		}
		else if (field.getType() instanceof StructureType) {
			Class childClass = classes.get(((StructureType) field.getType()).getName());
			methodVisitor.visitTypeInsn(CHECKCAST, Util.getInternalName(childClass));
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, targetClassName,
			                              setter, "(L" + Util.getInternalName(childClass) + ";)V");
		}
		else if (field.getType() instanceof ListType) {
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, targetClassName,
			                              setter,
			                              "(L" + Util.getInternalName(java.util.List.class) + ";)V");
		}
		else if (field.getType() instanceof SetType) {
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, targetClassName,
			                              setter,
			                              "(L" + Util.getInternalName(java.util.Set.class) + ";)V");

		}
		else if (field.getType() instanceof MapType) {
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, targetClassName,
			                              setter,
			                              "(L" + Util.getInternalName(java.util.Map.class) + ";)V");
		}

	}

	// leaves result in stack(0)
	private void generateReadList(MethodVisitor methodVisitor, FrameRegisterManager context, ListType listType)
	{
		// protocol.readListBegin()
		int tlistSizeLocal = context.newAnonymousSlot();
		int loopCounterLocal = context.newAnonymousSlot();

		methodVisitor.visitVarInsn(ALOAD, context.getSlot("protocol"));
		methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/facebook/thrift/protocol/TProtocol", "readListBegin",
		                              "()Lcom/facebook/thrift/protocol/TList;");
		methodVisitor.visitFieldInsn(GETFIELD, "com/facebook/thrift/protocol/TList", "size", "I");
		methodVisitor.visitVarInsn(ISTORE, tlistSizeLocal);

		// result = new ArrayList(tlist.size)
		methodVisitor.visitTypeInsn(NEW, "java/util/ArrayList");
		methodVisitor.visitInsn(DUP);
		methodVisitor.visitVarInsn(ILOAD, tlistSizeLocal);
		methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/util/ArrayList", "<init>", "(I)V");

		// i = 0
		methodVisitor.visitInsn(ICONST_0);
		methodVisitor.visitVarInsn(ISTORE, loopCounterLocal); // #4 = loop counter

		Label done = new Label();
		Label loop = new Label();
		methodVisitor.visitLabel(loop);
		methodVisitor.visitVarInsn(ILOAD, loopCounterLocal);
		methodVisitor.visitVarInsn(ILOAD, tlistSizeLocal);
		methodVisitor.visitJumpInsn(IF_ICMPGE, done);

		methodVisitor.visitInsn(DUP); // ArrayList

		generateReadElement(methodVisitor, context, listType.getValueType());
		generateConvertToObject(methodVisitor, listType.getValueType());

		// entry is left on stack(0). Add to list
		methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/util/ArrayList", "add", "(Ljava/lang/Object;)Z");
		methodVisitor.visitInsn(POP);

		methodVisitor.visitIincInsn(loopCounterLocal, 1);
		methodVisitor.visitJumpInsn(GOTO, loop);

		methodVisitor.visitLabel(done);
		methodVisitor.visitVarInsn(ALOAD, context.getSlot("protocol"));
		methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/facebook/thrift/protocol/TProtocol", "readListEnd", "()V");

		context.release(tlistSizeLocal);
		context.release(loopCounterLocal);
	}

	private void generateConvertToObject(MethodVisitor methodVisitor, Type type)
	{
		if (type == BasicType.BOOLEAN) {
			methodVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Boolean", "valueOf", "(Z)Ljava/lang/Boolean;");
		}
		else if (type == BasicType.BYTE) {
			methodVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Byte", "valueOf", "(B)Ljava/lang/Byte;");
		}
		else if (type == BasicType.I16) {
			methodVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Short", "valueOf", "(S)Ljava/lang/Short;");
		}
		else if (type == BasicType.I32) {
			methodVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Integer", "valueOf", "(I)Ljava/lang/Integer;");
		}
		else if (type == BasicType.I64) {
			methodVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Long", "valueOf", "(J)Ljava/lang/Long;");
		}
		else if (type == BasicType.DOUBLE) {
			methodVisitor.visitMethodInsn(INVOKESTATIC, "java/lang/Double", "valueOf", "(D)Ljava/lang/Double;");
		}
	}

	private void generateReadElement(MethodVisitor methodVisitor, FrameRegisterManager context, Type type)
	{
		if (type == BasicType.BOOLEAN) {
			methodVisitor.visitVarInsn(ALOAD, context.getSlot("protocol"));
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/facebook/thrift/protocol/TProtocol", "readBool", "()Z");
		}
		else if (type == BasicType.BYTE) {
			methodVisitor.visitVarInsn(ALOAD, context.getSlot("protocol"));
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/facebook/thrift/protocol/TProtocol", "readByte", "()B");
		}
		else if (type == BasicType.I16) {
			methodVisitor.visitVarInsn(ALOAD, context.getSlot("protocol"));
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/facebook/thrift/protocol/TProtocol", "readI16", "()S");
		}
		else if (type == BasicType.I32) {
			methodVisitor.visitVarInsn(ALOAD, context.getSlot("protocol"));
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/facebook/thrift/protocol/TProtocol", "readI32", "()I");
		}
		else if (type == BasicType.I64) {
			methodVisitor.visitVarInsn(ALOAD, context.getSlot("protocol"));
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/facebook/thrift/protocol/TProtocol", "readI64", "()J");
		}
		else if (type == BasicType.DOUBLE) {
			methodVisitor.visitVarInsn(ALOAD, context.getSlot("protocol"));
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/facebook/thrift/protocol/TProtocol", "readDouble", "()D");
		}
		else if (type == BasicType.BINARY) {
			methodVisitor.visitVarInsn(ALOAD, context.getSlot("protocol"));
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/facebook/thrift/protocol/TProtocol", "readBinary",
			                              "()[B");
		}
		else if (type == BasicType.STRING) {
			methodVisitor.visitVarInsn(ALOAD, context.getSlot("protocol"));
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/facebook/thrift/protocol/TProtocol", "readString",
			                              "()Ljava/lang/String;");
		}
		else if (type instanceof StructureType) {
			StructureType structureType = (StructureType) type;

			methodVisitor.visitVarInsn(ALOAD, context.getSlot("deserializer"));
			methodVisitor.visitLdcInsn(structureType.getName());
			methodVisitor.visitVarInsn(ALOAD, context.getSlot("protocol"));
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, Util.getInternalName(Deserializer.class),
			                              "deserialize",
			                              "(Ljava/lang/String;Lcom/facebook/thrift/protocol/TProtocol;)Ljava/lang/Object;");
		}
		else if (type instanceof ListType) {
			ListType listType = (ListType) type;
			generateReadList(methodVisitor, context, listType);
		}
		else if (type instanceof SetType) {
			SetType setType = (SetType) type;
			generateReadSet(methodVisitor, context, setType);
		}
		else if (type instanceof MapType) {
			MapType mapType = (MapType) type;
			generateReadMap(methodVisitor, context, mapType);
		}
	}

	private void generateReadSet(MethodVisitor methodVisitor, FrameRegisterManager context, SetType type)
	{
		// protocol.readListBegin()
		int tsetSizeLocal = context.newAnonymousSlot();
		int loopCounterLocal = context.newAnonymousSlot();

		methodVisitor.visitVarInsn(ALOAD, context.getSlot("protocol"));
		methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/facebook/thrift/protocol/TProtocol", "readSetBegin",
		                              "()Lcom/facebook/thrift/protocol/TSet;");
		methodVisitor.visitFieldInsn(GETFIELD, "com/facebook/thrift/protocol/TSet", "size", "I");
		methodVisitor.visitVarInsn(ISTORE, tsetSizeLocal);

		// result = new ArrayList(tlist.size)
		methodVisitor.visitTypeInsn(NEW, "java/util/HashSet");
		methodVisitor.visitInsn(DUP);
		methodVisitor.visitVarInsn(ILOAD, tsetSizeLocal);
		methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/util/HashSet", "<init>", "(I)V");

		// i = 0
		methodVisitor.visitInsn(ICONST_0);
		methodVisitor.visitVarInsn(ISTORE, loopCounterLocal); // #4 = loop counter

		Label done = new Label();
		Label loop = new Label();
		methodVisitor.visitLabel(loop);
		methodVisitor.visitVarInsn(ILOAD, loopCounterLocal);
		methodVisitor.visitVarInsn(ILOAD, tsetSizeLocal);
		methodVisitor.visitJumpInsn(IF_ICMPGE, done);

		methodVisitor.visitInsn(DUP); // ArrayList

		generateReadElement(methodVisitor, context, type.getValueType());
		generateConvertToObject(methodVisitor, type.getValueType());

		// entry is left on stack(0). Add to list
		methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/util/HashSet", "add", "(Ljava/lang/Object;)Z");
		methodVisitor.visitInsn(POP);

		methodVisitor.visitIincInsn(loopCounterLocal, 1);
		methodVisitor.visitJumpInsn(GOTO, loop);

		methodVisitor.visitLabel(done);
		methodVisitor.visitVarInsn(ALOAD, context.getSlot("protocol"));
		methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/facebook/thrift/protocol/TProtocol", "readSetEnd", "()V");

		context.release(tsetSizeLocal);
		context.release(loopCounterLocal);
	}

	private void generateReadMap(MethodVisitor methodVisitor, FrameRegisterManager context, MapType type)
	{
		// protocol.readListBegin()
		int tmapSizeLocal = context.newAnonymousSlot();
		int loopCounterLocal = context.newAnonymousSlot();

		methodVisitor.visitVarInsn(ALOAD, context.getSlot("protocol"));
		methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/facebook/thrift/protocol/TProtocol", "readMapBegin",
		                              "()Lcom/facebook/thrift/protocol/TMap;");
		methodVisitor.visitFieldInsn(GETFIELD, "com/facebook/thrift/protocol/TMap", "size", "I");
		methodVisitor.visitVarInsn(ISTORE, tmapSizeLocal);

		// result = new ArrayList(tlist.size)
		methodVisitor.visitTypeInsn(NEW, "java/util/LinkedHashMap");
		methodVisitor.visitInsn(DUP);
		methodVisitor.visitVarInsn(ILOAD, tmapSizeLocal);
		methodVisitor.visitInsn(ICONST_2); // allocate 2 * tmap.size to avoid rehashing while building map
		methodVisitor.visitInsn(IMUL);
		methodVisitor.visitMethodInsn(INVOKESPECIAL, "java/util/LinkedHashMap", "<init>", "(I)V");

		// i = 0
		methodVisitor.visitInsn(ICONST_0);
		methodVisitor.visitVarInsn(ISTORE, loopCounterLocal); // #4 = loop counter

		Label done = new Label();
		Label loop = new Label();
		methodVisitor.visitLabel(loop);
		methodVisitor.visitVarInsn(ILOAD, loopCounterLocal);
		methodVisitor.visitVarInsn(ILOAD, tmapSizeLocal);
		methodVisitor.visitJumpInsn(IF_ICMPGE, done);

		methodVisitor.visitInsn(DUP); // Map

		generateReadElement(methodVisitor, context, type.getKeyType());
		generateConvertToObject(methodVisitor, type.getKeyType());
		generateReadElement(methodVisitor, context, type.getValueType());
		generateConvertToObject(methodVisitor, type.getValueType());

		// entry is left on stack(0). Add to list
		methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "java/util/LinkedHashMap", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;");
		methodVisitor.visitInsn(POP);

		methodVisitor.visitIincInsn(loopCounterLocal, 1);
		methodVisitor.visitJumpInsn(GOTO, loop);

		methodVisitor.visitLabel(done);
		methodVisitor.visitVarInsn(ALOAD, context.getSlot("protocol"));
		methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/facebook/thrift/protocol/TProtocol", "readMapEnd", "()V");

		context.release(tmapSizeLocal);
		context.release(loopCounterLocal);
	}

}
