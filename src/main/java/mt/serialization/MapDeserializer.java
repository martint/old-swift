package mt.serialization;

import mt.serialization.schema.Schema;
import mt.serialization.protocol.Protocol;

import java.util.Map;

class MapDeserializer
		extends Deserializer<Map<String, ?>>
{
	public MapDeserializer(Schema schema)
	{
		super(schema);
	}

	public Map<String, ?> deserialize(String structName, Protocol protocol)
	{
		throw new UnsupportedOperationException("Not yet implemented"); // TODO: implement this
	}
}
