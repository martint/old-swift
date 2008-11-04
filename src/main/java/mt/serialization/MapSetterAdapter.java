package mt.serialization;

import mt.serialization.model.StructureType;

import java.util.Map;
import java.util.Set;
import java.util.List;
import java.util.HashMap;

class MapSetterAdapter
	implements TargetAdapter<Map<String, Object>>
{
	private StructureType type;

	public MapSetterAdapter(StructureType type)
	{
		this.type = type;
	}

	private void put(Map<String, Object> target, int field, Object value)
	{
		String fieldName = type.getField(field).getName();
		target.put(fieldName, value);
	}

	public Map<String, Object> newInstance()
	{
		return new HashMap<String, Object>();
	}

	public void setBoolean(Map<String, Object> target, int field, boolean value)
	{
		put(target, field, value);
	}

	public void setByte(Map<String, Object> target, int field, byte value)
	{
		put(target, field, value);
	}

	public void setI16(Map<String, Object> target, int field, short value)
	{
		put(target, field, value);
	}

	public void setI32(Map<String, Object> target, int field, int value)
	{
		put(target, field, value);
	}

	public void setI64(Map<String, Object> target, int field, long value)
	{
		put(target, field, value);
	}

	public void setDouble(Map<String, Object> target, int field, double value)
	{
		put(target, field, value);
	}

	public void setString(Map<String, Object> target, int field, String value)
	{
		put(target, field, value);
	}

	public void setBinary(Map<String, Object> target, int field, byte[] value)
	{
	 	put(target, field, value);
	}

	public void setMap(Map<String, Object> target, int field, Map<?, ?> value)
	{
		put(target, field, value);
	}

	public void setSet(Map<String, Object> target, int field, Set<?> value)
	{
		put(target, field, value);
	}

	public void setList(Map<String, Object> target, int field, List<?> value)
	{
		put(target, field, value);
	}

	public void setStructure(Map<String, Object> target, int field, Object value)
	{
		put(target, field, value);
	}
}
