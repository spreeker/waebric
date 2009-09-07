package org.cwi.waebric.parser.ast.markup;

import org.cwi.waebric.parser.ast.SyntaxNode;
import org.cwi.waebric.parser.ast.INodeVisitor;
import org.cwi.waebric.parser.ast.basic.IdCon;
import org.cwi.waebric.parser.ast.expression.Expression;

/**
 * Argument
 * @author Jeroen van Schagen
 * @date 22-06-2009
 */
public abstract class Argument extends SyntaxNode {

	protected Expression expression;

	public Expression getExpression() {
		return expression;
	}

	public void setExpression(Expression expression) {
		this.expression = expression;
	}
	
	/**
	 * Expression -> Argument
	 * @author Jeroen van Schagen
	 * @date 22-06-2009
	 */
	public static class RegularArgument extends Argument {
		
		public RegularArgument() { }
		
		public RegularArgument(Expression expression) {
			setExpression(expression);
		}
		
		public SyntaxNode[] getChildren() {
			return new SyntaxNode[] { expression };
		}
		
			@Override
	public <T> T accept(INodeVisitor<T> visitor) {
		return visitor.visit(this);
	}
		
	}
	
	/**
	 * IdCon "=" Expression -> Argument
	 * @author Jeroen van Schagen
	 * @date 22-06-2009
	 */
	public static class Attr extends Argument {

		private IdCon identifier;
		
		public Attr(IdCon identifier) {
			setIdentifier(identifier);
		}
		
		public Attr(IdCon identifier, Expression expression) {
			setIdentifier(identifier);
			setExpression(expression);
		}
		
		public IdCon getIdentifier() {
			return identifier;
		}
		
		public void setIdentifier(IdCon identifier) {
			this.identifier = identifier;
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

}