package mt.serialization;

import com.facebook.thrift.protocol.TProtocol;
import mt.serialization.schema.Schema;
import org.testng.annotations.Test;

import java.util.Map;

public class TestDeserialization
{
	@Test
	public void testReflectionDeserializer()
			throws Exception
	{

	}

	@Test
	public void testMapDeserializer()
			throws Exception
	{
		Schema schema = getSchema();
		TProtocol protocol = getProtocol();

		Deserializer<Map<String, ?>> deserializer = Deserializer.newMapDeserializer(schema);
		Map<String, ?> entry = deserializer.deserialize("ning.Person", protocol);

		Serializer<Map<String, ?>> serializer = Serializer.newMapSerializer(schema);
		serializer.serialize(entry, "ning.Person", protocol);

	}

	private Schema getSchema()
	{
		Schema result = new Schema();
//		result.add("ning.Person",
//		           new FieldDescriptor(1, Type.STRING, "name"),
//		           new FieldDescriptor(2, Type.BYTES, "password_hash"),
//		           new FieldDescriptor(4, Type.INTEGER, "age"),
//		           new FieldDescriptor(5, Type.DECIMAL, "height")
//		           );

		return result;
	}

	public TProtocol getProtocol()
	{
		throw new UnsupportedOperationException("Not yet implemented"); // TODO: implement this
	}

}
