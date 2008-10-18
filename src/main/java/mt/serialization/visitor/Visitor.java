package mt.serialization.visitor;

import mt.serialization.schema.BinaryType;
import mt.serialization.schema.Field;
import mt.serialization.schema.StructureType;

public interface Visitor
{
	void visit(StructureType type);
	void visit(Field type);
	void visit(BinaryType type);
}
