package mt.serialization;

import com.facebook.thrift.protocol.TProtocol;
import mt.serialization.schema.Schema;

class DynamicCodeGenSerializer
	extends Serializer<Object>
{
	public DynamicCodeGenSerializer(Schema schema)
	{
		super(schema);
	}

	public void serialize(Object object, String structName, TProtocol protocol)
	{

	}
}
