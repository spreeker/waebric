package org.cwi.waebric.parser.ast.statements;

import org.cwi.waebric.WaebricSymbol;
import org.cwi.waebric.parser.ast.CharacterLiteral;
import org.cwi.waebric.parser.ast.ISyntaxNode;
import org.cwi.waebric.parser.ast.basic.IdCon;

/**
 * IdCon Formals "=" Statement -> Assignment
 * @author schagen
 *
 */
public class Assignment implements ISyntaxNode {

	private IdCon identifier;
	private Formals formals;
	private Statement statement;
	
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

	public Statement getStatement() {
		return statement;
	}

	public void setStatement(Statement statement) {
		this.statement = statement;
	}

	public ISyntaxNode[] getChildren() {
		return new ISyntaxNode[] {
			identifier,
			formals,
			new CharacterLiteral(WaebricSymbol.EQUAL_SIGN),
			statement
		};
	}

}