package mt.serialization;

import org.testng.annotations.Test;
import org.testng.Assert;
import mt.serialization.model.StructureType;
import mt.serialization.model.Field;
import mt.serialization.model.BasicType;
import mt.serialization.test.TestStruct;
import com.facebook.thrift.protocol.TProtocol;
import com.facebook.thrift.protocol.TBinaryProtocol;
import com.facebook.thrift.TException;
import com.facebook.thrift.transport.TIOStreamTransport;

import java.util.Map;
import java.util.HashMap;
import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;

public class TestSerializer
{
	@Test
	public void testMapToBoolean()
		throws Exception
	{
		StructureType type = new StructureType(TestStruct.class.getName(),
		                                       new Field(BasicType.BOOLEAN, 1, "booleanField", false));

		Map<String, Boolean> data = new HashMap<String, Boolean>();
		data.put("booleanField", true);

		Serializer serializer = new Serializer();
		serializer.setDebug(true);
		serializer.bindToMap(type);

		ByteArrayOutputStream out = new ByteArrayOutputStream();
		TProtocol protocol = new TBinaryProtocol(new TIOStreamTransport(out));
		serializer.serialize(data, type.getName(), protocol);

		TestStruct result = deserialize(out.toByteArray());

		Assert.assertTrue(result.__isset.booleanField);
		Assert.assertEquals(result.booleanField, data.get("booleanField").booleanValue());
	}

	private TestStruct deserialize(byte[] data)
		throws TException
	{
		TestStruct result = new TestStruct();
		result.read(new TBinaryProtocol(new TIOStreamTransport(new ByteArrayInputStream(data))));

		return result;
	}

}
