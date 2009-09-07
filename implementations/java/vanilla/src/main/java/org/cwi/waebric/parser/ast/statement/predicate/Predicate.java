package org.cwi.waebric.parser.ast.statement.predicate;

import org.cwi.waebric.parser.ast.SyntaxNode;
import org.cwi.waebric.parser.ast.INodeVisitor;
import org.cwi.waebric.parser.ast.expression.Expression;

/**
 * Predicates are conditions, used in if(-else) statement constructs.
 * 
 * @author Jeroen van Schagen
 * @date 27-05-2009
 */
public abstract class Predicate extends SyntaxNode {

	/**
	 * Grammar:<br>
	 * <code>
	 * 	Expression -> Predicate
	 * </code>
	 */
	public static class RegularPredicate extends Predicate {

		protected Expression expression;
		
		public RegularPredicate() { }
		
		public RegularPredicate(Expression expression) {
			this.expression = expression;
		}
		
		public Expression getExpression() {
			return expression;
		}

		public void setExpression(Expression expression) {
			this.expression = expression;
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
	 * Grammar:<br>
	 * <code>
	 * 	Expression "." Type "?" -> Predicate
	 * </code>
	 */
	public static class Is extends RegularPredicate {

		private Type type;
		
		public Is() { }
		
		public Is(Expression expression, Type type) {
			this.expression = expression;
			this.type = type;
		}
		
		public Type getType() {
			return type;
		}

		public void setType(Type type) {
			this.type = type;
		}

		public SyntaxNode[] getChildren() {
			return new SyntaxNode[] { expression, type };
		}
		
			@Override
	public <T> T accept(INodeVisitor<T> visitor) {
		return visitor.visit(this);
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
		
		public Not() { }
	
		public Not(Predicate predicate) {
			this.predicate = predicate;
		}

		public Predicate getPredicate() {
			return predicate;
		}

		public void setPredicate(Predicate predicate) {
			this.predicate = predicate;
		}

		public SyntaxNode[] getChildren() {
			return new SyntaxNode[] { predicate	};
		}
		
			@Override
	public <T> T accept(INodeVisitor<T> visitor) {
		return visitor.visit(this);
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
		
		public And() { }
		
		public And(Predicate left, Predicate right) {
			this.left = left;
			this.right = right;
		}
		
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
		
		public SyntaxNode[] getChildren() {
			return new SyntaxNode[] { left, right };
		}
		
			@Override
	public <T> T accept(INodeVisitor<T> visitor) {
		return visitor.visit(this);
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
		
		public Or() { }
		
		public Or(Predicate left, Predicate right) {
			this.left = left;
			this.right = right;
		}
		
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
		
		public SyntaxNode[] getChildren() {
			return new SyntaxNode[] { left, right };
		}
		
			@Override
	public <T> T accept(INodeVisitor<T> visitor) {
		return visitor.visit(this);
	}
		
	}
	
}