package org.cwi.waebric.parser;

import java.util.List;

import org.cwi.waebric.WaebricKeyword;
import org.cwi.waebric.WaebricSymbol;
import org.cwi.waebric.parser.ast.basic.IdCon;
import org.cwi.waebric.parser.ast.functions.FunctionDef;
import org.cwi.waebric.parser.ast.module.Import;
import org.cwi.waebric.parser.ast.module.Module;
import org.cwi.waebric.parser.ast.module.ModuleId;
import org.cwi.waebric.parser.ast.module.Modules;
import org.cwi.waebric.parser.ast.site.Site;
import org.cwi.waebric.parser.exception.ParserException;
import org.cwi.waebric.parser.exception.UnexpectedTokenException;
import org.cwi.waebric.scanner.token.WaebricTokenIterator;
import org.cwi.waebric.scanner.token.WaebricTokenSort;

/**
 * Module parser
 * 
 * module languages/waebric/syntax/Modules
 * 
 * @author Jeroen van Schagen
 * @date 20-05-2009
 */
class ModuleParser extends AbstractParser {

	private final SiteParser siteParser;
	private final FunctionParser functionParser;
	
	public ModuleParser(WaebricTokenIterator tokens, List<ParserException> exceptions) {
		super(tokens, exceptions);
		
		// Initialize sub parsers
		siteParser = new SiteParser(tokens, exceptions);
		functionParser = new FunctionParser(tokens, exceptions);
	}
	
	/**
	 * 
	 * @param modules
	 */
	public void parse(Modules modules) {
		while(tokens.hasNext()) {
			current = tokens.next();
			if(current.getLexeme().equals(WaebricKeyword.MODULE)) {
				Module module = new Module();
				parse(module);
				modules.add(module);
			} else {
				exceptions.add(new UnexpectedTokenException(current, "module", "module identifier"));
			}
		}
	}
	
	/**
	 * 
	 * @param module
	 */
	public void parse(Module module) {
		// Module identifier
		ModuleId identifier = new ModuleId();
		parse(identifier);
		module.setIdentifier(identifier);
		
		// Module elements
		while(tokens.hasNext()) {
			if(tokens.peek(1).getLexeme().equals(WaebricKeyword.MODULE)) { 
				break; // Break current module parse, as new module is detected
			}
			
			// Delegate to element visitors
			current = tokens.next();
			if(current.getLexeme() == WaebricKeyword.IMPORT) {
				Import imprt = new Import();
				parse(imprt);
				module.addElement(imprt);
			} else if(current.getLexeme() == WaebricKeyword.SITE) {
				Site site = parseSite();
				module.addElement(site);
			} else if(current.getLexeme() == WaebricKeyword.DEF) {
				FunctionDef def = parseFunctionDef();
				module.addElement(def);
			} else {
				exceptions.add(new UnexpectedTokenException(
						current, "module keyword", "\"import\", \"site\" or \"def\""));
			}
		}	
	}
	
	/**
	 * 
	 * @param moduleId
	 */
	public void parse(ModuleId moduleId) {
		while(tokens.hasNext()) {
			// Parse identifier
			if(next("module identifier", "identifier", WaebricTokenSort.IDCON)) {
				moduleId.add(new IdCon(current.getLexeme().toString()));
			}
			
			// Parse potential separator
			if(tokens.hasNext() && tokens.peek(1).getLexeme().equals(WaebricSymbol.PERIOD)) {
				tokens.next(); // Skip period separator
			} else {
				break; // No period detected, end of identifier
			}
		}
	}
	
	/**
	 * 
	 * @param imprt
	 */
	public void parse(Import imprt) {
		ModuleId identifier = new ModuleId();
		parse(identifier);
		imprt.setIdentifier(identifier);
	}
	
	/**
	 * @see org.cwi.waebric.parser.SiteParser
	 * @param site
	 */
	public Site parseSite() {
		return siteParser.parseSite();
	}
	
	/**
	 * org.cwi.waebric.parser.FunctionParser
	 * @param def
	 */
	public FunctionDef parseFunctionDef() {
		return functionParser.parseFunctionDef();
	}

}