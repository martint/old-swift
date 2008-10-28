package mt.serialization;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface TargetAdapter<T>
{
	T newInstance();
	
	void setBoolean(T target, int field, boolean value);
	void setByte(T target, int field, byte value);
	void setI16(T target, int field, short value);
	void setI32(T target, int field, int value);
	void setI64(T target, int field, long value);
	void setDouble(T target, int field, double value);
	void setString(T target, int field, String value);
	void setBinary(T target, int field, byte[] value);
	void setMap(T target, int field, Map<?, ?> value);
	void setSet(T target, int field, Set<?> value);
	void setList(T target, int field, List<?> value);
	void setStructure(T target, int field, Object value);
}
