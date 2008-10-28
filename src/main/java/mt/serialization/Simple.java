package mt.serialization;

import java.util.Arrays;

public class Simple
{
	private String aString;
	private boolean aBool;
	private Boolean aBoolObject;
	private byte aByte;
	private double aDouble;
	private short aI16;
	private int aI32;
	private long aI64;
	private byte[] aBinary;
	private Nested aNested;

	public void setAString(String aString)
	{
		this.aString = aString;
	}

	public void setABool(boolean aBoolean)
	{
		this.aBool = aBoolean;
//		this.aBoolObject = aBoolean;
	}

	public void setAByte(byte aByte)
	{
		this.aByte = aByte;
	}

	public void setADouble(double aDouble)
	{
		this.aDouble = aDouble;
	}

	public void setAI16(short aI16)
	{
		this.aI16 = aI16;
	}

	public void setAI32(int aI32)
	{
		this.aI32 = aI32;
	}

	public void setAI64(long aI64)
	{
		this.aI64 = aI64;
	}

	public void setABinary(byte[] aBinary)
	{
		this.aBinary = aBinary;
	}

	public void setANested(Nested aNested)
	{
		this.aNested = aNested;
	}

	@Override public String toString()
	{
		return "Simple{" +
		       "aString='" + aString + '\'' +
		       ", aBool=" + aBool +
		       ", aByte=" + aByte +
		       ", aDouble=" + aDouble +
		       ", aI16=" + aI16 +
		       ", aI32=" + aI32 +
		       ", aI64=" + aI64 +
		       ", aBinary=" + (aBinary == null ? null : Arrays.asList(aBinary)) +
		       ", aNested=" + aNested +
		       '}';
	}

	public short getAI16()
	{
		return aI16;
	}
}
