package mt.serialization.schema;

import com.facebook.thrift.protocol.TType;

public class MapType
	implements Type
{
	private Type keyType;
	private Type valueType;

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

}
