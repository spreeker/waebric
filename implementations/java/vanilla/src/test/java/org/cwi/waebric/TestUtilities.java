package org.cwi.waebric;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;

import org.cwi.waebric.parser.WaebricParser;
import org.cwi.waebric.parser.ast.AbstractSyntaxTree;
import org.cwi.waebric.parser.exception.SyntaxException;
import org.cwi.waebric.scanner.WaebricScanner;
import org.cwi.waebric.scanner.token.TokenIterator;

public class TestUtilities {
	
	public static TokenIterator quickScan(String data) {
		Reader reader = new StringReader(data);
		WaebricScanner scanner = new WaebricScanner(reader);
		scanner.tokenizeStream();
		return scanner.iterator();
	}
	
	public static AbstractSyntaxTree quickParse(String path) throws FileNotFoundException {
		FileReader reader = new FileReader(path);
		WaebricScanner scanner = new WaebricScanner(reader);
		scanner.tokenizeStream();
		WaebricParser parser = new WaebricParser(scanner);
		List<SyntaxException> e = parser.parseTokens();
		assertEquals(0, e.size());

		// Retrieve root node
		return parser.getAbstractSyntaxTree();
	}
	
}
