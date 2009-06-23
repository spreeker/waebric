package org.cwi.waebric.scanner;

import java.util.List;

import org.cwi.waebric.scanner.token.Position;
import org.cwi.waebric.scanner.token.Token;

/**
 * Lexical exceptions are created during the scan phase.
 * @author Jeroen van Schagen
 * @date 23-06-2009
 */
public abstract class LexicalException extends Exception {

	public LexicalException() {
		// Default constructor
	}
	
	public LexicalException(String message) {
		super(message);
	}
	
	/**
	 * Generated ID
	 */
	private static final long serialVersionUID = 4401248624083268103L;

	/**
	 * Unclosed text exception
	 * @author Jeroen van Schagen
	 * @date 23-06-2009
	 */
	public static class UnclosedText extends LexicalException {
		
		/**
		 * Generated ID
		 */
		private static final long serialVersionUID = 821542076923517254L;

		public UnclosedText(String buffer, Position pos) {
			super("Text \"" + buffer + "\" at position " + pos.lineno + ", " 
					+ pos.charno + "is not closed, attached a double quote.");
		}
		
	}
	
	/**
	 * Unclosed embedding exception
	 * @author Jeroen van Schagen
	 * @date 23-06-2009
	 */
	public static class UnclosedEmbedding extends LexicalException {
		
		/**
		 * Generated ID
		 */
		private static final long serialVersionUID = 3945430865264270798L;

		public UnclosedEmbedding(String buffer, Position pos) {
			super("Embedding \"" + buffer + "\" at position " + pos.lineno + ", " 
					+ pos.charno + "is not closed, attach a > and double quote.");
		}
		
		public UnclosedEmbedding(List<Token> content) {
			super("Embedding " + content.toString() + "is not closed, " +
					"attach a > and double quote.");
		}
		
	}
	
}
