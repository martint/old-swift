package mt.serialization;

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

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

public class TestSerializeBean
{

	// TODO: serialize and read, verify protocol w/ easymock
	
	@Test
	public void testSerialize()
		throws Exception
	{
		StructureType nested = new StructureType(TNestedStruct.class.getName(),
		                                         new Field(BasicType.STRING, 1, "value", false));

		StructureType type = new StructureType(TTestStruct.class.getName(),
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
		                                       new Field(new MapType(BasicType.I32, BasicType.I32), 11,
		                                                 "mapOfIntsIntsField", false),
		                                       new Field(nested, 12, "structField", false),
		                                       new Field(new ListType(new ListType(BasicType.I32)), 13,
		                                                 "nestedListOfIntsField", false));

		TestStruct data = new TestStruct();
		data.setBooleanField(true);
		data.setByteField(Byte.MAX_VALUE);
		data.setShortField(Short.MAX_VALUE);
		data.setIntField(Integer.MAX_VALUE);
		data.setLongField(Long.MAX_VALUE);
		data.setDoubleField(Double.MAX_VALUE);
		data.setStringField("Hello World");
		data.setBinaryField("Bye bye".getBytes("UTF-8"));
		data.setListOfIntsField(Arrays.asList(Integer.MIN_VALUE, Integer.MAX_VALUE));
		data.setSetOfIntsField(new HashSet<Integer>(Arrays.asList(Integer.MIN_VALUE, Integer.MAX_VALUE)));

		NestedStruct nestedData = new NestedStruct();
		nestedData.setValue("bye bye");
		data.setStructField(nestedData);

		data.setNestedListOfIntsField(Arrays.asList(Arrays.asList(Integer.MAX_VALUE)));

		Map<Integer, Integer> mapOfIntsInts = new LinkedHashMap<Integer, Integer>();
		mapOfIntsInts.put(1, Integer.MIN_VALUE);
		mapOfIntsInts.put(2, Integer.MAX_VALUE);
		data.setMapOfIntsIntsField(mapOfIntsInts);

		Serializer serializer = new Serializer();
		serializer.bind(type, TestStruct.class);
		serializer.bind(nested, NestedStruct.class);

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		TProtocol protocol = new TBinaryProtocol(new TIOStreamTransport(out));
		serializer.serialize(data, type.getName(), protocol);

		TTestStruct result = TestUtil.deserialize(out.toByteArray());

		Assert.assertTrue(result.__isset.booleanField);
		Assert.assertEquals(result.booleanField, data.isBooleanField());

		Assert.assertTrue(result.__isset.byteField);
		Assert.assertEquals(result.byteField, data.getByteField());

		Assert.assertTrue(result.__isset.shortField);
		Assert.assertEquals(result.shortField, data.getShortField());

		Assert.assertTrue(result.__isset.intField);
		Assert.assertEquals(result.intField, data.getIntField());

		Assert.assertTrue(result.__isset.longField);
		Assert.assertEquals(result.longField, data.getLongField());

		Assert.assertTrue(result.__isset.doubleField);
		Assert.assertEquals(result.doubleField, data.getDoubleField());

		Assert.assertTrue(result.__isset.stringField);
		Assert.assertEquals(result.stringField, data.getStringField());

		Assert.assertTrue(result.__isset.binaryField);
		Assert.assertTrue(Arrays.equals(result.binaryField, data.getBinaryField()));

		Assert.assertTrue(result.__isset.listOfIntsField);
		Assert.assertEquals(result.listOfIntsField, data.getListOfIntsField());

		Assert.assertTrue(result.__isset.setOfIntsField);
		Assert.assertEquals(result.setOfIntsField, data.getSetOfIntsField());

		Assert.assertTrue(result.__isset.mapOfIntsIntsField);
		Assert.assertEquals(result.mapOfIntsIntsField, data.getMapOfIntsIntsField());

		Assert.assertTrue(result.__isset.structField);
		Assert.assertEquals(result.structField.value, nestedData.getValue());

		Assert.assertTrue(result.__isset.nestedListOfIntsField);
		Assert.assertEquals(result.nestedListOfIntsField, data.getNestedListOfIntsField());
	}



}
