package org.cwi.waebric.parser;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;

import org.cwi.waebric.parser.ast.SyntaxTree;
import org.cwi.waebric.parser.ast.module.Modules;
import org.cwi.waebric.scanner.WaebricScanner;
import org.junit.Test;

public class TestParser {

	@Test
	public void testModules() throws IOException {
		WaebricParser parser;
		List<ParserException> exceptions;
		
		/**
		 * Correct scenarios
		 */
		parser = getParser(new StringReader("module test"));
		exceptions = parser.parseTokens();
		assertNotNull(exceptions);
		assertTrue(exceptions.size() == 0);
		
		SyntaxTree tree = parser.getAbstractSyntaxTree();
		assertNotNull(tree);
		assertTrue(tree.getRoot() instanceof Modules);
		
		// TODO: Import
		
		
		// TODO: Site
		
		
		// TODO: Function
		
		
		/**
		 * Error scenarios
		 */
		// Fragment outside module and missing identifier
		parser = getParser(new StringReader("test module"));
		exceptions = parser.parseTokens();
		assertTrue(exceptions.size() == 2);
		
		// Invalid module identifier
		parser = getParser(new StringReader("module 1"));
		exceptions = parser.parseTokens();
		assertTrue(exceptions.size() == 1);
	}
	
	public WaebricParser getParser(Reader reader) throws IOException {
		WaebricScanner scanner = new WaebricScanner(reader);
		scanner.tokenizeStream();
		reader.close();
		
		return new WaebricParser(scanner);
	}
	
}
