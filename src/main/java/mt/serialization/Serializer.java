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
import org.objectweb.asm.MethodVisitor;
import static org.objectweb.asm.Opcodes.*;
import org.objectweb.asm.util.CheckClassAdapter;
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
		ClassVisitor writer = classWriter;

		if (debug) {
			writer = new CheckClassAdapter(classWriter);
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

			// protocol. ...
			methodVisitor.visitVarInsn(ALOAD, context.getSlot("protocol"));
			if (Map.class.isAssignableFrom(clazz)) {
				methodVisitor.visitVarInsn(ALOAD, context.getSlot("object"));
				methodVisitor.visitTypeInsn(CHECKCAST, targetClassName);
				methodVisitor.visitLdcInsn(field.getName());
				generateGetFromMap(targetClassName, methodVisitor, context, field);

				// ... writeXXX(map.get("field name"))
				generateWriteElement(methodVisitor, context, field.getType());
			}
			else {
//				generateReadElement(methodVisitor, context, field.getType());
//				generateSetTargetField(targetClassName, methodVisitor, context, field);
			}

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

	private void generateWriteElement(MethodVisitor methodVisitor, MethodBuilderContext context, Type type)
	{
		if (type == BasicType.BOOLEAN) {
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/facebook/thrift/protocol/TProtocol", "writeBool", "(Z)V");
		}
		else if (type == BasicType.BYTE) {
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/facebook/thrift/protocol/TProtocol", "writeByte", "(B)V");
		}
		else if (type == BasicType.I16) {
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/facebook/thrift/protocol/TProtocol", "writeI16", "(S)V");
		}
		else if (type == BasicType.I32) {
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/facebook/thrift/protocol/TProtocol", "writeI32", "(I)V");
		}
		else if (type == BasicType.I64) {
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/facebook/thrift/protocol/TProtocol", "writeI64", "(J)V");
		}
		else if (type == BasicType.DOUBLE) {
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/facebook/thrift/protocol/TProtocol", "writeDouble", "(D)V");
		}
		else if (type == BasicType.BINARY) {
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/facebook/thrift/protocol/TProtocol", "writeBinary", "([B)V");
		}
		else if (type == BasicType.STRING) {
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/facebook/thrift/protocol/TProtocol", "writeString",
			                              "(Ljava/lang/String;)V");
		}
		else if (type instanceof StructureType) {
			// TODO
//			StructureType structureType = (StructureType) type;
//
//			methodVisitor.visitVarInsn(ALOAD, context.getSlot("deserializer"));
//			methodVisitor.visitLdcInsn(structureType.getName());
//			methodVisitor.visitVarInsn(ALOAD, context.getSlot("protocol"));
//			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, Util.getInternalName(Deserializer.class),
//			                              "deserialize",
//			                              "(Ljava/lang/String;Lcom/facebook/thrift/protocol/TProtocol;)Ljava/lang/Object;");
		}
		else if (type instanceof ListType) {
			// TODO
//			ListType listType = (ListType) type;
//			generateReadList(methodVisitor, context, listType);
		}
		else if (type instanceof SetType) {
			// TODO
//			SetType setType = (SetType) type;
//			generateReadSet(methodVisitor, context, setType);
		}
		else if (type instanceof MapType) {
			// TODO
//			MapType mapType = (MapType) type;
//			generateReadMap(methodVisitor, context, mapType);
		}
	}

	private void generateGetFromMap(String targetClassName, MethodVisitor methodVisitor, MethodBuilderContext context,
	                                Field field)
	{
		methodVisitor.visitMethodInsn(INVOKEVIRTUAL, targetClassName, "get", "(Ljava/lang/Object;)Ljava/lang/Object;");
		generateCast(methodVisitor, field.getType());
	}

	/**
	 * Generates code to (optionally) convert the object at the top of the stack to primitive, depending on the 
	 * type of the field
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
	}

	// TODO: autoboxing support: setXXX(Integer) vs setXXX(int)
	private void generateGetField(String targetClassName, MethodVisitor methodVisitor, MethodBuilderContext context, Field field)
	{
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
			// TODO
			Class childClass = classes.get(((StructureType) field.getType()).getName());
			methodVisitor.visitTypeInsn(CHECKCAST, Util.getInternalName(childClass));
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, targetClassName,
			                              getter, "(L" + Util.getInternalName(childClass) + ";)V");
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
			                              "()(L" + Util.getInternalName(java.util.Map.class) + ";");
		}

	}

	private void pushValue(MethodVisitor methodVisitor, int value)
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
				if (value <= Byte.MAX_VALUE) {
					methodVisitor.visitIntInsn(BIPUSH, value);
				}
				else {
					methodVisitor.visitIntInsn(SIPUSH, value);
				}
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
