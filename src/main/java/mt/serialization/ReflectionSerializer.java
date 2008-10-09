package mt.serialization;

import mt.serialization.protocol.Protocol;
import mt.serialization.schema.Schema;

class ReflectionSerializer
	extends Serializer<Object>
{
	public ReflectionSerializer(Schema schema)
	{
		super(schema);
	}

	public void serialize(Object object, String structName, Protocol protocol)
	{

	}
}
