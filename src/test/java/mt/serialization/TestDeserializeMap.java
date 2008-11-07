package mt.serialization;

import com.facebook.thrift.TException;
import com.facebook.thrift.protocol.TBinaryProtocol;
import com.facebook.thrift.protocol.TProtocol;
import com.facebook.thrift.transport.TIOStreamTransport;
import mt.serialization.model.BasicType;
import mt.serialization.model.Field;
import mt.serialization.model.ListType;
import mt.serialization.model.MapType;
import mt.serialization.model.SetType;
import mt.serialization.model.StructureType;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

public class TestDeserializeMap
{
	// TODO: test protocol using easymock
	
	@Test
	public void testBoolean()
		throws Exception
	{
		StructureType type = new StructureType(TTestStruct.class.getName(),
		                                       new Field(BasicType.BOOLEAN, 1, "booleanField", false));

		TTestStruct data = new TTestStruct();
		data.booleanField = true;
		data.__isset.booleanField = true;

		TProtocol protocol = serialize(data);

		Deserializer deserializer = new Deserializer();
		deserializer.bindToMap(type);

		Map<String, ?> result = deserializer.deserialize(type.getName(), protocol);
		Assert.assertEquals(result.size(), 1);
		Assert.assertEquals(result.get("booleanField"), data.booleanField);
	}

	@Test
	public void testByte()
		throws Exception
	{
		StructureType type = new StructureType(TTestStruct.class.getName(),
		                                       new Field(BasicType.BYTE, 2, "byteField", false));

		TTestStruct data = new TTestStruct();
		data.byteField = Byte.MAX_VALUE;
		data.__isset.byteField = true;

		TProtocol protocol = serialize(data);

		Deserializer deserializer = new Deserializer();
		deserializer.bindToMap(type);

		Map<String, ?> result = deserializer.deserialize(type.getName(), protocol);
		Assert.assertEquals(result.size(), 1);
		Assert.assertEquals(result.get("byteField"), data.byteField);
	}


	@Test
	public void testShort()
		throws Exception
	{
		StructureType type = new StructureType(TTestStruct.class.getName(),
		                                       new Field(BasicType.I16, 3, "shortField", false));

		TTestStruct data = new TTestStruct();
		data.shortField = Short.MAX_VALUE;
		data.__isset.shortField = true;

		TProtocol protocol = serialize(data);

		Deserializer deserializer = new Deserializer();
		deserializer.bindToMap(type);

		Map<String, ?> result = deserializer.deserialize(type.getName(), protocol);
		Assert.assertEquals(result.size(), 1);
		Assert.assertEquals(result.get("shortField"), data.shortField);
	}

	@Test
	public void testInt()
		throws Exception
	{
		StructureType type = new StructureType(TTestStruct.class.getName(),
		                                       new Field(BasicType.I32, 4, "intField", false));

		TTestStruct data = new TTestStruct();
		data.intField = Integer.MAX_VALUE;
		data.__isset.intField = true;

		TProtocol protocol = serialize(data);

		Deserializer deserializer = new Deserializer();
		deserializer.bindToMap(type);

		Map<String, ?> result = deserializer.deserialize(type.getName(), protocol);
		Assert.assertEquals(result.size(), 1);
		Assert.assertEquals(result.get("intField"), data.intField);
	}

	@Test
	public void testLong()
		throws Exception
	{
		StructureType type = new StructureType(TTestStruct.class.getName(),
		                                       new Field(BasicType.I64, 5, "longField", false));

		TTestStruct data = new TTestStruct();
		data.longField = Long.MAX_VALUE;
		data.__isset.longField = true;

		TProtocol protocol = serialize(data);

		Deserializer deserializer = new Deserializer();
		deserializer.bindToMap(type);

		Map<String, ?> result = deserializer.deserialize(type.getName(), protocol);
		Assert.assertEquals(result.size(), 1);
		Assert.assertEquals(result.get("longField"), data.longField);
	}

	@Test
	public void testDouble()
		throws Exception
	{
		StructureType type = new StructureType(TTestStruct.class.getName(),
		                                       new Field(BasicType.DOUBLE, 6, "doubleField", false));

		TTestStruct data = new TTestStruct();
		data.doubleField = Double.MAX_VALUE;
		data.__isset.doubleField = true;

		TProtocol protocol = serialize(data);

		Deserializer deserializer = new Deserializer();
		deserializer.bindToMap(type);

		Map<String, ?> result = deserializer.deserialize(type.getName(), protocol);
		Assert.assertEquals(result.size(), 1);
		Assert.assertEquals(result.get("doubleField"), data.doubleField);
	}


	@Test
	public void testString()
		throws Exception
	{
		StructureType type = new StructureType(TTestStruct.class.getName(),
		                                       new Field(BasicType.STRING, 7, "stringField", false));

		TTestStruct data = new TTestStruct();
		data.stringField = "hello world";
		data.__isset.stringField = true;

		TProtocol protocol = serialize(data);

		Deserializer deserializer = new Deserializer();
		deserializer.bindToMap(type);

		Map<String, ?> result = deserializer.deserialize(type.getName(), protocol);
		Assert.assertEquals(result.size(), 1);
		Assert.assertEquals(result.get("stringField"), data.stringField);
	}

