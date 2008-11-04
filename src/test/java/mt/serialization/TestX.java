package mt.serialization;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;
import static org.objectweb.asm.Opcodes.*;

public class TestX
{
	Integer a;
	int b;
	short c;

	public void setA(Integer a)
	{
		this.a = a;
	}

	public void setB(int b)
	{
		this.b = b;
	}

	public void setAFromInt(int a)
	{
		this.a = a;
	}

	public void setBFromInteger(Integer b)
	{
		this.b = b;
	}

	public void setBFromShort(short v)
	{
		this.b = v;
	}

	public void setBFromLong(long v)
	{
		this.b = (int) v;
	}

	public void setShortFromInt(int v)
	{
		this.c = (short) v;
	}

	public void testLocalVariable()
	{
		int a = 1;
		a++;
	}

	public void tryCatch()
	{
		try {
			throw new Exception();
		}
		catch (Exception e) {

		}
	}

	public static byte[] dump()
		throws Exception
	{
		ClassWriter cw = new ClassWriter(0);
		FieldVisitor fv;
		MethodVisitor mv;
		AnnotationVisitor av0;

		cw.visit(V1_6, ACC_PUBLIC + ACC_SUPER, "ChildDeserializer",
		         "Ljava/lang/Object;Lmt/serialization/StructureDeserializer<Lmt/serialization/Child;>;",
		         "java/lang/Object", new String[] { "mt/serialization/StructureDeserializer" });

		{
			mv = cw.visitMethod(ACC_PUBLIC, "<init>", "()V", null, null);
			mv.visitCode();
			mv.visitVarInsn(ALOAD, 0);
			mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Object", "<init>", "()V");
			mv.visitInsn(RETURN);
			mv.visitMaxs(1, 1);
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC, "deserialize",
			                    "(Lmt/serialization/Deserializer;Lcom/facebook/thrift/protocol/TProtocol;)Lmt/serialization/Child;",
			                    null, new String[] { "com/facebook/thrift/TException" });
			mv.visitCode();
			mv.visitTypeInsn(NEW, "mt/serialization/Child");
			mv.visitInsn(DUP);
			mv.visitMethodInsn(INVOKESPECIAL, "mt/serialization/Child", "<init>", "()V");
			mv.visitVarInsn(ASTORE, 3);
			mv.visitVarInsn(ALOAD, 2);
			mv.visitMethodInsn(INVOKEVIRTUAL, "com/facebook/thrift/protocol/TProtocol", "readStructBegin",
			                   "()Lcom/facebook/thrift/protocol/TStruct;");
			mv.visitInsn(POP);
			Label l0 = new Label();
			mv.visitLabel(l0);
			mv.visitFrame(Opcodes.F_APPEND, 1, new Object[] { "mt/serialization/Child" }, 0, null);
			mv.visitInsn(ICONST_0);
			mv.visitVarInsn(ISTORE, 4);
			mv.visitVarInsn(ALOAD, 2);
			mv.visitMethodInsn(INVOKEVIRTUAL, "com/facebook/thrift/protocol/TProtocol", "readFieldBegin",
			                   "()Lcom/facebook/thrift/protocol/TField;");
			mv.visitVarInsn(ASTORE, 5);
			mv.visitVarInsn(ALOAD, 5);
			mv.visitFieldInsn(GETFIELD, "com/facebook/thrift/protocol/TField", "type", "B");
			Label l1 = new Label();
			mv.visitJumpInsn(IFNE, l1);
			Label l2 = new Label();
			mv.visitJumpInsn(GOTO, l2);
			mv.visitLabel(l1);
			mv.visitFrame(Opcodes.F_APPEND, 2,
			              new Object[] { Opcodes.INTEGER, "com/facebook/thrift/protocol/TField" }, 0, null);
			mv.visitVarInsn(ALOAD, 5);
			mv.visitFieldInsn(GETFIELD, "com/facebook/thrift/protocol/TField", "id", "S");
			Label l3 = new Label();
			Label l4 = new Label();
			mv.visitLookupSwitchInsn(l4, new int[] { 1 }, new Label[] { l3 });
			mv.visitLabel(l3);
			mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
			mv.visitVarInsn(ALOAD, 5);
			mv.visitFieldInsn(GETFIELD, "com/facebook/thrift/protocol/TField", "type", "B");
			mv.visitIntInsn(BIPUSH, 11);
			mv.visitJumpInsn(IF_ICMPNE, l4);
			mv.visitInsn(ICONST_1);
			mv.visitVarInsn(ISTORE, 4);
			mv.visitVarInsn(ALOAD, 2);
			mv.visitMethodInsn(INVOKEVIRTUAL, "com/facebook/thrift/protocol/TProtocol", "readString",
			                   "()Ljava/lang/String;");
			mv.visitVarInsn(ASTORE, 6);
			mv.visitVarInsn(ALOAD, 3);
			mv.visitVarInsn(ALOAD, 6);
			mv.visitMethodInsn(INVOKEVIRTUAL, "mt/serialization/Child", "setField", "(Ljava/lang/String;)V");
			mv.visitLabel(l4);
			mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
			mv.visitVarInsn(ILOAD, 4);
			Label l5 = new Label();
			mv.visitJumpInsn(IFNE, l5);
			mv.visitVarInsn(ALOAD, 2);
			mv.visitVarInsn(ALOAD, 5);
			mv.visitFieldInsn(GETFIELD, "com/facebook/thrift/protocol/TField", "type", "B");
			mv.visitMethodInsn(INVOKESTATIC, "com/facebook/thrift/protocol/TProtocolUtil", "skip",
			                   "(Lcom/facebook/thrift/protocol/TProtocol;B)V");
			mv.visitLabel(l5);
			mv.visitFrame(Opcodes.F_CHOP, 2, null, 0, null);
			mv.visitJumpInsn(GOTO, l0);
			mv.visitLabel(l2);
			mv.visitFrame(Opcodes.F_SAME, 0, null, 0, null);
			mv.visitVarInsn(ALOAD, 2);
			mv.visitMethodInsn(INVOKEVIRTUAL, "com/facebook/thrift/protocol/TProtocol", "readStructEnd", "()V");
			mv.visitVarInsn(ALOAD, 3);
			mv.visitInsn(ARETURN);
			mv.visitMaxs(2, 7);
			mv.visitEnd();
		}
		{
			mv = cw.visitMethod(ACC_PUBLIC + ACC_BRIDGE + ACC_SYNTHETIC, "deserialize",
			                    "(Lmt/serialization/Deserializer;Lcom/facebook/thrift/protocol/TProtocol;)Ljava/lang/Object;",
			                    null, new String[] { "com/facebook/thrift/TException" });
			mv.visitCode();
			mv.visitVarInsn(ALOAD, 0);
			mv.visitVarInsn(ALOAD, 1);
			mv.visitVarInsn(ALOAD, 2);
			mv.visitMethodInsn(INVOKEVIRTUAL, "ChildDeserializer", "deserialize",
			                   "(Lmt/serialization/Deserializer;Lcom/facebook/thrift/protocol/TProtocol;)Lmt/serialization/Child;");
			mv.visitInsn(ARETURN);
			mv.visitMaxs(3, 3);
			mv.visitEnd();
		}
		cw.visitEnd();

		return cw.toByteArray();
	}
}
