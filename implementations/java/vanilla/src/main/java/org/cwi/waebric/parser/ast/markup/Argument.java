package org.cwi.waebric.parser.ast.markup;

import org.cwi.waebric.parser.ast.ISyntaxNode;
import org.cwi.waebric.parser.ast.expressions.Expression;

public abstract class Argument implements ISyntaxNode {

	protected Expression expression;

	public Expression getExpression() {
		return expression;
	}

	public void setExpression(Expression expression) {
		this.expression = expression;
	}

	public static class ArgumentWithVar extends Argument {

		@Override
		public ISyntaxNode[] getChildren() {
			// TODO Auto-generated method stub
			return null;
		}
		
	}
	
	public static class ArgumentWithoutVar extends Argument {

		@Override
		public ISyntaxNode[] getChildren() {
			return new ISyntaxNode[] { expression };
		}
		
	}

}
