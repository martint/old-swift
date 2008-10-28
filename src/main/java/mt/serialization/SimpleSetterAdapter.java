package mt.serialization;

import java.util.List;
import java.util.Map;
import java.util.Set;

public class SimpleSetterAdapter
	implements TargetAdapter<Simple>
{
	public Simple newInstance()
	{
		return new Simple();
	}

	public void setBoolean(Simple target, int field, boolean value)
	{
	 	if (field == 1) {
			 target.setABool(value);
		}
	}

	public void setByte(Simple target, int field, byte value)
	{
		if (field == 2) {
			target.setAByte(value);
		}
	}

	public void setI16(Simple target, int field, short value)
	{
		if (field == 3) {
			target.setAI16(value);
		}
	}

	public void setI32(Simple target, int field, int value)
	{
		if (field == 4) {
			target.setAI32(value);
		}
	}

	public void setI64(Simple target, int field, long value)
	{
		if (field == 5) {
			target.setAI64(value);
		}
	}

	public void setDouble(Simple target, int field, double value)
	{
		if (field == 6) {
			target.setADouble(value);
		}
	}

	public void setString(Simple target, int field, String value)
	{
		if (field == 8) {
			target.setAString(value);
		}
	}

	public void setBinary(Simple target, int field, byte[] value)
	{
		if (field == 7) {
			target.setABinary(value);
		}
	}

	public void setMap(Simple target, int field, Map<?, ?> value)
	{

	}

	public void setSet(Simple target, int field, Set<?> value)
	{

	}

	public void setList(Simple target, int field, List<?> value)
	{

	}

	public void setStructure(Simple target, int field, Object value)
	{

	}
}
