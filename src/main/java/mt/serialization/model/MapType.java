package mt.serialization.model;

import com.facebook.thrift.protocol.TType;

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

}
