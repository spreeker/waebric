package org.cwi.waebric.parser.ast.embedding;

import org.cwi.waebric.parser.ast.AbstractSyntaxNode;

/**
 * PreText Embed TextTail -> Embedding
 * 
 * @author Jeroen van Schagen
 * @date 02-06-2009
 */
public class Embedding extends AbstractSyntaxNode {
	
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

	public AbstractSyntaxNode[] getChildren() {
		return new AbstractSyntaxNode[] { pre, embed, tail };
	}

}
