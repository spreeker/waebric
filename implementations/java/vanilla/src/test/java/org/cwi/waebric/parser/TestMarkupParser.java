package org.cwi.waebric.parser;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.cwi.waebric.parser.exception.ParserException;
import org.cwi.waebric.scanner.token.TokenIterator;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestMarkupParser {

	private MarkupParser parser;
	
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
	public void testMarkup() {
		// TODO
	}
	
	@Test
	public void testDesignator() {
		// TODO
	}
	
	@Test
	public void testArguments() {
		// TODO
	}
	
	@Test
	public void testArgument() {
		// TODO
	}
	
	@Test
	public void testAttribute() {
		// TODO
	}
	
	@Test
	public void testAttributes() {
		// TODO
	}
	
	@Test
	public void testVar() {
		// TODO
	}

	@Test
	public void testIsAttribute() {
		assertTrue(MarkupParser.isAttribute("#Hello"));
		assertTrue(MarkupParser.isAttribute("#Hello123"));
		assertTrue(MarkupParser.isAttribute("#123"));
		assertFalse(MarkupParser.isAttribute("#"));
		
		assertTrue(MarkupParser.isAttribute(".Hello"));
		assertTrue(MarkupParser.isAttribute(".Hello123"));
		assertTrue(MarkupParser.isAttribute(".123"));
		assertFalse(MarkupParser.isAttribute("."));
		
		assertTrue(MarkupParser.isAttribute("$Hello"));
		assertTrue(MarkupParser.isAttribute("$Hello123"));
		assertTrue(MarkupParser.isAttribute("$123"));
		assertFalse(MarkupParser.isAttribute("$"));
		
		assertTrue(MarkupParser.isAttribute(":Hello"));
		assertTrue(MarkupParser.isAttribute(":Hello123"));
		assertTrue(MarkupParser.isAttribute(":123"));
		assertFalse(MarkupParser.isAttribute(":"));
		
		assertTrue(MarkupParser.isAttribute("@123"));
		assertFalse(MarkupParser.isAttribute("@Hello"));
		assertFalse(MarkupParser.isAttribute("@Hello123"));
		assertFalse(MarkupParser.isAttribute("@"));
		
		assertTrue(MarkupParser.isAttribute("@123%123"));
		assertFalse(MarkupParser.isAttribute("@Hello%Hello"));
		assertFalse(MarkupParser.isAttribute("@Hello123%Hello123"));
		assertFalse(MarkupParser.isAttribute("@%"));
		assertFalse(MarkupParser.isAttribute("@%123"));
		assertFalse(MarkupParser.isAttribute("@%Hello"));
		assertFalse(MarkupParser.isAttribute("@%Hello123"));
	}

}