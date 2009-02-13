package mt.swift;

import com.facebook.thrift.TException;
import com.facebook.thrift.protocol.TField;
import com.facebook.thrift.protocol.TList;
import com.facebook.thrift.protocol.TMap;
import com.facebook.thrift.protocol.TProtocol;
import com.facebook.thrift.protocol.TSet;
import com.facebook.thrift.protocol.TType;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class SchemalessDeserializer
	implements StructureDeserializer<Map<Short, Object>>
{
	public Map<Short, Object> deserialize(Deserializer deserializer, TProtocol protocol)
		throws TException
	{
		return readStruct(protocol);
	}

	private Object read(TProtocol protocol, int type)
		throws TException
	{
		switch (type) {
			case TType.BOOL:
				return protocol.readBool();
			case TType.BYTE:
				return protocol.readByte();
			case TType.I16:
				return protocol.readI16();
			case TType.I32:
				return protocol.readI32();
			case TType.I64:
				return protocol.readI64();
			case TType.DOUBLE:
				return protocol.readDouble();
			case TType.STRING:
				return protocol.readBinary(); // TODO need to read string when thrift starts distinguishing between string & binary at protocol level
			case TType.LIST:
				return readList(protocol);
			case TType.SET:
				return readSet(protocol);
			case TType.MAP:
				return readMap(protocol);
			case TType.STRUCT:
				return readStruct(protocol);
		}

		throw new RuntimeException(String.format("Unknown type %d", type));
	}

	private Map<Short, Object> readStruct(TProtocol protocol)
		throws TException
	{
		Map<Short, Object> result = new HashMap<Short, Object>(1);

		protocol.readStructBegin();

		TField field = protocol.readFieldBegin();

		while (field.type != TType.STOP) {
			result.put(field.id, read(protocol, field.type));
			protocol.readFieldEnd();
			field = protocol.readFieldBegin();
		}

		protocol.readStructEnd();

		return result;
	}

	private List<Object> readList(TProtocol protocol)
		throws TException
	{
		List<Object> result = new ArrayList<Object>(1);
		TList list = protocol.readListBegin();
		for (int i = 0; i < list.size; ++i) {
			result.add(read(protocol, list.elemType));
		}
		protocol.readListEnd();

		return result;
	}

	private Set<Object> readSet(TProtocol protocol)
		throws TException
	{
		Set<Object> result = new HashSet<Object>(1);
		TSet set = protocol.readSetBegin();
		for (int i = 0; i < set.size; ++i) {
			result.add(read(protocol, set.elemType));
		}
		protocol.readSetEnd();

		return result;
	}

	private Map<Object, Object> readMap(TProtocol protocol)
		throws TException
	{
		Map<Object, Object> result = new HashMap<Object, Object>(1);
		TMap map = protocol.readMapBegin();
		for (int i = 0; i < map.size; ++i) {
			Object key = read(protocol, map.keyType);
			Object value = read(protocol, map.valueType);
			result.put(key, value);
		}
		protocol.readMapEnd();

		return result;
	}

}
