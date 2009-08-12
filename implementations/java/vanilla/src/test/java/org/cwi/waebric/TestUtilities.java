package org.cwi.waebric;

import static org.junit.Assert.assertEquals;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;

import org.cwi.waebric.lexer.LexicalException;
import org.cwi.waebric.lexer.WaebricScanner;
import org.cwi.waebric.lexer.token.TokenIterator;
import org.cwi.waebric.parser.WaebricParser;
import org.cwi.waebric.parser.ast.AbstractSyntaxTree;
import org.cwi.waebric.parser.exception.SyntaxException;

public class TestUtilities {
	
	public static TokenIterator quickScan(String data) throws IOException {
		Reader reader = new StringReader(data);
		WaebricScanner scanner = new WaebricScanner(reader);
		List<LexicalException> e = scanner.tokenizeStream();
		assertEquals(0, e.size());
		return scanner.iterator();
	}
	
	public static AbstractSyntaxTree quickParse(String path) throws IOException {
		FileReader reader = new FileReader(path);
		WaebricScanner scanner = new WaebricScanner(reader);
		List<LexicalException> le = scanner.tokenizeStream();
		WaebricParser parser = new WaebricParser(scanner.iterator());
		List<SyntaxException> se = parser.parseTokens();
		assertEquals(0, le.size());
		assertEquals(0, se.size());
		return parser.getAbstractSyntaxTree();
	}
	
}
