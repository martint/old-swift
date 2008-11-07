package mt.serialization;

import mt.serialization.model.Field;
import mt.serialization.model.BasicType;
import mt.serialization.model.ListType;
import mt.serialization.model.SetType;
import mt.serialization.model.MapType;

public class Fields
{
	public static final Field BOOLEAN_FIELD = new Field(BasicType.BOOLEAN, 1, "booleanField", false);
	public static final Field BYTE_FIELD = new Field(BasicType.BYTE, 2, "byteField", false);
	public static final Field I16_FIELD = new Field(BasicType.I16, 3, "shortField", false);
	public static final Field I32_FIELD = new Field(BasicType.I32, 4, "intField", false);
	public static final Field I64_FIELD = new Field(BasicType.I64, 5, "longField", false);
	public static final Field DOUBLE_FIELD = new Field(BasicType.DOUBLE, 6, "doubleField", false);
	public static final Field STRING_FIELD = new Field(BasicType.STRING, 7, "stringField", false);
	public static final Field BINARY_FIELD = new Field(BasicType.BINARY, 8, "binaryField", false);
	public static final Field LIST_OF_INTS_FIELD = new Field(new ListType(BasicType.I32), 9, "listOfIntsField", false);
	public static final Field SET_OF_INTS_FIELD = new Field(new SetType(BasicType.I32), 10, "setOfIntsField", false);
	public static final Field MAP_OF_INTS_INTS_FIELD =
		new Field(new MapType(BasicType.I32, BasicType.I32), 11, "mapOfIntsIntsField",
		          false);
	public static final Field NESTED_LIST_OF_INTS_FIELD = new Field(new ListType(new ListType(BasicType.I32)), 13,
	                                                          "nestedListOfIntsField",
	                                                          false);
}
