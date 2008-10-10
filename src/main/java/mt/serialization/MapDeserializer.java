package mt.serialization;

import com.facebook.thrift.protocol.TProtocol;
import mt.serialization.schema.Schema;

import java.util.Map;

class MapDeserializer
		extends Deserializer<Map<String, ?>>
{
	public MapDeserializer(Schema schema)
	{
		super(schema);
	}

	public Map<String, ?> deserialize(String structName, TProtocol protocol)
	{
		throw new UnsupportedOperationException("Not yet implemented"); // TODO: implement this
	}
}
