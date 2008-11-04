package mt.serialization;

import mt.serialization.model.Field;
import mt.serialization.model.StructureType;

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
