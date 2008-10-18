package mt.serialization.visitor;

import mt.serialization.schema.StructureType;
import mt.serialization.schema.Field;
import mt.serialization.schema.BinaryType;

public class ThriftReaderCompiler
	implements Visitor
{
	public void visit(StructureType type)
	{
		System.out.println("protocol.readStructBegin()");

		System.out.println("while (true) {");
		System.out.println("\t");
		System.out.println("}");
		System.out.println("protocol.readStructEnd()");
	}

	public void visit(Field type)
	{

	}

	public void visit(BinaryType type)
	{

	}
}
