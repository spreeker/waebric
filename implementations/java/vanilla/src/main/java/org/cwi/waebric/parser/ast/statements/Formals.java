package org.cwi.waebric.parser.ast.statements;

import org.cwi.waebric.WaebricSymbol;
import org.cwi.waebric.parser.ast.CharacterLiteral;
import org.cwi.waebric.parser.ast.ISyntaxNode;
import org.cwi.waebric.parser.ast.SyntaxNodeList.SyntaxNodeListWithSeparator;
import org.cwi.waebric.parser.ast.expressions.Var;

/**
 * "(" { Var "," }* ")" -> Formals
 * @author schagen
 *
 */
public class Formals implements ISyntaxNode {
	
	private SyntaxNodeListWithSeparator<Var> vars;
	
	public Formals() {
		vars = new SyntaxNodeListWithSeparator<Var>(WaebricSymbol.COMMA);
	}
	
	public int getVarCount() {
		return vars.size();
	}
	
	public Var getVar(int index) {
		return vars.get(index);
	}
	
	public boolean addVar(Var var) {
		return vars.add(var);
	}

	public ISyntaxNode[] getChildren() {
		return new ISyntaxNode[] {
			new CharacterLiteral(WaebricSymbol.LPARANTHESIS),
			vars,
			new CharacterLiteral(WaebricSymbol.RPARANTHESIS)
		};
	}

}
