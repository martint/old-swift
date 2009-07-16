package mt.swift;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TIOStreamTransport;

import java.io.ByteArrayOutputStream;

public class ByteArraySerializer
{
	private final Serializer serializer;

	public ByteArraySerializer(Serializer serializer)
	{
		this.serializer = serializer;
	}

	public byte[] serialize(String type, Object object)
	{
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		TProtocol protocol = new TBinaryProtocol(new TIOStreamTransport(bao));

		try {
			serializer.serialize(object, type, protocol);
			return bao.toByteArray();
		}
		catch (TException e) {
			// TODO: how to handle this?
			throw new RuntimeException(e);
		}
	}
}