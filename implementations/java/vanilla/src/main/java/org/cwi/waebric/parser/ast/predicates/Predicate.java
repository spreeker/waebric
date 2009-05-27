package org.cwi.waebric.parser.ast.predicates;

import org.cwi.waebric.WaebricSymbol;
import org.cwi.waebric.parser.ast.CharacterLiteral;
import org.cwi.waebric.parser.ast.ISyntaxNode;
import org.cwi.waebric.parser.ast.expressions.Expression;

public abstract class Predicate implements ISyntaxNode {

	protected Expression expression;
	
	public Expression getExpression() {
		return expression;
	}

	public void setExpression(Expression expression) {
		this.expression = expression;
	}

	/**
	 * Expression -> Predicate
	 * @author schagen
	 *
	 */
	public static class PredicateWithoutType extends Predicate {

		public ISyntaxNode[] getChildren() {
			return new ISyntaxNode[] { expression };
		}
		
	}
	
	/**
	 * Expression "." Type "?" -> Predicate
	 * @author schagen
	 *
	 */
	public static class PredicateWithType extends Predicate {

		private Type type;
		
		public Type getType() {
			return type;
		}

		public void setType(Type type) {
			this.type = type;
		}

		public ISyntaxNode[] getChildren() {
			return new ISyntaxNode[] { 
				expression,
				new CharacterLiteral(WaebricSymbol.PERIOD),
				type,
				new CharacterLiteral(WaebricSymbol.QUESTION_SIGN)
			};
		}
		
	}
	
}