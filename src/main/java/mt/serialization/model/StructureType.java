package mt.serialization.model;

import com.facebook.thrift.protocol.TStruct;
import com.facebook.thrift.protocol.TType;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StructureType
	implements Type<Map<String, ?>>
{
	private String name;
	private Map<Integer, Field> fields = new HashMap<Integer, Field>();

	@Deprecated
	private final TStruct tstruct;


	@Deprecated
	public StructureType(String name, List<Field> fields)
	{
		this(name, fields.toArray(new Field[0]));	
	}

	public StructureType(String name, Field... fields)
	{
		this.name = name;
		for (Field field : fields) {
			if (this.fields.containsKey(field.getId())) {
				throw new IllegalArgumentException(String.format("Duplicate field id %d", field.getId()));
			}
			this.fields.put(field.getId(), field);
		}

		tstruct = new TStruct(name);
	}

	public Collection<Field> getFields()
	{
		return fields.values();
	}

	@Deprecated
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


}
