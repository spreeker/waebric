package org.cwi.waebric.parser.ast.module.function;

import java.util.List;

import org.cwi.waebric.WaebricKeyword;
import org.cwi.waebric.parser.ast.AbstractSyntaxNode;
import org.cwi.waebric.parser.ast.INodeVisitor;
import org.cwi.waebric.parser.ast.NodeList;
import org.cwi.waebric.parser.ast.basic.IdCon;
import org.cwi.waebric.parser.ast.statement.Statement;
import org.cwi.waebric.parser.ast.token.StringLiteral;

/**
 * "def" IdCon Formals? Statement* "end" -> FunctionDef
 * @author schagen
 *
 */
public class FunctionDef extends AbstractSyntaxNode {
	
	// Keyword literals
	private static final String DEF_LITERAL = WaebricKeyword.getLiteral(WaebricKeyword.DEF);
	private static final String END_LITERAL = WaebricKeyword.getLiteral(WaebricKeyword.END);
	
	private NodeList<Statement> statements = new NodeList<Statement>();
	
	private IdCon identifier;
	private Formals formals;

	public FunctionDef() { }
	
	public FunctionDef(IdCon identifier) {
		this(identifier, new Formals.EmptyFormal());
	}
	
	public FunctionDef(IdCon identifier, Formals formals) {
		this.identifier = identifier;
		this.formals = formals;
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

	public AbstractSyntaxNode[] getChildren() {
		return new AbstractSyntaxNode[] {
			new StringLiteral(DEF_LITERAL),
			identifier,
			formals,
			statements,
			new StringLiteral(END_LITERAL)
		};
	}
	
	@Override
	public void accept(INodeVisitor visitor) {
		visitor.visit(this);
	}

}