package mt.serialization;

import static mt.serialization.TestUtil.*;

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
import org.easymock.classextension.EasyMock;
import static org.easymock.classextension.EasyMock.createMock;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.Map;

public class TestSerializeBean
{
	private final Field BOOLEAN_FIELD = new Field(BasicType.BOOLEAN, 1, "booleanField", false);
	private final Field BYTE_FIELD = new Field(BasicType.BYTE, 2, "byteField", false);
	private final Field I16_FIELD = new Field(BasicType.I16, 3, "shortField", false);
	private final Field I32_FIELD = new Field(BasicType.I32, 4, "intField", false);
	private final Field I64_FIELD = new Field(BasicType.I64, 5, "longField", false);
	private final Field DOUBLE_FIELD = new Field(BasicType.DOUBLE, 6, "doubleField", false);
	private final Field STRING_FIELD = new Field(BasicType.STRING, 7, "stringField", false);
	private final Field BINARY_FIELD = new Field(BasicType.BINARY, 8, "binaryField", false);

	// TODO: test composite structures
	
	@Test
	public void testBoolean()
		throws Exception
	{
		Field field = BOOLEAN_FIELD;
		for (final Boolean value : Arrays.asList(true, false)) {
			TestStruct bean = new TestStruct();
			bean.setBooleanField(value);

			verifyProtocol(field, bean, new TestUtil.WriteAction()
			{
				public void write(TProtocol protocol)
					throws TException
				{
					protocol.writeBool(value);
				}
			});

			TTestStruct result = serializeAndReadWithThrift(field, bean);
			Assert.assertTrue(result.__isset.booleanField);
			Assert.assertEquals(result.booleanField, value.booleanValue());
		}
	}

	@Test
	public void testByte()
		throws Exception
	{
		Field field = BYTE_FIELD;
		for (final Byte value : Arrays.asList((byte) 0, Byte.MIN_VALUE, Byte.MAX_VALUE)) {
			TestStruct bean = new TestStruct();
			bean.setByteField(value);

			verifyProtocol(field, bean, new TestUtil.WriteAction()
			{
				public void write(TProtocol protocol)
					throws TException
				{
					protocol.writeByte(value);
				}
			});

			TTestStruct result = serializeAndReadWithThrift(field, bean);
			Assert.assertTrue(result.__isset.byteField);
			Assert.assertEquals(result.byteField, value.byteValue());
		}
	}

	@Test
	public void testShort()
		throws Exception
	{
		Field field = I16_FIELD;
		for (final Short value : Arrays.asList((short) 0, Short.MIN_VALUE, Short.MAX_VALUE)) {
			TestStruct bean = new TestStruct();
			bean.setShortField(value);

			verifyProtocol(field, bean, new TestUtil.WriteAction()
			{
				public void write(TProtocol protocol)
					throws TException
				{
					protocol.writeI16(value);
				}
			});

			TTestStruct result = serializeAndReadWithThrift(field, bean);
			Assert.assertTrue(result.__isset.shortField);
			Assert.assertEquals(result.shortField, value.shortValue());
		}
	}

	@Test
	public void testI32()
		throws Exception
	{
		Field field = I32_FIELD;
		for (final Integer value : Arrays.asList(0, Integer.MIN_VALUE, Integer.MAX_VALUE)) {
			TestStruct bean = new TestStruct();
			bean.setIntField(value);

			verifyProtocol(field, bean, new TestUtil.WriteAction()
			{
				public void write(TProtocol protocol)
					throws TException
				{
					protocol.writeI32(value);
				}
			});

			TTestStruct result = serializeAndReadWithThrift(field, bean);
			Assert.assertTrue(result.__isset.intField);
			Assert.assertEquals(result.intField, value.intValue());
		}
	}


	@Test
	public void testI64()
		throws Exception
	{
		Field field = I64_FIELD;
		for (final Long value : Arrays.asList(0L, Long.MIN_VALUE, Long.MAX_VALUE)) {
			TestStruct bean = new TestStruct();
			bean.setLongField(value);

			verifyProtocol(field, bean, new TestUtil.WriteAction()
			{
				public void write(TProtocol protocol)
					throws TException
				{
					protocol.writeI64(value);
				}
			});

			TTestStruct result = serializeAndReadWithThrift(field, bean);
			Assert.assertTrue(result.__isset.longField);
			Assert.assertEquals(result.longField, value.longValue());
		}
	}

	@Test
	public void testDouble()
		throws Exception
	{
		Field field = DOUBLE_FIELD;
		for (final Double value : Arrays.asList(0.0, Double.MIN_VALUE, Double.MAX_VALUE, Double.NaN,
		                                        Double.NEGATIVE_INFINITY,
		                                        Double.POSITIVE_INFINITY)) {
			TestStruct bean = new TestStruct();
			bean.setDoubleField(value);

			verifyProtocol(field, bean, new TestUtil.WriteAction()
			{
				public void write(TProtocol protocol)
					throws TException
				{
					protocol.writeDouble(value);
				}
			});

			TTestStruct result = serializeAndReadWithThrift(field, bean);
			Assert.assertTrue(result.__isset.doubleField);
			Assert.assertEquals(result.doubleField, value.doubleValue());
		}
	}

	@Test
	public void testBinary()
		throws Exception
	{
		Field field = BINARY_FIELD;

		final byte[] value = "hello world".getBytes("UTF-8");

		TestStruct bean = new TestStruct();
		bean.setBinaryField(value);

		verifyProtocol(field, bean, new TestUtil.WriteAction()
		{
			public void write(TProtocol protocol)
				throws TException
			{
				protocol.writeBinary(value);
			}
		});

		TTestStruct result = serializeAndReadWithThrift(field, bean);
		Assert.assertTrue(result.__isset.binaryField);
		Assert.assertEquals(result.binaryField, value);
	}

	@Test
	public void testString()
		throws Exception
	{
		Field field = STRING_FIELD;

		final String value = "hello world";

		TestStruct bean = new TestStruct();
		bean.setStringField(value);

		verifyProtocol(field, bean, new TestUtil.WriteAction()
		{
			public void write(TProtocol protocol)
				throws TException
			{
				protocol.writeString(value);
			}
		});

		TTestStruct result = serializeAndReadWithThrift(field, bean);
		Assert.assertTrue(result.__isset.stringField);
		Assert.assertEquals(result.stringField, value);
	}

	private TTestStruct serializeAndReadWithThrift(Field field, TestStruct object)
		throws TException
	{
		StructureType type = new StructureType(TestStruct.class.getName(), field);

		Serializer serializer = new Serializer();
		serializer.bind(type, TestStruct.class);

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		TProtocol protocol = new TBinaryProtocol(new TIOStreamTransport(out));
		serializer.serialize(object, type.getName(), protocol);

		return TestUtil.deserialize(out.toByteArray());
	}

	private void verifyProtocol(Field field, TestStruct object, TestUtil.WriteAction action)
		throws TException
	{
		StructureType type = new StructureType(TestStruct.class.getName(), field);
		Serializer serializer = new Serializer();
		serializer.bind(type, TestStruct.class);

		TProtocol protocol = createMock(TProtocol.class);
		protocol.writeStructBegin(equal(toTStruct(type)));
		protocol.writeFieldBegin(equal(toTField(field)));
		action.write(protocol);
		protocol.writeFieldEnd();
		protocol.writeFieldStop();
		protocol.writeStructEnd();
		EasyMock.replay(protocol);

		serializer.serialize(object, type.getName(), protocol);
		EasyMock.verify(protocol);
	}


	@Test
	public void testAll()
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
