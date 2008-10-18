package mt.serialization;

import com.facebook.thrift.TException;
import com.facebook.thrift.protocol.TProtocol;
import mt.serialization.schema.Schema;
import mt.serialization.schema.StructureType;

import java.util.Map;

class MapDeserializer
		extends Deserializer<Map<String, ?>>
{
	public MapDeserializer(Schema schema)
	{
		super(schema);
	}

	public Map<String, ?> deserialize(String structName, TProtocol protocol)
			throws TException
	{
		StructureType structure = getSchema().getStructure(structName);

		return structure.read(protocol);
	}
}
