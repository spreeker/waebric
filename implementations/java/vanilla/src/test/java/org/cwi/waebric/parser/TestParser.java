package org.cwi.waebric.parser;

import static org.junit.Assert.*;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.util.List;

import org.cwi.waebric.parser.ast.ISyntaxNode;
import org.cwi.waebric.parser.ast.SyntaxTree;
import org.cwi.waebric.parser.ast.basic.IdCon;
import org.cwi.waebric.parser.ast.module.Import;
import org.cwi.waebric.parser.ast.module.Module;
import org.cwi.waebric.parser.ast.module.Modules;
import org.cwi.waebric.parser.exception.ParserException;
import org.cwi.waebric.scanner.WaebricScanner;
import org.junit.Test;

public class TestParser {

	@Test
	public void testModules() throws IOException {
		WaebricParser parser;
		List<ParserException> exceptions;
		SyntaxTree tree;
		Module module;
		
		/**
		 * Correct scenarios
		 */
		parser = getParser(new StringReader("module test"));
		exceptions = parser.parseTokens();
		assertNotNull(exceptions);
		assertTrue(exceptions.size() == 0);
		
		tree = parser.getAbstractSyntaxTree();
		assertNotNull(tree);
		assertTrue(tree.getRoot() instanceof Modules);
		assertTrue(tree.getRoot().getChildren()[0] instanceof Module);
		module = (Module) tree.getRoot().getChildren()[0];
		
		// Import
		parser = getParser(new StringReader("module test\nimport idcon"));
		exceptions = parser.parseTokens();
		assertNotNull(exceptions);
		assertTrue(exceptions.size() == 0);
		
		tree = parser.getAbstractSyntaxTree();
		module = (Module) tree.getRoot().getChildren()[0];
		
		Import imprt = (Import) module.getElements()[0];
		assertNotNull(imprt);
		ISyntaxNode[] identifierElements = imprt.getIdentifier().getIdentifierElements();
		assertTrue(identifierElements.length == 1);
		assertTrue(identifierElements[0].equals("idcon"));
		
		// Site
		parser = getParser(new StringReader("module test \n site index.html: home(\"Hello World!\") index2.html: home2(\"Hello World2!\") \n end"));
		exceptions = parser.parseTokens();
		assertNotNull(exceptions);
		assertTrue(exceptions.size() == 0);
		
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
