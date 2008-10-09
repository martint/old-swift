package mt.serialization;

import mt.serialization.schema.Schema;

import java.io.DataOutput;
import java.util.Map;

class MapSerializer
	extends Serializer<Map<String, ?>>
{
	public MapSerializer(Schema schema)
	{
		super(schema);
	}

	public void serialize(Map<String, ?> entry, String structName, DataOutput out)
	{

	}
}
