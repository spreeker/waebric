package org.cwi.waebric.parser.ast.statement.embedding;

import java.util.Collection;
import java.util.List;

import org.cwi.waebric.parser.ast.SyntaxNode;
import org.cwi.waebric.parser.ast.INodeVisitor;
import org.cwi.waebric.parser.ast.SyntaxNodeList;
import org.cwi.waebric.parser.ast.expression.Expression;
import org.cwi.waebric.parser.ast.markup.Markup;

/**
 * Embed
 * 
 * @author Jeroen van Schagen
 * @date 02-06-2009
 */
public abstract class Embed extends SyntaxNode {

	protected SyntaxNodeList<Markup> markups = new SyntaxNodeList<Markup>();

	public List<Markup> getMarkups() {
		return markups.clone();
	}
	
	/**
	 * Markup* Expression -> Embed
	 */
	public static class ExpressionEmbed extends Embed {
		
		private Expression expression;

		public ExpressionEmbed(Collection<Markup> args) {
			markups.addAll(args);
		}
		
		public ExpressionEmbed(Expression expression) {
			this.expression = expression;
		}
		
		public ExpressionEmbed(Collection<Markup> args, Expression expression) {
			markups.addAll(args);
			this.expression = expression;
		}

		public Expression getExpression() {
			return expression;
		}
		
		public void setExpression(Expression expression) {
			this.expression = expression;
		}

		public SyntaxNode[] getChildren() {
			return new SyntaxNode[] { markups, expression };
		}
		
			@Override
	public <T> T accept(INodeVisitor<T> visitor) {
		return visitor.visit(this);
	}
		
	}
	
	/**
	 * Markup* Markup -> Embed
	 */
	public static class MarkupEmbed extends Embed {
		
		public MarkupEmbed(Collection<Markup> args) {
			markups.addAll(args);
		}

		public SyntaxNode[] getChildren() {
			return new SyntaxNode[] { markups };
		}
		
			@Override
	public <T> T accept(INodeVisitor<T> visitor) {
		return visitor.visit(this);
	}
		
	}

}