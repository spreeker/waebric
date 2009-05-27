package org.cwi.waebric.parser.ast.functions;

import org.cwi.waebric.WaebricKeyword;
import org.cwi.waebric.parser.ast.ISyntaxNode;
import org.cwi.waebric.parser.ast.StringLiteral;
import org.cwi.waebric.parser.ast.SyntaxNodeList;
import org.cwi.waebric.parser.ast.basic.IdCon;
import org.cwi.waebric.parser.ast.module.IModuleElement;
import org.cwi.waebric.parser.ast.statements.Formals;
import org.cwi.waebric.parser.ast.statements.Statement;

/**
 * "def" IdCon Formals? Statement* "end" -> FunctionDef
 * @author schagen
 *
 */
public class FunctionDef implements IModuleElement {
	
	// Keyword literals
	private static final String DEF_LITERAL = WaebricKeyword.getLiteral(WaebricKeyword.DEF);
	private static final String END_LITERAL = WaebricKeyword.getLiteral(WaebricKeyword.END);
	
	private IdCon identifier;
	private Formals formals;
	private SyntaxNodeList<Statement> statements;

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

	public Statement getStatement(int index) {
		return statements.get(index);
	}

	public boolean addStatement(Statement statement) {
		return statements.add(statement);
	}
	
	public int getStatementCount() {
		return statements.size();
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