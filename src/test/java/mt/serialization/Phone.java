package mt.serialization;

public class Phone
{
	private String phone;
	private int type; // TODO: how to use java enum here?

	public String getPhone()
	{
		return phone;
	}

	public void setNumber(String phone)
	{
		this.phone = phone;
	}

	public int getType()
	{
		return type;
	}

	public void setType(int type)
	{
		this.type = type;
	}
}
