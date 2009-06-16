package org.cwi.waebric.parser;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.cwi.waebric.parser.ast.markup.Markup;
import org.cwi.waebric.parser.ast.module.site.DirName;
import org.cwi.waebric.parser.ast.module.site.Directory;
import org.cwi.waebric.parser.ast.module.site.FileName;
import org.cwi.waebric.parser.ast.module.site.Mapping;
import org.cwi.waebric.parser.ast.module.site.Mappings;
import org.cwi.waebric.parser.ast.module.site.Path;
import org.cwi.waebric.parser.ast.module.site.Site;
import org.cwi.waebric.TestUtilities;
import org.cwi.waebric.scanner.token.TokenIterator;
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
	public void testSite() throws SyntaxException {
		iterator = TestUtilities.quickScan("site index1.html:function1(1);index2.html:function2 end");
		parser = new SiteParser(iterator, exceptions);
		
		Site site = parser.parseSite();
		assertEquals(2, site.getMappings().size());
	}
	
	@Test
	public void testMappings() throws SyntaxException {
		iterator = TestUtilities.quickScan("index1.html:function1(1);index2.html:function2");
		parser = new SiteParser(iterator, exceptions);
		
		Mappings mappings = parser.parseMappings();
		assertEquals(2, mappings.size());
	}
	
	@Test
	public void testMapping() throws SyntaxException {
		Mapping mapping = null;
		
		// Path with directory, mapping with arguments
		iterator = TestUtilities.quickScan("home/index.html: home(\"Hello world!\")");
		parser = new SiteParser(iterator, exceptions);
		
		mapping = parser.parseMapping();
		assertTrue(mapping.getPath() instanceof Path.PathWithDir);
		assertTrue(mapping.getMarkup() instanceof Markup.Call);
		
		// Path without directory, mapping without arguments
		iterator = TestUtilities.quickScan("index.html: home");
		parser = new SiteParser(iterator, exceptions);
		
		mapping = parser.parseMapping();
		assertTrue(mapping.getPath() instanceof Path.PathWithoutDir);
	}
	
	@Test
	public void testPath() throws SyntaxException {
		// Path with directory
		iterator = TestUtilities.quickScan("org/cwi/waebric/java/vanilla/myfile.wae");
		parser = new SiteParser(iterator, exceptions);
		
		Path.PathWithDir pathdf = (Path.PathWithDir) parser.parsePath();
		assertNotNull(pathdf.getDirName());
		assertNotNull(pathdf.getFileName());
		
		// Path without directory
		iterator = TestUtilities.quickScan("myfile.wae");
		parser = new SiteParser(iterator, exceptions);
		
		Path.PathWithoutDir pathf = (Path.PathWithoutDir) parser.parsePath();
		assertNotNull(pathf.getFileName());
	}
	
	@Test
	public void testDirName() throws SyntaxException {
		iterator = TestUtilities.quickScan("org/cwi/waebric/java/vanilla");
		parser = new SiteParser(iterator, exceptions);
		
		DirName dirName = parser.parseDirName();
		assertEquals(Directory.class, dirName.getDirectory().getClass());
	}

	@Test
	public void testFileName() throws SyntaxException {
		iterator = TestUtilities.quickScan("myfile.wae");
		parser = new SiteParser(iterator, exceptions);
		
		FileName name = parser.parseFileName();
		assertEquals("myfile", name.getName().toString());
		assertEquals("wae", name.getExt().toString());
	}
	
	@Test
	public void testDirectory() throws SyntaxException {
		iterator = TestUtilities.quickScan("org/cwi/waebric/java/vanilla");
		parser = new SiteParser(iterator, exceptions);
		
		Directory directory = parser.parseDirectory();
		assertEquals(5, directory.size());
		assertEquals("org", directory.get(0).toString());
		assertEquals("cwi", directory.get(1).toString());
		assertEquals("waebric", directory.get(2).toString());
		assertEquals("java", directory.get(3).toString());
		assertEquals("vanilla", directory.get(4).toString());
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