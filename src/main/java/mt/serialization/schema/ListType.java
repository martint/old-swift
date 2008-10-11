package mt.serialization.schema;

import com.facebook.thrift.protocol.TType;
import com.facebook.thrift.protocol.TProtocol;
import com.facebook.thrift.protocol.TList;
import com.facebook.thrift.TException;

import java.util.List;
import java.util.ArrayList;

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

	public Object read(TProtocol protocol)
		throws TException
	{
		TList tlist = protocol.readListBegin();
		List<Object> result = new ArrayList<Object>(tlist.size);
		for (int i = 0; i < tlist.size; ++i) {
			result.add(valueType.read(protocol));
		}
		protocol.readListEnd();

		return result;
	}
}
