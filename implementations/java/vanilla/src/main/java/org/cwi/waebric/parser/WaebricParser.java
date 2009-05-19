package org.cwi.waebric.parser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.cwi.waebric.parser.ast.SyntaxTree;
import org.cwi.waebric.scanner.WaebricScanner;
import org.cwi.waebric.scanner.token.Token;
import org.cwi.waebric.scanner.token.TokenSort;
import org.cwi.waebric.scanner.token.WaebricKeyword;

/**
 * The parser attempts to reconstruct the derivation of a structured text.
 * 
 * @author Jeroen van Schagen
 * @date 18-05-2009
 */
public class WaebricParser {

	private final Iterator<Token> tokens;
	
	private List<ParserException> exceptions;
	private SyntaxTree tree;
	private Token current;

	public WaebricParser(WaebricScanner scanner) {
		exceptions = new ArrayList<ParserException>();
		this.tokens = scanner.iterator();
	}
	
	public List<ParserException> parseTokens() {
		exceptions.clear();
		tree = null;

		program();
		
		// TODO: Error processing
		return null;
	}
	
	private void program() {
		while(tokens.hasNext()) {
			current = tokens.next();
			if(current.getLexeme() == WaebricKeyword.MODULE) {
				module();
			} else {
				exceptions.add(new ParserException(
						current.toString() + " is placed outside module."));
			}
		}
	}
	
	private void module() {
		System.out.println(current.toString()); // TODO: Store current as module keyword!
		
		// Retrieve identifier
		current = tokens.next();
		if(current.getSort() == TokenSort.IDCON) {
			System.out.println(current.toString()); // TODO: Store current as module identifier!
		} else {
			exceptions.add(new ParserException(
					current.toString() + " is not a module identifier."));
		}
	}
	
	public SyntaxTree getTree() {
		return tree;
	}
	
}