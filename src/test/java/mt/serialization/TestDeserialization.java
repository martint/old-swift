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
		StructureType child = new StructureType("namespace.child", Arrays.asList(new Field(BasicType.STRING, 1, "field", false)));
		StructureType parent = new StructureType("namespace.parent",
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
		                                         new Field(child, 12, "aChild", false)
		                                 ));

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
		MapSerializer serializer = new MapSerializer(parent, child);
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		TProtocol outputProtocol = new TBinaryProtocol(new TIOStreamTransport(bao));
		serializer.serialize(data, parent.getName(), outputProtocol);
		
		System.out.println(new String(bao.toByteArray()));

		// deserialize
		TProtocol inputProtocol = new TBinaryProtocol(new TIOStreamTransport(new ByteArrayInputStream(bao.toByteArray())));
//		MapDeserializer deserializer = new MapDeserializer(model);
//		Map<String, ?> result = deserializer.deserialize(parent.getName(), inputProtocol);
//		System.out.println(result);
	}




	public TProtocol getProtocol()
	{
		throw new UnsupportedOperationException("Not yet implemented"); // TODO: implement this
	}

}
