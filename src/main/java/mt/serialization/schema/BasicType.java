package mt.serialization.schema;

import com.facebook.thrift.protocol.TType;

public final class BasicType
	implements Type
{
	public static final Type BINARY = new BasicType(TType.BYTE);  // TODO
	public static final Type BOOLEAN = new BasicType(TType.BOOL);
	public static final Type BYTE = new BasicType(TType.BYTE);
	public static final Type I16 = new BasicType(TType.I16);
	public static final Type I32 = new BasicType(TType.I32);
	public static final Type I64 = new BasicType(TType.I64);
	public static final Type DOUBLE = new BasicType(TType.DOUBLE);
	public static final Type STRING = new BasicType(TType.STRING);

	private final byte ttype;

	private BasicType(byte ttype)
	{
		this.ttype = ttype;
	}

	public byte getTType()
	{
		return ttype;
	}
}
