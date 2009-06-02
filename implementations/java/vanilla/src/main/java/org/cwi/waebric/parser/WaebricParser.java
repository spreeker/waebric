package org.cwi.waebric.parser;

import java.util.ArrayList;
import java.util.List;

import org.cwi.waebric.parser.ast.SyntaxTree;
import org.cwi.waebric.parser.ast.module.Modules;
import org.cwi.waebric.parser.exception.ParserException;
import org.cwi.waebric.scanner.WaebricScanner;
import org.cwi.waebric.scanner.token.WaebricTokenIterator;

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
	
	public WaebricParser(WaebricTokenIterator iterator) {
		super(iterator, new ArrayList<ParserException>());
		
		// Construct sub parser
		moduleParser = new ModuleParser(tokens, exceptions);
	}
	
	public List<ParserException> parseTokens() {
		exceptions.clear();
		
		Modules modules = new Modules(); // Construct root node
		tree = new SyntaxTree(modules); // Construct AST
		moduleParser.parse(modules); // Start parsing
		
		return exceptions;
	}
	
	public SyntaxTree getAbstractSyntaxTree() {
		return tree;
	}
	
}