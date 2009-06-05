package org.cwi.waebric.parser;

import static org.junit.Assert.*;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.cwi.waebric.parser.ast.AbstractSyntaxTree;
import org.cwi.waebric.parser.exception.SyntaxException;
import org.cwi.waebric.scanner.WaebricScanner;
import org.junit.After;
import org.junit.Test;

public class TestParser {

	private List<SyntaxException> exceptions = new ArrayList<SyntaxException>();
	
	@After
	public void tearDown() {
		exceptions.clear();
	}
	
	@Test
	public void helloWorld() throws IOException {
		FileReader reader = new FileReader("src/test/waebric/helloworld.wae");
		WaebricScanner scanner = new WaebricScanner(reader);
		scanner.tokenizeStream();
		WaebricParser parser = new WaebricParser(scanner);
		exceptions = parser.parseTokens();
		AbstractSyntaxTree tree = parser.getAbstractSyntaxTree();
		assertNotNull(tree);
		assertTrue(exceptions.size() == 0);
	}
	
}
