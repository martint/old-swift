package mt.serialization;

import com.facebook.thrift.TException;
import com.facebook.thrift.protocol.TField;
import com.facebook.thrift.protocol.TProtocol;
import com.facebook.thrift.protocol.TProtocolUtil;
import com.facebook.thrift.protocol.TType;
import mt.serialization.schema.BasicType;
import mt.serialization.schema.Field;
import mt.serialization.schema.ListType;
import mt.serialization.schema.MapType;
import mt.serialization.schema.SetType;
import mt.serialization.schema.StructureType;
import mt.serialization.schema.Type;

class DynamicDeserializer<T>
	implements StructureDeserializer<T>
{
	private StructureType structure;
	private final TargetAdapter<T> adapter;

	DynamicDeserializer(StructureType structure, TargetAdapter<T> adapter)
	{
		this.structure = structure;
		this.adapter = adapter;
	}

	public T deserialize(Deserializer deserializer, TProtocol protocol)
		throws TException
	{
		T target = adapter.newInstance();
		
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
				Type type = field.getType();
				int id = field.getId();

				if (type == BasicType.BOOLEAN) {
					adapter.setBoolean(target, id, protocol.readBool());
				}
				else if (type == BasicType.BYTE) {
					adapter.setByte(target, id, protocol.readByte());
				}
				else if (type == BasicType.I16) {
					adapter.setI16(target, id, protocol.readI16());
				}
				else if (type == BasicType.I32) {
					adapter.setI32(target, id, protocol.readI32());
				}
				else if (type == BasicType.I64) {
					adapter.setI64(target, id, protocol.readI64());
				}
				else if (type == BasicType.STRING) {
					adapter.setString(target, id, protocol.readString());
				}
				else if (type == BasicType.DOUBLE) {
					adapter.setDouble(target, id, protocol.readDouble());
				}
				else if (type == BasicType.BINARY) {
					adapter.setBinary(target, id, protocol.readBinary());
				}
				else if (type instanceof ListType) {
					adapter.setList(target, id, Utils.readList(deserializer, protocol, (ListType) type));
				}
				else if (type instanceof SetType) {
					adapter.setSet(target, id, Utils.readSet(deserializer, protocol, (SetType) type));
				}
				else if (type instanceof MapType) {
					adapter.setMap(target, id, Utils.readMap(deserializer, protocol, (MapType) type));
				}
				else if (type instanceof StructureType) {
					adapter.setStructure(target, id, Utils.readStructure(deserializer, protocol, (StructureType) type));
				}
				else {
					throw new UnsupportedOperationException(String.format("Type '%s' not supported", type.getSignature()));
				}
			}
			protocol.readFieldEnd();
		}
		protocol.readStructEnd();

		return target;
	}
}