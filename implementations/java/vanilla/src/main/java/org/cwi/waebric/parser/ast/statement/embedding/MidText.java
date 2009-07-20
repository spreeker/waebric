package org.cwi.waebric.parser.ast.statement.embedding;

import org.cwi.waebric.parser.ast.AbstractSyntaxNode;
import org.cwi.waebric.parser.ast.INodeVisitor;

/**
 * ">" TextChar* "<" -> MidText
 * 
 * @author Jeroen van Schagen
 * @date 02-06-2009
 */
public class MidText extends AbstractSyntaxNode {

	private String text;
	
	public MidText(String text) {
		this.text = text;
	}
	
	public String getText() {
		return text;
	}
	
	public AbstractSyntaxNode[] getChildren() {
		return new AbstractSyntaxNode[] { /* No children */ };
	}
	
	@Override
	public void accept(INodeVisitor visitor) {
		visitor.visit(this);
	}
	
}
