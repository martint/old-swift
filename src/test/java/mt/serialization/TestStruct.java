package mt.serialization;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class TestStruct
{
	private boolean booleanField;
	private byte byteField;
	private short shortField;
	private int intField;
	private long longField;
	private double doubleField;
	private String stringField;
	private byte[] binaryField;
	private List<Integer> listOfIntsField;
	private Set<Integer> setOfIntsField;
	private Map<Integer,Integer> mapOfIntsIntsField;
	private NestedStruct structField;
	private List<List<Integer>> nestedListOfIntsField;

	public void setBooleanField(boolean booleanField)
	{
		this.booleanField = booleanField;
	}

	public void setByteField(byte byteField)
	{
		this.byteField = byteField;
	}

	public void setShortField(short shortField)
	{
		this.shortField = shortField;
	}

	public void setIntField(int intField)
	{
		this.intField = intField;
	}

	public void setLongField(long longField)
	{
		this.longField = longField;
	}

	public void setDoubleField(double doubleField)
	{
		this.doubleField = doubleField;
	}

	public void setStringField(String stringField)
	{
		this.stringField = stringField;
	}

	public void setBinaryField(byte[] binaryField)
	{
		this.binaryField = binaryField;
	}

	public void setListOfIntsField(List<Integer> listOfIntsField)
	{
		this.listOfIntsField = listOfIntsField;
	}

	public void setSetOfIntsField(Set<Integer> setOfIntsField)
	{
		this.setOfIntsField = setOfIntsField;
	}

	public void setMapOfIntsIntsField(Map<Integer, Integer> mapOfIntsIntsField)
	{
		this.mapOfIntsIntsField = mapOfIntsIntsField;
	}

	public void setStructField(NestedStruct structField)
	{
		this.structField = structField;
	}

	public void setNestedListOfIntsField(List<List<Integer>> nestedListOfIntsField)
	{
		this.nestedListOfIntsField = nestedListOfIntsField;
	}

	public boolean isBooleanField()
	{
		return booleanField;
	}

	public byte getByteField()
	{
		return byteField;
	}

	public short getShortField()
	{
		return shortField;
	}

	public int getIntField()
	{
		return intField;
	}

	public long getLongField()
	{
		return longField;
	}

	public double getDoubleField()
	{
		return doubleField;
	}

	public String getStringField()
	{
		return stringField;
	}

	public byte[] getBinaryField()
	{
		return binaryField;
	}

	public List<Integer> getListOfIntsField()
	{
		return listOfIntsField;
	}

	public Set<Integer> getSetOfIntsField()
	{
		return setOfIntsField;
	}

	public Map<Integer, Integer> getMapOfIntsIntsField()
	{
		return mapOfIntsIntsField;
	}

	public NestedStruct getStructField()
	{
		return structField;
	}

	public List<List<Integer>> getNestedListOfIntsField()
	{
		return nestedListOfIntsField;
	}


}
