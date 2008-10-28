package mt.serialization;

import com.facebook.thrift.TException;
import com.facebook.thrift.protocol.TProtocol;

// TODO: new name: DeserializationStrategy ?
public interface StructureDeserializer<T>
{
	/**
	 * Deserialize an instance of T
	 * @param deserializer the global deserializer, to deserialize nested structures
	 * @param protocol the protocol to deserialize from
	 * @return an instance of T with all known fields filled in
	 * @throws TException if there's an error during deserialization
	 */
	T deserialize(Deserializer deserializer, TProtocol protocol)
		throws TException;
}
