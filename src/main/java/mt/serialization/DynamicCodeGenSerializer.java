package mt.serialization;

import mt.serialization.schema.Schema;

import java.io.DataOutput;

class DynamicCodeGenSerializer
	extends Serializer<Object>
{
	public DynamicCodeGenSerializer(Schema schema)
	{
		super(schema);
	}

	public void serialize(Object object, String structName, DataOutput out)
	{
	}
}
