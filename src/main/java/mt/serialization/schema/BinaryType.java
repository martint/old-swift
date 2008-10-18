package mt.serialization.schema;

import mt.serialization.visitor.Visitor;

public class BinaryType
{
	public void accept(Visitor visitor)
	{
		visitor.visit(this);
	}
}
