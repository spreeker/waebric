package org.cwi.waebric.parser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.cwi.waebric.parser.ast.SyntaxTree;
import org.cwi.waebric.parser.ast.module.Module;
import org.cwi.waebric.parser.ast.module.ModuleId;
import org.cwi.waebric.parser.ast.module.Modules;
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
		modules();
		
		// TODO: Error processing
		return exceptions;
	}
	
	private void modules() {
		Modules modules = new Modules();
		tree = new SyntaxTree(modules);
		
		while(tokens.hasNext()) {
			current = tokens.next();
			if(current.getLexeme() == WaebricKeyword.MODULE) {
				module(modules);
			} else {
				exceptions.add(new ParserException(
						current.toString() + " is placed outside module."));
			}
		}
	}
	
	private void module(Modules modules) {
		Module module = new Module();
		
		// Module identifier
		if(! tokens.hasNext()) {
			exceptions.add(new ParserException(current.toString() + " has no module identifier."));
			return;
		}
		
		current = tokens.next();
		if(current.getSort() == TokenSort.IDCON) {
			ModuleId identifier = new ModuleId(current.getLexeme().toString());
			module.setIdentifier(identifier);
		} else {
			exceptions.add(new ParserException(current.toString() + " is not a module identifier."));
			return;
		}
		
		// TODO: Module elements
	}
	
	public SyntaxTree getAbstractSyntaxTree() {
		return tree;
	}
	
}