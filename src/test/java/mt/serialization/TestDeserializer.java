package mt.serialization;

import com.facebook.thrift.protocol.TBinaryProtocol;
import com.facebook.thrift.protocol.TProtocol;
import com.facebook.thrift.transport.TIOStreamTransport;
import mt.serialization.schema.BasicType;
import mt.serialization.schema.Field;
import mt.serialization.schema.ListType;
import mt.serialization.schema.MapType;
import mt.serialization.schema.SetType;
import mt.serialization.schema.StructureType;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class TestDeserializer
{
	@Test
public void testSimple()
	throws Exception
{
	StructureType nestedType = new StructureType("namespace.nested",
	                                             Arrays.asList(new Field(BasicType.STRING, 1, "value", false)));

	StructureType simpleType = new StructureType("namespace.simple",
	                                        Arrays.asList(
		                                        new Field(BasicType.BOOLEAN, 1, "aBool", false),
		                                        new Field(BasicType.BYTE, 2, "aByte", false),
		                                        new Field(BasicType.I16, 3, "aI16", false),
		                                        new Field(BasicType.I32, 4, "aI32", false),
		                                        new Field(BasicType.I64, 5, "aI64", false),
		                                        new Field(BasicType.DOUBLE, 6, "aDouble", false),
		                                        new Field(BasicType.BINARY, 7, "aBinary", false),
		                                        new Field(BasicType.STRING, 8, "aString", false)
//			                                        new Field(nestedType, 9, "aNested", false)
	                                        ));

	Map<String, Object> data = new HashMap<String, Object>();
	data.put("aBool", true);
	data.put("aByte", Byte.MAX_VALUE);
	data.put("aI16", (short) 1);
	data.put("aI32", Integer.MAX_VALUE);
	data.put("aI64", Long.MAX_VALUE);
	data.put("aDouble", Math.PI);
	data.put("aString", "hello world");
	data.put("aBinary", new byte[] { 1, 2, 3, 4 });

	Map<String, String> nested = new HashMap<String, String>();
	nested.put("value", "hello nested");

	data.put("aNested", nested);

	// serialize
	Serializer<Map<String, ?>> serializer = new MapSerializer(simpleType, nestedType);
	ByteArrayOutputStream bao = new ByteArrayOutputStream();
	TProtocol outputProtocol = new TBinaryProtocol(new TIOStreamTransport(bao));
	serializer.serialize(data, simpleType.getName(), outputProtocol);


	System.out.println(new String(bao.toByteArray()));

	// deserialize to javabean
	Deserializer deserializerToJavabean = new Deserializer(false);
	deserializerToJavabean.bind(simpleType, Simple.class);
	deserializerToJavabean.bind(nestedType, Nested.class);

	// deserialize to map
	Deserializer deserializerToMap = new Deserializer(false);
	deserializerToMap.bindToMap(simpleType);
	deserializerToMap.bindToMap(nestedType);

	Deserializer compiledDeserializerToMBean = new Deserializer(true);
	compiledDeserializerToMBean.bind(simpleType, Simple.class);

	TSimple tsimple = new TSimple();
	TSimpleWithMethods tsimpleWithMethods = new TSimpleWithMethods();
	int max = 1;
	int warmup = 1;

	long dynamicToMap = 0;
	long dynamicToBean = 0;
	long thrift = 0;
	long thriftWithMethods = 0;
	long compiledToMBean = 0;
	
	for (int i = 0; i < max; ++i) {
		TProtocol protocol;

		protocol = new TBinaryProtocol(new TIOStreamTransport(new ByteArrayInputStream(bao.toByteArray())));
		if (i >= warmup) {
			dynamicToMap -= System.nanoTime();
		}
		deserializerToMap.deserialize(simpleType.getName(), protocol);
		if (i >= warmup) {
			dynamicToMap += System.nanoTime();
		}

		protocol = new TBinaryProtocol(new TIOStreamTransport(new ByteArrayInputStream(bao.toByteArray())));
		if (i >= warmup) {
			dynamicToBean -= System.nanoTime();
		}
		deserializerToJavabean.deserialize(simpleType.getName(), protocol);
		if (i >= warmup) {
			dynamicToBean += System.nanoTime();
		}

		protocol = new TBinaryProtocol(new TIOStreamTransport(new ByteArrayInputStream(bao.toByteArray())));
		if (i >= warmup) {
			thrift -= System.nanoTime();
		}
		tsimple.read(protocol);
		if (i >= warmup) {
			thrift += System.nanoTime();
		}

		protocol = new TBinaryProtocol(new TIOStreamTransport(new ByteArrayInputStream(bao.toByteArray())));
		if (i >= warmup) {
			thriftWithMethods -= System.nanoTime();
		}
		tsimpleWithMethods.read(protocol);
		if (i >= warmup) {
			thriftWithMethods += System.nanoTime();
		}

		protocol = new TBinaryProtocol(new TIOStreamTransport(new ByteArrayInputStream(bao.toByteArray())));
		if (i >= warmup) {
			compiledToMBean -= System.nanoTime();
		}
		compiledDeserializerToMBean.deserialize(simpleType.getName(), protocol);
		if (i >= warmup) {
			compiledToMBean += System.nanoTime();
		}



	}
	System.out.println(max - warmup + ",\n" +
	                   "\tdynamic->map:     " + dynamicToMap + " ms\n" +
	                   "\tdynamic->bean:    " + dynamicToBean + " ms\n" +
	                   "\tthrift:           " + thrift + " ms\n" +
	                   "\tthrift w/ methods:" + thriftWithMethods + " ms\n" +
	                   "\tcompiled->mbean:  " + compiledToMBean + " ms");
}



	@Test
	public void testDeserializer()
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
		                                         new Field(child, 12, "aChild", false),
		                                         new Field(new ListType(new ListType(BasicType.I32)), 13, "aListOfLists", false)
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
		Serializer<Map<String, ?>> serializer = new MapSerializer(parent, child);
		ByteArrayOutputStream bao = new ByteArrayOutputStream();
		TProtocol outputProtocol = new TBinaryProtocol(new TIOStreamTransport(bao));
		serializer.serialize(data, parent.getName(), outputProtocol);


		System.out.println(new String(bao.toByteArray()));

		// deserialize to javabean
		Deserializer deserializerToJavabean = new Deserializer();
		deserializerToJavabean.bind(parent, Parent.class);
		deserializerToJavabean.bind(child, Simple.class);
		Parent parentObject = deserializerToJavabean.deserialize(parent.getName(), new TBinaryProtocol(new TIOStreamTransport(new ByteArrayInputStream(bao.toByteArray()))));
		System.out.println(parentObject);

		// deserialize to map
		Deserializer deserializerToMap = new Deserializer();
		deserializerToMap.bind(parent, LinkedHashMap.class);
		deserializerToMap.bind(child, LinkedHashMap.class);
		Map parentMap = deserializerToMap.deserialize(parent.getName(), new TBinaryProtocol(new TIOStreamTransport(new ByteArrayInputStream(bao.toByteArray()))));
		System.out.println(parentMap);


	}


}
