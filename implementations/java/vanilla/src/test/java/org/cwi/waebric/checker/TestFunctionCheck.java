package org.cwi.waebric.checker;

import java.util.ArrayList;
import java.util.List;

import org.junit.After;

public class TestFunctionCheck {

	private FunctionCheck check;
	private WaebricChecker checker;
	private List<SemanticException> exceptions;
	
	public TestFunctionCheck() {
		exceptions = new ArrayList<SemanticException>();
		checker = new WaebricChecker();
		check = new FunctionCheck(checker);
	}
	
	@After
	public void tearDown() {
		exceptions.clear();
	}
	
	
	
	
	
}
