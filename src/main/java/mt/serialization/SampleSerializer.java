package mt.serialization;

import com.facebook.thrift.TException;
import com.facebook.thrift.protocol.TProtocol;
import com.facebook.thrift.protocol.TStruct;

public class SampleSerializer
	implements StructureSerializer
{
	public void serialize(Object object, Serializer serializer, TProtocol protocol)
		throws TException
	{
		TStruct tstruct = new TStruct("struct name");
		protocol.writeStructBegin(tstruct);
		protocol.writeStructEnd();
	}
}
