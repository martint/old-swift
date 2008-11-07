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
package mt.serialization;

import com.facebook.thrift.TException;
import com.facebook.thrift.protocol.TProtocol;
import mt.serialization.model.BasicType;
import mt.serialization.model.Field;
import mt.serialization.model.StructureType;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TestDeserializeMap
{
	// TODO: test protocol using easymock

	@Test
	public void testBoolean()
		throws Exception
	{
		Field field = Fields.BOOLEAN_FIELD;
		StructureType type = new StructureType(TestStruct.class.getName(), field);

		for (Boolean value : Arrays.asList(true, false)) {
			TTestStruct data = new TTestStruct();
			data.booleanField = value;
			data.__isset.booleanField = true;

			Map<String, ?> result = deserialize(type, data);
			Assert.assertEquals(result.size(), 1);
			Assert.assertEquals(result.get(field.getName()), value);
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
			data.byteField = value;
			data.__isset.byteField = true;

			Map<String, ?> result = deserialize(type, data);
			Assert.assertEquals(result.size(), 1);
			Assert.assertEquals(result.get(field.getName()), value);
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
			data.shortField = value;
			data.__isset.shortField = true;

			Map<String, ?> result = deserialize(type, data);
			Assert.assertEquals(result.size(), 1);
			Assert.assertEquals(result.get(field.getName()), value);
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
			data.intField = value;
			data.__isset.intField = true;

			Map<String, ?> result = deserialize(type, data);
			Assert.assertEquals(result.size(), 1);
			Assert.assertEquals(result.get(field.getName()), value);
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
			data.longField = value;
			data.__isset.longField = true;

			Map<String, ?> result = deserialize(type, data);
			Assert.assertEquals(result.size(), 1);
			Assert.assertEquals(result.get(field.getName()), value);
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
			data.doubleField = value;
			data.__isset.doubleField = true;

			Map<String, ?> result = deserialize(type, data);
			Assert.assertEquals(result.size(), 1);
			Assert.assertEquals(result.get(field.getName()), value);
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
		data.stringField = value;
		data.__isset.stringField = true;

		Map<String, ?> result = deserialize(type, data);
		Assert.assertEquals(result.size(), 1);
		Assert.assertEquals(result.get(field.getName()), value);
	}

	@Test
	public void testBinary()
		throws Exception
	{
		Field field = Fields.BINARY_FIELD;
		StructureType type = new StructureType(TestStruct.class.getName(), field);

		byte[] value = "hello world".getBytes("UTF-8");

		TTestStruct data = new TTestStruct();
		data.binaryField = value;
		data.__isset.binaryField = true;

		Map<String, ?> result = deserialize(type, data);
		Assert.assertEquals(result.size(), 1);
		Assert.assertTrue(Arrays.equals((byte[]) result.get(field.getName()), data.binaryField));
	}

	@Test
	public void testListOfInts()
		throws Exception
	{
		Field field = Fields.LIST_OF_INTS_FIELD;
		StructureType type = new StructureType(TestStruct.class.getName(), field);

		List<Integer> value = Arrays.asList(Integer.MAX_VALUE);

		TTestStruct data = new TTestStruct();
		data.listOfIntsField = value;
		data.__isset.listOfIntsField = true;

		Map<String, ?> result = deserialize(type, data);
		Assert.assertEquals(result.size(), 1);
		Assert.assertEquals(result.get(field.getName()), value);
	}

	@Test
	public void testSetOfInts()
		throws Exception
	{
		Field field = Fields.SET_OF_INTS_FIELD;
		StructureType type = new StructureType(TestStruct.class.getName(), field);

		Set<Integer> value = new HashSet<Integer>(Arrays.asList(Integer.MAX_VALUE));

		TTestStruct data = new TTestStruct();
		data.setOfIntsField = value;
		data.__isset.setOfIntsField = true;

		Map<String, ?> result = deserialize(type, data);
		Assert.assertEquals(result.size(), 1);
		Assert.assertEquals(result.get(field.getName()), value);
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
		data.mapOfIntsIntsField = value;
		data.__isset.mapOfIntsIntsField = true;

		Map<String, ?> result = deserialize(type, data);
		Assert.assertEquals(result.size(), 1);
		Assert.assertEquals(result.get(field.getName()), value);
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
		data.structField = new TNestedStruct("hello world");
		data.__isset.structField = true;

		TProtocol protocol = TestUtil.serialize(data);

		Deserializer deserializer = new Deserializer();
		deserializer.bindToMap(type);
		deserializer.bindToMap(nested);

		Map<String, ?> result = deserializer.deserialize(type.getName(), protocol);
		Assert.assertEquals(result.size(), 1);

		Map<String, String> expected = new LinkedHashMap<String, String>();
		expected.put("value", data.structField.value);
		Assert.assertEquals(result.get("structField"), expected);
	}


	@Test
	public void testNestedListOfInts()
		throws Exception
	{
		Field field = Fields.NESTED_LIST_OF_INTS_FIELD;
		StructureType type = new StructureType(TestStruct.class.getName(), field);

		List<List<Integer>> value = Arrays.asList(Arrays.asList(Integer.MAX_VALUE));

		TTestStruct data = new TTestStruct();
		data.nestedListOfIntsField = value;
		data.__isset.nestedListOfIntsField = true;

		Map<String, ?> result = deserialize(type, data);
		Assert.assertEquals(result.size(), 1);
		Assert.assertEquals(result.get(field.getName()), value);
	}

	@Test
	public void testSetAndList()
		throws Exception
	{
		StructureType type = new StructureType(TestStruct.class.getName(),
		                                       Fields.SET_OF_INTS_FIELD,
		                                       Fields.LIST_OF_INTS_FIELD);

		TTestStruct data = new TTestStruct();
		data.setOfIntsField = new HashSet<Integer>(Arrays.asList(Integer.MAX_VALUE));
		data.__isset.setOfIntsField = true;

		data.listOfIntsField = Arrays.asList(Integer.MIN_VALUE);
		data.__isset.listOfIntsField = true;

		Map<String, ?> result = deserialize(type, data);
		Assert.assertEquals(result.size(), 2);
		Assert.assertEquals(result.get(Fields.SET_OF_INTS_FIELD.getName()), data.setOfIntsField);
		Assert.assertEquals(result.get(Fields.LIST_OF_INTS_FIELD.getName()), data.listOfIntsField);
	}

	private Map<String, ?> deserialize(StructureType type, TTestStruct data)
		throws TException
	{
		TProtocol protocol = TestUtil.serialize(data);

		Deserializer deserializer = new Deserializer();
		deserializer.bindToMap(type);

		return deserializer.deserialize(type.getName(), protocol);
	}
}
