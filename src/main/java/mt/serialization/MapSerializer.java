package mt.serialization;

import com.facebook.thrift.TException;
import com.facebook.thrift.protocol.TMap;
import com.facebook.thrift.protocol.TProtocol;
import mt.serialization.schema.BasicType;
import mt.serialization.schema.Field;
import mt.serialization.schema.ListType;
import mt.serialization.schema.MapType;
import mt.serialization.schema.Schema;
import mt.serialization.schema.SetType;
import mt.serialization.schema.Structure;
import mt.serialization.schema.Type;

import java.util.Map;

class MapSerializer
	extends Serializer<Map<String, ?>>
{
	public MapSerializer(Schema schema)
	{
		super(schema);
	}

	public void serialize(Map<String, ?> map, String structName, TProtocol protocol)
		throws TException
	{
		Structure structure = getSchema().getStructure(structName);
		protocol.writeStructBegin(structure.toTStruct());
		for (Field field : structure.getFields()) {
			Object value = map.get(field.getName());

			if (value == null) {
				if (field.isRequired()) {
					throw new MissingFieldException(structure, field, map);
				}
				continue;
			}

			protocol.writeFieldBegin(field.toTField());

			Type type = field.getType();

			// TODO: switch on field.getType().getTType() ?
			if (type == BasicType.BINARY) {
				if (value instanceof byte[]) {
					protocol.writeBinary((byte[]) value);
				}
				else {
					throw new IllegalArgumentException("Can't convert %s to BINARY");
				}
			}
			else if (type == BasicType.BOOLEAN) {
				if (value instanceof Boolean) {
					protocol.writeBool((Boolean) value);
				}
				else {
					throw new IllegalArgumentException("Can't convert %s to BOOLEAN");
				}
			}
			else if (type == BasicType.BYTE) {
				if (value instanceof Byte) {
					protocol.writeByte((Byte) value);
				}
				else {
					throw new IllegalArgumentException("Can't convert %s to BYTE");
				}
			}
			else if (type == BasicType.I16) {
				if (value instanceof Short) {
					protocol.writeI16((Short) value);
				}
				else {
					throw new IllegalArgumentException("Can't convert %s to I16");
				}
			}
			else if (type == BasicType.I32) {
				if (value instanceof Integer) {
					protocol.writeI32((Integer) value);
				}
				else {
					throw new IllegalArgumentException("Can't convert %s to I32");
				}
			}
			else if (type == BasicType.I64) {
				if (value instanceof Long) {
					protocol.writeI64((Long) value);
				}
				else {
					throw new IllegalArgumentException("Can't convert %s to I64");
				}
			}
			else if (type == BasicType.DOUBLE) {
				if (value instanceof Double) {
					protocol.writeDouble((Double) value);
				}
				else {
					throw new IllegalArgumentException("Can't convert %s to DOUBLE");
				}
			}
			else if (type == BasicType.STRING) {
				if (value instanceof String) {
					protocol.writeString((String) value);
				}
				else {
					throw new IllegalArgumentException("Can't convert %s to STRING");
				}
			}
			else if (type instanceof MapType) {
				Map<?,?> mapValue = (Map<?,?>) value;
				MapType mapType = (MapType) type;
				TMap tmap = new TMap(mapType.getKeyType().getTType(), mapType.getValueType().getTType(), mapValue.size());
				protocol.writeMapBegin(tmap);

				// TODO

				protocol.writeMapEnd();
			}
			else if (type instanceof ListType) {
				// TODO
			}
			else if (type instanceof SetType) {
				// TODO
			}

			protocol.writeFieldEnd();
		}
		protocol.writeFieldStop();
		protocol.writeStructEnd();
	}
}
