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

import java.util.Map;
import java.util.HashMap;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.List;
import java.util.ArrayList;

class FrameRegisterManager
{
	private Map<String, Integer> nameToSlot = new HashMap<String, Integer>();
	private Map<Integer, String> slotToName = new HashMap<Integer, String>();
	private SortedSet<Integer> usedSlots = new TreeSet<Integer>();
	private List<Integer> reusableSlots = new ArrayList<Integer>();

	public void bindSlot(String name, int slot)
	{
		nameToSlot.put(name, slot);
		slotToName.put(slot, name);
		usedSlots.add(slot);
	}

	public int newAnonymousSlot()
	{
		Integer slot = null;
		if (!reusableSlots.isEmpty()) {
			slot = reusableSlots.remove(reusableSlots.size() - 1);
		}

		if (slot == null) {
			slot = usedSlots.last() + 1;
		}

		usedSlots.add(slot);

		return slot;
	}

	public int newSlot(String name)
	{
		int slot = newAnonymousSlot();
		bindSlot(name, slot);
		return slot;
	}

	public int getSlot(String name)
	{
		return nameToSlot.get(name);
	}

	public void release(String name)
	{
		Integer slot = getSlot(name);
		if (slot == null) {
			throw new IllegalArgumentException(String.format("Slot '%s' not bound", name));
		}

		release(slot);
	}
	
	public void release(int slot)
	{
		boolean removed = usedSlots.remove(slot);
		if (!removed) {
			throw new IllegalArgumentException(String.format("Slot %d not in use", slot));
		}
		String name = slotToName.get(slot);
		if (name != null) {
			nameToSlot.remove(name);
		}

		reusableSlots.add(slot);
	}

	public boolean isInUse(int slot)
	{
		return usedSlots.contains(slot);
	}
}
