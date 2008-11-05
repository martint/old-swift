package mt.serialization.other;

import mt.serialization.other.Simple;

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

	private Set<Boolean> setOfBooleans;
	private Set<Byte> setOfBytes;
	private Set<Short> setOfShorts;
	private Set<Integer> setOfInts;
	private Set<Long> setOfLongs;
	private Set<Double> setOfDoubles;
	private Set<Set<Integer>> setOfSets;

	private List<List<List<List<List<List<List<Integer>>>>>>> deepList;

	public void setDeepList(List<List<List<List<List<List<List<Integer>>>>>>> deepList)
	{
		this.deepList = deepList;
	}

	private Map<Integer, Integer> mapOfIntInt;

	public void setMapOfIntInt(Map<Integer, Integer> mapOfIntInt)
	{
		this.mapOfIntInt = mapOfIntInt;
	}

	public void setSetOfSets(Set<Set<Integer>> setOfSets)
	{
		this.setOfSets = setOfSets;
	}

	public void setSetOfBooleans(Set<Boolean> setOfBooleans)
	{
		this.setOfBooleans = setOfBooleans;
	}

	public void setSetOfBytes(Set<Byte> setOfBytes)
	{
		this.setOfBytes = setOfBytes;
	}

	public void setSetOfShorts(Set<Short> setOfShorts)
	{
		this.setOfShorts = setOfShorts;
	}

	public void setSetOfInts(Set<Integer> setOfInts)
	{
		this.setOfInts = setOfInts;
	}

	public void setSetOfLongs(Set<Long> setOfLongs)
	{
		this.setOfLongs = setOfLongs;
	}

	public void setSetOfDoubles(Set<Double> setOfDoubles)
	{
		this.setOfDoubles = setOfDoubles;
	}

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


	public String toString()
	{
		return "Parent{" +
		       "aString='" + aString + '\'' +
		       ", aBoolean=" + aBoolean +
		       ", aList=" + aList +
		       ", aChild=" + aChild +
		       ", aByte=" + aByte +
		       ", aDouble=" + aDouble +
		       ", aI16=" + aI16 +
		       ", aI32=" + aI32 +
		       ", aI64=" + aI64 +
		       ", aMap=" + aMap +
		       ", aSet=" + aSet +
		       ", aBinary=" + aBinary +
		       ", aListOfLists=" + aListOfLists +
		       ", listOfBooleans=" + listOfBooleans +
		       ", listOfBytes=" + listOfBytes +
		       ", listOfShorts=" + listOfShorts +
		       ", listOfInts=" + listOfInts +
		       ", listOfLongs=" + listOfLongs +
		       ", listOfDoubles=" + listOfDoubles +
		       ", setOfBooleans=" + setOfBooleans +
		       ", setOfBytes=" + setOfBytes +
		       ", setOfShorts=" + setOfShorts +
		       ", setOfInts=" + setOfInts +
		       ", setOfLongs=" + setOfLongs +
		       ", setOfDoubles=" + setOfDoubles +
		       ", setOfSets=" + setOfSets +
		       ", deepList=" + deepList +
		       ", mapOfIntInt=" + mapOfIntInt +
		       '}';
	}
}
