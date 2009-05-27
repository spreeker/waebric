package org.cwi.waebric.parser;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.List;

import org.cwi.waebric.parser.ast.markup.Markup;
import org.cwi.waebric.parser.ast.site.Directory;
import org.cwi.waebric.parser.ast.site.FileName;
import org.cwi.waebric.parser.ast.site.Mapping;
import org.cwi.waebric.parser.ast.site.Path;
import org.cwi.waebric.parser.ast.site.Path.PathWithDir;
import org.cwi.waebric.parser.ast.site.Path.PathWithoutDir;
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
		Mapping mapping = null;
		
		// Path with directory, mapping with arguments
		iterator = TestScanner.quickScan("home/index.html: home(\"Hello world!\")");
		parser = new SiteParser(iterator, exceptions);
		
		mapping = new Mapping();
		parser.parse(mapping);
		
		// Assertions
		assertTrue(exceptions.size() == 0);
		assertTrue(mapping.getPath() instanceof Path.PathWithDir);
		assertTrue(mapping.getMarkup() instanceof Markup.MarkupWithArguments);
		
		// Path without directory, mapping without arguments
		iterator = TestScanner.quickScan("index.html: home");
		parser = new SiteParser(iterator, exceptions);
		
		mapping = new Mapping();
		parser.parse(mapping);
		
		// Assertions
		assertTrue(exceptions.size() == 0);
		assertTrue(mapping.getPath() instanceof Path.PathWithoutDir);
		assertTrue(mapping.getMarkup() instanceof Markup.MarkupWithoutArguments);
	}
	
	@Test
	public void testPath() {
		// Path with directory
		iterator = TestScanner.quickScan("org/cwi/waebric/java/vanilla/myfile.wae");
		parser = new SiteParser(iterator, exceptions);
		
		PathWithDir pathdf = new Path.PathWithDir();
		parser.parse(pathdf);
		
		// Assertions
		assertTrue(exceptions.size() == 0);
		assertTrue(pathdf.getDirName().getDirectory().getElements().length == 5);
		assertTrue(pathdf.getDirName().getDirectory().getElements()[0].equals("org"));
		assertTrue(pathdf.getDirName().getDirectory().getElements()[1].equals("cwi"));
		assertTrue(pathdf.getDirName().getDirectory().getElements()[2].equals("waebric"));
		assertTrue(pathdf.getDirName().getDirectory().getElements()[3].equals("java"));
		assertTrue(pathdf.getDirName().getDirectory().getElements()[4].equals("vanilla"));
		assertTrue(pathdf.getFileName().getName().equals("myfile"));
		assertTrue(pathdf.getFileName().getExt().equals("wae"));
		
		// Path without directory
		iterator = TestScanner.quickScan("myfile.wae");
		parser = new SiteParser(iterator, exceptions);
		
		PathWithoutDir pathf = new Path.PathWithoutDir();
		parser.parse(pathf);
		
		// Assertions
		assertTrue(exceptions.size() == 0);
		assertTrue(pathf.getFileName().getName().equals("myfile"));
		assertTrue(pathf.getFileName().getExt().equals("wae"));
	}
	
//	@Test
//	public void testDirName() {
//		iterator = TestScanner.quickScan("org/cwi/waebric/java/vanilla");
//		parser = new SiteParser(iterator, exceptions);
//		
//		DirName name = new DirName();
//		parser.visit(name); // Parse directory name
//		
//		// Assertions
//		assertTrue(exceptions.size() == 0);
//		assertTrue(name.getDirectory() instanceof Directory);
//	}

	@Test
	public void testFileName() {
		iterator = TestScanner.quickScan("myfile.wae");
		parser = new SiteParser(iterator, exceptions);
		
		FileName name = new FileName();
		parser.parse(name); // Parse filename
		
		// Assertions
		assertTrue(exceptions.size() == 0);
		assertTrue(name.getName().equals("myfile"));
		assertTrue(name.getExt().equals("wae"));
	}
	
	@Test
	public void testDirectory() {
		iterator = TestScanner.quickScan("org/cwi/waebric/java/vanilla");
		parser = new SiteParser(iterator, exceptions);
		
		Directory directory = new Directory();
		parser.parse(directory); // Parse directory
		
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
