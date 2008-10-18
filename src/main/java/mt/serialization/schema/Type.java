package mt.serialization.schema;

import com.facebook.thrift.protocol.TProtocol;
import com.facebook.thrift.TException;
import mt.serialization.visitor.Visitor;

public interface Type<T>
{
	byte getTType();
	String getSignature();

	T read(TProtocol protocol)
		throws TException;

	void accept(Visitor visitor);
}
