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
package mt.swift.model;

import com.facebook.thrift.protocol.TStruct;
import com.facebook.thrift.protocol.TType;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StructureType
	implements Type<Map<String, ?>>
{
	private String name;
	private Map<Short, Field> fields = new HashMap<Short, Field>();

	@Deprecated
	private final TStruct tstruct;


	@Deprecated
	public StructureType(String name, List<Field> fields)
	{
		this(name, fields.toArray(new Field[0]));	
	}

	public StructureType(String name, Field... fields)
	{
		this.name = name;
		for (Field field : fields) {
			if (this.fields.containsKey(field.getId())) {
				throw new IllegalArgumentException(String.format("Duplicate field id %d", field.getId()));
			}
			this.fields.put(field.getId(), field);
		}

		tstruct = new TStruct(name);
	}

	public Collection<Field> getFields()
	{
		return fields.values();
	}

	@Deprecated
	public TStruct toTStruct()
	{
		return tstruct;
	}

	public String getName()
	{
		return name;
	}

	public Field getField(int id)
	{
		return fields.get(id);
	}

	public byte getTType()
	{
		return TType.STRUCT;
	}

	public String getSignature()
	{
		return name;
	}


}
