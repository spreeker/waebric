package org.cwi.waebric.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.cwi.waebric.lexer.token.TokenIterator;
import org.cwi.waebric.parser.ast.markup.Markup;
import org.cwi.waebric.parser.ast.module.site.Mapping;
import org.cwi.waebric.parser.ast.module.site.Mappings;
import org.cwi.waebric.parser.ast.module.site.Path;
import org.cwi.waebric.parser.ast.module.site.Site;
import org.cwi.waebric.TestUtilities;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestSiteParser {

	private SiteParser parser;
	
	private List<SyntaxException> exceptions;
	private TokenIterator iterator;
	
	@Before
	public void setUp() {
		exceptions = new ArrayList<SyntaxException>();
	}
	
	@After
	public void tearDown() {
		exceptions.clear();
		exceptions = null;
		parser = null;
		iterator = null;
	}
	
	@Test
	public void testSite() throws SyntaxException, IOException {
		iterator = TestUtilities.quickScan("site index1.html:function1(1);index2.html:function2 end");
		parser = new SiteParser(iterator, exceptions);
		
		Site site = parser.parseSite();
		assertEquals(2, site.getMappings().size());
	}
	
	@Test
	public void testMappings() throws SyntaxException, IOException {
		iterator = TestUtilities.quickScan("index1.html:function1(1);index2.html:function2");
		parser = new SiteParser(iterator, exceptions);
		
		Mappings mappings = parser.parseMappings();
		assertEquals(2, mappings.size());
	}
	
	@Test
	public void testMapping() throws SyntaxException, IOException {
		Mapping mapping = null;
		
		// Path with directory, mapping with arguments
		iterator = TestUtilities.quickScan("home/index.html: home(\"Hello world!\")");
		parser = new SiteParser(iterator, exceptions);
		
		mapping = parser.parseMapping();
		assertEquals("home/index.html", mapping.getPath().getValue().toString());
		assertTrue(mapping.getMarkup() instanceof Markup.Call);
		
		// Path without directory, mapping without arguments
		iterator = TestUtilities.quickScan("index.html: home");
		parser = new SiteParser(iterator, exceptions);
		
		mapping = parser.parseMapping();
		assertEquals("index.html", mapping.getPath().getValue().toString());
	}
	
	@Test
	public void testPath() throws SyntaxException, IOException {
		// Path with directory
		iterator = TestUtilities.quickScan("org/cwi/waebric/java/vanilla/myfile.wae");
		parser = new SiteParser(iterator, exceptions);
		
		Path pathdf = parser.parsePath();
		assertNotNull(pathdf.getValue());
		
		// Path without directory
		iterator = TestUtilities.quickScan("myfile.wae");
		parser = new SiteParser(iterator, exceptions);
		
		Path pathf = parser.parsePath();
		assertNotNull(pathf.getValue());
	}
	
	@Test
	public void testDirectory() throws SyntaxException, IOException {
		iterator = TestUtilities.quickScan("org/cwi/waebric/java/vanilla");
		parser = new SiteParser(iterator, exceptions);
		
		String dirName = parser.parseDirectory();
		assertEquals("org/cwi/waebric/java/vanilla", dirName);
	}

	@Test
	public void testFileName() throws SyntaxException, IOException {
		iterator = TestUtilities.quickScan("myfile.wae");
		parser = new SiteParser(iterator, exceptions);
		
		String name = parser.parseFileName();
		assertEquals("myfile.wae", name);
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