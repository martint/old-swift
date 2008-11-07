package mt.serialization;

import com.facebook.thrift.TException;
import com.facebook.thrift.protocol.TBinaryProtocol;
import com.facebook.thrift.protocol.TProtocol;
import com.facebook.thrift.transport.TIOStreamTransport;
import static mt.serialization.TestUtil.*;
import mt.serialization.model.BasicType;
import mt.serialization.model.Field;
import mt.serialization.model.StructureType;
import mt.serialization.model.ListType;
import mt.serialization.model.SetType;
import mt.serialization.model.MapType;
import static org.easymock.EasyMock.eq;
import org.easymock.classextension.EasyMock;
import static org.easymock.classextension.EasyMock.createMock;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Set;

public class TestSerializeMap
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
		for (final Boolean value : Arrays.asList(true, false)) {
			verifyProtocol(BOOLEAN_FIELD, value, new WriteAction()
			{
				public void write(TProtocol protocol)
					throws TException
				{
					protocol.writeBool(value);
				}
			});

			TTestStruct result = serializeAndRead(BOOLEAN_FIELD, value);
			Assert.assertTrue(result.__isset.booleanField);
			Assert.assertEquals(result.booleanField, value.booleanValue());
		}
	}

	@Test
	public void testByte()
		throws Exception
	{
//		for (Number value : Arrays.<Number>asList((byte) 0, (short) 0, (int) 0, (long) 0, BigInteger.ZERO, min, max)) {
		for (final Number value : Arrays.asList((byte) 0, Byte.MIN_VALUE, Byte.MAX_VALUE)) {
			verifyProtocol(BYTE_FIELD, value, new WriteAction()
			{
				public void write(TProtocol protocol)
					throws TException
				{
					protocol.writeByte(value.byteValue());
				}
			});

			TTestStruct result = serializeAndRead(BYTE_FIELD, value);
			Assert.assertTrue(result.__isset.byteField);
			Assert.assertEquals(result.byteField, value.byteValue());
		}
	}

	@Test
	public void testI16()
		throws Exception
	{

//		for (Number value : Arrays.<Number>asList((byte) 0, (short) 0, (int) 0, (long) 0, BigInteger.ZERO, min, max)) {
		for (final Number value : Arrays.asList((short) 0, Short.MIN_VALUE, Short.MAX_VALUE)) {
			verifyProtocol(I16_FIELD, value, new WriteAction()
			{
				public void write(TProtocol protocol)
					throws TException
				{
					protocol.writeI16(value.shortValue());
				}
			});

			TTestStruct result = serializeAndRead(I16_FIELD, value);
			Assert.assertTrue(result.__isset.shortField);
			Assert.assertEquals(result.shortField, value.shortValue());
		}
	}

	@Test
	public void testI32()
		throws Exception
	{
//		for (Number value : Arrays.<Number>asList((byte) 0, (short) 0, (int) 0, (long) 0, BigInteger.ZERO, min, max)) {
		for (final Number value : Arrays.asList(0, Integer.MIN_VALUE, Integer.MAX_VALUE)) {
			verifyProtocol(I32_FIELD, value, new WriteAction()
			{
				public void write(TProtocol protocol)
					throws TException
				{
					protocol.writeI32(value.intValue());
				}
			});

			TTestStruct result = serializeAndRead(I32_FIELD, value);
			Assert.assertTrue(result.__isset.intField);
			Assert.assertEquals(result.intField, value.intValue());
		}
	}

	@Test
	public void testI64()
		throws Exception
	{
//		for (Number value : Arrays.<Number>asList((byte) 0, (short) 0, (int) 0, (long) 0, BigInteger.ZERO, min, max)) {
		for (final Number value : Arrays.asList(0L, Long.MIN_VALUE, Long.MAX_VALUE)) {
			verifyProtocol(I64_FIELD, value, new WriteAction()
			{
				public void write(TProtocol protocol)
					throws TException
				{
					protocol.writeI64(value.longValue());
				}
			});

			TTestStruct result = serializeAndRead(I64_FIELD, value);
			Assert.assertTrue(result.__isset.longField);
			Assert.assertEquals(result.longField, value.longValue());
		}
	}

	@Test
	public void testDouble()
		throws Exception
	{
//		for (Number value : Arrays.<Number>asList((byte) 0, (short) 0, (int) 0, (long) 0, BigInteger.ZERO, min, max)) {
		for (final Number value : Arrays.asList(0.0, Double.MIN_VALUE, Double.MAX_VALUE, Double.NaN,
		                                        Double.NEGATIVE_INFINITY,
		                                        Double.POSITIVE_INFINITY)) {
			verifyProtocol(DOUBLE_FIELD, value, new TestUtil.WriteAction()
			{
				public void write(TProtocol protocol)
					throws TException
				{
					protocol.writeDouble(value.doubleValue());
				}
			});

			TTestStruct result = serializeAndRead(DOUBLE_FIELD, value);
			Assert.assertTrue(result.__isset.doubleField);
			Assert.assertEquals(result.doubleField, value.doubleValue());
		}
	}

	@Test
	public void testString()
		throws Exception
	{
		final String value = "hello world";
		verifyProtocol(STRING_FIELD, value, new WriteAction()
		{
			public void write(TProtocol protocol)
				throws TException
			{
				protocol.writeString(eq(value));
			}
		});

		TTestStruct result = serializeAndRead(STRING_FIELD, value);
		Assert.assertTrue(result.__isset.stringField);
		Assert.assertEquals(result.stringField, value);
	}

	@Test
	public void testBinary()
		throws Exception
	{
		final byte[] value = "hello world".getBytes("UTF-8");
		verifyProtocol(BINARY_FIELD, value, new WriteAction()
		{
			public void write(TProtocol protocol)
				throws TException
			{
				protocol.writeBinary(value);
			}
		});

		TTestStruct result = serializeAndRead(BINARY_FIELD, value);
		Assert.assertTrue(result.__isset.binaryField);
		Assert.assertEquals(result.binaryField, value);
	}



	private TTestStruct serializeAndRead(Field field, Object value)
		throws TException
	{
		StructureType type = new StructureType(TTestStruct.class.getName(), field);

		Map<String, Object> data = new HashMap<String, Object>();
		data.put(field.getName(), value);

		Serializer serializer = new Serializer();
		serializer.bindToMap(type);

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		TProtocol protocol = new TBinaryProtocol(new TIOStreamTransport(out));
		serializer.serialize(data, type.getName(), protocol);

		return TestUtil.deserialize(out.toByteArray());
	}


	private void verifyProtocol(Field field, Object value,
	                            TestUtil.WriteAction action)
		throws TException
	{
		StructureType type = new StructureType(TestStruct.class.getName(), field);

		Serializer serializer = new Serializer();
		serializer.bindToMap(type);

		TProtocol protocol = createMock(TProtocol.class);
		protocol.writeStructBegin(equal(toTStruct(type)));
		protocol.writeFieldBegin(equal(toTField(field)));
		action.write(protocol);
		protocol.writeFieldEnd();
		protocol.writeFieldStop();
		protocol.writeStructEnd();
		EasyMock.replay(protocol);

		Map<String, Object> data = new HashMap<String, Object>();
		data.put(field.getName(), value);
		serializer.serialize(data, type.getName(), protocol);
		EasyMock.verify(protocol);
	}

	@Test
	public void testAll()
		throws Exception
	{
		StructureType nested = new StructureType(TNestedStruct.class.getName(),
		                                         new Field(BasicType.STRING, 1, "value", false));

		StructureType type = new StructureType(TTestStruct.class.getName(),
		                                       BOOLEAN_FIELD, BYTE_FIELD, I16_FIELD, I32_FIELD, I64_FIELD,
		                                       DOUBLE_FIELD, STRING_FIELD, BINARY_FIELD,
		                                       new Field(new ListType(BasicType.I32), 9, "listOfIntsField", false),
		                                       new Field(new SetType(BasicType.I32), 10, "setOfIntsField", false),
		                                       new Field(new MapType(BasicType.I32, BasicType.I32), 11,
		                                                 "mapOfIntsIntsField", false),
		                                       new Field(nested, 12, "structField", false),
		                                       new Field(new ListType(new ListType(BasicType.I32)), 13,
		                                                 "nestedListOfIntsField", false)
		);

		Map<String, Object> data = new HashMap<String, Object>();
		data.put(BOOLEAN_FIELD.getName(), true);
		data.put(BYTE_FIELD.getName(), Byte.MAX_VALUE);
		data.put(I16_FIELD.getName(), Short.MAX_VALUE);
		data.put(I32_FIELD.getName(), Integer.MAX_VALUE);
		data.put(I64_FIELD.getName(), Long.MAX_VALUE);
		data.put(DOUBLE_FIELD.getName(), Double.MAX_VALUE);
		data.put(STRING_FIELD.getName(), "Hello World");
		data.put(BINARY_FIELD.getName(), "Bye bye".getBytes("UTF-8"));
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
		Assert.assertEquals(result.booleanField, ((Boolean) data.get(BOOLEAN_FIELD.getName())).booleanValue());

		Assert.assertTrue(result.__isset.byteField);
		Assert.assertEquals(result.byteField, ((Byte) data.get(BYTE_FIELD.getName())).byteValue());

		Assert.assertTrue(result.__isset.shortField);
		Assert.assertEquals(result.shortField, ((Short) data.get(I16_FIELD.getName())).shortValue());

		Assert.assertTrue(result.__isset.intField);
		Assert.assertEquals(result.intField, ((Integer) data.get(I32_FIELD.getName())).intValue());

		Assert.assertTrue(result.__isset.longField);
		Assert.assertEquals(result.longField, ((Long) data.get(I64_FIELD.getName())).longValue());

		Assert.assertTrue(result.__isset.doubleField);
		Assert.assertEquals(result.doubleField, data.get(DOUBLE_FIELD.getName()));

		Assert.assertTrue(result.__isset.stringField);
		Assert.assertEquals(result.stringField, data.get(STRING_FIELD.getName()));

		Assert.assertTrue(result.__isset.binaryField);
		Assert.assertTrue(Arrays.equals(result.binaryField, (byte[]) data.get(BINARY_FIELD.getName())));

		Assert.assertTrue(result.__isset.listOfIntsField);
		Assert.assertEquals(result.listOfIntsField, (List) data.get("listOfIntsField"));

		Assert.assertTrue(result.__isset.setOfIntsField);
		Assert.assertEquals(result.setOfIntsField, (Set) data.get("setOfIntsField"));

		Assert.assertTrue(result.__isset.mapOfIntsIntsField);
		Assert.assertEquals(result.mapOfIntsIntsField, data.get("mapOfIntsIntsField"));

		Assert.assertTrue(result.__isset.structField);
		Assert.assertEquals(result.structField.value, nestedData.get("value"));

		Assert.assertTrue(result.__isset.nestedListOfIntsField);
		Assert.assertEquals(result.nestedListOfIntsField, (List) data.get("nestedListOfIntsField"));
	}

}
