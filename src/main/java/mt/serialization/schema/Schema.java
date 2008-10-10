package mt.serialization.schema;

import java.util.Map;

public class Schema
{
	private Map<String, Structure> structures;

	public Structure getStructure(String name)
	{
		return structures.get(name);
	}
}
