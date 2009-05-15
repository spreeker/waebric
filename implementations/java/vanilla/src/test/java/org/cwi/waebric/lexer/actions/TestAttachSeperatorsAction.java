package org.cwi.waebric.lexer.actions;

import static org.junit.Assert.*;

import org.cwi.waebric.lexer.preprocess.AttachSeperatorsProcess;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestAttachSeperatorsAction {
	
	private AttachSeperatorsProcess action;
	
	@Before
	public void setUp() throws Exception {
		action = new AttachSeperatorsProcess();
	}
	
	@After
	public void tearDown() throws Exception {
		action = null;
	}

	@Test
	public void testSemicolon() {
		String text = "p;;lolal;;lolalz";
		String seperated = action.execute(text);
		assertNotNull(seperated);
		assertTrue(seperated.length() > text.length());
		assertTrue(seperated.charAt(1) == ' ');
		assertTrue(seperated.charAt(2) == ';');
		assertTrue(seperated.charAt(3) == ' ');
	}
	
	@Test
	public void testEqualSign() {
		String text = "p=143*34+324=f=g;";
		String seperated = action.execute(text);
		assertNotNull(seperated);
		assertTrue(seperated.length() > text.length());
		assertTrue(seperated.charAt(1) == ' ');
		assertTrue(seperated.charAt(2) == '=');
		assertTrue(seperated.charAt(3) == ' ');
	}

}
