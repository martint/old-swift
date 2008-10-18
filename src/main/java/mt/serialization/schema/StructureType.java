package mt.serialization.schema;

import com.facebook.thrift.TException;
import com.facebook.thrift.protocol.TField;
import com.facebook.thrift.protocol.TProtocol;
import com.facebook.thrift.protocol.TProtocolUtil;
import com.facebook.thrift.protocol.TStruct;
import com.facebook.thrift.protocol.TType;
import mt.serialization.visitor.Visitor;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StructureType
	implements Type<Map<String, ?>>
{
	private String name;
	private Map<Integer, Field> fields = new HashMap<Integer, Field>();

	private final TStruct tstruct;

	public StructureType(String name, List<Field> fields)
	{
		this.name = name;
		for (Field field : fields) {
			this.fields.put(field.getId(), field);
		}

		tstruct = new TStruct(name);
	}

	public Collection<Field> getFields()
	{
		return fields.values();
	}

	public TStruct toTStruct()
	{
		return tstruct;
	}

	public String getName()
	{
		return name;
	}

	public Field getField(int id)
	{
		return fields.get(id);
	}

	public byte getTType()
	{
		return TType.STRUCT;
	}

	public String getSignature()
	{
		return name;
	}

	public void accept(Visitor visitor)
	{
		visitor.visit(this);
	}
	
	public Map<String, ?> read(TProtocol protocol)
		throws TException
	{
		Map<String, Object> result = new HashMap<String, Object>();

		protocol.readStructBegin();
		while (true) {
			TField tfield = protocol.readFieldBegin();
			if (tfield.type == TType.STOP) {
				break;
			}

			Field field = getField(tfield.id);
			if (field == null || field.getType().getTType() != tfield.type) {
				TProtocolUtil.skip(protocol, tfield.type);
			}
			else {
				result.put(field.getName(), field.getType().read(protocol));
			}
			protocol.readFieldEnd();
		}
		protocol.readStructEnd();

		return result;
	}
}
