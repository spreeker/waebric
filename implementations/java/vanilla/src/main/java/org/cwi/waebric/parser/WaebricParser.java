package org.cwi.waebric.parser;

import java.util.List;

import org.cwi.waebric.scanner.token.Token;

/**
 * The parser attempts to reconstruct the derivation of a structured text.
 * 
 * @author Jeroen van Schagen
 * @date 18-05-2009
 */
public class WaebricParser {

	private final List<Token> tokens;
	
	public WaebricParser(List<Token> tokens) {
		this.tokens = tokens;
	}
	
	
	
}
