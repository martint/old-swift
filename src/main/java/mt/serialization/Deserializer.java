package mt.serialization;

import mt.serialization.schema.Schema;

import java.util.Map;

public abstract class Deserializer<T>
{
	private final Schema schema;

	public Deserializer(Schema schema)
	{
		this.schema = schema;
		// TODO: resolve StructureTypeRef -> StructureType
	}

	public static Deserializer<Map<String, ?>> newMapDeserializer(Schema schema)
	{
		return new MapDeserializer(schema);
	}

	public Schema getSchema()
	{
		return schema;
	}

}
