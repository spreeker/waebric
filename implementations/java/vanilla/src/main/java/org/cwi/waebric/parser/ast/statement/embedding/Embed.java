package org.cwi.waebric.parser.ast.statement.embedding;

import org.cwi.waebric.parser.ast.AbstractSyntaxNode;
import org.cwi.waebric.parser.ast.INodeVisitor;
import org.cwi.waebric.parser.ast.NodeList;
import org.cwi.waebric.parser.ast.expression.Expression;
import org.cwi.waebric.parser.ast.markup.Markup;

/**
 * Embed
 * 
 * @author Jeroen van Schagen
 * @date 02-06-2009
 */
public abstract class Embed extends AbstractSyntaxNode {

	protected NodeList<Markup> markups;
	
	public Embed(NodeList<Markup> markups) {
		this.markups = markups;
	}
	
	public Markup getMarkup(int index) {
		return markups.get(index);
	}
	
	public int getMarkupCount() {
		return markups.size();
	}
	
	/**
	 * Markup* Expression -> Embed
	 */
	public static class ExpressionEmbed extends Embed {
		
		public ExpressionEmbed(NodeList<Markup> markups) {
			super(markups);
		}

		private Expression expression;

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
		
		public MarkupEmbed(NodeList<Markup> markups) {
			super(markups);
		}

		private Markup markup;

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