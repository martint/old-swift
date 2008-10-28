package mt.serialization;

import mt.serialization.schema.StructureType;
import org.antlr.runtime.ANTLRStringStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.RecognitionException;
import org.antlr.runtime.tree.CommonTree;

public class Dictionary
{
	private final String[] includePaths;

	public Dictionary(String[] includePaths)
	{
		this.includePaths = includePaths;
	}

	public void add(StructureType structure)
	{

	}

	public void add(String structure)
	{
		ThriftLexer lexer = new ThriftLexer(new ANTLRStringStream(structure));
		ThriftParser parser = new ThriftParser(new CommonTokenStream(lexer));

		try {
			CommonTree tree = (CommonTree) parser.document().getTree();
		}
		catch (RecognitionException e) {
			throw new RuntimeException(e);
		}

		// TODO: parse structure
		// TODO: resolve namespaces
		// TODO: resolve includes
		// TODO: convert to StructureType
	}
}