	@Test
	public void testBinary()
		throws Exception
	{
		StructureType type = new StructureType(TTestStruct.class.getName(),
		                                       new Field(BasicType.BINARY, 8, "binaryField", false));

		TTestStruct data = new TTestStruct();
		data.binaryField = "hello world".getBytes("UTF-8");
		data.__isset.stringField = true;

		TProtocol protocol = serialize(data);

		Deserializer deserializer = new Deserializer();
		deserializer.bindToMap(type);

		Map<String, ?> result = deserializer.deserialize(type.getName(), protocol);
		Assert.assertEquals(result.size(), 1);
		Assert.assertTrue(Arrays.equals((byte[]) result.get("binaryField"), data.binaryField));
	}

	@Test
	public void testListOfInts()
		throws Exception
	{
		StructureType type = new StructureType(TTestStruct.class.getName(),
		                                       new Field(new ListType(BasicType.I32), 9, "listOfInts", false));

		TTestStruct data = new TTestStruct();
		data.listOfIntsField = Arrays.asList(Integer.MAX_VALUE);
		data.__isset.listOfIntsField = true;

		TProtocol protocol = serialize(data);

		Deserializer deserializer = new Deserializer();
		deserializer.bindToMap(type);

		Map<String, ?> result = deserializer.deserialize(type.getName(), protocol);
		Assert.assertEquals(result.size(), 1);
		Assert.assertEquals(result.get("listOfInts"), data.listOfIntsField);
	}

	@Test
	public void testSetOfInts()
		throws Exception
	{
		StructureType type = new StructureType(TTestStruct.class.getName(),
		                                       new Field(new SetType(BasicType.I32), 10, "setOfInts", false));

		TTestStruct data = new TTestStruct();
		data.setOfIntsField = new HashSet<Integer>(Arrays.asList(Integer.MAX_VALUE));
		data.__isset.setOfIntsField = true;

		TProtocol protocol = serialize(data);

		Deserializer deserializer = new Deserializer();
		deserializer.bindToMap(type);

		Map<String, ?> result = deserializer.deserialize(type.getName(), protocol);
		Assert.assertEquals(result.size(), 1);
		Assert.assertEquals(result.get("setOfInts"), data.setOfIntsField);
	}

	@Test
	public void testMapOfIntsInts()
		throws Exception
	{
		StructureType type = new StructureType(TTestStruct.class.getName(),
		                                       new Field(new MapType(BasicType.I32, BasicType.I32), 11, "mapOfIntsInts",
		                                                 false));

		TTestStruct data = new TTestStruct();
		data.mapOfIntsIntsField = new LinkedHashMap<Integer, Integer>();
		data.mapOfIntsIntsField.put(Integer.MAX_VALUE, Integer.MIN_VALUE);
		data.__isset.mapOfIntsIntsField = true;

		TProtocol protocol = serialize(data);

		Deserializer deserializer = new Deserializer();
		deserializer.bindToMap(type);

		Map<String, ?> result = deserializer.deserialize(type.getName(), protocol);
		Assert.assertEquals(result.size(), 1);
		Assert.assertEquals(result.get("mapOfIntsInts"), data.mapOfIntsIntsField);
	}


	@Test
	public void testNestedStruct()
		throws Exception
	{
		StructureType nested = new StructureType(TNestedStruct.class.getName(),
		                                         new Field(BasicType.STRING, 1, "value", false));
		StructureType type = new StructureType(TTestStruct.class.getName(),
		                                       new Field(nested, 12, "structField",
		                                                 false));

		TTestStruct data = new TTestStruct();
		data.structField = new TNestedStruct("hello world");
		data.__isset.structField = true;

		TProtocol protocol = serialize(data);

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
		StructureType type = new StructureType(TTestStruct.class.getName(),
		                                       new Field(new ListType(new ListType(BasicType.I32)), 13, "nestedListOfIntsField",
		                                                 false));

		TTestStruct data = new TTestStruct();
		data.nestedListOfIntsField = Arrays.asList(Arrays.asList(Integer.MAX_VALUE));
		data.__isset.nestedListOfIntsField = true;

		TProtocol protocol = serialize(data);

		Deserializer deserializer = new Deserializer();
		deserializer.bindToMap(type);

		Map<String, ?> result = deserializer.deserialize(type.getName(), protocol);
		Assert.assertEquals(result.size(), 1);
		Assert.assertEquals(result.get("nestedListOfIntsField"), data.nestedListOfIntsField);
	}

	@Test
	public void testSetAndList()
		throws Exception
	{
		StructureType type = new StructureType(TTestStruct.class.getName(),
		                                       new Field(new SetType(BasicType.I32), 10, "setOfInts", false),
		                                       new Field(new ListType(BasicType.I32), 9, "listOfInts", false));

		TTestStruct data = new TTestStruct();
		data.setOfIntsField = new HashSet<Integer>(Arrays.asList(Integer.MAX_VALUE));
		data.__isset.setOfIntsField = true;

		data.listOfIntsField = Arrays.asList(Integer.MIN_VALUE);
		data.__isset.listOfIntsField = true;

		TProtocol protocol = serialize(data);

		Deserializer deserializer = new Deserializer();
		deserializer.bindToMap(type);

		Map<String, ?> result = deserializer.deserialize(type.getName(), protocol);
		Assert.assertEquals(result.size(), 2);
		Assert.assertEquals(result.get("setOfInts"), data.setOfIntsField);
		Assert.assertEquals(result.get("listOfInts"), data.listOfIntsField);

	}

	private TProtocol serialize(TTestStruct data)
		throws TException
	{
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		TProtocol outputProtocol = new TBinaryProtocol(new TIOStreamTransport(bao));
		data.write(outputProtocol);

		return new TBinaryProtocol(new TIOStreamTransport(new ByteArrayInputStream(bao.toByteArray())));
	}
}
