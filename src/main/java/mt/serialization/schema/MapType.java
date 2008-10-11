package mt.serialization.schema;

import com.facebook.thrift.protocol.TType;
import com.facebook.thrift.protocol.TProtocol;
import com.facebook.thrift.protocol.TMap;
import com.facebook.thrift.TException;

import java.util.Map;
import java.util.HashMap;

public class MapType
	implements Type
{
	private Type keyType;
	private Type valueType;

	public MapType(Type keyType, Type valueType)
	{
		this.keyType = keyType;
		this.valueType = valueType;
	}

	public Type getKeyType()
	{
		return keyType;
	}

	public Type getValueType()
	{
		return valueType;
	}

	public byte getTType()
	{
		return TType.MAP;
	}

	public String getSignature()
	{
		return String.format("map<%s,%s>", keyType.getSignature(), valueType.getSignature());
	}

	public Object read(TProtocol protocol)
		throws TException
	{
		TMap tmap = protocol.readMapBegin();
		Map<Object, Object> result = new HashMap<Object, Object>(2 * tmap.size);
		for (int i = 0; i < tmap.size; ++i) {
			Object key = keyType.read(protocol);
			Object value = valueType.read(protocol);
			result.put(key, value);
		}
		protocol.readMapEnd();

		return result;
	}
}
