package org.cwi.waebric.parser.ast.markup;

import org.cwi.waebric.WaebricSymbol;
import org.cwi.waebric.parser.ast.AbstractSyntaxNode;
import org.cwi.waebric.parser.ast.INodeVisitor;
import org.cwi.waebric.parser.ast.StringLiteral;
import org.cwi.waebric.parser.ast.basic.IdCon;
import org.cwi.waebric.parser.ast.expression.Expression;

/**
 * Argument
 * @author Jeroen van Schagen
 * @date 22-06-2009
 */
public abstract class Argument extends AbstractSyntaxNode {

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
		
		public AbstractSyntaxNode[] getChildren() {
			return new AbstractSyntaxNode[] { expression };
		}
		
		@Override
		public void accept(INodeVisitor visitor, Object[] args) {
			visitor.visit(this, args);
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
			this.identifier = identifier;
		}
		
		public IdCon getIdentifier() {
			return identifier;
		}

		public AbstractSyntaxNode[] getChildren() {
			return new AbstractSyntaxNode[] { 
				identifier,
				new StringLiteral("" + WaebricSymbol.EQUAL_SIGN),
				expression
			};
		}
		
		@Override
		public void accept(INodeVisitor visitor, Object[] args) {
			visitor.visit(this, args);
		}
		
	}

}