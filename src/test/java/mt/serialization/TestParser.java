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
package mt.serialization;

import mt.serialization.parser.ThriftLexer;
import mt.serialization.parser.ThriftParser;
import org.antlr.runtime.ANTLRFileStream;
import org.antlr.runtime.CommonTokenStream;
import org.antlr.runtime.TokenStream;
import org.antlr.runtime.tree.CommonTree;

public class TestParser
{
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

	public void testParserComplex()
		throws Exception
	{
		ThriftLexer lexer = new ThriftLexer(new ANTLRFileStream("src/test/java/mt/serialization/tutorial.thrift"));
		ThriftParser parser = new ThriftParser(new CommonTokenStream(lexer));

		CommonTree tree = (CommonTree) parser.document().getTree();

		System.out.println(tree.toStringTree());
	}
}
