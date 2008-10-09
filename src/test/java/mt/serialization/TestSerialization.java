package mt.serialization;

import mt.serialization.schema.FieldDescriptor;
import mt.serialization.schema.Schema;
import org.testng.annotations.Test;

import java.io.DataOutput;
import java.io.DataOutputStream;
import java.util.HashMap;
import java.util.Map;

public class TestSerialization
{
	@Test
	public void testReflectionSerializer()
		throws Exception
	{
		Schema schema = getSchema();
		DataOutput out = getOutput();

		Person person = new Person();
		person.setName("Martin Traverso");
		person.setHeight(175.6f);
		person.setAge(31);
		person.setPasswordHash(new byte[160]);

		Phone phone = new Phone();
		phone.setNumber("650-796-4453");
		phone.setType(1);

		person.setPhone(phone);
		
		Serializer<Object> serializer = Serializer.newReflectiveSerializer(schema);

		// introspects "person" by referring to the fields in ning.Person
		// if it encounters a fields of type "structure", it recurses:
		//    -> this.serialize(person.getPhone, field.getType().getName())
		// where field.getType().getName() would return "ning.Phone", for instance
		serializer.serialize(person, "ning.Person", out);
	}

	@Test
	public void testDynamicCodeGenSerializer()
		throws Exception
	{
		Schema schema = getSchema();
		DataOutput out = getOutput();

		Person person = new Person();
		person.setName("Martin Traverso");
		person.setHeight(175.6f);
		person.setAge(31);
		person.setPasswordHash(new byte[160]);

		Phone phone = new Phone();
		phone.setNumber("650-796-4453");
		phone.setType(1);

		person.setPhone(phone);

		// Pass DataOut on call to serialize() so that we can reuse the instance and avoid
		// regeneration of code. I.e., this serializer does a lot of bookkeeping and is fairly heavyweight
		// Also, it should be stateless, save for the codegen caches so that it can be accessed concurrently
		Serializer<Object> serializer = Serializer.newDynamicCodeGenSerializer(schema);

		// discovery is done via reflection (a la ReflectionSerializer) & invokers are compiled
		// at serialization time to avoid reflective calls (sort of like method inlining)
		serializer.serialize(person, "ning.Person", out);
	}

	@Test
	public void testMapSerializer()
		throws Exception
	{                                                                               
		Schema schema = getSchema();
		DataOutput out = getOutput();
		
		Map<String, Object> entry = new HashMap<String, Object>();
		entry.put("name", "Martin Traverso");
		entry.put("height", 176.5);
		entry.put("age", 31);
		entry.put("password_hash", new byte[160]);

		Serializer<Map<String, ?>> serializer = Serializer.newMapSerializer(schema);
		serializer.serialize(entry, "ning.Person", out);
	}

	@Test
	public void testPreGeneratedSerializer()
		throws Exception
	{
		/*
			SerializablePerson person = new SerializablePerson();

			person.setName("Martin Traverso");
			person.setHeight(175.6f);
			person.setAge(31);
			person.setPasswordHash(new byte[160]);

			SerializalblePhone phone = new SerializablePhone();
			phone.setNumber("650-796-4453");
			phone.setType(1);

			person.setPhone(phone);

			person.serialize(new DataOutputStream(System.out));
		*/
	}

	private Schema getSchema()
	{
		Schema result = new Schema();
		result.add("ning.Person",
		           new FieldDescriptor(1, Type.STRING, "name"),
		           new FieldDescriptor(2, Type.BYTES, "password_hash"),
		           new FieldDescriptor(4, Type.INTEGER, "age"),
		           new FieldDescriptor(5, Type.DECIMAL, "height")
		           );

		return result;
	}

	public DataOutput getOutput()
	{
		return new DataOutputStream(System.out);
	}
}
