package mt.serialization;

import mt.serialization.protocol.Protocol;
import mt.serialization.schema.FieldDescriptor;
import mt.serialization.schema.Schema;
import mt.serialization.schema.StructureDescriptor;

import java.util.Map;

class MapSerializer
	extends Serializer<Map<String, ?>>
{
	public MapSerializer(Schema schema)
	{
		super(schema);
	}

	public void serialize(Map<String, ?> map, String structName, Protocol protocol)
	{
		StructureDescriptor descriptor = new StructureDescriptor();
		for (FieldDescriptor field : descriptor.getFields()) {
			Object value = map.get(field.getName());

			switch (field.getType()) {
				case BOOLEAN:
					break;
				case BYTES:
					break;
				case DECIMAL:
					break;
				case INTEGER:
					break;
				case STRING:
					break;
				case STRUCTURE:
					break;
			}
		}
	}
}
