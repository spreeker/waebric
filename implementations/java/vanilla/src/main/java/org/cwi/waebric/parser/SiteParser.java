package org.cwi.waebric.parser;

import java.util.List;

import org.cwi.waebric.WaebricKeyword;
import org.cwi.waebric.WaebricSymbol;
import org.cwi.waebric.parser.ast.module.site.DirName;
import org.cwi.waebric.parser.ast.module.site.Directory;
import org.cwi.waebric.parser.ast.module.site.FileExt;
import org.cwi.waebric.parser.ast.module.site.FileName;
import org.cwi.waebric.parser.ast.module.site.Mapping;
import org.cwi.waebric.parser.ast.module.site.Mappings;
import org.cwi.waebric.parser.ast.module.site.Path;
import org.cwi.waebric.parser.ast.module.site.PathElement;
import org.cwi.waebric.parser.ast.module.site.Site;
import org.cwi.waebric.parser.exception.SyntaxException;
import org.cwi.waebric.scanner.token.WaebricToken;
import org.cwi.waebric.scanner.token.WaebricTokenIterator;
import org.cwi.waebric.scanner.token.WaebricTokenSort;

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
	public SiteParser(WaebricTokenIterator tokens, List<SyntaxException> exceptions) {
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
		Path path = null; // Determine path type based on look-ahead
		if(tokens.hasNext(2) && tokens.peek(2).getLexeme().equals(WaebricSymbol.SLASH)) {
			path = new Path.PathWithDir(parseDirName());
		} else {
			path = new Path.PathWithoutDir();
		}

		path.setFileName(parseFileName());
		return path;
	}
	
	/**
	 * Directory -> DirName
	 * @throws SyntaxException 
	 */
	public DirName parseDirName() throws SyntaxException {
		DirName name = new DirName();
		name.setDirectory(parseDirectory());
		return name;
	}
	
	/**
	 * { PathElement "/" }+ -> Directory
	 * @throws SyntaxException 
	 */
	public Directory parseDirectory() throws SyntaxException {
		Directory directory = new Directory();
		
		do {
			// Detect period separator for potential file names
			if(tokens.hasNext(2) && tokens.peek(2).getLexeme().equals(WaebricSymbol.PERIOD)) {
				break; // File name detected, break from directory parsing
			}
			
			
			
			WaebricToken element = tokens.next();
			if(isPathElement(element.getLexeme().toString())) {
				directory.add(new PathElement(element.getLexeme().toString()));
			} else {
				reportUnexpectedToken(element, "Path element", "Identifier without layout");
			}
			
			next(WaebricSymbol.SLASH, "Path separator \"/\"", "Path element \"/\" Path element");
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
	public FileName parseFileName() throws SyntaxException {
		FileName name = new FileName();
		
		next(WaebricTokenSort.IDCON, "File name", "Name \".\" Extension");
		name.setName(new PathElement(tokens.current().getLexeme().toString()));

		next(WaebricSymbol.PERIOD, "period", "name \".\" extension");
		
		next(WaebricTokenSort.IDCON, "File extension", "Name \".\" Extension");
		name.setExt(new FileExt(tokens.current().getLexeme().toString()));
		
		return name;
	}

}