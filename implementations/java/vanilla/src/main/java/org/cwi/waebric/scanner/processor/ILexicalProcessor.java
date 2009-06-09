package org.cwi.waebric.scanner.processor;

import java.util.List;

import org.cwi.waebric.scanner.token.WaebricToken;

/**
 * Lexical processors read a token stream and execute various
 * pre/post processing activities on this stream.
 * 
 * @author Jeroen van Schagen
 * @date 05-06-2009
 */
public interface ILexicalProcessor {

	/**
	 * Process token stream and store violations.
	 * 
	 * @param tokens Token stream
	 * @param exceptions Exception collection
	 */
	public void process(List<WaebricToken> tokens, List<LexicalException> exceptions);
	
}
