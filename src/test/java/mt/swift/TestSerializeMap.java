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

import com.facebook.thrift.TException;
import com.facebook.thrift.protocol.TBinaryProtocol;
import com.facebook.thrift.protocol.TProtocol;
import com.facebook.thrift.transport.TIOStreamTransport;
import static mt.swift.TestUtil.*;
import mt.swift.model.BasicType;
import mt.swift.model.Field;
import mt.swift.model.StructureType;
import static org.easymock.EasyMock.eq;
import org.easymock.classextension.EasyMock;
import static org.easymock.classextension.EasyMock.createMock;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TestSerializeMap
{
	// TODO: test composite structures

	@Test
	public void testBoolean()
		throws Exception
	{
		for (final Boolean value : Arrays.asList(true, false)) {
			verifyProtocol(Fields.BOOLEAN_FIELD, value, new WriteAction()
			{
				public void write(TProtocol protocol)
					throws TException
				{
					protocol.writeBool(value);
				}
			});

			TTestStruct result = serializeAndRead(Fields.BOOLEAN_FIELD, value);
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
			verifyProtocol(Fields.BYTE_FIELD, value, new WriteAction()
			{
				public void write(TProtocol protocol)
					throws TException
				{
					protocol.writeByte(value.byteValue());
				}
			});

			TTestStruct result = serializeAndRead(Fields.BYTE_FIELD, value);
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
			verifyProtocol(Fields.I16_FIELD, value, new WriteAction()
			{
				public void write(TProtocol protocol)
					throws TException
				{
					protocol.writeI16(value.shortValue());
				}
			});

			TTestStruct result = serializeAndRead(Fields.I16_FIELD, value);
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
			verifyProtocol(Fields.I32_FIELD, value, new WriteAction()
			{
				public void write(TProtocol protocol)
					throws TException
				{
					protocol.writeI32(value.intValue());
				}
			});

			TTestStruct result = serializeAndRead(Fields.I32_FIELD, value);
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
			verifyProtocol(Fields.I64_FIELD, value, new WriteAction()
			{
				public void write(TProtocol protocol)
					throws TException
				{
					protocol.writeI64(value.longValue());
				}
			});

			TTestStruct result = serializeAndRead(Fields.I64_FIELD, value);
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
			verifyProtocol(Fields.DOUBLE_FIELD, value, new TestUtil.WriteAction()
			{
				public void write(TProtocol protocol)
					throws TException
				{
					protocol.writeDouble(value.doubleValue());
				}
			});

			TTestStruct result = serializeAndRead(Fields.DOUBLE_FIELD, value);
			Assert.assertTrue(result.__isset.doubleField);
			Assert.assertEquals(result.doubleField, value.doubleValue());
		}
	}

	@Test
	public void testString()
		throws Exception
	{
		final String value = "hello world";
		verifyProtocol(Fields.STRING_FIELD, value, new WriteAction()
		{
			public void write(TProtocol protocol)
				throws TException
			{
				protocol.writeString(eq(value));
			}
		});

		TTestStruct result = serializeAndRead(Fields.STRING_FIELD, value);
		Assert.assertTrue(result.__isset.stringField);
		Assert.assertEquals(result.stringField, value);
	}

	@Test
	public void testBinary()
		throws Exception
	{
		final byte[] value = "hello world".getBytes("UTF-8");
		verifyProtocol(Fields.BINARY_FIELD, value, new WriteAction()
		{
			public void write(TProtocol protocol)
				throws TException
			{
				protocol.writeBinary(value);
			}
		});

		TTestStruct result = serializeAndRead(Fields.BINARY_FIELD, value);
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
		                                       Fields.BOOLEAN_FIELD, Fields.BYTE_FIELD, Fields.I16_FIELD,
		                                       Fields.I32_FIELD, Fields.I64_FIELD,
		                                       Fields.DOUBLE_FIELD, Fields.STRING_FIELD, Fields.BINARY_FIELD,
		                                       Fields.LIST_OF_INTS_FIELD,
		                                       Fields.SET_OF_INTS_FIELD,
		                                       Fields.MAP_OF_INTS_INTS_FIELD,
		                                       new Field(nested, 12, "structField", false),
		                                       Fields.NESTED_LIST_OF_INTS_FIELD,
		                                       Fields.MAP_OF_INTS_STRINGS_FIELD
		);

		Map<String, Object> data = new HashMap<String, Object>();
		data.put(Fields.BOOLEAN_FIELD.getName(), true);
		data.put(Fields.BYTE_FIELD.getName(), Byte.MAX_VALUE);
		data.put(Fields.I16_FIELD.getName(), Short.MAX_VALUE);
		data.put(Fields.I32_FIELD.getName(), Integer.MAX_VALUE);
		data.put(Fields.I64_FIELD.getName(), Long.MAX_VALUE);
		data.put(Fields.DOUBLE_FIELD.getName(), Double.MAX_VALUE);
		data.put(Fields.STRING_FIELD.getName(), "Hello World");
		data.put(Fields.BINARY_FIELD.getName(), "Bye bye".getBytes("UTF-8"));
		data.put(Fields.LIST_OF_INTS_FIELD.getName(), Arrays.asList(Integer.MIN_VALUE, Integer.MAX_VALUE));
		data.put(Fields.SET_OF_INTS_FIELD.getName(),
		         new HashSet<Integer>(Arrays.asList(Integer.MIN_VALUE, Integer.MAX_VALUE)));

		Map<String, Object> nestedData = new HashMap<String, Object>();
		nestedData.put("value", "bye bye");
		data.put("structField", nestedData);

		data.put(Fields.NESTED_LIST_OF_INTS_FIELD.getName(), Arrays.asList(Arrays.asList(Integer.MAX_VALUE)));

		Map<Integer, Integer> mapOfIntsInts = new LinkedHashMap<Integer, Integer>();
		mapOfIntsInts.put(1, Integer.MIN_VALUE);
		mapOfIntsInts.put(2, Integer.MAX_VALUE);
		data.put(Fields.MAP_OF_INTS_INTS_FIELD.getName(), mapOfIntsInts);

		Map<Integer, String> mapOfIntsStrings = new LinkedHashMap<Integer, String>();
		mapOfIntsStrings.put(1, "hello");
		mapOfIntsStrings.put(2, "world");
		data.put(Fields.MAP_OF_INTS_STRINGS_FIELD.getName(), mapOfIntsStrings);

		Serializer serializer = new Serializer();
		serializer.bindToMap(type);
		serializer.bindToMap(nested);

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		TProtocol protocol = new TBinaryProtocol(new TIOStreamTransport(out));
		serializer.serialize(data, type.getName(), protocol);

		TTestStruct result = TestUtil.deserialize(out.toByteArray());

		Assert.assertTrue(result.__isset.booleanField);
		Assert.assertEquals(result.booleanField, ((Boolean) data.get(Fields.BOOLEAN_FIELD.getName())).booleanValue());

		Assert.assertTrue(result.__isset.byteField);
		Assert.assertEquals(result.byteField, ((Byte) data.get(Fields.BYTE_FIELD.getName())).byteValue());

		Assert.assertTrue(result.__isset.shortField);
		Assert.assertEquals(result.shortField, ((Short) data.get(Fields.I16_FIELD.getName())).shortValue());

		Assert.assertTrue(result.__isset.intField);
		Assert.assertEquals(result.intField, ((Integer) data.get(Fields.I32_FIELD.getName())).intValue());

		Assert.assertTrue(result.__isset.longField);
		Assert.assertEquals(result.longField, ((Long) data.get(Fields.I64_FIELD.getName())).longValue());

		Assert.assertTrue(result.__isset.doubleField);
		Assert.assertEquals(result.doubleField, data.get(Fields.DOUBLE_FIELD.getName()));

		Assert.assertTrue(result.__isset.stringField);
		Assert.assertEquals(result.stringField, data.get(Fields.STRING_FIELD.getName()));

		Assert.assertTrue(result.__isset.binaryField);
		Assert.assertTrue(Arrays.equals(result.binaryField, (byte[]) data.get(Fields.BINARY_FIELD.getName())));

		Assert.assertTrue(result.__isset.listOfIntsField);
		Assert.assertEquals(result.listOfIntsField, (List) data.get(Fields.LIST_OF_INTS_FIELD.getName()));

		Assert.assertTrue(result.__isset.setOfIntsField);
		Assert.assertEquals(result.setOfIntsField, (Set) data.get(Fields.SET_OF_INTS_FIELD.getName()));

		Assert.assertTrue(result.__isset.mapOfIntsIntsField);
		Assert.assertEquals(result.mapOfIntsIntsField, data.get(Fields.MAP_OF_INTS_INTS_FIELD.getName()));

		Assert.assertTrue(result.__isset.mapOfIntsStringsField);
		Assert.assertEquals(result.mapOfIntsStringsField, data.get(Fields.MAP_OF_INTS_STRINGS_FIELD.getName()));

		Assert.assertTrue(result.__isset.structField);
		Assert.assertEquals(result.structField.value, nestedData.get("value"));

		Assert.assertTrue(result.__isset.nestedListOfIntsField);
		Assert.assertEquals(result.nestedListOfIntsField, (List) data.get(Fields.NESTED_LIST_OF_INTS_FIELD.getName()));
	}
}
