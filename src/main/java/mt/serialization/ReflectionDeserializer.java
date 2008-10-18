package mt.serialization;

import com.facebook.thrift.TException;
import com.facebook.thrift.protocol.TField;
import com.facebook.thrift.protocol.TProtocol;
import com.facebook.thrift.protocol.TProtocolUtil;
import com.facebook.thrift.protocol.TType;
import mt.serialization.schema.BasicType;
import mt.serialization.schema.Field;
import mt.serialization.schema.ListType;
import mt.serialization.schema.MapType;
import mt.serialization.schema.Schema;
import mt.serialization.schema.SetType;
import mt.serialization.schema.StructureType;
import mt.serialization.schema.Type;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class ReflectionDeserializer
	extends Deserializer
{
	public ReflectionDeserializer(Schema schema)
	{
		super(schema);
	}

	public <T> T deserialize(String structName, Class<T> clazz, TProtocol protocol)
		throws TException, IllegalAccessException, InstantiationException, InvocationTargetException,
		       NoSuchMethodException
	{
		StructureType structure = getSchema().getStructure(structName);

		Object result = clazz.newInstance();

		protocol.readStructBegin();
		while (true) {
			TField tfield = protocol.readFieldBegin();
			if (tfield.type == TType.STOP) {
				break;
			}

			Field field = structure.getField(tfield.id);
			if (field == null || field.getType().getTType() != tfield.type) {
				TProtocolUtil.skip(protocol, tfield.type);
			}
			else {
				String fieldName = field.getName();

				// TODO: find candidate method based on field type, bounds of field value & setter parameter type
				// TODO: what to do if more than one method qualifies? Especially problematic if parameter type is not
				// TODO: one of the primitive types
				Method setter = clazz.getMethod("set" + toCamelCase(fieldName), getClassFor(field.getType()));

				Object value = field.getType().read(protocol);
				setter.invoke(result, value);
			}
			protocol.readFieldEnd();
		}
		protocol.readStructEnd();

		return clazz.cast(result);
	}

	private Class getClassFor(Type type)
	{
		if (type == BasicType.BINARY) {
			return byte[].class;
		}
		else if (type == BasicType.BOOLEAN) {
			return Boolean.TYPE;
		}
		else if (type == BasicType.BYTE) {
			return Byte.TYPE;
		}
		else if (type == BasicType.I16) {
			return Short.TYPE;
		}
		else if (type == BasicType.I32) {
			return Integer.TYPE;
		}
		else if (type == BasicType.I64) {
			return Long.TYPE;
		}
		else if (type == BasicType.STRING) {
			return String.class;
		}
		else if (type instanceof MapType) {
			return Map.class;
		}
		else if (type instanceof ListType) {
			return List.class;
		}
		else if (type instanceof SetType) {
			return Set.class;
		}
		else if (type instanceof StructureType) {
			// TODO:
		}
		return null;
	}

	public void bind(StructureType structure, Class clazz)
	{
		// Establishes mapping between structure and class. Finds relevant setters for each field in the structure
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

}
