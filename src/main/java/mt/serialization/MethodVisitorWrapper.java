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
package mt.serialization;

import org.objectweb.asm.MethodVisitor;
import static org.objectweb.asm.Opcodes.*;

class MethodVisitorWrapper
{
	private MethodVisitor visitor;
	private FrameRegisterManager context;

	public MethodVisitorWrapper(MethodVisitor visitor, FrameRegisterManager context)
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
