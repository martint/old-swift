package mt.serialization;

import mt.serialization.protocol.Protocol;
import mt.serialization.schema.Schema;

import java.util.Map;

class MapSerializer
	extends Serializer<Map<String, ?>>
{
	public MapSerializer(Schema schema)
	{
		super(schema);
	}

	public void serialize(Map<String, ?> object, String structName, Protocol protocol)
	{

	}
}
