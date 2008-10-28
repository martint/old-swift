package mt.serialization;

import com.facebook.thrift.TException;
import com.facebook.thrift.protocol.TList;
import com.facebook.thrift.protocol.TProtocol;
import com.facebook.thrift.protocol.TSet;
import com.facebook.thrift.protocol.TMap;
import mt.serialization.schema.BasicType;
import mt.serialization.schema.ListType;
import mt.serialization.schema.MapType;
import mt.serialization.schema.SetType;
import mt.serialization.schema.StructureType;
import mt.serialization.schema.Type;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;

public class Utils<T>
{
	protected List<?> readList(Deserializer deserializer, TProtocol protocol, Type valueType)
		throws TException
	{
		TList tlist = protocol.readListBegin();
		List<Object> result = new ArrayList<Object>(tlist.size);
		for (int i = 0; i < tlist.size; ++i) {
			result.add(read(deserializer, protocol, valueType));
		}
		protocol.readListEnd();

		return result;
	}

	protected Set<?> readSet(Deserializer deserializer, TProtocol protocol, Type valueType)
		throws TException
	{
		TSet tset = protocol.readSetBegin();
		Set<Object> result = new HashSet<Object>(tset.size);
		for (int i = 0; i < tset.size; ++i) {
			result.add(read(deserializer, protocol, valueType));
		}
		protocol.readSetEnd();

		return result;
	}

	protected Map<?, ?> readMap(Deserializer deserializer, TProtocol protocol, Type keyType, Type valueType)
		throws TException
	{
		TMap tmap = protocol.readMapBegin();
		Map<Object, Object> result = new HashMap<Object, Object>(2 * tmap.size);
		for (int i = 0; i < tmap.size; ++i) {
			Object key = read(deserializer, protocol, keyType);
			Object value = read(deserializer, protocol, valueType);
			result.put(key, value);
		}
		protocol.readMapEnd();

		return result;
	}

	protected Object read(Deserializer deserializer, TProtocol protocol, Type type)
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
			return readList(deserializer, protocol, ((ListType) type).getValueType());
		}
		else if (type instanceof SetType) {
			return readSet(deserializer, protocol, ((SetType) type).getValueType());
		}
		else if (type instanceof MapType) {
			MapType mapType = (MapType) type;
			return readMap(deserializer, protocol, mapType.getKeyType(), mapType.getValueType());
		}
		else if (type instanceof StructureType) {
			StructureType structureType = (StructureType) type;
			return deserializer.deserialize(structureType.getName(), protocol);
		}
		else {
			throw new UnsupportedOperationException(String.format("Unsupported type %s", type.getSignature()));
		}
	}
}
