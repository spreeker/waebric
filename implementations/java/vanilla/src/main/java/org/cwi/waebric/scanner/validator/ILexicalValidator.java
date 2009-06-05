package org.cwi.waebric.scanner.validator;

import java.util.List;

import org.cwi.waebric.scanner.token.WaebricToken;

/**
 * Lexical validators read a token stream and verify that all lexical
 * restrictions are being maintained.
 * 
 * @author Jeroen van Schagen
 * @date 05-06-2009
 */
public interface ILexicalValidator {

	/**
	 * Validate token stream and store violations.
	 * 
	 * @param tokens Token stream
	 * @param exceptions Exception collection
	 */
	public void validate(List<WaebricToken> tokens, List<LexicalException> exceptions);
	
}
