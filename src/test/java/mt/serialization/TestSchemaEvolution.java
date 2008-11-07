/**
 *  Copyright 2008 Martin Traverso
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
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
