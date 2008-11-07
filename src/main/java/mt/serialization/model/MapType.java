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
package mt.serialization.model;

import com.facebook.thrift.protocol.TType;

public class MapType
	implements Type
{
	private Type keyType;
	private Type valueType;

	public MapType(Type keyType, Type valueType)
	{
		this.keyType = keyType;
		this.valueType = valueType;
	}

	public Type getKeyType()
	{
		return keyType;
	}

	public Type getValueType()
	{
		return valueType;
	}

	public byte getTType()
	{
		return TType.MAP;
	}

	public String getSignature()
	{
		return String.format("map<%s,%s>", keyType.getSignature(), valueType.getSignature());
	}

}
