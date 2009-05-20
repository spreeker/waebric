package org.cwi.waebric.parser;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestMarkupParser {

	@Before
	public void setUp() throws Exception {
		// Currently not needed as only static method is tested
	}

	@After
	public void tearDown() throws Exception {
		// Currently not needed as only static method is tested
	}

	@Test
	public void testIsAttribute() {
		assertTrue(MarkupParser.isAttribute(""));
		
		assertTrue(MarkupParser.isAttribute("#Hello"));
		assertTrue(MarkupParser.isAttribute("#Hello123"));
		assertFalse(MarkupParser.isAttribute("#123"));
		assertFalse(MarkupParser.isAttribute("#"));
		
		assertTrue(MarkupParser.isAttribute(".Hello"));
		assertTrue(MarkupParser.isAttribute(".Hello123"));
		assertFalse(MarkupParser.isAttribute(".123"));
		assertFalse(MarkupParser.isAttribute("."));
		
		assertTrue(MarkupParser.isAttribute("$Hello"));
		assertTrue(MarkupParser.isAttribute("$Hello123"));
		assertFalse(MarkupParser.isAttribute("$123"));
		assertFalse(MarkupParser.isAttribute("$"));
		
		assertTrue(MarkupParser.isAttribute(":Hello"));
		assertTrue(MarkupParser.isAttribute(":Hello123"));
		assertFalse(MarkupParser.isAttribute(":123"));
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
