package mt.serialization.schema;

import mt.serialization.Type;

public class FieldDescriptor
{
	private final int id;
	private final Type type;
	private final String name;

	public FieldDescriptor(int id, Type type, String name)
	{
		this.id = id;
		this.type = type;
		this.name = name;
	}

	public String getName()
	{
		return name;
	}

	public Type getType()
	{
		return type;
	}
}
