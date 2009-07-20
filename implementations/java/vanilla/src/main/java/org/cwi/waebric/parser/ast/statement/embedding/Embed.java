package org.cwi.waebric.parser.ast.statement.embedding;

import java.util.Collection;
import java.util.List;

import org.cwi.waebric.parser.ast.AbstractSyntaxNode;
import org.cwi.waebric.parser.ast.INodeVisitor;
import org.cwi.waebric.parser.ast.AbstractSyntaxNodeList;
import org.cwi.waebric.parser.ast.expression.Expression;
import org.cwi.waebric.parser.ast.markup.Markup;

/**
 * Embed
 * 
 * @author Jeroen van Schagen
 * @date 02-06-2009
 */
public abstract class Embed extends AbstractSyntaxNode {

	protected AbstractSyntaxNodeList<Markup> markups = new AbstractSyntaxNodeList<Markup>();

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

		public AbstractSyntaxNode[] getChildren() {
			return new AbstractSyntaxNode[] { markups, expression };
		}
		
		@Override
		public void accept(INodeVisitor visitor) {
			visitor.visit(this);
		}
		
	}
	
	/**
	 * Markup* Markup -> Embed
	 */
	public static class MarkupEmbed extends Embed {
		
		private Markup markup;
		
		public MarkupEmbed(Collection<Markup> args) {
			markups.addAll(args);
		}

		public Markup getMarkup() {
			return markup;
		}

		public void setMarkup(Markup markup) {
			this.markup = markup;
		}

		public AbstractSyntaxNode[] getChildren() {
			return new AbstractSyntaxNode[] { markups, markup };
		}
		
		@Override
		public void accept(INodeVisitor visitor) {
			visitor.visit(this);
		}
		
	}

}