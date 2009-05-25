package org.cwi.waebric.parser;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.cwi.waebric.parser.ast.site.Directory;
import org.cwi.waebric.parser.exception.ParserException;
import org.cwi.waebric.scanner.TestScanner;
import org.cwi.waebric.scanner.token.TokenIterator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestSiteParser {

	private SiteParser parser;
	
	private List<ParserException> exceptions;
	private TokenIterator iterator;
	
	@Before
	public void setUp() {
		exceptions = new ArrayList<ParserException>();
	}
	
	@After
	public void tearDown() {
		exceptions.clear();
		exceptions = null;
		parser = null;
		iterator = null;
	}
	
	@Test
	public void testSite() {
		// TODO: Test this last
		//iterator = TestScanner.quickScan("index.html: home(\"Hello World!\") end");
	}
	
	@Test
	public void testMappings() {
		// TODO
	}
	
	@Test
	public void testMapping() {
		// TODO
	}
	
	@Test
	public void testPath() {
		// TODO
	}
	
	@Test
	public void testDirName() {
		// TODO
	}

	@Test
	public void testFileName() {
		// TODO
	}
	
	@Test
	public void testDirectory() {
		iterator = TestScanner.quickScan("org/cwi/waebric/java/vanilla");
		parser = new SiteParser(iterator, exceptions);
		
		Directory directory = new Directory();
		parser.visit(directory); // Parse directory
		
		// Assertions
		assertTrue(exceptions.size() == 0);
		assertTrue(directory.getElements().length == 5);
		assertTrue(directory.getElements()[0].equals("org"));
		assertTrue(directory.getElements()[1].equals("cwi"));
		assertTrue(directory.getElements()[2].equals("waebric"));
		assertTrue(directory.getElements()[3].equals("java"));
		assertTrue(directory.getElements()[4].equals("vanilla"));
	}
	
	@Test
	public void testIsPathElement() {
		assertTrue(SiteParser.isPathElement("directory"));
		assertTrue(SiteParser.isPathElement("directory123"));
		assertTrue(SiteParser.isPathElement("directory%20%123"));
		assertTrue(SiteParser.isPathElement("directory\123"));
		assertFalse(SiteParser.isPathElement("directory 123"));
		assertFalse(SiteParser.isPathElement("directory\t123"));
		assertFalse(SiteParser.isPathElement("directory\n123"));
		assertFalse(SiteParser.isPathElement("directory\t123"));
		assertFalse(SiteParser.isPathElement("directory.123"));
		assertFalse(SiteParser.isPathElement("directory/123"));
		assertFalse(SiteParser.isPathElement("directory\\123"));
	}

}
