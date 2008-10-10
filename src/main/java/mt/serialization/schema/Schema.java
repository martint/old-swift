package mt.serialization.schema;

import java.util.Map;
import java.util.HashMap;

public class Schema
{
	private final Map<String, Structure> structures = new HashMap<String, Structure>();

	public Schema(Structure... structures)
	{
		for (Structure structure : structures) {
			this.structures.put(structure.getName(), structure);
		}
	}

	public Structure getStructure(String name)
	{
		return structures.get(name);
	}
}
