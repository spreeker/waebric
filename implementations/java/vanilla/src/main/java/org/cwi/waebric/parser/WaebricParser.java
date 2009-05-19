package org.cwi.waebric.parser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.cwi.waebric.parser.ast.SyntaxTree;
import org.cwi.waebric.parser.ast.functions.FunctionDef;
import org.cwi.waebric.parser.ast.markup.Markup;
import org.cwi.waebric.parser.ast.module.Import;
import org.cwi.waebric.parser.ast.module.Module;
import org.cwi.waebric.parser.ast.module.ModuleId;
import org.cwi.waebric.parser.ast.module.Modules;
import org.cwi.waebric.parser.ast.site.Mapping;
import org.cwi.waebric.parser.ast.site.Path;
import org.cwi.waebric.parser.ast.site.Site;
import org.cwi.waebric.parser.exception.ParserException;
import org.cwi.waebric.scanner.WaebricScanner;
import org.cwi.waebric.scanner.token.Token;
import org.cwi.waebric.scanner.token.TokenSort;
import org.cwi.waebric.scanner.token.WaebricKeyword;
import org.cwi.waebric.scanner.token.WaebricSymbol;

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
	
	public SyntaxTree getAbstractSyntaxTree() {
		return tree;
	}
	
	private boolean isKeyword(Token token, WaebricKeyword keyword) {
		return token.getSort() == TokenSort.KEYWORD && token.getLexeme() == keyword;
	}

	/**
	 * !! Below the recursive descent parser is (partially) implemented !!
	 * TODO: Re factor parsing in separate source files
	 */
	
	/**
	 * Modules
	 */
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
	
	/**
	 * Module
	 * @param modules
	 */
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
			return;	// Attach module to parse tree after all conditions have been checked

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
			}
		}
		
		modules.add(module);
	}
	
	/**
	 * Import
	 * @param module
	 */
	private void imprt(Module module) {
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
	
	/**
	 * Site
	 * @param module
	 */
	private void site(Module module) {
		Site site = new Site();
		Token start = current; // Store site token for error reporting
		
		// Parse mappings
		while(tokens.hasNext()) {
			mapping(site);
			
			// Retrieve separator
			current = tokens.next();
			if(isKeyword(current, WaebricKeyword.END)) {
				break; // End token reached, stop parsing mappings
			} else if(! current.getLexeme().equals(WaebricSymbol.SEMICOLON)) {
				exceptions.add(new ParserException(current.toString() + " is not a valid " +
						"mapping separator, use \";\""));
			}
		}
		
		if(!isKeyword(current, WaebricKeyword.END)) {
			exceptions.add(new ParserException(start.toString() + " is never closed, use \"end\"."));
			return;
		}
		
		module.addElement(site);
	}
	
	/**
	 * Mapping
	 * @param site
	 */
	private void mapping(Site site) {
		Mapping mapping = new Mapping();
		path(mapping);
		
		// Retrieve separator ":"
		current = tokens.next();
		if(! current.getLexeme().equals(WaebricSymbol.COLON)) {
			exceptions.add(new ParserException(current.toString() + " is not a valid mapping " +
					"syntax, use: path \":\" markup."));
			return;
		}
		
		markup(mapping);
		site.addMapping(mapping);
	}
	
	/**
	 * Path
	 * @param mapping
	 */
	private void path(Mapping mapping) {
		Path path = new Path();
		System.out.println(path.toString());
	}
	
	/**
	 * Markup
	 * @param mapping
	 */
	private void markup(Mapping mapping) {
		Markup markup = new Markup();
		System.out.println(markup.toString());
	}
	
	/**
	 * Function definition
	 * @param module
	 */
	private void functionDef(Module module) {
		FunctionDef functionDef = new FunctionDef();
		System.out.println(functionDef.toString());
	}
	
}