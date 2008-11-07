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
import mt.serialization.test.NestedStruct;
import mt.serialization.test.TestStruct;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TestSerializer
{
	@Test
	public void testSerializer()
		throws Exception
	{
		StructureType nested = new StructureType(NestedStruct.class.getName(),
		                                         new Field(BasicType.STRING, 1, "value", false));

		StructureType type = new StructureType(TestStruct.class.getName(),
		                                       new Field(BasicType.BOOLEAN, 1, "booleanField", false),
		                                       new Field(BasicType.BYTE, 2, "byteField", false),
		                                       new Field(BasicType.I16, 3, "shortField", false),
		                                       new Field(BasicType.I32, 4, "intField", false),
		                                       new Field(BasicType.I64, 5, "longField", false),
		                                       new Field(BasicType.DOUBLE, 6, "doubleField", false),
		                                       new Field(BasicType.STRING, 7, "stringField", false),
		                                       new Field(BasicType.BINARY, 8, "binaryField", false),
		                                       new Field(new ListType(BasicType.I32), 9, "listOfIntsField", false),
		                                       new Field(new SetType(BasicType.I32), 10, "setOfIntsField", false),
		                                       new Field(new MapType(BasicType.I32, BasicType.I32), 11, "mapOfIntsIntsField", false),
		                                       new Field(nested, 12, "structField", false),
		                                       new Field(new ListType(new ListType(BasicType.I32)), 13, "nestedListOfIntsField", false)
        );

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("booleanField", true);
		data.put("byteField", Byte.MAX_VALUE);
		data.put("shortField", Short.MAX_VALUE);
		data.put("intField", Integer.MAX_VALUE);
		data.put("longField", Long.MAX_VALUE);
		data.put("doubleField", Double.MAX_VALUE);
		data.put("stringField", "Hello World");
		data.put("binaryField", "Bye bye".getBytes("UTF-8"));
		data.put("listOfIntsField", Arrays.asList(Integer.MIN_VALUE, Integer.MAX_VALUE));
		data.put("setOfIntsField", new HashSet<Integer>(Arrays.asList(Integer.MIN_VALUE, Integer.MAX_VALUE)));

		Map<String, Object> nestedData = new HashMap<String, Object>();
		nestedData.put("value", "bye bye");
		data.put("structField", nestedData);

		data.put("nestedListOfIntsField", Arrays.asList(Arrays.asList(Integer.MAX_VALUE)));

		Map<Integer, Integer> mapOfIntsInts = new LinkedHashMap<Integer, Integer>();
		mapOfIntsInts.put(1, Integer.MIN_VALUE);
		mapOfIntsInts.put(2, Integer.MAX_VALUE);
		data.put("mapOfIntsIntsField", mapOfIntsInts);

		Serializer serializer = new Serializer();
		serializer.setDebug(true);
		serializer.bindToMap(type);

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		TProtocol protocol = new TBinaryProtocol(new TIOStreamTransport(out));
		serializer.serialize(data, type.getName(), protocol);

		TestStruct result = deserialize(out.toByteArray());

		Assert.assertTrue(result.__isset.booleanField);
		Assert.assertEquals(result.booleanField, ((Boolean) data.get("booleanField")).booleanValue());

		Assert.assertTrue(result.__isset.byteField);
		Assert.assertEquals(result.byteField, ((Byte) data.get("byteField")).byteValue());

		Assert.assertTrue(result.__isset.shortField);
		Assert.assertEquals(result.shortField, ((Short) data.get("shortField")).shortValue());

		Assert.assertTrue(result.__isset.intField);
		Assert.assertEquals(result.intField, ((Integer) data.get("intField")).intValue());

		Assert.assertTrue(result.__isset.longField);
		Assert.assertEquals(result.longField, ((Long) data.get("longField")).longValue());

		Assert.assertTrue(result.__isset.doubleField);
		Assert.assertEquals(result.doubleField, data.get("doubleField"));

		Assert.assertTrue(result.__isset.stringField);
		Assert.assertEquals(result.stringField, data.get("stringField"));

		Assert.assertTrue(result.__isset.binaryField);
		Assert.assertTrue(Arrays.equals(result.binaryField, (byte[]) data.get("binaryField")));

		Assert.assertTrue(result.__isset.listOfIntsField);
		Assert.assertEquals(result.listOfIntsField, (List) data.get("listOfIntsField"));

		Assert.assertTrue(result.__isset.setOfIntsField);
		Assert.assertEquals(result.setOfIntsField, (Set) data.get("setOfIntsField"));

		Assert.assertTrue(result.__isset.mapOfIntsIntsField);
		Assert.assertEquals(result.mapOfIntsIntsField, (Map) data.get("mapOfIntsIntsField"));

		Assert.assertTrue(result.__isset.structField);
		Assert.assertEquals(result.structField.value, nestedData.get("value"));
		
		Assert.assertTrue(result.__isset.nestedListOfIntsField);
		Assert.assertEquals(result.nestedListOfIntsField, (List) data.get("nestedListOfIntsField"));
	}

	@Test
	public void testMapToBoolean()
		throws Exception
	{
		StructureType type = new StructureType(TestStruct.class.getName(),
		                                       new Field(BasicType.BOOLEAN, 1, "booleanField", false));

		Map<String, Boolean> data = new HashMap<String, Boolean>();
		data.put("booleanField", true);

		Serializer serializer = new Serializer();
		serializer.setDebug(true);
		serializer.bindToMap(type);

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		TProtocol protocol = new TBinaryProtocol(new TIOStreamTransport(out));
		serializer.serialize(data, type.getName(), protocol);

		System.out.println(out.toByteArray().length);
		TestStruct result = deserialize(out.toByteArray());

		Assert.assertTrue(result.__isset.booleanField);
		Assert.assertEquals(result.booleanField, data.get("booleanField").booleanValue());
	}

	private TestStruct deserialize(byte[] data)
		throws TException
	{
		TestStruct result = new TestStruct();
		result.read(new TBinaryProtocol(new TIOStreamTransport(new ByteArrayInputStream(data))));

		return result;
	}

}
