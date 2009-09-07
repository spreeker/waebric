package org.cwi.waebric.parser.ast;

import static org.junit.Assert.*;

import org.cwi.waebric.parser.ast.module.site.Mapping;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestNodeList {

	private SyntaxNodeList<Mapping> mappings;
	
	@Before
	public void setUp() {
		mappings = new SyntaxNodeList<Mapping>();
		mappings.add(new Mapping());
		mappings.add(new Mapping());
	}
	
	@After
	public void tearDown() {
		mappings.clear();
		mappings = null;
	}
	
	@Test
	public void testGetChildren() {
		assertNotNull(mappings.getChildren());
		assertTrue(mappings.getChildren().length == 2);
		
		// Assert after inserting new element
		mappings.add(new Mapping());
		assertTrue(mappings.getChildren().length == 3);
	}
	
	@Test
	public void testRemove() {
		assertTrue(mappings.getChildren().length == 2);
		
		Mapping argument = new Mapping();
		mappings.add(argument);
		assertTrue(mappings.getChildren().length == 3);
		assertTrue(mappings.getChildren()[2].equals(argument));
		
		mappings.remove(2);
		assertTrue(mappings.getChildren().length == 2);
	}

}