package mt.swift;


import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TIOStreamTransport;

import java.io.ByteArrayInputStream;

public class ByteArrayDeserializer
{
	private final Deserializer deserializer;

	public ByteArrayDeserializer(Deserializer deserializer)
	{
		this.deserializer = deserializer;
	}

	public <T> T deserialize(String type, byte[] bytes)
	{
		ByteArrayInputStream bai = new ByteArrayInputStream(bytes);
		TProtocol protocol = new TBinaryProtocol(new TIOStreamTransport(bai));

		try {
			return deserializer.<T>deserialize(type, protocol);
		}
		catch (TException e) {
			// TODO: how to handle this?
			throw new RuntimeException(e);
		}
	}
}

