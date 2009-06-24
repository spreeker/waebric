package org.cwi.waebric.scanner;

import org.cwi.waebric.scanner.token.Position;

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
					+ pos.charno + " is not closed, attach a > and double quote.");
		}
		
	}
	
	public static class InvalidText extends LexicalException {

		/**
		 * Generated ID
		 */
		private static final long serialVersionUID = -4562789537715438227L;
		
		public InvalidText(String buffer, Position pos) {
			super("\"" + buffer + "\" at position " + pos.lineno + ", "
					+ pos.charno + " is not a valid text construction.");
		}
		
	}
	
	public static class InvalidString extends LexicalException {

		/**
		 * Generated ID
		 */
		private static final long serialVersionUID = -8037309772287571791L;
		
		public InvalidString(String buffer, Position pos) {
			super("\"" + buffer + "\" at position " + pos.lineno + ", "
					+ pos.charno + " is not a valid string construction.");
		}
		
	}
	
}
