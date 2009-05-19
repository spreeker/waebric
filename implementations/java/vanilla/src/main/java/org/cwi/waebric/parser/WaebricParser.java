package org.cwi.waebric.parser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.cwi.waebric.parser.ast.SyntaxTree;
import org.cwi.waebric.parser.ast.module.Import;
import org.cwi.waebric.parser.ast.module.Module;
import org.cwi.waebric.parser.ast.module.ModuleId;
import org.cwi.waebric.parser.ast.module.Modules;
import org.cwi.waebric.parser.ast.module.functions.FunctionDef;
import org.cwi.waebric.parser.ast.module.site.Site;
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
		return exceptions;
	}
	
	private boolean isKeyword(Token token, WaebricKeyword keyword) {
		return token.getSort() == TokenSort.KEYWORD && token.getLexeme() == keyword;
	}
	
	private void modules() {
		Modules modules = new Modules();
		tree = new SyntaxTree(modules);

		while(tokens.hasNext()) {
			// Parse module
			current = tokens.next();
			if(isKeyword(current, WaebricKeyword.MODULE)) {
				module(modules);
			} else {
				exceptions.add(new ParserException(
						current.toString() + " is placed outside module."));
			}
		}
	}
	
	private void module(Modules modules) {
		Module module = new Module();
		
		// Parse module identifier
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
		
		// Parse module elements
		while(tokens.hasNext()) {
			current = tokens.next();
			
			// Apply recursion on successor module
			if(isKeyword(current, WaebricKeyword.MODULE)) { 
				module(modules);
				break; // Quit while loop so no exception is thrown
			}
			
			// Delegate elements
			if(current.getLexeme() == WaebricKeyword.IMPORT) {
				imprt(module);
			} else if(current.getLexeme() == WaebricKeyword.SITE) {
				site(module);
			} else if(current.getLexeme() == WaebricKeyword.DEF) {
				functionDef(module);
			} else {
				exceptions.add(new ParserException(current.toString() + " is not a valid module keyword, " +
						"expected \"import\", \"site\" or \"def\"."));
				return;
			}
		}
		
		// Attach module to parse tree after all conditions have been checked
		modules.add(module);
	}
	
	public void imprt(Module module) {
		Import imprt = new Import();
		
		// Parse module identifier
		if(! tokens.hasNext()) {
			exceptions.add(new ParserException(current.toString() + " has no module identifier."));
			return;
		}
		
		current = tokens.next();
		if(current.getSort() == TokenSort.IDCON) {
			ModuleId identifier = new ModuleId(current.getLexeme().toString());
			imprt.setIdentifier(identifier);
		} else {
			exceptions.add(new ParserException(current.toString() + " is not a module identifier."));
			return;
		}
		
		module.addElement(imprt);
	}
	
	public void site(Module module) {
		Site site = new Site();
	}
	
	public void functionDef(Module module) {
		FunctionDef functionDef = new FunctionDef();
	}

	public SyntaxTree getAbstractSyntaxTree() {
		return tree;
	}
	
}