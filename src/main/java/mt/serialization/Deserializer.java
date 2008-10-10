package mt.serialization;

import mt.serialization.schema.Schema;
import mt.serialization.protocol.Protocol;

import java.util.Map;

public abstract class Deserializer<T>
{
	public Deserializer(Schema schema)
	{
	}

	public static Deserializer<Map<String, ?>> newMapDeserializer(Schema schema)
	{
		return new MapDeserializer(schema);
	}

	public abstract T deserialize(String structName, Protocol protocol);
}
