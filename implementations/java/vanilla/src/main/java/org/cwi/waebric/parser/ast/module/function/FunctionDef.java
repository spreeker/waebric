package org.cwi.waebric.parser.ast.module.function;

import java.util.List;

import org.cwi.waebric.parser.ast.SyntaxNode;
import org.cwi.waebric.parser.ast.INodeVisitor;
import org.cwi.waebric.parser.ast.SyntaxNodeList;
import org.cwi.waebric.parser.ast.basic.IdCon;
import org.cwi.waebric.parser.ast.statement.Statement;

/**
 * "def" IdCon Formals? Statement* "end" -> FunctionDef
 * @author schagen
 *
 */
public class FunctionDef extends SyntaxNode {

	private SyntaxNodeList<Statement> statements;
	private IdCon identifier;
	private Formals formals;

	public FunctionDef() {
		this.statements = new SyntaxNodeList<Statement>();
	}
	
	public FunctionDef(IdCon identifier) {
		this(identifier, new Formals.EmptyFormal());
	}
	
	public FunctionDef(IdCon identifier, Formals formals) {
		this.identifier = identifier;
		this.formals = formals;
		this.statements = new SyntaxNodeList<Statement>();
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
		return statements.clone();
	}

	public SyntaxNode[] getChildren() {
		return new SyntaxNode[] { identifier, formals, statements };
	}
	
	@Override
	public <T> T accept(INodeVisitor<T> visitor) {
		return visitor.visit(this);
	}

}