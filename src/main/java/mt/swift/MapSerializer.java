/**
 *  Copyright 2008 Martin Traverso
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package mt.swift;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.*;
import mt.swift.model.BasicType;
import mt.swift.model.Field;
import mt.swift.model.ListType;
import mt.swift.model.MapType;
import mt.swift.model.SetType;
import mt.swift.model.StructureType;
import mt.swift.model.Type;

import java.math.BigInteger;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.HashMap;

class MapSerializer
{
	private Map<String, StructureType> structures;

	MapSerializer(StructureType... types)
	{
		this.structures = new HashMap<String, StructureType>();

		for (StructureType type : types) {
			structures.put(type.getName(), type);
		}
	}

	public void serialize(Map<String, ?> map, String structName, TProtocol protocol)
			throws TException
	{
		StructureType structure = structures.get(structName);
		protocol.writeStructBegin(new TStruct(structName));
		for (Field field : structure.getFields()) {
			Object value = map.get(field.getName());

			if (value == null) {
				if (field.isRequired()) {
					throw new MissingFieldException(structure, field, map);
				}
				continue;
			}

			protocol.writeFieldBegin(new TField(field.getName(), field.getType().getTType(), field.getId()));
			write(protocol, value, field.getType(), field);
			protocol.writeFieldEnd();
		}

		protocol.writeFieldStop();
		protocol.writeStructEnd();
	}

	private void write(TProtocol protocol, Object value, Type type, Field field)
			throws TException
	{
		if (type == BasicType.BINARY) {
			if (value instanceof byte[]) {
				protocol.writeBinary((byte[]) value);
			}
			else if (value instanceof ByteBuffer) {
				protocol.writeBinary(((ByteBuffer) value).array());
			}
			else {
				throwInvalidTypeException(field, value);
			}
		}
		else if (type == BasicType.BOOLEAN) {
			if (value instanceof Boolean) {
				protocol.writeBool((Boolean) value);
			}
			else {
				throwInvalidTypeException(field, value);
			}
		}
		else if (type == BasicType.BYTE) {
			if (value instanceof Byte || value instanceof Short || value instanceof Integer || value instanceof Long || value instanceof BigInteger) {
				Number number = (Number) value;
				if (number.shortValue() < Byte.MIN_VALUE || number.shortValue() > Byte.MAX_VALUE) {
					throwOverflowException(field, number, Byte.MIN_VALUE, Byte.MAX_VALUE);
				}

				protocol.writeByte(number.byteValue());
			}
			else {
				throwInvalidTypeException(field, value);
			}
		}
		else if (type == BasicType.I16) {
			if (value instanceof Byte || value instanceof Short || value instanceof Integer || value instanceof Long || value instanceof BigInteger) {
				Number number = (Number) value;
				if (number.intValue() < Short.MIN_VALUE || number.intValue() > Short.MAX_VALUE) {
					throwOverflowException(field, number, Short.MIN_VALUE, Short.MAX_VALUE);
				}

				protocol.writeI16(number.shortValue());
			}
			else {
				throwInvalidTypeException(field, value);
			}
		}
		else if (type == BasicType.I32) {
			if (value instanceof Byte || value instanceof Short || value instanceof Integer || value instanceof Long || value instanceof BigInteger) {
				Number number = (Number) value;
				if (number.longValue() < Integer.MIN_VALUE || number.longValue() > Integer.MAX_VALUE) {
					throwOverflowException(field, number, Integer.MIN_VALUE, Integer.MAX_VALUE);
				}

				protocol.writeI32(number.intValue());
			}
			else {
				throwInvalidTypeException(field, value);
			}
		}
		else if (type == BasicType.I64) {
			if (value instanceof Byte || value instanceof Short || value instanceof Integer || value instanceof Long) {
				protocol.writeI64(((Number) value).longValue());
			}
			else if (value instanceof BigInteger) {
				BigInteger number = (BigInteger) value;
				if (number.compareTo(BigInteger.valueOf(Long.MAX_VALUE)) > 0 || number.compareTo(BigInteger.valueOf(Long.MIN_VALUE)) < 0) {
					throwOverflowException(field, number, Long.MAX_VALUE, Long.MIN_VALUE);
				}

				protocol.writeI64(((Number) value).longValue());
			}
			else {
				throwInvalidTypeException(field, value);
			}
		}
		else if (type == BasicType.DOUBLE) {
			if (value instanceof Byte || value instanceof Short || value instanceof Integer || value instanceof Long
					|| value instanceof Float || value instanceof Double) {
				protocol.writeDouble(((Number) value).doubleValue());
			}
			// TODO: support BigDecimal, BigInteger if they fit
			else {
				throwInvalidTypeException(field, value);
			}
		}
		else if (type == BasicType.STRING) {
			if (value instanceof String) {
				protocol.writeString((String) value);
			}
			else if (value instanceof CharSequence) {
				protocol.writeString(value.toString());
			}
			// TODO: support any object by calling toString
			else {
				throwInvalidTypeException(field, value);
			}
		}
		else if (type instanceof MapType) {
			Map<?, ?> mapValue = (Map<?, ?>) value;
			MapType mapType = (MapType) type;
			TMap tmap = new TMap(mapType.getKeyType().getTType(), mapType.getValueType().getTType(), mapValue.size());
			protocol.writeMapBegin(tmap);
			for (Map.Entry<?, ?> entry : mapValue.entrySet()) {
				write(protocol, entry.getKey(), mapType.getKeyType(), field);
				write(protocol, entry.getValue(), mapType.getValueType(), field);
			}
			protocol.writeMapEnd();
		}
		else if (type instanceof ListType) {
			List<?> list = (List<?>) value;
			ListType listType = (ListType) type;
			TList tlist = new TList(listType.getValueType().getTType(), list.size());
			protocol.writeListBegin(tlist);
			for (Object obj: list) {
				write(protocol, obj, listType.getValueType(), field);
			}
			protocol.writeListEnd();
		}
		else if (type instanceof SetType) {
			Set<?> set = (Set<?>) value;
			SetType setType = (SetType) type;
			TSet tset = new TSet(setType.getValueType().getTType(), set.size());
			protocol.writeSetBegin(tset);
			for (Object obj: set) {
				write(protocol, obj, setType.getValueType(), field);
			}
			protocol.writeSetEnd();
		}
		else if (type instanceof StructureType) {
			if (value instanceof Map) {
				Map<String, ?> child = (Map<String, ?>) value;
				StructureType structureType = (StructureType) type;
				String structureName = structureType.getName();
				serialize(child, structureName, protocol);
			}
			else {
				throwInvalidTypeException(field, value);
			}
		}
		else {
			throw new IllegalArgumentException(String.format("Don't know how to serialize '%s' (%s)", field.getName(), field.getType().getSignature()));
		}
	}

	private void throwInvalidTypeException(Field field, Object value)
	{
		throw new IllegalArgumentException(String.format("Can't convert '%s' (%s = %s) to %s",
		                                                 field.getName(),
		                                                 value.getClass().getName(),
		                                                 value,
		                                                 field.getType().getSignature()));
	}

	private void throwOverflowException(Field field, Number value, Number min, Number max)
	{
		throw new IllegalArgumentException(String.format("Can't convert '%s' (%s = %s) to %s. Value is outside of range [%s, %s]",
		                                                 field.getName(),
		                                                 value.getClass().getName(),
		                                                 value,
		                                                 field.getType().getSignature(),
		                                                 min,
		                                                 max));

	}
}

