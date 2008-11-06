// a comment
namespace java mt.serialization


struct TNested
{
	1:string value
}

struct TSimple
{
	1:required bool aBool;
	2:optional byte aByte;
	3:i16 aI16;
	4:i32 aI32;
	5:i64 aI64;
	6:binary aBinary;
	7:string aString;
	8:map<i32, string> mapOfI32s;
	9:TNested nested;
	10:list<list<binary>> listOfListOfBinaries;
}
