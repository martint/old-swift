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

public class Field
{
	private final Type type;
	private final short id;
	private final String name;
	private final boolean required;

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
}
