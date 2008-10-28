package mt.serialization;

import mt.serialization.schema.StructureType;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;

public class JavaBeanSetterAdapter<T>
	implements TargetAdapter<T>
{
	private final StructureType type;
	private Class<T> clazz;
	private Map<Integer, Method> methodCache = new HashMap<Integer, Method>();

	public JavaBeanSetterAdapter(StructureType type, Class<T> clazz)
	{
		this.type = type;
		this.clazz = clazz;
	}

	private void setField(T target, int field, Object value, Class clazz)
	{
		Method method = methodCache.get(field);

		if (method == null) {
			String setter = "set" + toCamelCase(type.getField(field).getName());
			try {
				method = target.getClass().getMethod(setter, clazz);
				methodCache.put(field, method);
			}
			catch (NoSuchMethodException e) {
				throw new RuntimeException(e);
			}
		}

		try {
			method.invoke(target, value);
		}
		catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
		catch (InvocationTargetException e) {
			e.printStackTrace();
		}
	}

	private static String toCamelCase(String name)
	{
		StringBuilder builder = new StringBuilder(name.length());
		for (int i = 0; i < name.length(); ++i) {
			char c = name.charAt(i);
			if (i == 0 && c != '_') {
				builder.append(Character.toUpperCase(c));
			}
			else if (c == '_' && i < name.length() - 1) {
				++i;
				builder.append(Character.toUpperCase(name.charAt(i)));
			}
			else if (c != '_') {
				builder.append(c);
			}
		}

		return builder.toString();
	}

	public T newInstance()
	{
		try {
			return clazz.newInstance();
		}
		catch (InstantiationException e) {
			throw new RuntimeException(e);
		}
		catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	public void setBoolean(T target, int field, boolean value)
	{
		setField(target, field, value, Boolean.TYPE);
	}

	public void setByte(T target, int field, byte value)
	{
		setField(target, field, value, Byte.TYPE);
	}

	public void setI16(T target, int field, short value)
	{
		setField(target, field, value, Short.TYPE);
	}

	public void setI32(T target, int field, int value)
	{
		setField(target, field, value, Integer.TYPE);
	}

	public void setI64(T target, int field, long value)
	{
		setField(target, field, value, Long.TYPE);
	}

	public void setDouble(T target, int field, double value)
	{
		setField(target, field, value, Double.TYPE);
	}

	public void setString(T target, int field, String value)
	{
		setField(target, field, value, String.class);
	}

	public void setBinary(T target, int field, byte[] value)
	{
		setField(target, field, value, byte[].class);
	}

	public void setMap(T target, int field, Map<?, ?> value)
	{
		setField(target, field, value, Map.class);
	}

	public void setSet(T target, int field, Set<?> value)
	{
		setField(target, field, value, Set.class);
	}

	public void setList(T target, int field, List<?> value)
	{
		setField(target, field, value, List.class);
	}

	public void setStructure(T target, int field, Object value)
	{
		setField(target, field, value, value.getClass());
	}
}
