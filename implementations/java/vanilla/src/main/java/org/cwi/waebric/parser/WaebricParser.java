package org.cwi.waebric.parser;

import java.util.ArrayList;
import java.util.List;

import org.cwi.waebric.WaebricKeyword;
import org.cwi.waebric.parser.ast.SyntaxTree;
import org.cwi.waebric.parser.ast.module.Modules;
import org.cwi.waebric.parser.exception.ParserException;
import org.cwi.waebric.scanner.WaebricScanner;
import org.cwi.waebric.scanner.token.Token;
import org.cwi.waebric.scanner.token.TokenIterator;
import org.cwi.waebric.scanner.token.TokenSort;

/**
 * Parser attempts to reconstruct the derivation of a Waebric program,
 * this returns a collection of error messages. The parsing process was 
 * successful when zero errors are returned. After a successful parsing 
 * attempt the Abstract Syntax Tree (AST) can be retrieved.
 * 
 * @author Jeroen van Schagen
 * @date 18-05-2009
 */
public class WaebricParser extends AbstractParser {

	private final ModuleParser moduleParser;
	private SyntaxTree tree;

	public WaebricParser(WaebricScanner scanner) {
		this(scanner.iterator());
	}
	
	public WaebricParser(TokenIterator iterator) {
		super(iterator, new ArrayList<ParserException>());
		
		// Initialise sub parser
		moduleParser = new ModuleParser(tokens, exceptions);
	}
	
	public List<ParserException> parseTokens() {
		exceptions.clear();
		
		Modules modules = new Modules(); // Initialise root node
		tree = new SyntaxTree(modules); // Initialise AST
		moduleParser.visit(modules); // Start parsing
		
		return exceptions;
	}
	
	public SyntaxTree getAbstractSyntaxTree() {
		return tree;
	}
	
	public static boolean isKeyword(Token token, WaebricKeyword keyword) {
		if(token == null) { return false; }
		return token.getSort() == TokenSort.KEYWORD && token.getLexeme() == keyword;
	}
	
}