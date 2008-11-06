package mt.serialization;

import com.facebook.thrift.TException;
import com.facebook.thrift.protocol.TProtocol;

public interface StructureSerializer
{
	void serialize(Object object, Serializer serializer, TProtocol protocol)
		throws TException;
}
