package mt.serialization.other;

public class Nested
{
	private String value;

	public void setValue(String value)
	{
		this.value = value;
	}

	@Override public String toString()
	{
		return "Nested{" +
		       "value='" + value + '\'' +
		       '}';
	}
}
