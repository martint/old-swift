package mt.serialization;

import com.facebook.thrift.TException;
import com.facebook.thrift.protocol.TProtocol;
import mt.serialization.schema.BasicType;
import mt.serialization.schema.Field;
import mt.serialization.schema.ListType;
import mt.serialization.schema.MapType;
import mt.serialization.schema.SetType;
import mt.serialization.schema.StructureType;
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
import java.util.concurrent.atomic.AtomicInteger;

public class Deserializer
{
	private Map<String, StructureType> types = new HashMap<String, StructureType>();
	private Map<String, Class<?>> classes = new HashMap<String, Class<?>>();

	private Map<String, StructureDeserializer<?>> deserializers = new HashMap<String, StructureDeserializer<?>>();
	private Map<String, TargetAdapter<?>> adapters = new HashMap<String, TargetAdapter<?>>();

	private boolean useCompiler;

	public Deserializer()
	{
		this(true);
	}

	public Deserializer(boolean useCompiler)
	{
		this.useCompiler = useCompiler;
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

	public <T> T deserialize(String name, TProtocol protocol)
		throws TException
	{
		StructureDeserializer<T> deserializer = (StructureDeserializer<T>) deserializers.get(name);
		TargetAdapter<T> adapter = (TargetAdapter<T>) adapters.get(name);
		Class<T> clazz = (Class<T>) classes.get(name);

		StructureType type = types.get(name);

		// construct adapter
		if (adapter == null) {
			if (useCompiler) {
				// TODO: use compiled adapter
				adapter = (TargetAdapter<T>) new SimpleSetterAdapter();
//				throw new UnsupportedOperationException("Not yet implemented"); // TODO: implement this
			}
			else {
				if (Map.class.isAssignableFrom(clazz)) {
					adapter = (TargetAdapter<T>) new MapSetterAdapter(type);
				}
				else {
					adapter = new JavaBeanSetterAdapter<T>(type, clazz);
				}
			}

			adapters.put(name, adapter);
		}

		// construct deserializer
		if (deserializer == null) {
			if (useCompiler) {
				deserializer = compileDeserializer(type, clazz, adapter);
			}
			else {
				deserializer = new DynamicDeserializer<T>(type, adapter);
			}

			deserializers.put(name, deserializer);
		}

		
		T result = deserializer.deserialize(this, protocol);

		return result;
	}


	private AtomicInteger sequence = new AtomicInteger();

	private <T> StructureDeserializer<T> compileDeserializer(StructureType type, Class<T> clazz, TargetAdapter<T> adapter)
	{
		ClassWriter classWriter = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		CheckClassAdapter checker = new CheckClassAdapter(classWriter);
		ClassVisitor writer = new TraceClassVisitor(checker, new PrintWriter(System.out));

		String targetClassName = org.objectweb.asm.Type.getInternalName(clazz);
		String deserializerClassName =
			"mt/serialization/generated/" + clazz.getSimpleName() + "_" + sequence.incrementAndGet();

		// class metadata
		writer.visit(V1_6, ACC_PUBLIC + ACC_SUPER, deserializerClassName,
		             "Ljava/lang/Object;L" + org.objectweb.asm.Type.getInternalName(StructureDeserializer.class) +
		             "<L" + targetClassName + ";>;",
		             "java/lang/Object",
		             new String[] { org.objectweb.asm.Type.getInternalName(StructureDeserializer.class) });

		compileConstructor(writer);
		compileDeserializeMethod(type, writer, targetClassName);
		compileSyntheticMethod(writer, targetClassName, deserializerClassName);

		writer.visitEnd();


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

	private <T> void compileSyntheticMethod(ClassVisitor writer, String targetClassName, String deserializerClassName)
	{
		MethodVisitor syntheticMethodVisitor = writer.visitMethod(ACC_PUBLIC + ACC_BRIDGE + ACC_SYNTHETIC,
		                                                          "deserialize",
		                                                          "(L" + org.objectweb
			                                                          .asm
			                                                          .Type
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
		                                       "(L" + org.objectweb.asm.Type.getInternalName(Deserializer.class)
		                                       + ";Lcom/facebook/thrift/protocol/TProtocol;)L" + targetClassName + ";");
		syntheticMethodVisitor.visitInsn(ARETURN);
		syntheticMethodVisitor.visitMaxs(3, 3);
		syntheticMethodVisitor.visitEnd();
	}

	private void compileDeserializeMethod(StructureType type, ClassVisitor writer, String targetClassName)
	{
		MethodVisitor methodVisitor = writer.visitMethod(ACC_PUBLIC, "deserialize",
		                                                 "(L" +
		                                                 org.objectweb.asm.Type.getInternalName(Deserializer.class)
		                                                 + ";Lcom/facebook/thrift/protocol/TProtocol;)L"
		                                                 + targetClassName + ";",
		                                                 null, new String[] { "com/facebook/thrift/TException" });

		int thisLocal = 0;
		int deserializerLocal = 1;
		int protocolLocal = 2;

		int maxVars = 3;
		int targetLocal = maxVars++;
		int tfieldLocal = maxVars++;

		methodVisitor.visitCode();

		// <target> result = new <target>()
		methodVisitor.visitTypeInsn(NEW, targetClassName);
		methodVisitor.visitInsn(DUP);
		methodVisitor.visitMethodInsn(INVOKESPECIAL, targetClassName, "<init>", "()V");
		methodVisitor.visitVarInsn(ASTORE, targetLocal); // store reference in slot #3

		// protocol.readStructBegin()
		methodVisitor.visitVarInsn(ALOAD, protocolLocal);
		methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/facebook/thrift/protocol/TProtocol", "readStructBegin",
		                              "()Lcom/facebook/thrift/protocol/TStruct;");
		methodVisitor.visitInsn(POP); // discard return value

		// while (true)
		Label whileLabel = new Label();
		methodVisitor.visitLabel(whileLabel);

		// TField tfield = protocol.readFieldBegin()
		methodVisitor.visitVarInsn(ALOAD, protocolLocal);
		methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/facebook/thrift/protocol/TProtocol", "readFieldBegin",
		                              "()Lcom/facebook/thrift/protocol/TField;");
		methodVisitor.visitVarInsn(ASTORE, tfieldLocal); // store in slot #5

		// tfield.type
		methodVisitor.visitVarInsn(ALOAD, tfieldLocal);
		methodVisitor.visitFieldInsn(GETFIELD, "com/facebook/thrift/protocol/TField", "type", "B");

		methodVisitor.visitFieldInsn(GETSTATIC, "com/facebook/thrift/protocol/TType", "STOP", "B");

		// if (tfield.type == TType.STOP) { break; }
		Label endWhile = new Label();
		methodVisitor.visitJumpInsn(IF_ICMPEQ, endWhile);

		// tfield.id
		methodVisitor.visitVarInsn(ALOAD, tfieldLocal);
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
			methodVisitor.visitVarInsn(ALOAD, tfieldLocal);
			methodVisitor.visitFieldInsn(GETFIELD, "com/facebook/thrift/protocol/TField", "type", "B");
			methodVisitor.visitIntInsn(BIPUSH, field.getType().getTType());
			methodVisitor.visitJumpInsn(IF_ICMPNE, fieldSkipped);

			generateRead(targetClassName, field, methodVisitor, targetLocal, protocolLocal, deserializerLocal);

			methodVisitor.visitJumpInsn(GOTO, whileLabel);
		}

