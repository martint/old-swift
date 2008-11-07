package mt.serialization;

import com.facebook.thrift.TException;
import com.facebook.thrift.transport.TIOStreamTransport;
import com.facebook.thrift.protocol.TField;
import com.facebook.thrift.protocol.TProtocol;
import com.facebook.thrift.protocol.TStruct;
import com.facebook.thrift.protocol.TBinaryProtocol;
import mt.serialization.model.BasicType;
import mt.serialization.model.Field;
import mt.serialization.model.StructureType;
import mt.serialization.model.ListType;
import mt.serialization.model.SetType;
import mt.serialization.model.MapType;
import static org.easymock.EasyMock.cmp;
import static org.easymock.EasyMock.eq;
import org.easymock.LogicalOperator;
import org.easymock.classextension.EasyMock;
import static org.easymock.classextension.EasyMock.createMock;
import org.testng.annotations.Test;
import org.testng.Assert;

import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;
import java.io.ByteArrayOutputStream;

public class TestSerializeMap
{
	@Test
	public void testSerializer()
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
		                                                 "nestedListOfIntsField", false)
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
		serializer.bindToMap(type);
		serializer.bindToMap(nested);

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		TProtocol protocol = new TBinaryProtocol(new TIOStreamTransport(out));
		serializer.serialize(data, type.getName(), protocol);

		TTestStruct result = TestUtil.deserialize(out.toByteArray());

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
	public void testSerializeBooleanAndRead()
		throws Exception
	{
		StructureType type = new StructureType(TTestStruct.class.getName(),
		                                       new Field(BasicType.BOOLEAN, 1, "booleanField", false));

		Map<String, Boolean> data = new HashMap<String, Boolean>();
		data.put("booleanField", true);

		Serializer serializer = new Serializer();
		serializer.bindToMap(type);

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		TProtocol protocol = new TBinaryProtocol(new TIOStreamTransport(out));
		serializer.serialize(data, type.getName(), protocol);

		System.out.println(out.toByteArray().length);
		TTestStruct result = TestUtil.deserialize(out.toByteArray());

		Assert.assertTrue(result.__isset.booleanField);
		Assert.assertEquals(result.booleanField, data.get("booleanField").booleanValue());
	}

	// TODO: testSerializeXXXAndRead
	
	@Test
	public void testSerializeBoolean()
		throws Exception
	{
		Field field = new Field(BasicType.BOOLEAN, 1, "value", false);
		StructureType struct = new StructureType("someStruct", field);

		Serializer serializer = new Serializer();
		serializer.bindToMap(struct);

		for (final Boolean value : Arrays.asList(true, false)) {
			verifyProtocol(field, struct, serializer, value, new WriteAction()
			{
				public void write(TProtocol protocol)
					throws TException
				{
					protocol.writeBool(value);
				}
			});
		}
	}

	@Test
	public void testSerializeByte()
		throws Exception
	{
		Field field = new Field(BasicType.BYTE, 2, "value", false);
		StructureType struct = new StructureType("someStruct", field);

		Serializer serializer = new Serializer();
		serializer.bindToMap(struct);

		Number max = Byte.MAX_VALUE;
		Number min = Byte.MIN_VALUE;

//		for (Number value : Arrays.<Number>asList((byte) 0, (short) 0, (int) 0, (long) 0, BigInteger.ZERO, min, max)) {
		for (final Number value : Arrays.asList((byte) 0, min, max)) {
			verifyProtocol(field, struct, serializer, value, new WriteAction()
			{
				public void write(TProtocol protocol)
					throws TException
				{
					protocol.writeByte(value.byteValue());
				}
			});
		}
	}

	@Test
	public void testSerializeI16()
		throws Exception
	{
		Field field = new Field(BasicType.I16, 1, "value", false);
		StructureType struct = new StructureType("someStruct", field);

		Serializer serializer = new Serializer();
		serializer.bindToMap(struct);

		Number max = Short.MAX_VALUE;
		Number min = Short.MIN_VALUE;

//		for (Number value : Arrays.<Number>asList((byte) 0, (short) 0, (int) 0, (long) 0, BigInteger.ZERO, min, max)) {
		for (final Number value : Arrays.asList((short) 0, min, max)) {
			verifyProtocol(field, struct, serializer, value, new WriteAction()
			{
				public void write(TProtocol protocol)
					throws TException
				{
					protocol.writeI16(value.shortValue());
				}
			});
		}
	}

	@Test
	public void testSerializeI32()
		throws Exception
	{
		Field field = new Field(BasicType.I32, 3, "value", false);
		StructureType struct = new StructureType("someStruct", field);

		Serializer serializer = new Serializer();
		serializer.bindToMap(struct);

		Number max = Integer.MAX_VALUE;
		Number min = Integer.MIN_VALUE;

//		for (Number value : Arrays.<Number>asList((byte) 0, (short) 0, (int) 0, (long) 0, BigInteger.ZERO, min, max)) {
		for (final Number value : Arrays.asList(0, min, max)) {
			verifyProtocol(field, struct, serializer, value, new WriteAction()
			{
				public void write(TProtocol protocol)
					throws TException
				{
					protocol.writeI32(value.intValue());
				}
			});
		}
	}

	@Test
	public void testSerializeI64()
		throws Exception
	{
		Field field = new Field(BasicType.I64, 4, "value", false);
		StructureType struct = new StructureType("someStruct", field);

		Serializer serializer = new Serializer();
		serializer.bindToMap(struct);

		Number max = Long.MAX_VALUE;
		Number min = Long.MIN_VALUE;

//		for (Number value : Arrays.<Number>asList((byte) 0, (short) 0, (int) 0, (long) 0, BigInteger.ZERO, min, max)) {
		for (final Number value : Arrays.asList(0L, min, max)) {
			verifyProtocol(field, struct, serializer, value, new WriteAction()
			{
				public void write(TProtocol protocol)
					throws TException
				{
					protocol.writeI64(value.longValue());
				}
			});
		}
	}

	@Test
	public void testSerializeBinary()
		throws Exception
	{
		Field field = new Field(BasicType.BINARY, 4, "value", false);
		StructureType struct = new StructureType("someStruct", field);

		Serializer serializer = new Serializer();
		serializer.bindToMap(struct);

		final byte[] value = "hello world".getBytes("UTF-8");
		verifyProtocol(field, struct, serializer, value, new WriteAction()
		{
			public void write(TProtocol protocol)
				throws TException
			{
				protocol.writeBinary(value);
			}
		});
	}

	@Test
	public void testSerializeString()
		throws Exception
	{
		Field field = new Field(BasicType.STRING, 4, "value", false);
		StructureType struct = new StructureType("someStruct", field);

		Serializer serializer = new Serializer();
		serializer.bindToMap(struct);

		final String value = "hello world";
		verifyProtocol(field, struct, serializer, value, new WriteAction()
		{
			public void write(TProtocol protocol)
				throws TException
			{
				protocol.writeString(eq(value));
			}
		});
	}

	private void verifyProtocol(Field field, StructureType struct, Serializer serializer, Object value,
	                            WriteAction action)
		throws TException
	{
		TProtocol protocol = createMock(TProtocol.class);
		protocol.writeStructBegin(equal(toTStruct(struct)));
		protocol.writeFieldBegin(equal(toTField(field)));
		action.write(protocol);
		protocol.writeFieldEnd();
		protocol.writeFieldStop();
		protocol.writeStructEnd();
		EasyMock.replay(protocol);

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("value", value);
		serializer.serialize(data, struct.getName(), protocol);
		EasyMock.verify(protocol);
	}

	private static interface WriteAction
	{
		public void write(TProtocol protocol)
			throws TException;
	}


	private TStruct equal(TStruct struct)
	{
		return cmp(struct, new Comparator<TStruct>()
		{
			public int compare(TStruct o1, TStruct o2)
			{
				return o1.name.compareTo(o2.name);
			}
		}, LogicalOperator.EQUAL);
	}

	private TField equal(TField field)
	{
		return cmp(field, new Comparator<TField>()
		{
			public int compare(TField o1, TField o2)
			{
				int result = Short.valueOf(o1.id).compareTo(o2.id);

				if (result == 0) {
					result = Byte.valueOf(o1.type).compareTo(o2.type);
				}

				if (result == 0) {
					result = o1.name.compareTo(o2.name);
				}

				return result;
			}
		}, LogicalOperator.EQUAL);
	}

	private TStruct toTStruct(StructureType type)
	{
		return new TStruct(type.getName());
	}

	private TField toTField(Field field)
	{
		return new TField(field.getName(), field.getType().getTType(), field.getId());
	}
}
