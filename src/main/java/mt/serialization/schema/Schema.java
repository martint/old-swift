package mt.serialization.schema;

import java.util.Map;
import java.util.HashMap;

public class Schema
{
	private final Map<String, StructureType> structures = new HashMap<String, StructureType>();

	public Schema(StructureType... structures)
	{
		for (StructureType structure : structures) {
			this.structures.put(structure.getName(), structure);
		}
	}

	public StructureType getStructure(String name)
	{
		return structures.get(name);
	}
}
