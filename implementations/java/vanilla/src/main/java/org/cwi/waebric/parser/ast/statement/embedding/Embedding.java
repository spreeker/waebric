package org.cwi.waebric.parser.ast.statement.embedding;

import org.cwi.waebric.parser.ast.SyntaxNode;
import org.cwi.waebric.parser.ast.INodeVisitor;

/**
 * PreText Embed TextTail -> Embedding
 * 
 * @author Jeroen van Schagen
 * @date 02-06-2009
 */
public class Embedding extends SyntaxNode {
	
	private PreText pre;
	private Embed embed;
	private TextTail tail;

	public PreText getPre() {
		return pre;
	}

	public void setPre(PreText pre) {
		this.pre = pre;
	}

	public Embed getEmbed() {
		return embed;
	}

	public void setEmbed(Embed embed) {
		this.embed = embed;
	}

	public TextTail getTail() {
		return tail;
	}

	public void setTail(TextTail tail) {
		this.tail = tail;
	}

	public SyntaxNode[] getChildren() {
		return new SyntaxNode[] { pre, embed, tail };
	}
	
	@Override
	public <T> T accept(INodeVisitor<T> visitor) {
		return visitor.visit(this);
	}

}