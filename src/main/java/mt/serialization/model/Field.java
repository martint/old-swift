package mt.serialization.model;

import com.facebook.thrift.protocol.TField;

public class Field
{
	private Type type;
	private short id;
	private String name;
	private boolean required;

	private final TField tfield;

	public Field(Type type, int id, String name, boolean required)
	{
		this(type, (short) id, name, required);	
	}

	public Field(Type type, short id, String name, boolean required)
	{
		this.type = type;
		this.id = id;
		this.name = name;
		this.required = required;

		tfield = new TField(name, type.getTType(), id);
	}

	public short getId()
	{
		return id;
	}

	public String getName()
	{
		return name;
	}

	public boolean isRequired()
	{
		return required;
	}

	public Type getType()
	{
		return type;
	}

	@Deprecated
	public TField toTField()
	{
		return tfield;
	}

}
