package org.cwi.waebric.parser.ast.module.function;

import java.util.List;

import org.cwi.waebric.WaebricKeyword;
import org.cwi.waebric.parser.ast.AbstractSyntaxNode;
import org.cwi.waebric.parser.ast.StringLiteral;
import org.cwi.waebric.parser.ast.AbstractSyntaxNodeList;
import org.cwi.waebric.parser.ast.basic.IdCon;
import org.cwi.waebric.parser.ast.module.ModuleElement;
import org.cwi.waebric.parser.ast.statement.Statement;

/**
 * "def" IdCon Formals? Statement* "end" -> FunctionDef
 * @author schagen
 *
 */
public class FunctionDef extends ModuleElement {
	
	// Keyword literals
	private static final String DEF_LITERAL = WaebricKeyword.getLiteral(WaebricKeyword.DEF);
	private static final String END_LITERAL = WaebricKeyword.getLiteral(WaebricKeyword.END);
	
	private IdCon identifier;
	private Formals formals;
	private AbstractSyntaxNodeList<Statement> statements;

	public FunctionDef() {
		statements = new AbstractSyntaxNodeList<Statement>();
	}
	
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

	public boolean addStatement(Statement statement) {
		return statements.add(statement);
	}
	
	public List<Statement> getStatements() {
		return statements;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof FunctionDef) {
			FunctionDef def = (FunctionDef) obj;
			return this.identifier.equals(def.getIdentifier());
		}
		
		return false;
	}

	public AbstractSyntaxNode[] getChildren() {
		return new AbstractSyntaxNode[] {
			new StringLiteral(DEF_LITERAL),
			identifier,
			formals,
			statements,
			new StringLiteral(END_LITERAL)
		};
	}

}