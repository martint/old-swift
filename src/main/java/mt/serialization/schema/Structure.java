package mt.serialization.schema;

import com.facebook.thrift.protocol.TStruct;

import java.util.List;

public class Structure
{
	private String name;
	private List<Field> fields;

	private final TStruct tstruct;

	public Structure(String name, List<Field> fields)
	{
		this.name = name;
		this.fields = fields;

		tstruct = new TStruct(name);
	}

	public List<Field> getFields()
	{
		return fields;
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
		// TODO: use a map
		for (Field field : fields) {
			if (field.getId() == id) {
				return field;
			}
		}

		return null;
	}
}
