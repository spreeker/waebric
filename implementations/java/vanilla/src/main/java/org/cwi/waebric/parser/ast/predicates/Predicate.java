package org.cwi.waebric.parser.ast.predicates;

import org.cwi.waebric.WaebricSymbol;
import org.cwi.waebric.parser.ast.CharacterLiteral;
import org.cwi.waebric.parser.ast.ISyntaxNode;
import org.cwi.waebric.parser.ast.expressions.Expression;

/**
 * Predicates are ...
 * 
 * @author Jeroen van Schagen
 * @date 27-05-2009
 */
public abstract class Predicate implements ISyntaxNode {

	protected Expression expression;
	
	public Expression getExpression() {
		return expression;
	}

	public void setExpression(Expression expression) {
		this.expression = expression;
	}

	/**
	 * Grammar:<br>
	 * <code>
	 * 	Expression -> Predicate
	 * </code>
	 */
	public static class PredicateWithoutType extends Predicate {

		public ISyntaxNode[] getChildren() {
			return new ISyntaxNode[] { expression };
		}
		
	}
	
	/**
	 * Grammar:<br>
	 * <code>
	 * 	Expression "." Type "?" -> Predicate
	 * </code>
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