package org.cwi.waebric.scanner.validator;

import java.util.List;

import org.cwi.waebric.scanner.token.WaebricToken;
import org.cwi.waebric.scanner.token.WaebricTokenSort;

/**
 * Reject validator checks if certain identifiers with rejected values
 * are present. On occurrence a lexical exception is generated.
 * 
 * @author Jeroen van Schagen
 * @date 05-06-2009
 */
public class RejectValidator implements ILexicalValidator {

	public void validate(List<WaebricToken> tokens,	List<LexicalException> exceptions) {
		for(WaebricToken token : tokens) {
			if(token.getSort() == WaebricTokenSort.IDCON) {
				// Check value over rejected values
				if(isRejectedIdentifier(token.getLexeme().toString())) {
					// Create exception and store exception
					LexicalException exception = new RejectedIdentifierException(token);
					exceptions.add(exception);
				}
			}
		}
	}
	
	/**
	 * Check if lexeme is a rejected identifier.
	 * 
	 * @param lexeme
	 * @return Rejected?
	 */
	private boolean isRejectedIdentifier(String lexeme) {
		try {
			// Literal should be in enumeration
			RejectedIdentifiers rejection = RejectedIdentifiers.valueOf(lexeme.toUpperCase());
			return rejection != null;
		} catch(IllegalArgumentException e) {
			// Enumeration does not exists
			return false;
		}
	}

	/**
	 * Collection of rejected identifier values.
	 * 
	 * @author Jeroen van Schagen
	 * @date 05-06-2009
	 */
	private enum RejectedIdentifiers {
		// Module: waebric/syntax/statements
		IF, COMMENT, ECHO, CDATA, EACH, LET, YIELD,
		
		// Module: waebric/syntax/module
		MODULE, IMPORT, DEF, END, SITE;
	}
	
	/**
	 * Rejected identifier exception
	 * 
	 * @author Jeroen van Schagen
	 * @date 05-06-2009
	 */
	private class RejectedIdentifierException extends LexicalException {
		
		/**
		 * Generated serial ID
		 */
		private static final long serialVersionUID = 825621756412780941L;

		/**
		 * Construct exception message based on token object.
		 * @param token
		 */
		public RejectedIdentifierException(WaebricToken token) {
			super(token.toString() + " is a rejected identifier, use another name.");
		}
		
	}

}