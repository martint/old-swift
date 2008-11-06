package mt.serialization;

import org.objectweb.asm.MethodVisitor;
import static org.objectweb.asm.Opcodes.*;

public class MethodVisitorWrapper
{
	private MethodVisitor visitor;
	private MethodBuilderContext context;

	public MethodVisitorWrapper(MethodVisitor visitor, MethodBuilderContext context)
	{
		this.visitor = visitor;
		this.context = context;
	}

	public void bindSlot(String name, int slot)
	{
		context.bindSlot(name, slot);
	}

	public int newAnonymousSlot()
	{
		return context.newAnonymousSlot();
	}

	public int newSlot(String name)
	{
		return context.newSlot(name);
	}

	public int getSlot(String name)
	{
		return context.getSlot(name);
	}

	public void releaseSlot(int slot)
	{
		context.release(slot);
	}

	public void start()
	{
		visitor.visitCode();
	}

	public void end()
	{
		visitor.visitEnd();
	}

	public void aload(int slot)
	{
		if (!context.isInUse(slot)) {
			throw new IllegalArgumentException(String.format("Slot %d not allocated", slot));
		}

		visitor.visitVarInsn(ALOAD, slot);
	}

	public void aload(String name)
	{
		Integer slot = context.getSlot(name);

		if (slot == null) {
			throw new IllegalArgumentException(String.format("Slot '%s' not bound", name));
		}

		aload(slot);
	}


}
