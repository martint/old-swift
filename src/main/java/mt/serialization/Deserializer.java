package mt.serialization;

import com.facebook.thrift.protocol.TProtocol;
import com.facebook.thrift.TException;
import mt.serialization.schema.Schema;

import java.util.Map;

public abstract class Deserializer<T>
{
	private final Schema schema;

	public Deserializer(Schema schema)
	{
		this.schema = schema;
	}

	public static Deserializer<Map<String, ?>> newMapDeserializer(Schema schema)
	{
		return new MapDeserializer(schema);
	}

	public Schema getSchema()
	{
		return schema;
	}

	public abstract T deserialize(String structName, TProtocol protocol)
			throws TException;
}
