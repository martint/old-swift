package mt.serialization;

public interface ClassSerializer<T>
{
	public void serialize(T object);
}
