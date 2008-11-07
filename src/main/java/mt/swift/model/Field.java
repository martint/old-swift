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

import com.facebook.thrift.protocol.TField;

public class Field
{
	private Type type;
	private short id;
	private String name;
	private boolean required;

	private final TField tfield;

	public Field(Type type, int id, String name, boolean required)
	{
		this(type, (short) id, name, required);	
	}

	public Field(Type type, short id, String name, boolean required)
	{
		this.type = type;
		this.id = id;
		this.name = name;
		this.required = required;

		tfield = new TField(name, type.getTType(), id);
	}

	public short getId()
	{
		return id;
	}

	public String getName()
	{
		return name;
	}

	public boolean isRequired()
	{
		return required;
	}

	public Type getType()
	{
		return type;
	}

	@Deprecated
	public TField toTField()
	{
		return tfield;
	}

}
