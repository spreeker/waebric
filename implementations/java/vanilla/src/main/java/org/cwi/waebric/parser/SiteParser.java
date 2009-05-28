package org.cwi.waebric.parser;

import java.util.List;

import org.cwi.waebric.WaebricKeyword;
import org.cwi.waebric.WaebricSymbol;
import org.cwi.waebric.parser.ast.markup.Markup;
import org.cwi.waebric.parser.ast.site.DirName;
import org.cwi.waebric.parser.ast.site.Directory;
import org.cwi.waebric.parser.ast.site.FileExt;
import org.cwi.waebric.parser.ast.site.FileName;
import org.cwi.waebric.parser.ast.site.Mapping;
import org.cwi.waebric.parser.ast.site.Mappings;
import org.cwi.waebric.parser.ast.site.Path;
import org.cwi.waebric.parser.ast.site.PathElement;
import org.cwi.waebric.parser.ast.site.Site;
import org.cwi.waebric.parser.ast.site.Path.PathWithDir;
import org.cwi.waebric.parser.exception.ParserException;
import org.cwi.waebric.parser.exception.UnexpectedTokenException;
import org.cwi.waebric.scanner.token.Token;
import org.cwi.waebric.scanner.token.TokenIterator;
import org.cwi.waebric.scanner.token.TokenSort;

/**
 * Site parser
 * 
 * module languages/waebric/syntax/Site
 * 
 * @author Jeroen van Schagen
 * @date 20-05-2009
 */
class SiteParser extends AbstractParser {

	private final MarkupParser markupParser;
	
	public SiteParser(TokenIterator tokens, List<ParserException> exceptions) {
		super(tokens, exceptions);
		
		// Construct sub parser
		markupParser = new MarkupParser(tokens, exceptions);
	}
	
	/**
	 * 
	 * @param site
	 */
	public void parse(Site site) {
		parse(site.getMappings()); // Delegate mappings
		next("site end", "site mappings end", WaebricKeyword.END); // Parse end keyword
	}
	
	/**
	 * 
	 * @param mappings
	 */
	public void parse(Mappings mappings) {
		while(tokens.hasNext()) {
			Mapping mapping = new Mapping();
			parse(mapping);
			mappings.add(mapping);
				
			if(tokens.hasNext()) {
				Token peek = tokens.peek(1);
				if(peek.getLexeme().equals(WaebricKeyword.END)) {
					break; // End token reached, stop parsing mappings
				} else {
					// Without end, a semicolon mapping separator is expected
					next("mapping separator", "semicolon", WaebricSymbol.SEMICOLON);
				}
			}
		}
	}
	
	/**
	 * 
	 * @param mapping
	 */
	public void parse(Mapping mapping) {
		Path path = null; // Determine path type based on look-ahead
		if(tokens.hasNext(2) && tokens.peek(2).getLexeme().equals(WaebricSymbol.SLASH)) {
			path = new Path.PathWithDir();
		} else {
			path = new Path.PathWithoutDir();
		}
		
		parse(path); // Parse path
		mapping.setPath(path);
		
		// Parse colon separator
		next("mapping separator", "path \":\" markup", WaebricSymbol.COLON);
		
		Markup markup = parseMarkup();
		mapping.setMarkup(markup);
	}
	
	/**
	 * 
	 * @param path
	 */
	public void parse(Path path) {
		if(path instanceof Path.PathWithDir) {
			// Parse directory
			DirName dir = new DirName();
			parse(dir);
			((PathWithDir) path).setDirName(dir);
		}
		
		// Parse filename
		FileName file = new FileName();
		parse(file);
		path.setFileName(file);
	}
	
	/**
	 * 
	 * @param dirName
	 */
	public void parse(DirName dirName) {
		Directory directory = new Directory();
		parse(directory); // Delegate to directory
		dirName.setDirectory(directory);
	}
	
	/**
	 * 
	 * @param directory
	 */
	public void parse(Directory directory) {
		while(tokens.hasNext()) {
			if(directory.getElements().length != 0) {
				// Between each path element a slash is expected
				next("directory separator", "slash", WaebricSymbol.SLASH);
			}
			
			// Detect period separator for potential file names
			if(tokens.hasNext(2) && tokens.peek(2).getLexeme().equals(WaebricSymbol.PERIOD)) {
				break; // File name detected, break from directory parsing
			}
			
			// Parse directory element
			current = tokens.next();
			if(isPathElement(current.getLexeme().toString())) {
				directory.add(new PathElement(current.getLexeme().toString()));
			} else {
				exceptions.add(new UnexpectedTokenException(current, " path element,", 
						"identifier without white spaces, layout symbols, periods and backward slashes"));
			}
		}
	}
	
	/**
	 * 
	 * @param lexeme
	 * @return
	 */
	public static boolean isPathElement(String lexeme) {
		return ! lexeme.matches("(.* .*)|(.*\t.*)|(.*\n.*)|(.*\r.*)|(.*/.*)|(.*\\..*)|(.*\\\\.*)");
	}
	
	/**
	 * 
	 * @param fileName
	 */
	public void parse(FileName fileName) {
		// Parse file name
		if(next("file name", "name \".\" extension", TokenSort.IDENTIFIER)) {
			fileName.setName(new PathElement(current.getLexeme().toString()));
		}
		
		// Parse period separator
		next("period", "name \".\" extension", WaebricSymbol.PERIOD);
		
		// Parse file extension
		if(next("file extension", "name \".\" extension", TokenSort.IDENTIFIER)) {
			fileName.setExt(new FileExt(current.getLexeme().toString()));
		}
	}
	
	/**
	 * @see Markup
	 * @see org.cwi.waebric.parser.MarkupParser
	 * @param name
	 * @param syntax
	 * @return Markup
	 */
	public Markup parseMarkup() {
		return markupParser.parseMarkup();
	}

}