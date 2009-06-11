package org.cwi.waebric.parser.ast.statement.predicate;

import org.cwi.waebric.WaebricSymbol;
import org.cwi.waebric.parser.ast.AbstractSyntaxNode;
import org.cwi.waebric.parser.ast.CharacterLiteral;
import org.cwi.waebric.parser.ast.INodeVisitor;
import org.cwi.waebric.parser.ast.StringLiteral;
import org.cwi.waebric.parser.ast.expression.Expression;

/**
 * Predicates are conditions, used in if(-else) statement constructs.
 * 
 * @author Jeroen van Schagen
 * @date 27-05-2009
 */
public abstract class Predicate extends AbstractSyntaxNode {

	/**
	 * Grammar:<br>
	 * <code>
	 * 	Expression -> Predicate
	 * </code>
	 */
	public static class RegularPredicate extends Predicate {

		protected Expression expression;
		
		public Expression getExpression() {
			return expression;
		}

		public void setExpression(Expression expression) {
			this.expression = expression;
		}
		
		public AbstractSyntaxNode[] getChildren() {
			return new AbstractSyntaxNode[] { expression };
		}
		
		@Override
		public void accept(INodeVisitor visitor, Object[] args) {
			visitor.visit(this, args);
		}
		
	}
	
	/**
	 * Grammar:<br>
	 * <code>
	 * 	Expression "." Type "?" -> Predicate
	 * </code>
	 */
	public static class Is extends RegularPredicate {

		private Type type;
		
		public Type getType() {
			return type;
		}

		public void setType(Type type) {
			this.type = type;
		}

		public AbstractSyntaxNode[] getChildren() {
			return new AbstractSyntaxNode[] { 
				expression,
				new CharacterLiteral(WaebricSymbol.PERIOD),
				type,
				new CharacterLiteral(WaebricSymbol.QUESTION_SIGN)
			};
		}
		
		@Override
		public void accept(INodeVisitor visitor, Object[] args) {
			visitor.visit(this, args);
		}
		
	}
	
	/**
	 * Grammar:<br>
	 * <code>
	 * 	"!" Predicate -> Predicate
	 * </code>
	 */
	public static class Not extends Predicate {

		private Predicate predicate;

		public Predicate getPredicate() {
			return predicate;
		}

		public void setPredicate(Predicate predicate) {
			this.predicate = predicate;
		}

		public AbstractSyntaxNode[] getChildren() {
			return new AbstractSyntaxNode[] {
				new CharacterLiteral('!'), predicate
			};
		}
		
		@Override
		public void accept(INodeVisitor visitor, Object[] args) {
			visitor.visit(this, args);
		}
		
	}
	
	/**
	 * Grammar:<br>
	 * <code>
	 * 	Predicate "&&" Predicate -> Predicate
	 * </code>
	 */
	public static class And extends Predicate {

		private Predicate left;
		private Predicate right;
		
		public Predicate getLeft() {
			return left;
		}

		public void setLeft(Predicate left) {
			this.left = left;
		}

		public Predicate getRight() {
			return right;
		}

		public void setRight(Predicate right) {
			this.right = right;
		}
		
		public AbstractSyntaxNode[] getChildren() {
			return new AbstractSyntaxNode[] {
				left, new StringLiteral("&&"), right
			};
		}
		
		@Override
		public void accept(INodeVisitor visitor, Object[] args) {
			visitor.visit(this, args);
		}
		
	}
	
	/**
	 * Grammar:<br>
	 * <code>
	 * 	Predicate "||" Predicate -> Predicate
	 * </code>
	 */
	public static class Or extends Predicate {

		private Predicate left;
		private Predicate right;
		
		public Predicate getLeft() {
			return left;
		}

		public void setLeft(Predicate left) {
			this.left = left;
		}

		public Predicate getRight() {
			return right;
		}

		public void setRight(Predicate right) {
			this.right = right;
		}
		
		public AbstractSyntaxNode[] getChildren() {
			return new AbstractSyntaxNode[] {
				left, new StringLiteral("||"), right
			};
		}
		
		@Override
		public void accept(INodeVisitor visitor, Object[] args) {
			visitor.visit(this, args);
		}
		
	}
	
}