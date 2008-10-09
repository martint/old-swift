package mt.serialization;

import org.testng.annotations.Test;

public class TestSerialization
{
	@Test
	public void testSomething()
			throws Exception
	{
		Scheme scheme = null;

		Serializer serializer = Serializer.getInstance(scheme);

		Person person = null;

		serializer.forClass(Person.class).serialize(person);
	}
}
