package mt.serialization;

/**
 * boolean => Type.BOOLEAN
 * byte/short/int/long/BigInteger => Type.INTEGER
 * float/double/BigDecimal => Type.DECIMAL
 * char/String/CharSequence => Type.STRING
 * byte[]/ByteBuffer => Type.BYTES
 * Object => Type.STRUCTURE
 */
public class Serializer
{
	public static Serializer getInstance(Scheme scheme)
	{
		throw new UnsupportedOperationException("Not yet implemented"); // TODO: implement this
	}

	public <T> ClassSerializer<T> forClass(Class<T> clazz)
	{
		throw new UnsupportedOperationException("Not yet implemented"); // TODO: implement this
	}

	public <T> ClassSerializer<T> forType(String type)
	{
		throw new UnsupportedOperationException("Not yet implemented"); // TODO: implement this
	}

}

/**
 * Reflection-based serializer
 * Dynamically compiled serializer (reflection for determining what to serialize)
 */