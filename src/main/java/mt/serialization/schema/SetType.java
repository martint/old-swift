package mt.serialization.schema;

import com.facebook.thrift.protocol.TType;
import com.facebook.thrift.protocol.TProtocol;
import com.facebook.thrift.protocol.TSet;
import com.facebook.thrift.TException;

import java.util.Set;
import java.util.HashSet;

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

	public Object read(TProtocol protocol)
		throws TException
	{
		TSet tset = protocol.readSetBegin();
		Set<Object> result = new HashSet<Object>(tset.size);
		for (int i = 0; i < tset.size; ++i) {
			result.add(valueType.read(protocol));
		}
		protocol.readSetEnd();

		return result;
	}
}
