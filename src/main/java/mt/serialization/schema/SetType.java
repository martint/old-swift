package mt.serialization.schema;

import com.facebook.thrift.protocol.TType;

public class SetType
	implements Type
{
	private Type valueType;

	public SetType(Type valueType)
	{
		this.valueType = valueType;
	}

	public byte getTType()
	{
		return TType.SET;
	}

	public Type getValueType()
	{
		return valueType;
	}

	public String getSignature()
	{
		return String.format("set<%s>", valueType.getSignature());
	}

}
