package mt.serialization;

public class Person
{
	private String name;
	private byte[] passwordHash;
	private int age;
	private float height;
	private Phone phone;

	public String getName()
	{
		return name;
	}

	public void setName(String name)
	{
		this.name = name;
	}

	public byte[] getPasswordHash()
	{
		return passwordHash;
	}

	public void setPasswordHash(byte[] passwordHash)
	{
		this.passwordHash = passwordHash;
	}

	public int getAge()
	{
		return age;
	}

	public void setAge(int age)
	{
		this.age = age;
	}

	public float getHeight()
	{
		return height;
	}

	public void setHeight(float height)
	{
		this.height = height;
	}

	public Phone getPhone()
	{
		return phone;
	}

	public void setPhone(Phone phone)
	{
		this.phone = phone;
	}
}
