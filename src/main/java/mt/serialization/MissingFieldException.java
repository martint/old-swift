package mt.serialization;

import mt.serialization.schema.Field;
import mt.serialization.schema.Structure;

public class MissingFieldException
	extends RuntimeException
{
	private final Structure structure;
	private final Field field;
	private final Object object;

	public MissingFieldException(Structure structure, Field field, Object object)
	{
		this.structure = structure;
		this.field = field;
		this.object = object;
	}
}
