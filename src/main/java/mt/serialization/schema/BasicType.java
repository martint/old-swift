package mt.serialization.schema;

import com.facebook.thrift.protocol.TType;
import com.facebook.thrift.protocol.TProtocol;
import com.facebook.thrift.TException;

public abstract class BasicType
	implements Type
{
	public static final Type BINARY = new BasicType(TType.STRING, "binary") {
		public Object read(TProtocol protocol)
			throws TException
		{
			return protocol.readBinary();
		}
	};

	public static final Type BOOLEAN = new BasicType(TType.BOOL, "bool") {
		public Object read(TProtocol protocol)
			throws TException
		{
			return protocol.readBool();
		}
	};

	public static final Type BYTE = new BasicType(TType.BYTE, "byte") {
		public Object read(TProtocol protocol)
			throws TException
		{
			return protocol.readByte();
		}
	};

	public static final Type I16 = new BasicType(TType.I16, "i16") {
		public Object read(TProtocol protocol)
			throws TException
		{
			return protocol.readI16();
		}
	};

	public static final Type I32 = new BasicType(TType.I32, "i32") {
		public Object read(TProtocol protocol)
			throws TException
		{
			return protocol.readI32();
		}
	};

	public static final Type I64 = new BasicType(TType.I64, "i64") {
		public Object read(TProtocol protocol)
			throws TException
		{
			return protocol.readI64();
		}
	};

	public static final Type DOUBLE = new BasicType(TType.DOUBLE, "double") {
		public Object read(TProtocol protocol)
			throws TException
		{
			return protocol.readDouble();
		}
	};

	public static final Type STRING = new BasicType(TType.STRING, "string") {
		public Object read(TProtocol protocol)
			throws TException
		{
			return protocol.readString();
		}
	};

	private final byte ttype;
	private final String signature;

	private BasicType(byte ttype, String signature)
	{
		this.ttype = ttype;
		this.signature = signature;
	}

	public byte getTType()
	{
		return ttype;
	}

	public String getSignature()
	{
		return signature;
	}
}
