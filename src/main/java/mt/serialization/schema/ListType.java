package mt.serialization.schema;

import com.facebook.thrift.protocol.TType;

public class ListType
	implements Type
{
	private Type valueType;

	public byte getTType()
	{
		return TType.LIST;
	}

}
