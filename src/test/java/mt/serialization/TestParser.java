package mt.serialization;

import org.antlr.runtime.ANTLRFileStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.tree.CommonTree;
import org.testng.annotations.Test;

public class TestParser
{
	@Test
	public void testParser()
		throws Exception
	{
		ThriftLexer lexer = new ThriftLexer(new ANTLRFileStream("src/test/java/mt/serialization/TSimple.thrift"));
		TokenStream tokenStream = new CommonTokenStream(lexer);

//		Token t = null;
//		do {
//			t = tokenStream.LT(1);
//			System.out.println(t.getText());
//			tokenStream.consume();
//		}
//		while (t.getType() != Token.EOF);

		ThriftParser parser = new ThriftParser(tokenStream) {
			public void traceIn(String s, int i)
			{
				System.out.println(s);
			}

			public void traceOut(String s, int i)
			{
				System.out.println(s);
			}

			public void traceIn(String s, int i, Object o)
			{
				System.out.println(s);
			}

			public void traceOut(String s, int i, Object o)
			{
				System.out.println(s);
			}
		};

		CommonTree tree = (CommonTree) parser.document().getTree();

		System.out.println(tree.toStringTree());
	}

	@Test
	public void testParserComplex()
		throws Exception
	{
		ThriftLexer lexer = new ThriftLexer(new ANTLRFileStream("src/test/java/mt/serialization/tutorial.thrift"));
		ThriftParser parser = new ThriftParser(new CommonTokenStream(lexer));

		CommonTree tree = (CommonTree) parser.document().getTree();

		System.out.println(tree.toStringTree());
	}
}
