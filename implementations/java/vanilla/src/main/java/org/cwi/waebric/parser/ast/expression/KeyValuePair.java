package org.cwi.waebric.parser.ast.expression;

import org.cwi.waebric.parser.ast.SyntaxNode;
import org.cwi.waebric.parser.ast.INodeVisitor;
import org.cwi.waebric.parser.ast.basic.IdCon;

/**
 * IdCon ":" Expression -> Expression
 * @author schagen
 *
 */
public class KeyValuePair extends SyntaxNode {

	private IdCon identifier;
	private Expression expression;
	
	public KeyValuePair() { }
	
	public KeyValuePair(IdCon identifier, Expression expression) {
		this.identifier = identifier;
		this.expression = expression;
	}
	
	public IdCon getIdentifier() {
		return identifier;
	}

	public void setIdentifier(IdCon identifier) {
		this.identifier = identifier;
	}

	public Expression getExpression() {
		return expression;
	}

	public void setExpression(Expression expression) {
		this.expression = expression;
	}

	public SyntaxNode[] getChildren() {
		return new SyntaxNode[] { 
			identifier,
			expression
		};
	}
	
	@Override
	public <T> T accept(INodeVisitor<T> visitor) {
		return visitor.visit(this);
	}

}