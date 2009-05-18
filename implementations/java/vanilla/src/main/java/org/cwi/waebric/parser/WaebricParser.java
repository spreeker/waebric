package org.cwi.waebric.parser;

import java.util.List;

import org.cwi.waebric.parser.ast.SyntaxTree;
import org.cwi.waebric.scanner.token.Token;

/**
 * The parser attempts to reconstruct the derivation of a structured text.
 * 
 * @author Jeroen van Schagen
 * @date 18-05-2009
 */
public class WaebricParser {

	private final List<Token> tokens;
	private SyntaxTree tree;
	
	public WaebricParser(List<Token> tokens) {
		this.tokens = tokens;
	}
	
	public List<Exception> parseTokens() {
		for(Token token : tokens) {
			System.out.println(token.toString());
		}
		
		return null;
	}
	
	public SyntaxTree getTree() {
		return tree;
	}
	
}