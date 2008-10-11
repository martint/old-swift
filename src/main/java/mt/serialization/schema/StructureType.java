package mt.serialization.schema;

import com.facebook.thrift.protocol.TType;
import com.facebook.thrift.protocol.TProtocol;
import com.facebook.thrift.TException;

public class StructureType
	implements Type
{
	private final String structureName;

	public StructureType(String structureName)
	{
		this.structureName = structureName;
	}

	public String getStructureName()
	{
		return structureName;
	}

	public byte getTType()
	{
		return TType.STRUCT;
	}

	public String getSignature()
	{
		return structureName;
	}

	public Object read(TProtocol protocol)
		throws TException
	{
		throw new UnsupportedOperationException();
	}
}
