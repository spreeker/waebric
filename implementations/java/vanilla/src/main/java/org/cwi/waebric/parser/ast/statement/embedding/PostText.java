package org.cwi.waebric.parser.ast.statement.embedding;

import org.cwi.waebric.parser.ast.SyntaxNode;
import org.cwi.waebric.parser.ast.INodeVisitor;

/**
 * ">" TextChar* "\""
 * 
 * @author Jeroen van Schagen 
 * @date 02-06-2009
 */
public class PostText extends SyntaxNode {

	private String text;

	public PostText(String text) {
		this.text = text;
	}
	
	public String getText() {
		return text;
	}

	public SyntaxNode[] getChildren() {
		return new SyntaxNode[] { /* No children */ };
	}
	
	@Override
	public <T> T accept(INodeVisitor<T> visitor) {
		return visitor.visit(this);
	}

}
