package org.cwi.waebric.parser.ast;

import static org.junit.Assert.*;

import org.cwi.waebric.parser.ast.markup.Argument;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestSyntaxNodeList {

	private SyntaxNodeList<Argument> arguments;
	
	@Before
	public void setUp() {
		arguments = new SyntaxNodeList<Argument>();
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
		assertTrue(arguments.getChildren().length == 2);
		
		arguments.add(new Argument());
		assertTrue(arguments.getChildren().length == 3);
	}
	
	@Test
	public void testRemove() {
		assertTrue(arguments.getChildren().length == 2);
		
		Argument argument = new Argument();
		arguments.add(argument);
		assertTrue(arguments.getChildren().length == 3);
		assertTrue(arguments.getChildren()[2].equals(argument));
		
		arguments.remove(2);
		assertTrue(arguments.getChildren().length == 2);
	}

}