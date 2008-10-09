package mt.serialization;

import mt.serialization.schema.Schema;

import java.io.DataOutput;

class ReflectionSerializer
	extends Serializer<Object>
{
	public ReflectionSerializer(Schema schema)
	{
		super(schema);
	}

	public void serialize(Object object, String structName, DataOutput out)
	{
	}
}
