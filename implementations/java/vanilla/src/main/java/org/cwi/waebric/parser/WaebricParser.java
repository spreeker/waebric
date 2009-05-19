package org.cwi.waebric.parser;

import java.util.Iterator;
import java.util.List;

import org.cwi.waebric.parser.ast.SyntaxTree;
import org.cwi.waebric.scanner.WaebricScanner;
import org.cwi.waebric.scanner.token.Token;

/**
 * The parser attempts to reconstruct the derivation of a structured text.
 * 
 * @author Jeroen van Schagen
 * @date 18-05-2009
 */
public class WaebricParser {

	private final Iterator<Token> tokens;
	private SyntaxTree tree;

	public WaebricParser(WaebricScanner scanner) {
		this.tokens = scanner.iterator();
		Token curr = tokens.next();
		while(tokens.hasNext()) {
			System.out.println(curr.toString());
			curr = tokens.next();
		}
	}
	
	public List<ParserException> parseTokens() {

		
		return null;
	}
	
	private void program() {
		
	}
	
	private void module() {
		
	}
	
	public SyntaxTree getTree() {
		return tree;
	}
	
}