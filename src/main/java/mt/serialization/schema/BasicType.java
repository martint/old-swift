package mt.serialization.schema;

import com.facebook.thrift.protocol.TType;
import com.facebook.thrift.protocol.TProtocol;
import com.facebook.thrift.TException;

public abstract class BasicType<T>
	implements Type<T>
{
	public static final Type BINARY = new BasicType<byte[]>(TType.STRING, "binary") {
		public byte[] read(TProtocol protocol)
			throws TException
		{
			return protocol.readBinary();
		}
	};

	public static final Type BOOLEAN = new BasicType<Boolean>(TType.BOOL, "bool") {
		public Boolean read(TProtocol protocol)
			throws TException
		{
			return protocol.readBool();
		}
	};

	public static final Type BYTE = new BasicType<Byte>(TType.BYTE, "byte") {
		public Byte read(TProtocol protocol)
			throws TException
		{
			return protocol.readByte();
		}
	};

	public static final Type I16 = new BasicType<Short>(TType.I16, "i16") {
		public Short read(TProtocol protocol)
			throws TException
		{
			return protocol.readI16();
		}
	};

	public static final Type I32 = new BasicType<Integer>(TType.I32, "i32") {
		public Integer read(TProtocol protocol)
			throws TException
		{
			return protocol.readI32();
		}
	};

	public static final Type I64 = new BasicType<Long>(TType.I64, "i64") {
		public Long read(TProtocol protocol)
			throws TException
		{
			return protocol.readI64();
		}
	};

	public static final Type DOUBLE = new BasicType<Double>(TType.DOUBLE, "double") {
		public Double read(TProtocol protocol)
			throws TException
		{
			return protocol.readDouble();
		}
	};

	public static final Type STRING = new BasicType<String>(TType.STRING, "string") {
		public String read(TProtocol protocol)
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
