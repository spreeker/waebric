package org.cwi.waebric.parser.ast.embedding;

import org.cwi.waebric.parser.ast.AbstractSyntaxNode;
import org.cwi.waebric.parser.ast.AbstractSyntaxNodeList;
import org.cwi.waebric.parser.ast.expressions.Expression;
import org.cwi.waebric.parser.ast.markup.Markup;

/**
 * Markup* Expression -> Embed
 * 
 * @author Jeroen van Schagen
 * @date 02-06-2009
 */
public class Embed extends AbstractSyntaxNode {

	private AbstractSyntaxNodeList<Markup> markups;
	private Expression expression;
	
	public Embed() {
		this.markups = new AbstractSyntaxNodeList<Markup>();
	}
	
	public Markup getMarkup(int index) {
		return markups.get(index);
	}
	
	public int getMarkupCount() {
		return markups.size();
	}
	
	public void addMarkup(Markup markup) {
		markups.add(markup);
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

}