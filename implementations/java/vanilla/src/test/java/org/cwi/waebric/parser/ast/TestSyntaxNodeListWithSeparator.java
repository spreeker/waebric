package org.cwi.waebric.parser.ast;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.cwi.waebric.parser.ast.SyntaxNodeList.SyntaxNodeListWithSeparator;
import org.cwi.waebric.parser.ast.markup.Argument;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestSyntaxNodeListWithSeparator {

	private SyntaxNodeListWithSeparator<Argument> arguments;
	
	@Before
	public void setUp() {
		arguments = new SyntaxNodeListWithSeparator<Argument>(".");
		arguments.add(new Argument());
		arguments.add(new Argument());
	}
	
	@After
	public void tearDown() {
		arguments.clear();
		arguments = null;
	}
	
	@Test
	public void testGetChildren() {
		assertNotNull(arguments.getChildren());
		assertTrue(arguments.getChildren().length == 3);
		
		int expected = 3;
		while(expected < 25) {
			arguments.add(new Argument());
			expected += 2; // When an element is added, a separator is automatically applied
			assertTrue(arguments.getChildren().length == expected);
		}
	}
	
	@Test
	public void testRemove() {
		assertTrue(arguments.getChildren().length == 3);
		
		Argument argument = new Argument();
		arguments.add(argument);
		assertTrue(arguments.getChildren().length == 5);
		assertTrue(arguments.getChildren()[4].equals(argument));
		
		arguments.remove(2);
		assertTrue(arguments.getChildren().length == 3);
	}
	
}
