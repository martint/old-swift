package mt.serialization;

import com.facebook.thrift.protocol.TBinaryProtocol;
import com.facebook.thrift.protocol.TProtocol;
import com.facebook.thrift.transport.TIOStreamTransport;
import mt.serialization.model.StructureType;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

public class TestSchemaEvolution
{
	@Test
	public void testAddField()
		throws Exception
	{
		StructureType before = new StructureType(TestStruct.class.getName(), Fields.BOOLEAN_FIELD);

		TestStruct original = new TestStruct();
		original.setBooleanField(true);

		Serializer serializer = new Serializer();
		serializer.bind(before, TestStruct.class);

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		TProtocol outputProtocol = new TBinaryProtocol(new TIOStreamTransport(out));

		serializer.serialize(original, TestStruct.class.getName(), outputProtocol);


		// add a byte field
		StructureType after = new StructureType(TestStruct.class.getName(), Fields.BOOLEAN_FIELD, Fields.BYTE_FIELD);
		Deserializer deserializer = new Deserializer();
		deserializer.bind(after, TestStruct.class);

		TProtocol inputProtocol = new TBinaryProtocol(new TIOStreamTransport(new ByteArrayInputStream(out.toByteArray())));
		TestStruct deserialized = deserializer.deserialize(TestStruct.class.getName(), inputProtocol);

		Assert.assertEquals(deserialized.isBooleanField(), original.isBooleanField());
		Assert.assertEquals(deserialized.getByteField(), 0);
	}

	@Test
	public void testRemoveField()
		throws Exception
	{
		StructureType before = new StructureType(TestStruct.class.getName(), Fields.BOOLEAN_FIELD, Fields.BYTE_FIELD);

		TestStruct original = new TestStruct();
		original.setBooleanField(true);
		original.setByteField(Byte.MAX_VALUE);

		Serializer serializer = new Serializer();
		serializer.bind(before, TestStruct.class);

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		TProtocol outputProtocol = new TBinaryProtocol(new TIOStreamTransport(out));

		serializer.serialize(original, TestStruct.class.getName(), outputProtocol);

		// remove byte field
		StructureType after = new StructureType(TestStruct.class.getName(), Fields.BOOLEAN_FIELD);
		Deserializer deserializer = new Deserializer();
		deserializer.bind(after, TestStruct.class);

		TProtocol inputProtocol = new TBinaryProtocol(new TIOStreamTransport(new ByteArrayInputStream(out.toByteArray())));
		TestStruct deserialized = deserializer.deserialize(TestStruct.class.getName(), inputProtocol);

		Assert.assertEquals(deserialized.isBooleanField(), original.isBooleanField());
		Assert.assertEquals(deserialized.getByteField(), 0);
	}
}
