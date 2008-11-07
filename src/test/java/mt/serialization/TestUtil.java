package mt.serialization;

import com.facebook.thrift.TException;
import com.facebook.thrift.transport.TIOStreamTransport;
import com.facebook.thrift.protocol.TBinaryProtocol;

import java.io.ByteArrayInputStream;

public class TestUtil
{
	public static TTestStruct deserialize(byte[] data)
		throws TException
	{
		TTestStruct result = new TTestStruct();
		result.read(new TBinaryProtocol(new TIOStreamTransport(new ByteArrayInputStream(data))));

		return result;
	}

}
