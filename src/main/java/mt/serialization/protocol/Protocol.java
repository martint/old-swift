package mt.serialization.protocol;

import mt.serialization.schema.FieldDescriptor;

import java.io.DataOutput;

/**
 * TODO: how much info about fields and types do we need to pass around?
 * TODO: How does ordering affect protocol operation? (e.g., binary protocol where order is known vs json)
 */
public interface Protocol
{
	void writeInteger(DataOutput out, FieldDescriptor descriptor, int value);

	void writeBeginStructure(DataOutput out, FieldDescriptor descriptor);
	void writeEndStructure(DataOutput out, FieldDescriptor descriptor);
	
//	void writeInteger(DataOutput out, byte value);
//	void writeInteger(DataOutput out, short value);
//	void writeInteger(DataOutput out, int value);
//	void writeInteger(DataOutput out, long value);
//	void writeInteger(DataOutput out, BigInteger value);
//
//	void writeDecimal(DataOutput out, float value);
//	void writeDecimal(DataOutput out, double value);
//	void writeDecimal(DataOutput out, BigDecimal value);
//
//	void writeString(DataOutput out, String value);
//	void writeString(DataOutput out, CharSequence value);
//	void writeString(DataOutput out, char value);
//
//	void writeBoolean(DataOutput out, boolean value);
//
//	void writeBytes(DataOutput out, byte[] value);
//	void writeBytes(DataOutput out, ByteBuffer[] value);
}
