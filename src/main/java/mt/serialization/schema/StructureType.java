package mt.serialization.schema;

import com.facebook.thrift.protocol.TType;

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
}
