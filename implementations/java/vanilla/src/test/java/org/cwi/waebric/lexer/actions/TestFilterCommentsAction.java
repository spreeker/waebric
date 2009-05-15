package org.cwi.waebric.lexer.actions;

import static org.junit.Assert.*;

import java.io.FileInputStream;

import org.cwi.waebric.lexer.WaebricLexer;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestFilterCommentsAction {

	private final String PROGRAM_PATH = "src/test/waebric/hellocomments.waebric";
	
	private FilterCommentsAction action;
	private String data;
	
	@Before
	public void setUp() throws Exception {
		action = new FilterCommentsAction();
		data = new WaebricLexer().parseStream(new FileInputStream(PROGRAM_PATH));
	}

	@After
	public void tearDown() throws Exception {
		data = "";
	}

	@Test
	public void testRemoveStringAt() {
		String text = "test me please";
		String stripped = action.removeStringAt(text, 6, 8);
		assertNotNull(stripped);
		assertTrue(stripped.equals("test please"));
	}
	
	@Test
	public void testExecute() {
		String plainProgram = action.execute(data);
		assertNotNull(plainProgram);
		assertTrue(plainProgram.indexOf(action.MLC_START) == -1);
		assertTrue(plainProgram.indexOf(action.MLC_END) == -1);
		assertTrue(plainProgram.indexOf(action.SLC_START) == -1);
	}

}