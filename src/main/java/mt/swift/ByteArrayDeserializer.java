package mt.swift;


import com.facebook.thrift.TException;
import com.facebook.thrift.protocol.TBinaryProtocol;
import com.facebook.thrift.protocol.TProtocol;
import com.facebook.thrift.transport.TIOStreamTransport;

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

