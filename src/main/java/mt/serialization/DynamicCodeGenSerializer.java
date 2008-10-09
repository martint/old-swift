package mt.serialization;

import mt.serialization.protocol.Protocol;
import mt.serialization.schema.Schema;

class DynamicCodeGenSerializer
	extends Serializer<Object>
{
	public DynamicCodeGenSerializer(Schema schema)
	{
		super(schema);
	}

	public void serialize(Object object, String structName, Protocol protocol)
	{

	}
}
