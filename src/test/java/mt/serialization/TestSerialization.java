package mt.serialization;

import com.facebook.thrift.TException;
import com.facebook.thrift.protocol.TJSONProtocol;
import com.facebook.thrift.protocol.TProtocol;
import com.facebook.thrift.transport.TIOStreamTransport;
import mt.serialization.schema.BasicType;
import mt.serialization.schema.Field;
import mt.serialization.schema.ListType;
import mt.serialization.schema.MapType;
import mt.serialization.schema.Schema;
import mt.serialization.schema.SetType;
import mt.serialization.schema.Structure;
import mt.serialization.schema.StructureType;
import org.easymock.classextension.EasyMock;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TestSerialization
{
	private TProtocol protocol;

	@BeforeMethod(alwaysRun = true)
	public void setUp()
			throws Exception
	{
		protocol = new TJSONProtocol(new TIOStreamTransport(System.out));
	}

	@Test
	public void testSerializeBoolean()
			throws Exception
	{
		Structure struct = new Structure("someStruct",
		                                 Arrays.asList(
				                                 new Field(BasicType.BOOLEAN, 1, "value", false)
		                                 ));
		Schema schema = new Schema(struct);
		Serializer<Map<String, ?>> serializer = Serializer.newMapSerializer(schema);

		for (Boolean value : Arrays.asList(true, false, Boolean.TRUE, Boolean.FALSE)) {
			TProtocol protocol = EasyMock.createMock(TProtocol.class);
			protocol.writeStructBegin(struct.toTStruct());
			protocol.writeFieldBegin(struct.getField(1).toTField());
			protocol.writeBool(value);
			protocol.writeFieldEnd();
			protocol.writeFieldStop();
			protocol.writeStructEnd();
			EasyMock.replay(protocol);

			Map<String, Object> data = new HashMap<String, Object>();
			data.put("value", value);
			serializer.serialize(data, struct.getName(), protocol);
			EasyMock.verify(protocol);
		}
	}

	@Test
	public void testSerializeByte()
			throws Exception
	{
		Structure struct = new Structure("someStruct",
		                                 Arrays.asList(
				                                 new Field(BasicType.BYTE, 1, "value", false)
		                                 ));
		Schema schema = new Schema(struct);
		Serializer<Map<String, ?>> serializer = Serializer.newMapSerializer(schema);

		Number max = Byte.MAX_VALUE;
		Number min = Byte.MIN_VALUE;

		for (Number value : Arrays.<Number>asList((byte) 0, (short) 0, (int) 0, (long) 0, BigInteger.ZERO, min, max)) {
			TProtocol protocol = EasyMock.createMock(TProtocol.class);
			protocol.writeStructBegin(struct.toTStruct());
			protocol.writeFieldBegin(struct.getField(1).toTField());
			protocol.writeByte(value.byteValue());
			protocol.writeFieldEnd();
			protocol.writeFieldStop();
			protocol.writeStructEnd();
			EasyMock.replay(protocol);

			Map<String, Object> data = new HashMap<String, Object>();
			data.put("value", value);
			serializer.serialize(data, struct.getName(), protocol);
			EasyMock.verify(protocol);
		}

		testWithinBounds(struct, serializer, max, min);
	}

	@Test
	public void testSerializeI16()
			throws Exception
	{
		Structure struct = new Structure("someStruct",
		                                 Arrays.asList(
				                                 new Field(BasicType.I16, 1, "value", false)
		                                 ));
		Schema schema = new Schema(struct);
		Serializer<Map<String, ?>> serializer = Serializer.newMapSerializer(schema);

		Number max = Short.MAX_VALUE;
		Number min = Short.MIN_VALUE;

		for (Number value : Arrays.<Number>asList((byte) 0, (short) 0, (int) 0, (long) 0, BigInteger.ZERO, min, max)) {
			TProtocol protocol = EasyMock.createMock(TProtocol.class);
			protocol.writeStructBegin(struct.toTStruct());
			protocol.writeFieldBegin(struct.getField(1).toTField());
			protocol.writeI16(value.shortValue());
			protocol.writeFieldEnd();
			protocol.writeFieldStop();
			protocol.writeStructEnd();
			EasyMock.replay(protocol);

			Map<String, Object> data = new HashMap<String, Object>();
			data.put("value", value);
			serializer.serialize(data, struct.getName(), protocol);
			EasyMock.verify(protocol);
		}

		testWithinBounds(struct, serializer, max, min);
	}

	@Test
	public void testSerializeI32()
			throws Exception
	{
		Structure struct = new Structure("someStruct",
		                                 Arrays.asList(
				                                 new Field(BasicType.I32, 1, "value", false)
		                                 ));
		Schema schema = new Schema(struct);
		Serializer<Map<String, ?>> serializer = Serializer.newMapSerializer(schema);

		Number max = Integer.MAX_VALUE;
		Number min = Integer.MIN_VALUE;

		for (Number value : Arrays.<Number>asList((byte) 0, (short) 0, (int) 0, (long) 0, BigInteger.ZERO, min, max)) {
			TProtocol protocol = EasyMock.createMock(TProtocol.class);
			protocol.writeStructBegin(struct.toTStruct());
			protocol.writeFieldBegin(struct.getField(1).toTField());
			protocol.writeI32(value.intValue());
			protocol.writeFieldEnd();
			protocol.writeFieldStop();
			protocol.writeStructEnd();
			EasyMock.replay(protocol);

			Map<String, Object> data = new HashMap<String, Object>();
			data.put("value", value);
			serializer.serialize(data, struct.getName(), protocol);
			EasyMock.verify(protocol);
		}

		testWithinBounds(struct, serializer, max, min);
	}

	@Test
	public void testSerializeI64()
			throws Exception
	{
		Structure struct = new Structure("someStruct",
		                                 Arrays.asList(
				                                 new Field(BasicType.I64, 1, "value", false)
		                                 ));
		Schema schema = new Schema(struct);
		Serializer<Map<String, ?>> serializer = Serializer.newMapSerializer(schema);

		Number max = Long.MAX_VALUE;
		Number min = Long.MIN_VALUE;

		for (Number value : Arrays.<Number>asList((byte) 0, (short) 0, (int) 0, (long) 0, BigInteger.ZERO, min, max)) {
			TProtocol protocol = EasyMock.createMock(TProtocol.class);
			protocol.writeStructBegin(struct.toTStruct());
			protocol.writeFieldBegin(struct.getField(1).toTField());
			protocol.writeI64(value.longValue());
			protocol.writeFieldEnd();
			protocol.writeFieldStop();
			protocol.writeStructEnd();
			EasyMock.replay(protocol);

			Map<String, Object> data = new HashMap<String, Object>();
			data.put("value", value);
			serializer.serialize(data, struct.getName(), protocol);
			EasyMock.verify(protocol);
		}

		testWithinBounds(struct, serializer, max, min);
	}


	@Test
	public void testSerializeBinary()
			throws Exception
	{
		Structure struct = new Structure("someStruct",
		                                 Arrays.asList(
				                                 new Field(BasicType.BINARY, 1, "value", false)
		                                 ));
		Schema schema = new Schema(struct);
		Serializer<Map<String, ?>> serializer = Serializer.newMapSerializer(schema);

		byte[] buffer = new byte[10];

		TProtocol protocol = EasyMock.createMock(TProtocol.class);
		protocol.writeStructBegin(struct.toTStruct());
		protocol.writeFieldBegin(struct.getField(1).toTField());
		protocol.writeBinary(buffer);
		protocol.writeFieldEnd();
		protocol.writeFieldStop();
		protocol.writeStructEnd();
		EasyMock.replay(protocol);

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("value", buffer);
		serializer.serialize(data, struct.getName(), protocol);
		EasyMock.verify(protocol);

		// TODO: factor this code out
		EasyMock.reset(protocol);
		protocol.writeStructBegin(struct.toTStruct());
		protocol.writeFieldBegin(struct.getField(1).toTField());
		protocol.writeBinary(buffer);
		protocol.writeFieldEnd();
		protocol.writeFieldStop();
		protocol.writeStructEnd();
		EasyMock.replay(protocol);

		data.put("value", ByteBuffer.wrap(buffer));
		serializer.serialize(data, struct.getName(), protocol);
		EasyMock.verify(protocol);
	}

	@Test
	public void testSerializeString()
			throws Exception
	{
		Structure struct = new Structure("someStruct",
		                                 Arrays.asList(
				                                 new Field(BasicType.STRING, 1, "value", false)
		                                 ));
		Schema schema = new Schema(struct);
		Serializer<Map<String, ?>> serializer = Serializer.newMapSerializer(schema);

		TProtocol protocol = EasyMock.createMock(TProtocol.class);
		protocol.writeStructBegin(struct.toTStruct());
		protocol.writeFieldBegin(struct.getField(1).toTField());
		protocol.writeString("hello world");
		protocol.writeFieldEnd();
		protocol.writeFieldStop();
		protocol.writeStructEnd();
		EasyMock.replay(protocol);

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("value", "hello world");
		serializer.serialize(data, struct.getName(), protocol);
		EasyMock.verify(protocol);
	}

	public void testSerializeMap()
			throws Exception
	{

	}


	@Test
	public void testThriftMapSerialization()
			throws Exception
	{
		Structure child = new Structure("namespace.child", Arrays.asList(new Field(BasicType.STRING, 1, "field", false)));
		Structure parent = new Structure("namespace.parent",
		                                 Arrays.asList(
				                                 new Field(BasicType.STRING, 1, "aString", false),
				                                 new Field(BasicType.BOOLEAN, 2, "aBoolean", false),
				                                 new Field(BasicType.BYTE, 3, "aByte", false),
				                                 new Field(BasicType.DOUBLE, 4, "aDouble", false),
				                                 new Field(BasicType.I16, 5, "aI16", false),
				                                 new Field(BasicType.I32, 6, "aI32", false),
				                                 new Field(BasicType.I64, 7, "aI64", false),
				                                 new Field(new MapType(BasicType.I32, BasicType.STRING), 8, "aMap", false),
				                                 new Field(new ListType(BasicType.I32), 9, "aList", false),
				                                 new Field(new SetType(BasicType.STRING), 10, "aSet", false),
				                                 new Field(BasicType.BINARY, 11, "aBinary", false),
		                                         new Field(new StructureType("namespace.child"), 12, "aChild", false)
		                                 ));

		Schema schema = new Schema(parent, child);

		Map<String, Object> data = new HashMap<String, Object>();
		data.put("aString", "hello world");
		data.put("aBoolean", true);
		data.put("aByte", 1);
		data.put("aDouble", 3.14);
		data.put("aI16", 2);
		data.put("aI32", 3);
		data.put("aI64", 4l);
		data.put("aBinary", new byte[] { 1,2,3,4,5,6,8,9,10 });

		Map<Object, Object> map = new HashMap<Object, Object>();
		map.put(1, "blah");
		map.put(2, "bleh");
		data.put("aMap", map);

		List<Object> list = new ArrayList<Object>();
		list.add(1);
		list.add(2);
		list.add(3);
		data.put("aList", list);

		Set<Object> set = new HashSet<Object>();
		set.add("hello");
		set.add("foo");
		set.add("bar");
		data.put("aSet", set);

		Map<String, Object> childMap = new HashMap<String, Object>();
		childMap.put("field", "hello child");

		data.put("aChild", childMap);

		Serializer<Map<String, ?>> serializer = Serializer.newMapSerializer(schema);
//		serializer.serialize(data, parent.getName(), new TBinaryProtocol(new TIOStreamTransport(System.out)));
//		serializer.serialize(data, parent.getName(), new TSimpleJSONProtocol(new TIOStreamTransport(System.out)));
		serializer.serialize(data, parent.getName(), new TJSONProtocol(new TIOStreamTransport(System.out)));
	}

	@Test
	public void testReflectionSerializer()
			throws Exception
	{
		Schema schema = getSchema();

		Person person = new Person();
		person.setName("Martin Traverso");
		person.setHeight(175.6f);
		person.setAge(31);
		person.setPasswordHash(new byte[160]);

		Phone phone = new Phone();
		phone.setNumber("650-796-4453");
		phone.setType(1);

		person.setPhone(phone);

		Serializer<Object> serializer = Serializer.newReflectiveSerializer(schema);

		// introspects "person" by referring to the fields in ning.Person
		// if it encounters a fields of type "structure", it recurses:
		//    -> this.serialize(person.getPhone, field.getType().getName())
		// where field.getType().getName() would return "ning.Phone", for instance
		serializer.serialize(person, "ning.Person", protocol);
	}

	public void testDynamicCodeGenSerializer()
			throws Exception
	{
		Schema schema = getSchema();

		Person person = new Person();
		person.setName("Martin Traverso");
		person.setHeight(175.6f);
		person.setAge(31);
		person.setPasswordHash(new byte[160]);

		Phone phone = new Phone();
		phone.setNumber("650-796-4453");
		phone.setType(1);

		person.setPhone(phone);

		// Pass DataOut on call to serialize() so that we can reuse the instance and avoid
		// regeneration of code. I.e., this serializer does a lot of bookkeeping and is fairly heavyweight
		// Also, it should be stateless, save for the codegen caches so that it can be accessed concurrently
		Serializer<Object> serializer = Serializer.newDynamicCodeGenSerializer(schema);

		// discovery is done via reflection (a la ReflectionSerializer) & invokers are compiled
		// at serialization time to avoid reflective calls (sort of like method inlining)
		serializer.serialize(person, "ning.Person", protocol);
	}

	public void testMapSerializer()
			throws Exception
	{
		Schema schema = getSchema();

		Map<String, Object> entry = new HashMap<String, Object>();
		entry.put("name", "Martin Traverso");
		entry.put("height", 176.5);
		entry.put("age", 31);
		entry.put("password_hash", new byte[160]);

		Serializer<Map<String, ?>> serializer = Serializer.newMapSerializer(schema);
		serializer.serialize(entry, "ning.Person", protocol);
	}

	@Test
	public void testPreGeneratedSerializer()
			throws Exception
	{
		/*
			SerializablePerson person = new SerializablePerson();

			person.setName("Martin Traverso");
			person.setHeight(175.6f);
			person.setAge(31);
			person.setPasswordHash(new byte[160]);

			SerializalblePhone phone = new SerializablePhone();
			phone.setNumber("650-796-4453");
			phone.setType(1);

			person.setPhone(phone);

			person.serialize(new DataOutputStream(System.out));
		*/
	}

	private void testWithinBounds(Structure struct, Serializer<Map<String, ?>> serializer, Number max, Number min)
			throws TException
	{
		for (Number value : Arrays.asList(BigInteger.valueOf(max.longValue()).add(BigInteger.ONE), BigInteger.valueOf(min.longValue()).subtract(BigInteger.ONE))) {
			TProtocol protocol = EasyMock.createMock(TProtocol.class);
			try {
				Map<String, Object> data = new HashMap<String, Object>();
				data.put("value", value);
				serializer.serialize(data, struct.getName(), protocol);

				Assert.fail();
			}
			catch (IllegalArgumentException e) {
			}
		}
	}

	private Schema getSchema()
	{
		Schema result = new Schema();
//		result.add("ning.Person",
//		           new FieldDescriptor(1, Type.STRING, "name"),
//		           new FieldDescriptor(2, Type.BYTES, "password_hash"),
//		           new FieldDescriptor(4, Type.INTEGER, "age"),
//		           new FieldDescriptor(5, Type.DECIMAL, "height")
//		           );

		return result;
	}

}
