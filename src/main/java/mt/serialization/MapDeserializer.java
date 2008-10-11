package mt.serialization;

import com.facebook.thrift.TException;
import com.facebook.thrift.protocol.TField;
import com.facebook.thrift.protocol.TList;
import com.facebook.thrift.protocol.TMap;
import com.facebook.thrift.protocol.TProtocol;
import com.facebook.thrift.protocol.TProtocolUtil;
import com.facebook.thrift.protocol.TSet;
import com.facebook.thrift.protocol.TType;
import mt.serialization.schema.BasicType;
import mt.serialization.schema.Field;
import mt.serialization.schema.ListType;
import mt.serialization.schema.MapType;
import mt.serialization.schema.Schema;
import mt.serialization.schema.SetType;
import mt.serialization.schema.Structure;
import mt.serialization.schema.StructureType;
import mt.serialization.schema.Type;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

class MapDeserializer
		extends Deserializer<Map<String, ?>>
{
	public MapDeserializer(Schema schema)
	{
		super(schema);
	}

	public Map<String, ?> deserialize(String structName, TProtocol protocol)
			throws TException
	{
		Structure structure = getSchema().getStructure(structName);
		protocol.readStructBegin();

		Map<String, Object> result = new HashMap<String, Object>();

		protocol.readStructBegin();
		while (true) {
			TField tfield = protocol.readFieldBegin();
			if (tfield.type == TType.STOP) {
				break;
			}
			Field field = structure.getField(tfield.id);

			if (field == null) {
				TProtocolUtil.skip(protocol, tfield.type);
			}
			else {
				result.put(field.getName(), read(protocol, field.getType(), tfield.type));
			}
			protocol.readFieldEnd();
		}
		protocol.readStructEnd();

		return result;
	}

	private Object read(TProtocol protocol, Type type, byte ttype)
			throws TException
	{
		Object result = null;
		if (type == BasicType.BOOLEAN && ttype == TType.BOOL) {
			result = protocol.readBool();
		}
		else if (type == BasicType.BYTE && ttype == TType.BYTE) {
			result = protocol.readByte();
		}
		else if (type == BasicType.I16 && ttype == TType.I16) {
			result = protocol.readI16();
		}
		else if (type == BasicType.I32 && ttype == TType.I32) {
			result = protocol.readI32();
		}
		else if (type == BasicType.I64 && ttype == TType.I64) {
			result = protocol.readI64();
		}
		else if (type == BasicType.STRING && ttype == TType.STRING) {
			result = protocol.readString();
		}
		else if (type == BasicType.BINARY && ttype == TType.STRING) {
			result = protocol.readBinary();
		}
		else if (type == BasicType.DOUBLE && ttype == TType.DOUBLE) {
			result = protocol.readDouble();
		}
		else if (type instanceof ListType && ttype == TType.LIST) {
			ListType listType = (ListType) type;
			TList tlist = protocol.readListBegin();
			List<Object> list = new ArrayList<Object>(tlist.size);
			for (int i = 0; i < tlist.size; ++i) {
				Object value = read(protocol, listType.getValueType(), listType.getValueType().getTType());
				list.add(value);
			}
			protocol.readListEnd();

			result = list;
		}
		else if (type instanceof SetType && ttype == TType.SET) {
			SetType setType = (SetType) type;
			TSet tset = protocol.readSetBegin();
			Set<Object> set = new HashSet<Object>(tset.size);
			for (int i = 0; i < tset.size; ++i) {
				Object value = read(protocol, setType.getValueType(), setType.getValueType().getTType());
				set.add(value);
			}
			protocol.readSetEnd();

			result = set;
		}
		else if (type instanceof MapType && ttype == TType.MAP) {
			MapType mapType = (MapType) type;
			TMap tmap = protocol.readMapBegin();
			Map<Object, Object> map = new HashMap<Object, Object>(2 * tmap.size);
			for (int i = 0; i < tmap.size; ++i) {
				Object key = read(protocol, mapType.getKeyType(), mapType.getKeyType().getTType());
				Object value = read(protocol, mapType.getValueType(), mapType.getValueType().getTType());
				map.put(key, value);
			}
			protocol.readMapEnd();

			result = map;
		}
		else if (type instanceof StructureType && ttype == TType.STRUCT) {
 	        StructureType structureType = (StructureType) type;

			// TODO
			//result = deserialize(f, )
		}
		else {
			TProtocolUtil.skip(protocol, ttype);
		}
		
		return result;
	}
}
