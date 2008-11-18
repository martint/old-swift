#
#  Copyright 2008 Martin Traverso
#
#  Licensed under the Apache License, Version 2.0 (the "License");
#  you may not use this file except in compliance with the License.
#  You may obtain a copy of the License at
#
#      http://www.apache.org/licenses/LICENSE-2.0
#
#  Unless required by applicable law or agreed to in writing, software
#  distributed under the License is distributed on an "AS IS" BASIS,
#  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#  See the License for the specific language governing permissions and
#  limitations under the License.
#
namespace java mt.swift

struct TNestedStruct
{
	1:string value;
}

struct TTestStruct
{                          
	1:bool booleanField;
	2:byte byteField;
	3:i16  shortField;
	4:i32  intField;
	5:i64  longField;
	6:double doubleField;
	7:string stringField;
	8:binary binaryField;
	9:list<i32> listOfIntsField;
	10:set<i32> setOfIntsField;
	11:map<i32,i32> mapOfIntsIntsField;
	12:TNestedStruct structField;
	13:list<list<i32>> nestedListOfIntsField;
	14:map<i32,string> mapOfIntsStringsField;
}