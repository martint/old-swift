package mt.serialization;

import com.facebook.thrift.TException;
import com.facebook.thrift.protocol.TProtocol;

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
	public abstract void serialize(T object, String structName, TProtocol protocol)
		throws TException;

}

