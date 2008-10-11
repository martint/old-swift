package mt.serialization;

import com.facebook.thrift.protocol.TBinaryProtocol;
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
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TestDeserialization
{
	@Test
	public void testDeserializeBoolean()
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
		data.put("aByte", Byte.MAX_VALUE);
		data.put("aDouble", Math.PI);
		data.put("aI16", Short.MAX_VALUE);
		data.put("aI32", Integer.MAX_VALUE);
		data.put("aI64", Long.MAX_VALUE);
		data.put("aBinary", new byte[] { 1,2,3,4,5,6,8,9,10 });

		Map<Object, Object> map = new HashMap<Object, Object>();
		map.put(1, "blah");
		map.put(2, "bleh");
		data.put("aMap", map);

		List<Integer> list = new ArrayList<Integer>();
		list.add(100);
		list.add(200);
		list.add(300);
		data.put("aList", list);

		Set<String> set = new HashSet<String>();
		set.add("hello");
		set.add("foo");
		set.add("bar");
		data.put("aSet", set);

		Map<String, String> childMap = new HashMap<String, String>();
		childMap.put("field", "hello child");

		data.put("aChild", childMap);

		// serialize
		Serializer<Map<String, ?>> serializer = Serializer.newMapSerializer(schema);
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		TProtocol outputProtocol = new TBinaryProtocol(new TIOStreamTransport(bao));
		serializer.serialize(data, parent.getName(), outputProtocol);
		
		System.out.println(new String(bao.toByteArray()));

		// deserialize
		TProtocol inputProtocol = new TBinaryProtocol(new TIOStreamTransport(new ByteArrayInputStream(bao.toByteArray())));
		Deserializer<Map<String, ?>> deserializer = Deserializer.newMapDeserializer(schema);
		Map<String, ?> result = deserializer.deserialize(parent.getName(), inputProtocol);
		System.out.println(result);
	}

	@Test
	public void testOther()
			throws Exception
	{
		Schema schema = getSchema();
		TProtocol protocol = getProtocol();

		Deserializer<Map<String, ?>> deserializer = Deserializer.newMapDeserializer(schema);
		Map<String, ?> entry = deserializer.deserialize("ning.Person", protocol);

		Serializer<Map<String, ?>> serializer = Serializer.newMapSerializer(schema);
		serializer.serialize(entry, "ning.Person", protocol);

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

	public TProtocol getProtocol()
	{
		throw new UnsupportedOperationException("Not yet implemented"); // TODO: implement this
	}

}
