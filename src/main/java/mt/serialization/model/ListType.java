package mt.serialization.model;

import com.facebook.thrift.protocol.TType;

public class ListType
	implements Type
{
	private Type valueType;

	public ListType(Type valueType)
	{
		this.valueType = valueType;
	}

	public byte getTType()
	{
		return TType.LIST;
	}

	public Type getValueType()
	{
		return valueType;
	}

	public String getSignature()
	{
		return String.format("list<%s>", valueType.getSignature());
	}

}
