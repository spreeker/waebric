package org.cwi.waebric.parser.ast.markup;

import org.cwi.waebric.WaebricSymbol;
import org.cwi.waebric.parser.ast.ISyntaxNode;
import org.cwi.waebric.parser.ast.StringLiteral;
import org.cwi.waebric.parser.ast.expressions.Expression;
import org.cwi.waebric.parser.ast.expressions.Var;

public abstract class Argument implements ISyntaxNode {

	protected Expression expression;

	public Expression getExpression() {
		return expression;
	}

	public void setExpression(Expression expression) {
		this.expression = expression;
	}

	/**
	 * Var "=" Expression -> Argument
	 * @author schagen
	 *
	 */
	public static class ArgumentWithVar extends Argument {

		private Var var;

		public ArgumentWithVar(Var var) {
			this.var = var;
		}
		
		public Var getVar() {
			return var;
		}

		public void setVar(Var var) {
			this.var = var;
		}

		public ISyntaxNode[] getChildren() {
			return new ISyntaxNode[] { 
				var,
				new StringLiteral("" + WaebricSymbol.EQUAL_SIGN),
				expression
			};
		}
		
	}
	
	/**
	 * Expression -> Var
	 * @author schagen
	 *
	 */
	public static class ArgumentWithoutVar extends Argument {

		public ISyntaxNode[] getChildren() {
			return new ISyntaxNode[] { expression };
		}
		
	}

}