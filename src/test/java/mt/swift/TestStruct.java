/**
 *  Copyright 2008 Martin Traverso
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package mt.swift;

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
