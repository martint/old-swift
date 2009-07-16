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

import static mt.swift.TestUtil.*;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TIOStreamTransport;
import mt.swift.model.BasicType;
import mt.swift.model.Field;
import mt.swift.model.ListType;
import mt.swift.model.MapType;
import mt.swift.model.SetType;
import mt.swift.model.StructureType;
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
	// TODO: test composite structures
	
	@Test
	public void testBoolean()
		throws Exception
	{
		Field field = Fields.BOOLEAN_FIELD;
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
			Assert.assertTrue(result.isSetBooleanField());
			Assert.assertEquals(result.isBooleanField(), value.booleanValue());
		}
	}

	@Test
	public void testByte()
		throws Exception
	{
		Field field = Fields.BYTE_FIELD;
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
			Assert.assertTrue(result.isSetByteField());
			Assert.assertEquals(result.getByteField(), value.byteValue());
		}
	}

	@Test
	public void testShort()
		throws Exception
	{
		Field field = Fields.I16_FIELD;
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
			Assert.assertTrue(result.isSetShortField());
			Assert.assertEquals(result.getShortField(), value.shortValue());
		}
	}

	@Test
	public void testI32()
		throws Exception
	{
		Field field = Fields.I32_FIELD;
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
			Assert.assertTrue(result.isSetIntField());
			Assert.assertEquals(result.getIntField(), value.intValue());
		}
	}


	@Test
	public void testI64()
		throws Exception
	{
		Field field = Fields.I64_FIELD;
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
			Assert.assertTrue(result.isSetLongField());
			Assert.assertEquals(result.getLongField(), value.longValue());
		}
	}

	@Test
	public void testDouble()
		throws Exception
	{
		Field field = Fields.DOUBLE_FIELD;
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
			Assert.assertTrue(result.isSetDoubleField());
			Assert.assertEquals(result.getDoubleField(), value.doubleValue());
		}
	}

	@Test
	public void testBinary()
		throws Exception
	{
		Field field = Fields.BINARY_FIELD;

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
		Assert.assertTrue(result.isSetBinaryField());
		Assert.assertEquals(result.getBinaryField(), value);
	}

	@Test
	public void testString()
		throws Exception
	{
		Field field = Fields.STRING_FIELD;

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
		Assert.assertTrue(result.isSetStringField());
		Assert.assertEquals(result.getStringField(), value);
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
		                                                 "nestedListOfIntsField", false),
		                                       Fields.MAP_OF_INTS_STRINGS_FIELD);

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

		Map<Integer, String> mapOfIntsStrings = new LinkedHashMap<Integer, String>();
		mapOfIntsStrings.put(1, "hello");
		mapOfIntsStrings.put(2, "world");
		data.setMapOfIntsStringsField(mapOfIntsStrings);

		Serializer serializer = new Serializer();
		serializer.bind(type, TestStruct.class);
		serializer.bind(nested, NestedStruct.class);

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		TProtocol protocol = new TBinaryProtocol(new TIOStreamTransport(out));
		serializer.serialize(data, type.getName(), protocol);

		TTestStruct result = TestUtil.deserialize(out.toByteArray());

		Assert.assertTrue(result.isSetBooleanField());
		Assert.assertEquals(result.isBooleanField(), data.isBooleanField());

		Assert.assertTrue(result.isSetByteField());
		Assert.assertEquals(result.getByteField(), data.getByteField());

		Assert.assertTrue(result.isSetShortField());
		Assert.assertEquals(result.getShortField(), data.getShortField());

		Assert.assertTrue(result.isSetIntField());
		Assert.assertEquals(result.getIntField(), data.getIntField());

		Assert.assertTrue(result.isSetLongField());
		Assert.assertEquals(result.getLongField(), data.getLongField());

		Assert.assertTrue(result.isSetDoubleField());
		Assert.assertEquals(result.getDoubleField(), data.getDoubleField());

		Assert.assertTrue(result.isSetStringField());
		Assert.assertEquals(result.getStringField(), data.getStringField());

		Assert.assertTrue(result.isSetBinaryField());
		Assert.assertTrue(Arrays.equals(result.getBinaryField(), data.getBinaryField()));

		Assert.assertTrue(result.isSetListOfIntsField());
		Assert.assertEquals(result.getListOfIntsField(), data.getListOfIntsField());

		Assert.assertTrue(result.isSetSetOfIntsField());
		Assert.assertEquals(result.getSetOfIntsField(), data.getSetOfIntsField());

		Assert.assertTrue(result.isSetMapOfIntsIntsField());
		Assert.assertEquals(result.getMapOfIntsIntsField(), data.getMapOfIntsIntsField());

		Assert.assertTrue(result.isSetMapOfIntsStringsField());
		Assert.assertEquals(result.getMapOfIntsStringsField(), data.getMapOfIntsStringsField());

		Assert.assertTrue(result.isSetStructField());
		Assert.assertEquals(result.getStructField().getValue(), nestedData.getValue());

		Assert.assertTrue(result.isSetNestedListOfIntsField());
		Assert.assertEquals(result.getNestedListOfIntsField(), data.getNestedListOfIntsField());
	}
}
