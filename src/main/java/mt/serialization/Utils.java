package mt.serialization;

import com.facebook.thrift.TException;
import com.facebook.thrift.protocol.TList;
import com.facebook.thrift.protocol.TProtocol;
import com.facebook.thrift.protocol.TSet;
import com.facebook.thrift.protocol.TMap;
import mt.serialization.model.BasicType;
import mt.serialization.model.ListType;
import mt.serialization.model.MapType;
import mt.serialization.model.SetType;
import mt.serialization.model.StructureType;
import mt.serialization.model.Type;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;

public class Utils
{
	public static Object readStructure(Deserializer deserializer, TProtocol protocol, StructureType structureType)
		throws TException
	{
		return deserializer.deserialize(structureType.getName(), protocol);
	}
	
	public static List<?> readList(Deserializer deserializer, TProtocol protocol, ListType listType)
		throws TException
	{
		TList tlist = protocol.readListBegin();
		List<Object> result = new ArrayList<Object>(tlist.size);
		for (int i = 0; i < tlist.size; ++i) {
			result.add(read(deserializer, protocol, listType.getValueType()));
		}
		protocol.readListEnd();

		return result;
	}

	public static Set<?> readSet(Deserializer deserializer, TProtocol protocol, SetType setType)
		throws TException
	{
		TSet tset = protocol.readSetBegin();
		Set<Object> result = new HashSet<Object>(tset.size);
		for (int i = 0; i < tset.size; ++i) {
			result.add(read(deserializer, protocol, setType.getValueType()));
		}
		protocol.readSetEnd();

		return result;
	}

	public static Map<?, ?> readMap(Deserializer deserializer, TProtocol protocol, MapType mapType)
		throws TException
	{
		TMap tmap = protocol.readMapBegin();
		Map<Object, Object> result = new HashMap<Object, Object>(2 * tmap.size);
		for (int i = 0; i < tmap.size; ++i) {
			Object key = read(deserializer, protocol, mapType.getKeyType());
			Object value = read(deserializer, protocol, mapType.getValueType());
			result.put(key, value);
		}
		protocol.readMapEnd();

		return result;
	}

	public static Object read(Deserializer deserializer, TProtocol protocol, Type type)
		throws TException
	{
		if (type == BasicType.BOOLEAN) {
			return protocol.readBool();
		}
		else if (type == BasicType.BYTE) {
			return protocol.readByte();
		}
		else if (type == BasicType.I16) {
			return protocol.readI16();
		}
		else if (type == BasicType.I32) {
			return protocol.readI32();
		}
		else if (type == BasicType.I64) {
			return protocol.readI64();
		}
		else if (type == BasicType.DOUBLE) {
			return protocol.readDouble();
		}
		else if (type == BasicType.STRING) {
			return protocol.readString();
		}
		else if (type == BasicType.BINARY) {
			return protocol.readBinary();
		}
		else if (type instanceof ListType) {
			return readList(deserializer, protocol, (ListType) type);
		}
		else if (type instanceof SetType) {
			return readSet(deserializer, protocol, (SetType) type);
		}
		else if (type instanceof MapType) {
			return readMap(deserializer, protocol, (MapType) type);
		}
		else if (type instanceof StructureType) {
			return readStructure(deserializer, protocol, (StructureType) type);
		}
		else {
			throw new UnsupportedOperationException(String.format("Unsupported type %s", type.getSignature()));
		}
	}
}
