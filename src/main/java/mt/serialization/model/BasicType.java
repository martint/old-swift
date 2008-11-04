package mt.serialization.model;

import com.facebook.thrift.protocol.TType;

public class BasicType<T>
	implements Type<T>
{
	public static final Type BINARY = new BasicType<byte[]>(TType.STRING, "binary");
	public static final Type BOOLEAN = new BasicType<Boolean>(TType.BOOL, "bool");
	public static final Type BYTE = new BasicType<Byte>(TType.BYTE, "byte");
	public static final Type I16 = new BasicType<Short>(TType.I16, "i16");
	public static final Type I32 = new BasicType<Integer>(TType.I32, "i32");
	public static final Type I64 = new BasicType<Long>(TType.I64, "i64");
	public static final Type DOUBLE = new BasicType<Double>(TType.DOUBLE, "double");
	public static final Type STRING = new BasicType<String>(TType.STRING, "string");

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
