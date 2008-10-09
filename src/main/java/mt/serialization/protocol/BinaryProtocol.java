package mt.serialization.protocol;

import mt.serialization.schema.FieldDescriptor;

import java.io.DataOutput;

public class BinaryProtocol
	implements Protocol
{
	public void writeInteger(DataOutput out, FieldDescriptor descriptor, int value)
	{
		throw new UnsupportedOperationException("Not yet implemented"); // TODO: implement this
	}

	public void writeBeginStructure(DataOutput out, FieldDescriptor descriptor)
	{
		throw new UnsupportedOperationException("Not yet implemented"); // TODO: implement this
	}

	public void writeEndStructure(DataOutput out, FieldDescriptor descriptor)
	{
		throw new UnsupportedOperationException("Not yet implemented"); // TODO: implement this
	}
}
