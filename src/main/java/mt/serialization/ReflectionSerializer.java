package mt.serialization;

import com.facebook.thrift.protocol.TProtocol;
import mt.serialization.schema.Schema;

class ReflectionSerializer
	extends Serializer<Object>
{
	public ReflectionSerializer(Schema schema)
	{
		super(schema);
	}

	public void serialize(Object object, String structName, TProtocol protocol)
	{

	}
}
