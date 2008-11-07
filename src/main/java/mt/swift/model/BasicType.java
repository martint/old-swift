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

import com.facebook.thrift.protocol.TType;

public class BasicType<T>
	implements Type<T>
{
	public static final Type BINARY = new BasicType<byte[]>(TType.STRING, "binary");
	public static final Type BOOLEAN = new BasicType<Boolean>(TType.BOOL, "bool");
	public static final Type BYTE = new BasicType<Byte>(TType.BYTE, "byte");
	public static final Type I16 = new BasicType<Short>(TType.I16, "i16");
	public static final Type I32 = new BasicType<Integer>(TType.I32, "i32");
	public static final Type I64 = new BasicType<Long>(TType.I64, "i64");
	public static final Type DOUBLE = new BasicType<Double>(TType.DOUBLE, "double");
	public static final Type STRING = new BasicType<String>(TType.STRING, "string");

	private final byte ttype;
	private final String signature;

	private BasicType(byte ttype, String signature)
	{
		this.ttype = ttype;
		this.signature = signature;
	}

	public byte getTType()
	{
		return ttype;
	}

	public String getSignature()
	{
		return signature;
	}

}