		methodVisitor.visitLabel(fieldSkipped);
		methodVisitor.visitVarInsn(ALOAD, protocolLocal);
		methodVisitor.visitVarInsn(ALOAD, tfieldLocal);
		methodVisitor.visitFieldInsn(GETFIELD, "com/facebook/thrift/protocol/TField", "type", "B");
		methodVisitor.visitMethodInsn(INVOKESTATIC, "com/facebook/thrift/protocol/TProtocolUtil", "skip",
		                              "(Lcom/facebook/thrift/protocol/TProtocol;B)V");

		// end while
		methodVisitor.visitJumpInsn(GOTO, whileLabel);

		methodVisitor.visitLabel(endWhile);

		// protocol.readStructEnd()
		methodVisitor.visitVarInsn(ALOAD, protocolLocal);
		methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/facebook/thrift/protocol/TProtocol", "readStructEnd", "()V");

		// return result
		methodVisitor.visitVarInsn(ALOAD, targetLocal);
		methodVisitor.visitInsn(ARETURN);

		methodVisitor.visitMaxs(10, maxVars); // TODO: what should these be?
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

	private void generateRead(String targetClassName, Field field, MethodVisitor methodVisitor,
	                          int targetLocal, int protocolLocal, int deserializerLocal)
	{
		String setter = "set" + toCamelCase(field.getName());

		methodVisitor.visitVarInsn(ALOAD, targetLocal); // target object

		if (field.getType() == BasicType.BOOLEAN) {
			methodVisitor.visitVarInsn(ALOAD, protocolLocal); // protocol
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/facebook/thrift/protocol/TProtocol", "readBool", "()Z");
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, targetClassName, setter, "(Z)V");
		}
		else if (field.getType() == BasicType.BYTE) {
			methodVisitor.visitVarInsn(ALOAD, protocolLocal); // protocol
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/facebook/thrift/protocol/TProtocol", "readByte", "()B");
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, targetClassName, setter, "(B)V");
		}
		else if (field.getType() == BasicType.I16) {
			methodVisitor.visitVarInsn(ALOAD, protocolLocal); // protocol
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/facebook/thrift/protocol/TProtocol", "readI16", "()S");
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, targetClassName, setter, "(S)V");
		}
		else if (field.getType() == BasicType.I32) {
			methodVisitor.visitVarInsn(ALOAD, protocolLocal); // protocol
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/facebook/thrift/protocol/TProtocol", "readI32", "()I");
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, targetClassName, setter, "(I)V");
		}
		else if (field.getType() == BasicType.I64) {
			methodVisitor.visitVarInsn(ALOAD, protocolLocal); // protocol
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/facebook/thrift/protocol/TProtocol", "readI64", "()J");
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, targetClassName, setter, "(J)V");
		}
		else if (field.getType() == BasicType.DOUBLE) {
			methodVisitor.visitVarInsn(ALOAD, protocolLocal); // protocol
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/facebook/thrift/protocol/TProtocol", "readDouble", "()D");
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, targetClassName, setter, "(D)V");
		}
		else if (field.getType() == BasicType.BINARY) {
			methodVisitor.visitVarInsn(ALOAD, protocolLocal); // protocol
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/facebook/thrift/protocol/TProtocol", "readBinary",
			                              "()[B");
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, targetClassName, setter, "([B)V");
		}
		else if (field.getType() == BasicType.STRING) {
			methodVisitor.visitVarInsn(ALOAD, protocolLocal); // protocol
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "com/facebook/thrift/protocol/TProtocol", "readString",
			                              "()Ljava/lang/String;");

			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, targetClassName,
			                              setter, "(Ljava/lang/String;)V");
		}
		else if (field.getType() instanceof StructureType) {
			StructureType structureType = (StructureType) field.getType();
			Class childClass = classes.get(((StructureType) field.getType()).getName());

			methodVisitor.visitVarInsn(ALOAD, deserializerLocal);
			methodVisitor.visitLdcInsn(structureType.getName());
			methodVisitor.visitVarInsn(ALOAD, protocolLocal);
			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, org.objectweb.asm.Type.getInternalName(Deserializer.class),
			                              "deserialize",
			                              "(Ljava/lang/String;Lcom/facebook/thrift/protocol/TProtocol;)Ljava/lang/Object;");
			methodVisitor.visitTypeInsn(CHECKCAST, org.objectweb.asm.Type.getInternalName(childClass));

			methodVisitor.visitMethodInsn(INVOKEVIRTUAL, targetClassName,
			                              setter, "(L" + org.objectweb.asm.Type.getInternalName(childClass) + ";)V");
		}
		else if (field.getType() instanceof ListType) {

		}
		else if (field.getType() instanceof SetType) {

		}
		else if (field.getType() instanceof MapType) {

		}
	}

	private static String toCamelCase(String name)
	{
		StringBuilder builder = new StringBuilder(name.length());
		for (int i = 0; i < name.length(); ++i) {
			char c = name.charAt(i);
			if (i == 0 && c != '_') {
				builder.append(Character.toUpperCase(c));
			}
			else if (c == '_' && i < name.length() - 1) {
				++i;
				builder.append(Character.toUpperCase(name.charAt(i)));
			}
			else if (c != '_') {
				builder.append(c);
			}
		}

		return builder.toString();
	}
}
