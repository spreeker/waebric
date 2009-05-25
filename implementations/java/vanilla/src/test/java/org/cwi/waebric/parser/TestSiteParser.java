package org.cwi.waebric.parser;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

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
		//iterator = TestScanner.quickScan("index.html: home(\"Hello World!\") end");
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
