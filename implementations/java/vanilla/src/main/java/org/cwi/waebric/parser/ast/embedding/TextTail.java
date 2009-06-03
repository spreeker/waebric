package org.cwi.waebric.parser.ast.embedding;

import org.cwi.waebric.parser.ast.AbstractSyntaxNode;
import org.cwi.waebric.parser.ast.ISyntaxNode;

/**
 * Text tail
 * 
 * @author Jeroen van Schagen
 * @date 02-06-2009
 */
public abstract class TextTail extends AbstractSyntaxNode {

	/**
	 * PostText -> TextTail
	 */
	public static class PostTail extends TextTail {
		
		private PostText post;

		public PostText getPost() {
			return post;
		}

		public void setPost(PostText post) {
			this.post = post;
		}

		public ISyntaxNode[] getChildren() {
			return new ISyntaxNode[] { post };
		}
		
	}
	
	/**
	 * MidText Embed TextTail -> TextTail
	 */
	public static class MidTail extends TextTail {

		private MidText mid;
		private Embed embed;
		private TextTail tail;
		
		public MidText getMid() {
			return mid;
		}

		public void setMid(MidText mid) {
			this.mid = mid;
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

		public ISyntaxNode[] getChildren() {
			return new ISyntaxNode[] { mid, embed, tail };
		}
		
	}
	
}