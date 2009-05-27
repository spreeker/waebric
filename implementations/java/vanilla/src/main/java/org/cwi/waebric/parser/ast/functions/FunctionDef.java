package org.cwi.waebric.parser.ast.functions;

import org.cwi.waebric.WaebricKeyword;
import org.cwi.waebric.parser.ast.ISyntaxNode;
import org.cwi.waebric.parser.ast.StringLiteral;
import org.cwi.waebric.parser.ast.basic.IdCon;
import org.cwi.waebric.parser.ast.module.IModuleElement;
import org.cwi.waebric.parser.ast.statements.Formals;
import org.cwi.waebric.parser.ast.statements.Statements;

public class FunctionDef implements IModuleElement {
	
	// Keyword literals
	private static final String DEF_LITERAL = WaebricKeyword.DEF.name().toLowerCase();
	private static final String END_LITERAL = WaebricKeyword.END.name().toLowerCase();
	
	private IdCon identifier;
	private Formals formals;
	private Statements statements;

	public IdCon getIdentifier() {
		return identifier;
	}

	public void setIdentifier(IdCon identifier) {
		this.identifier = identifier;
	}

	public Formals getFormals() {
		return formals;
	}

	public void setFormals(Formals formals) {
		this.formals = formals;
	}

	public Statements getStatements() {
		return statements;
	}

	public void setStatements(Statements statements) {
		this.statements = statements;
	}

	public ISyntaxNode[] getChildren() {
		return new ISyntaxNode[] {
			new StringLiteral(DEF_LITERAL),
			identifier,
			formals,
			statements,
			new StringLiteral(END_LITERAL)
		};
	}

}
