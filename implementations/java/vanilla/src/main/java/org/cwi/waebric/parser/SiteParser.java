package org.cwi.waebric.parser;

import java.util.List;

import org.cwi.waebric.WaebricKeyword;
import org.cwi.waebric.WaebricSymbol;
import org.cwi.waebric.lexer.token.Token;
import org.cwi.waebric.lexer.token.TokenIterator;
import org.cwi.waebric.lexer.token.WaebricTokenSort;
import org.cwi.waebric.parser.ast.module.site.Mapping;
import org.cwi.waebric.parser.ast.module.site.Mappings;
import org.cwi.waebric.parser.ast.module.site.Path;
import org.cwi.waebric.parser.ast.module.site.Site;

/**
 * module languages/waebric/syntax/Site
 * 
 * @see ModuleParser
 * @author Jeroen van Schagen
 * @date 20-05-2009
 */
class SiteParser extends AbstractParser {

	private final MarkupParser markupParser;
	
	/**
	 * Construct site parser
	 * @param tokens
	 * @param exceptions
	 */
	public SiteParser(TokenIterator tokens, List<SyntaxException> exceptions) {
		super(tokens, exceptions);
		
		// Initialize sub-parser
		markupParser = new MarkupParser(tokens, exceptions);
	}
	

	/**
	 * "site" Mapping "end" -> Site
	 * @throws SyntaxException
	 */
	public Site parseSite() throws SyntaxException {
		current(WaebricKeyword.SITE, "Site begin", "\"site\" Mapping \"end\"");
		Site site = new Site();
		site.setMappings(parseMappings());
		next(WaebricKeyword.END, "Site end", "\"site\" Mapping \"end\"");
		return site;
	}
	
	/**
	 * { Mapping ";" }* -> Mappings
	 * @param mappings
	 */
	public Mappings parseMappings() throws SyntaxException {
		Mappings mappings = new Mappings();
		
		while(tokens.hasNext()) {
			mappings.add(parseMapping());
				
			if(tokens.hasNext()) {
				if(tokens.peek(1).getLexeme().equals(WaebricKeyword.END)) {
					break; // End token reached, quit parsing mappings
				} else {
					// Expect mapping separator ";" between each mapping
					next(WaebricSymbol.SEMICOLON, "Mapping separator \";\"", "Mapping \";\" Mapping");
				}
			}
		}
		
		return mappings;
	}
	
	/**
	 * Path ":" Markup -> Mapping
	 * @param mapping
	 */
	public Mapping parseMapping() throws SyntaxException {
		Mapping mapping = new Mapping();
		mapping.setPath(parsePath());
		next(WaebricSymbol.COLON, "Mapping separator \":\"", "Path \":\" Markup");
		mapping.setMarkup(markupParser.parseMarkup());
		return mapping;
	}
	
	/**
	 * DirName "/" FileName -> Path
	 * FileName -> Path
	 * @throws SyntaxException 
	 */
	public Path parsePath() throws SyntaxException {
		String path = ""; // Determine path type based on look-ahead
		if(tokens.hasNext(2) && tokens.peek(2).getLexeme().equals(WaebricSymbol.SLASH)) {
			path = parseDirectory();
		}

		path += parseFileName();
		return new Path(path);
	}
	
	/**
	 * { PathElement "/" }+ -> Directory
	 * @throws SyntaxException 
	 */
	public String parseDirectory() throws SyntaxException {
		String directory = "";
		
		do {
			if(! directory.equals("")) {
				// Expect slash separator between each path element
				next(WaebricSymbol.SLASH, "Path separator \"/\"", "Path element \"/\" Path element");
				directory += "/";
			}
			
			// Detect period separator for potential file names
			if(tokens.hasNext(2) && tokens.peek(2).getLexeme().equals(WaebricSymbol.PERIOD)) {
				break; // File name detected, break from directory parsing
			}

			Token element = tokens.next(); // Retrieve next path element
			if(isPathElement(element.getLexeme().toString())) {
				directory += element.getLexeme().toString();
			} else {
				// Token does not match path element syntax, thus directory is invalid
				reportUnexpectedToken(element, "Path element", "Identifier without layout");
			}
		} while(tokens.hasNext());
		
		return directory;
	}
	
	/**
	 * Check if a certain lexeme is a path element.
	 * @param lexeme
	 * @return PathElement?
	 */
	public static boolean isPathElement(String lexeme) {
		return ! lexeme.matches("(.* .*)|(.*\t.*)|(.*\n.*)|(.*\r.*)|(.*/.*)|(.*\\..*)|(.*\\\\.*)");
	}
	
	/**
	 * PathElement "." FileExt -> FileName
	 * @param File name
	 */
	public String parseFileName() throws SyntaxException {
		String name = "";
		// TODO: Create path element token
		next(WaebricTokenSort.IDCON, "File name", "Name \".\" Extension");
		name += tokens.current().getLexeme().toString() + "."; // Name
		next(WaebricSymbol.PERIOD, "period", "name \".\" extension");
		next(WaebricTokenSort.IDCON, "File extension", "Name \".\" Extension");
		name += tokens.current().getLexeme().toString(); // Extension
		return name;
	}

}