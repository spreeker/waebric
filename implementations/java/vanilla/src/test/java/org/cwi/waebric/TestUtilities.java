package org.cwi.waebric;

import static org.junit.Assert.assertEquals;

import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.cwi.waebric.parser.SyntaxException;
import org.cwi.waebric.parser.WaebricParser;
import org.cwi.waebric.parser.ast.AbstractSyntaxTree;
import org.cwi.waebric.scanner.LexicalException;
import org.cwi.waebric.scanner.WaebricScanner;
import org.cwi.waebric.scanner.token.Token;
import org.cwi.waebric.scanner.token.TokenIterator;

public class TestUtilities {
	
	public static TokenIterator quickScan(String data) {
		try {
			Reader reader = new StringReader(data);
			WaebricScanner scanner = new WaebricScanner(reader);
			List<LexicalException> e = scanner.tokenizeStream();
			assertEquals(0, e.size());
			return scanner.iterator();
		} catch(IOException e) {
			return new TokenIterator(new ArrayList<Token>());
		}
	}
	
	public static AbstractSyntaxTree quickParse(String path) {
		try {
			FileReader reader = new FileReader(path);
			WaebricScanner scanner = new WaebricScanner(reader);
			List<LexicalException> le = scanner.tokenizeStream();
			WaebricParser parser = new WaebricParser(scanner.iterator());
			List<SyntaxException> se = parser.parseTokens();
			assertEquals(0, le.size());
			assertEquals(0, se.size());
			// Retrieve root node
			return parser.getAbstractSyntaxTree();
		} catch(IOException e) {
			return new AbstractSyntaxTree();
		}
	}
	
}
