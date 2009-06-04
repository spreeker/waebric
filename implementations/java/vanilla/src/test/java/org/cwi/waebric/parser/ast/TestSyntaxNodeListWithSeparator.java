package org.cwi.waebric.parser.ast;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.cwi.waebric.parser.ast.AbstractSyntaxNodeList.AbstractSeparatedSyntaxNodeList;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestSyntaxNodeListWithSeparator {

	private AbstractSeparatedSyntaxNodeList<StringLiteral> literals;
	
	@Before
	public void setUp() {
		literals = new AbstractSeparatedSyntaxNodeList<StringLiteral>(';');
		literals.add(new StringLiteral("LOL"));
		literals.add(new StringLiteral("LOL2"));
	}
	
	@After
	public void tearDown() {
		literals.clear();
		literals = null;
	}
	
	@Test
	public void testGetChildren() {
		AbstractSyntaxNode[] children = literals.getChildren();
		
		assertNotNull(children);
		assertEquals(3, children.length);
		assertEquals(StringLiteral.class, literals.getChildren()[0].getClass());
		assertEquals(CharacterLiteral.class, literals.getChildren()[1].getClass());
		
		int expected = 3;
		while(expected < 25) {
			literals.add(new StringLiteral("test"));
			expected += 2; // When an element is added, a separator is automatically applied
			assertTrue(literals.getChildren().length == expected);
		}
	}
	
	@Test
	public void testRemove() {
		assertTrue(literals.getChildren().length == 3);
		
		literals.add(new StringLiteral("mappingftw"));
		assertTrue(literals.getChildren().length == 5);
		assertEquals("mappingftw", literals.getChildren()[4].toString());
		
		literals.remove(2);
		assertTrue(literals.getChildren().length == 3);
	}
	
}
