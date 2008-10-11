package mt.serialization.schema;

import com.facebook.thrift.protocol.TProtocol;
import com.facebook.thrift.TException;

public interface Type
{
	byte getTType();
	String getSignature();

	Object read(TProtocol protocol)
		throws TException;
}
