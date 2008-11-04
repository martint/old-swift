package mt.serialization;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class Parent
{
	private String aString;
	private boolean aBoolean;
	private List<Integer> aList;
	private Simple aChild;
	private byte aByte;
	private double aDouble;
	private short aI16;
	private int aI32;
	private long aI64;
	private Map<Integer, String> aMap;
	private Set<String> aSet;
	private byte[] aBinary;
	private List<List<Integer>> aListOfLists;

	private List<Boolean> listOfBooleans;
	private List<Byte> listOfBytes;
	private List<Short> listOfShorts;
	private List<Integer> listOfInts;
	private List<Long> listOfLongs;
	private List<Double> listOfDoubles;

	public void setListOfBooleans(List<Boolean> listOfBooleans)
	{
		this.listOfBooleans = listOfBooleans;
	}

	public void setListOfBytes(List<Byte> listOfBytes)
	{
		this.listOfBytes = listOfBytes;
	}

	public void setListOfShorts(List<Short> listOfShorts)
	{
		this.listOfShorts = listOfShorts;
	}

	public void setListOfInts(List<Integer> listOfInts)
	{
		this.listOfInts = listOfInts;
	}

	public void setListOfLongs(List<Long> listOfLongs)
	{
		this.listOfLongs = listOfLongs;
	}

	public void setListOfDoubles(List<Double> listOfDoubles)
	{
		this.listOfDoubles = listOfDoubles;
	}

	public void setAChild(Simple aChild)
	{
		this.aChild = aChild;
	}

	public void setAString(String aString)
	{
		this.aString = aString;
	}

	public void setABoolean(boolean aBoolean)
	{
		this.aBoolean = aBoolean;
	}

	public void setAList(List<Integer> aList)
	{
		this.aList = aList;
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

	public void setAMap(Map<Integer, String> aMap)
	{
		this.aMap = aMap;
	}

	public void setASet(Set<String> aSet)
	{
		System.out.println("setting aSet: " + aSet);
		this.aSet = aSet;
	}

	public void setABinary(byte[] aBinary)
	{

		this.aBinary = aBinary;
	}

	public void setAListOfLists(List<List<Integer>> aListOfLists)
	{
		this.aListOfLists = aListOfLists;
	}
}
