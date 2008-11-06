package mt.serialization;

import java.util.Map;
import java.util.HashMap;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.List;
import java.util.ArrayList;

class MethodBuilderContext
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
