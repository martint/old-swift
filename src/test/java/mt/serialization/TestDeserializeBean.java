package mt.serialization;

import com.facebook.thrift.TException;
import com.facebook.thrift.protocol.TProtocol;
import mt.serialization.model.BasicType;
import mt.serialization.model.Field;
import mt.serialization.model.ListType;
import mt.serialization.model.MapType;
import mt.serialization.model.SetType;
import mt.serialization.model.StructureType;
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

	private final Field BOOLEAN_FIELD = new Field(BasicType.BOOLEAN, 1, "booleanField", false);
	private final Field BYTE_FIELD = new Field(BasicType.BYTE, 2, "byteField", false);
	private final Field I16_FIELD = new Field(BasicType.I16, 3, "shortField", false);
	private final Field I32_FIELD = new Field(BasicType.I32, 4, "intField", false);
	private final Field I64_FIELD = new Field(BasicType.I64, 5, "longField", false);
	private final Field DOUBLE_FIELD = new Field(BasicType.DOUBLE, 6, "doubleField", false);
	private final Field STRING_FIELD = new Field(BasicType.STRING, 7, "stringField", false);
	private final Field BINARY_FIELD = new Field(BasicType.BINARY, 8, "binaryField", false);
	private final Field LIST_OF_INTS_FIELD = new Field(new ListType(BasicType.I32), 9, "listOfIntsField", false);
	private final Field SET_OF_INTS_FIELD = new Field(new SetType(BasicType.I32), 10, "setOfIntsField", false);
	private final Field MAP_OF_INTS_INTS_FIELD =
		new Field(new MapType(BasicType.I32, BasicType.I32), 11, "mapOfIntsIntsField",
		          false);
	private final Field NESTED_LIST_OF_INTS_FIELD = new Field(new ListType(new ListType(BasicType.I32)), 13,
	                                                          "nestedListOfIntsField",
	                                                          false);

	// TODO: test protocol using easymock

	@Test
	public void testBoolean()
		throws Exception
	{
		Field field = BOOLEAN_FIELD;
		StructureType type = new StructureType(TestStruct.class.getName(), field);

		for (Boolean value : Arrays.asList(true, false)) {
			TTestStruct data = new TTestStruct();
			data.booleanField = value;
			data.__isset.booleanField = true;

			TestStruct result = deserialize(type, data);
			Assert.assertEquals(result.isBooleanField(), value.booleanValue());
		}
	}

	@Test
	public void testByte()
		throws Exception
	{
		Field field = BYTE_FIELD;
		StructureType type = new StructureType(TestStruct.class.getName(), field);

		for (final Byte value : Arrays.asList((byte) 0, Byte.MIN_VALUE, Byte.MAX_VALUE)) {
			TTestStruct data = new TTestStruct();
			data.byteField = value;
			data.__isset.byteField = true;

			TestStruct result = deserialize(type, data);
			Assert.assertEquals(result.getByteField(), value.byteValue());
		}
	}


	@Test
	public void testI16()
		throws Exception
	{
		Field field = I16_FIELD;
		StructureType type = new StructureType(TestStruct.class.getName(), field);

		for (final Short value : Arrays.asList((short) 0, Short.MIN_VALUE, Short.MAX_VALUE)) {
			TTestStruct data = new TTestStruct();
			data.shortField = value;
			data.__isset.shortField = true;

			TestStruct result = deserialize(type, data);
			Assert.assertEquals(result.getShortField(), value.shortValue());
		}
	}

	@Test
	public void testI32()
		throws Exception
	{
		Field field = I32_FIELD;
		StructureType type = new StructureType(TestStruct.class.getName(), field);

		for (final Integer value : Arrays.asList(0, Integer.MIN_VALUE, Integer.MAX_VALUE)) {
			TTestStruct data = new TTestStruct();
			data.intField = value;
			data.__isset.intField = true;

			TestStruct result = deserialize(type, data);
			Assert.assertEquals(result.getIntField(), value.intValue());
		}
	}

	@Test
	public void testI64()
		throws Exception
	{
		Field field = I64_FIELD;
		StructureType type = new StructureType(TestStruct.class.getName(), field);

		for (final Long value : Arrays.asList(0L, Long.MIN_VALUE, Long.MAX_VALUE)) {
			TTestStruct data = new TTestStruct();
			data.longField = value;
			data.__isset.longField = true;

			TestStruct result = deserialize(type, data);
			Assert.assertEquals(result.getLongField(), value.longValue());
		}
	}

	@Test
	public void testDouble()
		throws Exception
	{
		Field field = DOUBLE_FIELD;
		StructureType type = new StructureType(TestStruct.class.getName(), field);

		for (final Double value : Arrays.asList(0.0, Double.MIN_VALUE, Double.MAX_VALUE, Double.NaN,
		                                        Double.NEGATIVE_INFINITY,
		                                        Double.POSITIVE_INFINITY)) {
			TTestStruct data = new TTestStruct();
			data.doubleField = value;
			data.__isset.doubleField = true;

			TestStruct result = deserialize(type, data);
			Assert.assertEquals(result.getDoubleField(), value);
		}
	}


	@Test
	public void testString()
		throws Exception
	{
		Field field = STRING_FIELD;
		StructureType type = new StructureType(TestStruct.class.getName(), field);

		String value = "hello world";

		TTestStruct data = new TTestStruct();
		data.stringField = value;
		data.__isset.stringField = true;

		TestStruct result = deserialize(type, data);
		Assert.assertEquals(result.getStringField(), value);
	}

	@Test
	public void testBinary()
		throws Exception
	{
		Field field = BINARY_FIELD;
		StructureType type = new StructureType(TestStruct.class.getName(), field);

		byte[] value = "hello world".getBytes("UTF-8");

		TTestStruct data = new TTestStruct();
		data.binaryField = value;
		data.__isset.binaryField = true;

		TestStruct result = deserialize(type, data);
		Assert.assertTrue(Arrays.equals(result.getBinaryField(), data.binaryField));
	}

	@Test
	public void testListOfInts()
		throws Exception
	{
		Field field = LIST_OF_INTS_FIELD;
		StructureType type = new StructureType(TestStruct.class.getName(), field);

		List<Integer> value = Arrays.asList(Integer.MAX_VALUE);

		TTestStruct data = new TTestStruct();
		data.listOfIntsField = value;
		data.__isset.listOfIntsField = true;

		TestStruct result = deserialize(type, data);
		Assert.assertEquals(result.getListOfIntsField(), value);
	}

	@Test
	public void testSetOfInts()
		throws Exception
	{
		Field field = SET_OF_INTS_FIELD;
		StructureType type = new StructureType(TestStruct.class.getName(), field);

		Set<Integer> value = new HashSet<Integer>(Arrays.asList(Integer.MAX_VALUE));

		TTestStruct data = new TTestStruct();
		data.setOfIntsField = value;
		data.__isset.setOfIntsField = true;

		TestStruct result = deserialize(type, data);
		Assert.assertEquals(result.getSetOfIntsField(), value);
	}

	@Test
	public void testMapOfIntsInts()
		throws Exception
	{
		Field field = MAP_OF_INTS_INTS_FIELD;
		StructureType type = new StructureType(TestStruct.class.getName(), field);

		Map<Integer, Integer> value = new LinkedHashMap<Integer, Integer>();
		value.put(Integer.MAX_VALUE, Integer.MIN_VALUE);

		TTestStruct data = new TTestStruct();
		data.mapOfIntsIntsField = value;
		data.__isset.mapOfIntsIntsField = true;

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
		data.structField = new TNestedStruct("hello world");
		data.__isset.structField = true;

		TProtocol protocol = TestUtil.serialize(data);

		Deserializer deserializer = new Deserializer();
		deserializer.bind(type, TestStruct.class);
		deserializer.bind(nested, NestedStruct.class);

		TestStruct result = deserializer.deserialize(type.getName(), protocol);
		Assert.assertNotNull(result.getStructField());
		Assert.assertEquals(result.getStructField().getValue(), data.structField.value);
	}


	@Test
	public void testNestedListOfInts()
		throws Exception
	{
		Field field = NESTED_LIST_OF_INTS_FIELD;
		StructureType type = new StructureType(TestStruct.class.getName(), field);

		List<List<Integer>> value = Arrays.asList(Arrays.asList(Integer.MAX_VALUE));

		TTestStruct data = new TTestStruct();
		data.nestedListOfIntsField = value;
		data.__isset.nestedListOfIntsField = true;

		TestStruct result = deserialize(type, data);
		Assert.assertEquals(result.getNestedListOfIntsField(), value);
	}

	@Test
	public void testSetAndList()
		throws Exception
	{
		StructureType type = new StructureType(TestStruct.class.getName(),
		                                       SET_OF_INTS_FIELD,
		                                       LIST_OF_INTS_FIELD);

		TTestStruct data = new TTestStruct();
		data.setOfIntsField = new HashSet<Integer>(Arrays.asList(Integer.MAX_VALUE));
		data.__isset.setOfIntsField = true;

		data.listOfIntsField = Arrays.asList(Integer.MIN_VALUE);
		data.__isset.listOfIntsField = true;

		TestStruct result = deserialize(type, data);
		Assert.assertEquals(result.getSetOfIntsField(), data.setOfIntsField);
		Assert.assertEquals(result.getListOfIntsField(), data.listOfIntsField);
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
