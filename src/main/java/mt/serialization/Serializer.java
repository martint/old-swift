package mt.serialization;

import com.facebook.thrift.TException;
import com.facebook.thrift.protocol.TProtocol;
import mt.serialization.model.BasicType;
import mt.serialization.model.Field;
import mt.serialization.model.ListType;
import mt.serialization.model.MapType;
import mt.serialization.model.SetType;
import mt.serialization.model.StructureType;
import mt.serialization.model.Type;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import static org.objectweb.asm.Opcodes.*;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class Serializer
{
	private Map<String, StructureType> types = new ConcurrentHashMap<String, StructureType>();
	private Map<String, Class<?>> classes = new ConcurrentHashMap<String, Class<?>>();

	private Map<String, StructureSerializer> serializers = new HashMap<String, StructureSerializer>();

	private AtomicInteger sequence = new AtomicInteger();

	private boolean debug;

	public void setDebug(boolean debug)
	{
		this.debug = debug;
	}

	public void bind(StructureType type, Class clazz)
	{
		types.put(type.getName(), type);
		classes.put(type.getName(), clazz);
	}

	public void bindToMap(StructureType type)
	{
		types.put(type.getName(), type);
		classes.put(type.getName(), HashMap.class);
	}


	public void serialize(Object object, String name, TProtocol protocol)
		throws TException
	{
		StructureSerializer serializer = serializers.get(name);
		Class clazz = classes.get(name);

		if (clazz == null) {
			throw new IllegalStateException(String.format("Type '%s' not bound to a class", name));
		}

		StructureType type = types.get(name);

		// construct deserializer
		if (serializer == null) {
			serializer = compileSerializer(type, clazz);
			serializers.put(name, serializer);
		}

		serializer.serialize(object, this, protocol);
	}

	private StructureSerializer compileSerializer(StructureType type, Class clazz)
	{
		ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_FRAMES); // TODO: compute this ourselves?
//		ClassWriter classWriter = new ClassWriter(0); // TODO: compute this ourselves?
		ClassVisitor writer = classWriter;

		if (debug) {
//			writer = new CheckClassAdapter(classWriter);
//			writer = new TraceClassVisitor(writer, new PrintWriter(System.out));
		}

		String targetClassName = Util.getInternalName(clazz);
		String serializerClassName =
			"mt/serialization/generated/Serializer" + clazz.getSimpleName() + "_" + sequence.incrementAndGet();

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
		                                                 + ";Lcom/facebook/thrift/protocol/TProtocol;)V",
		                                                 null, new String[] { "com/facebook/thrift/TException" });

		MethodBuilderContext context = new MethodBuilderContext();
		context.bindSlot("this", 0);
		context.bindSlot("object", 1);
		context.bindSlot("serializer", 2);
		context.bindSlot("protocol", 3);

		methodVisitor.visitCode();

		// protocol.writeStructBegin(new TStruct("name"))
		methodVisitor.visitVarInsn(ALOAD, context.getSlot("protocol"));

		methodVisitor.visitTypeInsn(NEW, "com/facebook/thrift/protocol/TStruct");
		methodVisitor.visitInsn(DUP);
		methodVisitor.visitLdcInsn(type.getName());
		methodVisitor.visitMethodInsn(INVOKESPECIAL, "com/facebook/thrift/protocol/TStruct", "<init>", "(Ljava/lang/String;)V");

		methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/facebook/thrift/protocol/TProtocol", "writeStructBegin", "(Lcom/facebook/thrift/protocol/TStruct;)V");

		// TField tfield = new TField()
		int fieldSlot = context.newAnonymousSlot();
		
		methodVisitor.visitTypeInsn(NEW, "com/facebook/thrift/protocol/TField");
		methodVisitor.visitInsn(DUP);
		methodVisitor.visitMethodInsn(INVOKESPECIAL, "com/facebook/thrift/protocol/TField", "<init>", "()V");
		methodVisitor.visitVarInsn(ASTORE, fieldSlot);

		for (Field field : type.getFields()) {
			// tfield.id = ...
			methodVisitor.visitVarInsn(ALOAD, fieldSlot);
			pushValue(methodVisitor, (short) field.getId()); // TODO: field.getId() should return short
			methodVisitor.visitFieldInsn(PUTFIELD, "com/facebook/thrift/protocol/TField", "id", "S");

			// tfield.type = ...
			methodVisitor.visitVarInsn(ALOAD, fieldSlot);
			pushValue(methodVisitor, field.getType().getTType());
			methodVisitor.visitFieldInsn(PUTFIELD, "com/facebook/thrift/protocol/TField", "type", "B");

			// tfield.name = ...
			methodVisitor.visitVarInsn(ALOAD, fieldSlot);
			methodVisitor.visitLdcInsn(field.getName());
			methodVisitor.visitFieldInsn(PUTFIELD, "com/facebook/thrift/protocol/TField", "name", "Ljava/lang/String;");

			methodVisitor.visitVarInsn(ALOAD, context.getSlot("protocol"));
			methodVisitor.visitVarInsn(ALOAD, fieldSlot);
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/facebook/thrift/protocol/TProtocol", "writeFieldBegin", "(Lcom/facebook/thrift/protocol/TField;)V");

			if (Map.class.isAssignableFrom(clazz)) {
				generateGetFromMap(methodVisitor, context, field);
			}
			else {
				generateGetField(targetClassName, methodVisitor, context, field);
			}
			// protocol.writeXXX(element)
			generateWriteElement(methodVisitor, context, field.getType());

			methodVisitor.visitVarInsn(ALOAD, context.getSlot("protocol"));
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/facebook/thrift/protocol/TProtocol", "writeFieldEnd", "()V");
		}

		methodVisitor.visitVarInsn(ALOAD, context.getSlot("protocol"));
		methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/facebook/thrift/protocol/TProtocol", "writeFieldStop", "()V");

		methodVisitor.visitVarInsn(ALOAD, context.getSlot("protocol"));
		methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/facebook/thrift/protocol/TProtocol", "writeStructEnd", "()V");

		context.release(fieldSlot);
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
	private void generateWriteElement(MethodVisitor methodVisitor, MethodBuilderContext context, Type type)
	{
		if (type == BasicType.BOOLEAN) {
			methodVisitor.visitVarInsn(ALOAD, context.getSlot("protocol"));
			methodVisitor.visitInsn(SWAP);
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/facebook/thrift/protocol/TProtocol", "writeBool", "(Z)V");
		}
		else if (type == BasicType.BYTE) {
			methodVisitor.visitVarInsn(ALOAD, context.getSlot("protocol"));
			methodVisitor.visitInsn(SWAP);
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/facebook/thrift/protocol/TProtocol", "writeByte", "(B)V");
		}
		else if (type == BasicType.I16) {
			methodVisitor.visitVarInsn(ALOAD, context.getSlot("protocol"));
			methodVisitor.visitInsn(SWAP);
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/facebook/thrift/protocol/TProtocol", "writeI16", "(S)V");
		}
		else if (type == BasicType.I32) {
			methodVisitor.visitVarInsn(ALOAD, context.getSlot("protocol"));
			methodVisitor.visitInsn(SWAP);
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/facebook/thrift/protocol/TProtocol", "writeI32", "(I)V");
		}
		else if (type == BasicType.I64) {
			// can't use swap for double... use a temp variable instead
			int slot = context.newAnonymousSlot();
			methodVisitor.visitVarInsn(LSTORE, slot);
			methodVisitor.visitVarInsn(ALOAD, context.getSlot("protocol"));
			methodVisitor.visitVarInsn(LLOAD, slot);
			context.release(slot);
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/facebook/thrift/protocol/TProtocol", "writeI64", "(J)V");
		}
		else if (type == BasicType.DOUBLE) {
			// can't use swap for double... use a temp variable instead
			int slot = context.newAnonymousSlot();
			methodVisitor.visitVarInsn(DSTORE, slot);
			methodVisitor.visitVarInsn(ALOAD, context.getSlot("protocol"));
			methodVisitor.visitVarInsn(DLOAD, slot);
			context.release(slot);
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/facebook/thrift/protocol/TProtocol", "writeDouble", "(D)V");
		}
		else if (type == BasicType.BINARY) {
			methodVisitor.visitVarInsn(ALOAD, context.getSlot("protocol"));
			methodVisitor.visitInsn(SWAP);
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/facebook/thrift/protocol/TProtocol", "writeBinary", "([B)V");
		}
		else if (type == BasicType.STRING) {
			methodVisitor.visitVarInsn(ALOAD, context.getSlot("protocol"));
			methodVisitor.visitInsn(SWAP);
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/facebook/thrift/protocol/TProtocol", "writeString",
			                              "(Ljava/lang/String;)V");
		}
		else if (type instanceof StructureType) {
			StructureType structureType = (StructureType) type;

			methodVisitor.visitVarInsn(ALOAD, context.getSlot("serializer"));
			methodVisitor.visitInsn(SWAP); // element, serializer => serializer, element
			methodVisitor.visitLdcInsn(structureType.getName());
			methodVisitor.visitVarInsn(ALOAD, context.getSlot("protocol"));
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, Util.getInternalName(Serializer.class), "serialize",
			                              "(Ljava/lang/Object;Ljava/lang/String;Lcom/facebook/thrift/protocol/TProtocol;)V");
				
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

	
	private void generateWriteList(MethodVisitor methodVisitor, MethodBuilderContext context, ListType listType)
	{
		// top of stack is list we're serializing
		int listSlot = context.newAnonymousSlot();
		methodVisitor.visitVarInsn(ASTORE, listSlot);

		// protocol.writeListBegin(new TList(ttype, object.size))
		methodVisitor.visitVarInsn(ALOAD, context.getSlot("protocol"));

		methodVisitor.visitTypeInsn(NEW, "com/facebook/thrift/protocol/TList");
		methodVisitor.visitInsn(DUP);
		pushValue(methodVisitor, listType.getValueType().getTType());
		methodVisitor.visitVarInsn(ALOAD, listSlot);
		methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "size", "()I");
		methodVisitor.visitMethodInsn(INVOKESPECIAL, "com/facebook/thrift/protocol/TList", "<init>", "(BI)V");

		methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/facebook/thrift/protocol/TProtocol", "writeListBegin", "(Lcom/facebook/thrift/protocol/TList;)V");

		// at this point, stack is empty
		
		// for (element : value), using a while (iterator.hasNext()) { ... } loop
		methodVisitor.visitVarInsn(ALOAD, listSlot);
		methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "iterator", "()Ljava/util/Iterator;");

		generateWriteIteratorElements(methodVisitor, context, listType.getValueType());
		
		// protocol.writeListEnd()
		methodVisitor.visitVarInsn(ALOAD, context.getSlot("protocol"));
		methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/facebook/thrift/protocol/TProtocol", "writeListEnd", "()V");

		context.release(listSlot);
	}

	private void generateWriteSet(MethodVisitor methodVisitor, MethodBuilderContext context, SetType setType)
	{
		// top of stack is list we're serializing
		int setSlot = context.newAnonymousSlot();
		methodVisitor.visitVarInsn(ASTORE, setSlot);

		// protocol.writeListBegin(new TList(ttype, object.size))
		methodVisitor.visitVarInsn(ALOAD, context.getSlot("protocol"));

		methodVisitor.visitTypeInsn(NEW, "com/facebook/thrift/protocol/TSet");
		methodVisitor.visitInsn(DUP);
		pushValue(methodVisitor, setType.getValueType().getTType());
		methodVisitor.visitVarInsn(ALOAD, setSlot);
		methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/Set", "size", "()I");
		methodVisitor.visitMethodInsn(INVOKESPECIAL, "com/facebook/thrift/protocol/TSet", "<init>", "(BI)V");

		methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/facebook/thrift/protocol/TProtocol", "writeSetBegin", "(Lcom/facebook/thrift/protocol/TSet;)V");

		// at this point, stack is empty

		// for (element : value), using a while (iterator.hasNext()) { ... } loop
		methodVisitor.visitVarInsn(ALOAD, setSlot);
		methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/Set", "iterator", "()Ljava/util/Iterator;");

		generateWriteIteratorElements(methodVisitor, context, setType.getValueType());

		// protocol.writeSetEnd()
		methodVisitor.visitVarInsn(ALOAD, context.getSlot("protocol"));
		methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/facebook/thrift/protocol/TProtocol", "writeSetEnd", "()V");

		context.release(setSlot);
	}


	private void generateWriteMap(MethodVisitor methodVisitor, MethodBuilderContext context, MapType mapType)
	{
		// top of stack is list we're serializing
		int mapSlot = context.newAnonymousSlot();
		methodVisitor.visitVarInsn(ASTORE, mapSlot);

		// protocol.writeListBegin(new TList(ttype, object.size))
		methodVisitor.visitVarInsn(ALOAD, context.getSlot("protocol"));

		methodVisitor.visitTypeInsn(NEW, "com/facebook/thrift/protocol/TMap");
		methodVisitor.visitInsn(DUP);
		pushValue(methodVisitor, mapType.getKeyType().getTType());
		pushValue(methodVisitor, mapType.getValueType().getTType());
		methodVisitor.visitVarInsn(ALOAD, mapSlot);
		methodVisitor.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "size", "()I");
		methodVisitor.visitMethodInsn(INVOKESPECIAL, "com/facebook/thrift/protocol/TMap", "<init>", "(BBI)V");

		methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/facebook/thrift/protocol/TProtocol", "writeMapBegin", "(Lcom/facebook/thrift/protocol/TMap;)V");

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
		generateCast(methodVisitor, mapType.getKeyType());
		generateWriteElement(methodVisitor, context, mapType.getKeyType());

		context.release(entrySlot);
		
		methodVisitor.visitJumpInsn(GOTO, loopLabel);

		methodVisitor.visitLabel(doneLabel);
		methodVisitor.visitInsn(POP); // lingering reference to iterator

		// protocol.writeSetEnd()
		methodVisitor.visitVarInsn(ALOAD, context.getSlot("protocol"));
		methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/facebook/thrift/protocol/TProtocol", "writeMapEnd", "()V");
		
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
	private void generateWriteIteratorElements(MethodVisitor methodVisitor, MethodBuilderContext context,
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
	private void generateGetFromMap(MethodVisitor methodVisitor, MethodBuilderContext context,
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
	private void generateGetField(String targetClassName, MethodVisitor methodVisitor, MethodBuilderContext context, Field field)
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
			Class childClass = classes.get(((StructureType) field.getType()).getName());
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
