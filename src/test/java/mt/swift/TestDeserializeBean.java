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

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TProtocol;
import mt.swift.model.BasicType;
import mt.swift.model.Field;
import mt.swift.model.StructureType;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TestDeserializeBean
{
	// TODO: verify protocol w/ easymock

	@Test
	public void testBoolean()
		throws Exception
	{
		Field field = Fields.BOOLEAN_FIELD;
		StructureType type = new StructureType(TestStruct.class.getName(), field);

		for (Boolean value : Arrays.asList(true, false)) {
			TTestStruct data = new TTestStruct();
			data.setBooleanField(value);

			TestStruct result = deserialize(type, data);
			Assert.assertEquals(result.isBooleanField(), value.booleanValue());
		}
	}

	@Test
	public void testByte()
		throws Exception
	{
		Field field = Fields.BYTE_FIELD;
		StructureType type = new StructureType(TestStruct.class.getName(), field);

		for (final Byte value : Arrays.asList((byte) 0, Byte.MIN_VALUE, Byte.MAX_VALUE)) {
			TTestStruct data = new TTestStruct();
			data.setByteField(value);

			TestStruct result = deserialize(type, data);
			Assert.assertEquals(result.getByteField(), value.byteValue());
		}
	}


	@Test
	public void testI16()
		throws Exception
	{
		Field field = Fields.I16_FIELD;
		StructureType type = new StructureType(TestStruct.class.getName(), field);

		for (final Short value : Arrays.asList((short) 0, Short.MIN_VALUE, Short.MAX_VALUE)) {
			TTestStruct data = new TTestStruct();
			data.setShortField(value);

			TestStruct result = deserialize(type, data);
			Assert.assertEquals(result.getShortField(), value.shortValue());
		}
	}

	@Test
	public void testI32()
		throws Exception
	{
		Field field = Fields.I32_FIELD;
		StructureType type = new StructureType(TestStruct.class.getName(), field);

		for (final Integer value : Arrays.asList(0, Integer.MIN_VALUE, Integer.MAX_VALUE)) {
			TTestStruct data = new TTestStruct();
			data.setIntField(value);

			TestStruct result = deserialize(type, data);
			Assert.assertEquals(result.getIntField(), value.intValue());
		}
	}

	@Test
	public void testI64()
		throws Exception
	{
		Field field = Fields.I64_FIELD;
		StructureType type = new StructureType(TestStruct.class.getName(), field);

		for (final Long value : Arrays.asList(0L, Long.MIN_VALUE, Long.MAX_VALUE)) {
			TTestStruct data = new TTestStruct();
			data.setLongField(value);

			TestStruct result = deserialize(type, data);
			Assert.assertEquals(result.getLongField(), value.longValue());
		}
	}

	@Test
	public void testDouble()
		throws Exception
	{
		Field field = Fields.DOUBLE_FIELD;
		StructureType type = new StructureType(TestStruct.class.getName(), field);

		for (final Double value : Arrays.asList(0.0, Double.MIN_VALUE, Double.MAX_VALUE, Double.NaN,
		                                        Double.NEGATIVE_INFINITY,
		                                        Double.POSITIVE_INFINITY)) {
			TTestStruct data = new TTestStruct();
			data.setDoubleField(value);

			TestStruct result = deserialize(type, data);
			Assert.assertEquals(result.getDoubleField(), value);
		}
	}


	@Test
	public void testString()
		throws Exception
	{
		Field field = Fields.STRING_FIELD;
		StructureType type = new StructureType(TestStruct.class.getName(), field);

		String value = "hello world";

		TTestStruct data = new TTestStruct();
		data.setStringField(value);

		TestStruct result = deserialize(type, data);
		Assert.assertEquals(result.getStringField(), value);
	}

	@Test
	public void testBinary()
		throws Exception
	{
		Field field = Fields.BINARY_FIELD;
		StructureType type = new StructureType(TestStruct.class.getName(), field);

		byte[] value = "hello world".getBytes("UTF-8");

		TTestStruct data = new TTestStruct();
		data.setBinaryField(value);

		TestStruct result = deserialize(type, data);
		Assert.assertTrue(Arrays.equals(result.getBinaryField(), data.getBinaryField()));
	}

	@Test
	public void testListOfInts()
		throws Exception
	{
		Field field = Fields.LIST_OF_INTS_FIELD;
		StructureType type = new StructureType(TestStruct.class.getName(), field);

		List<Integer> value = Arrays.asList(Integer.MAX_VALUE);

		TTestStruct data = new TTestStruct();
		data.setListOfIntsField(value);

		TestStruct result = deserialize(type, data);
		Assert.assertEquals(result.getListOfIntsField(), value);
	}

	@Test
	public void testSetOfInts()
		throws Exception
	{
		Field field = Fields.SET_OF_INTS_FIELD;
		StructureType type = new StructureType(TestStruct.class.getName(), field);

		Set<Integer> value = new HashSet<Integer>(Arrays.asList(Integer.MAX_VALUE));

		TTestStruct data = new TTestStruct();
		data.setSetOfIntsField(value);

		TestStruct result = deserialize(type, data);
		Assert.assertEquals(result.getSetOfIntsField(), value);
	}

	@Test
	public void testMapOfIntsInts()
		throws Exception
	{
		Field field = Fields.MAP_OF_INTS_INTS_FIELD;
		StructureType type = new StructureType(TestStruct.class.getName(), field);

		Map<Integer, Integer> value = new LinkedHashMap<Integer, Integer>();
		value.put(Integer.MAX_VALUE, Integer.MIN_VALUE);

		TTestStruct data = new TTestStruct();
		data.setMapOfIntsIntsField(value);

		TestStruct result = deserialize(type, data);
		Assert.assertEquals(result.getMapOfIntsIntsField(), value);
	}


	@Test
	public void testNestedStruct()
		throws Exception
	{
		// TODO: clean up
		StructureType nested = new StructureType(TNestedStruct.class.getName(),
		                                         new Field(BasicType.STRING, 1, "value", false));
		StructureType type = new StructureType(TestStruct.class.getName(),
		                                       new Field(nested, 12, "structField",
		                                                 false));

		TTestStruct data = new TTestStruct();
		data.setStructField(new TNestedStruct("hello world"));

		TProtocol protocol = TestUtil.serialize(data);

		Deserializer deserializer = new Deserializer();
        deserializer.bind(nested, NestedStruct.class);
		deserializer.bind(type, TestStruct.class);

		TestStruct result = deserializer.deserialize(type.getName(), protocol);
		Assert.assertNotNull(result.getStructField());
		Assert.assertEquals(result.getStructField().getValue(), data.getStructField().getValue());
	}


	@Test
	public void testNestedListOfInts()
		throws Exception
	{
		Field field = Fields.NESTED_LIST_OF_INTS_FIELD;
		StructureType type = new StructureType(TestStruct.class.getName(), field);

		List<List<Integer>> value = Arrays.asList(Arrays.asList(Integer.MAX_VALUE));

		TTestStruct data = new TTestStruct();
		data.setNestedListOfIntsField(value);

		TestStruct result = deserialize(type, data);
		Assert.assertEquals(result.getNestedListOfIntsField(), value);
	}

	@Test
	public void testSetAndList()
		throws Exception
	{
		StructureType type = new StructureType(TestStruct.class.getName(),
		                                       Fields.SET_OF_INTS_FIELD,
		                                       Fields.LIST_OF_INTS_FIELD);

		TTestStruct data = new TTestStruct();
		data.setSetOfIntsField(new HashSet<Integer>(Arrays.asList(Integer.MAX_VALUE)));

		data.setListOfIntsField(Arrays.asList(Integer.MIN_VALUE));

		TestStruct result = deserialize(type, data);
		Assert.assertEquals(result.getSetOfIntsField(), data.getSetOfIntsField());
		Assert.assertEquals(result.getListOfIntsField(), data.getListOfIntsField());
	}

	private TestStruct deserialize(StructureType type, TTestStruct data)
		throws TException
	{
		TProtocol protocol = TestUtil.serialize(data);

		Deserializer deserializer = new Deserializer();
		deserializer.bind(type, TestStruct.class);

		return deserializer.deserialize(type.getName(), protocol);
	}
}
