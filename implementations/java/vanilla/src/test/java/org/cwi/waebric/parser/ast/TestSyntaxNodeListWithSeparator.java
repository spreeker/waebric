package org.cwi.waebric.parser.ast;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.cwi.waebric.parser.ast.AbstractSyntaxNodeList.AbstractSeparatedSyntaxNodeList;
import org.cwi.waebric.parser.ast.site.Mapping;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestSyntaxNodeListWithSeparator {

	private AbstractSeparatedSyntaxNodeList<Mapping> mappings;
	
	@Before
	public void setUp() {
		mappings = new AbstractSeparatedSyntaxNodeList<Mapping>(';');
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
		assertTrue(mappings.getChildren().length == 3);
		assertEquals(Mapping.class, mappings.getChildren()[0].getClass());
		assertEquals(CharacterLiteral.class, mappings.getChildren()[1].getClass());
		
		int expected = 3;
		while(expected < 25) {
			mappings.add(new Mapping());
			expected += 2; // When an element is added, a separator is automatically applied
			assertTrue(mappings.getChildren().length == expected);
		}
	}
	
	@Test
	public void testRemove() {
		assertTrue(mappings.getChildren().length == 3);
		
		Mapping mapping = new Mapping();
		mappings.add(mapping);
		assertTrue(mappings.getChildren().length == 5);
		assertTrue(mappings.getChildren()[4].equals(mapping));
		
		mappings.remove(2);
		assertTrue(mappings.getChildren().length == 3);
	}
	
}
