package org.cwi.waebric.parser.ast.markup;

import static org.junit.Assert.*;

import org.cwi.waebric.parser.ast.ISyntaxNode;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestArguments {

	private Arguments arguments;
	
	@Before
	public void setUp() {
		arguments = new Arguments();
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
		ISyntaxNode[] nodes;
		
		nodes = arguments.getChildren();
		assertNotNull(nodes);
		assertTrue(nodes.length == 3);
		
		arguments.add(new Argument());
		nodes = arguments.getChildren();
		assertTrue(nodes.length == 5);
	}
	
}
