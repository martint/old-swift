package mt.serialization;

import com.facebook.thrift.protocol.TProtocol;
import com.facebook.thrift.TException;
import mt.serialization.schema.Schema;

import java.util.Map;

/**
 * boolean => Type.BOOLEAN
 * byte/short/int/long/BigInteger => Type.INTEGER
 * float/double/BigDecimal => Type.DECIMAL
 * char/String/CharSequence => Type.STRING
 * byte[]/ByteBuffer => Type.BYTES
 * Object => Type.STRUCTURE
 */
public abstract class Serializer<T>
{
	private Schema schema;

	public Serializer(Schema schema)
	{
		this.schema = schema;
	}
	
	public abstract void serialize(T object, String structName, TProtocol protocol)
		throws TException;

	// factory methods
	public static Serializer<Map<String, ?>> newMapSerializer(Schema schema)
	{
		return new MapSerializer(schema);
	}

	public static Serializer<Object> newReflectiveSerializer(Schema schema)
	{
		return new ReflectionSerializer(schema);
	}

	public static Serializer<Object> newDynamicCodeGenSerializer(Schema schema)
	{
		return new DynamicCodeGenSerializer(schema);
	}

	protected Schema getSchema()
	{
		return schema;
	}
}

