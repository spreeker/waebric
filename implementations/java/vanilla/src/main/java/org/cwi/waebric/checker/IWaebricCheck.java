package org.cwi.waebric.checker;

import java.util.List;

import org.cwi.waebric.parser.ast.module.Modules;

public interface IWaebricCheck {

	public List<Exception> checkAST(Modules modules);
	
}