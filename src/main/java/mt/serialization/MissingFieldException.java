package mt.serialization;

import mt.serialization.schema.Field;
import mt.serialization.schema.StructureType;

public class MissingFieldException
	extends RuntimeException
{
	private final StructureType structure;
	private final Field field;
	private final Object object;

	public MissingFieldException(StructureType structure, Field field, Object object)
	{
		this.structure = structure;
		this.field = field;
		this.object = object;
	}
}
