namespace java mt.serialization.test

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
	12:NestedStruct structField;
	13:list<list<i32>> nestedListOfIntsField;
}