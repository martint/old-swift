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
import org.apache.thrift.protocol.TProtocol;

// TODO: new name: DeserializationStrategy ?
public interface StructureDeserializer<T>
{
	/**
	 * Deserialize an instance of T
	 * @param deserializer the global deserializer, to deserialize nested structures
	 * @param protocol the protocol to deserialize from
	 * @return an instance of T with all known fields filled in
	 * @throws TException if there's an error during deserialization
	 */
	T deserialize(Deserializer deserializer, TProtocol protocol)
		throws TException;
}
