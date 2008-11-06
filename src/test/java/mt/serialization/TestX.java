package mt.serialization;

import com.facebook.thrift.TException;
import com.facebook.thrift.protocol.TField;
import com.facebook.thrift.protocol.TProtocol;
import com.facebook.thrift.protocol.TStruct;

public class TestX
{
	void x(TProtocol protocol)
		throws TException
	{
		protocol.writeStructBegin(new TStruct("test"));

		TField field = new TField();

	}
}
