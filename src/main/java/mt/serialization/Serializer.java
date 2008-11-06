package mt.serialization;

import com.facebook.thrift.TException;
import com.facebook.thrift.protocol.TProtocol;
import mt.serialization.model.Field;
import mt.serialization.model.StructureType;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;
import static org.objectweb.asm.Opcodes.*;
import org.objectweb.asm.util.CheckClassAdapter;
import org.objectweb.asm.util.TraceClassVisitor;

import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
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

			// TODO: write data
			
			methodVisitor.visitVarInsn(ALOAD, context.getSlot("protocol"));
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/facebook/thrift/protocol/TProtocol", "writeFieldEnd", "()V");
		}

		methodVisitor.visitVarInsn(ALOAD, context.getSlot("protocol"));
		methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/facebook/thrift/protocol/TProtocol", "writeStructEnd", "()V");

		context.release(fieldSlot);
		methodVisitor.visitInsn(RETURN);
		methodVisitor.visitMaxs(1, 1); // TODO: compute these
		methodVisitor.visitEnd();
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
