package mt.serialization;

import com.facebook.thrift.TException;
import com.facebook.thrift.protocol.TField;
import com.facebook.thrift.protocol.TProtocol;
import com.facebook.thrift.protocol.TProtocolUtil;
import com.facebook.thrift.protocol.TType;
import mt.serialization.schema.Field;
import mt.serialization.schema.Schema;
import mt.serialization.schema.Structure;
import mt.serialization.schema.StructureType;
import mt.serialization.schema.Type;

import java.util.HashMap;
import java.util.Map;

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
			if (field == null || field.getType().getTType() != tfield.type) {
				TProtocolUtil.skip(protocol, tfield.type);
			}
			else {
				result.put(field.getName(), read(protocol, field.getType()));
			}
			protocol.readFieldEnd();
		}
		protocol.readStructEnd();

		return result;
	}

	private Object read(TProtocol protocol, Type type)
		throws TException
	{
		Object result;

		if (type instanceof StructureType) {
 	        StructureType structureType = (StructureType) type;
			result = deserialize(structureType.getStructureName(), protocol);
		}
		else {
			result = type.read(protocol);
		}

		return result;
	}
}
