package org.cwi.waebric.checker;

import org.cwi.waebric.parser.ast.DefaultNodeVisitor;
import org.cwi.waebric.parser.ast.markup.Markup;
import org.cwi.waebric.parser.ast.module.function.FunctionDef;
import org.cwi.waebric.parser.ast.statement.Assignment.FuncBind;
import org.cwi.waebric.parser.ast.statement.Assignment.VarBind;

public class CheckerVisitor extends DefaultNodeVisitor {

	@Override
	public void visit(FunctionDef function) {
		
	}
	
	@Override
	public void visit(FuncBind function) {
		
	}
	
	@Override
	public void visit(VarBind variable) {
		
	}
	
	@Override
	public void visit(Markup.Tag tag) {
		
	}
	
	@Override
	public void visit(Markup.Call call) {
		
	}
	
}
