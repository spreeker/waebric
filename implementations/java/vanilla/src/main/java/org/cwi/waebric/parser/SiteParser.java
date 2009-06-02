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
import org.cwi.waebric.parser.exception.ParserException;
import org.cwi.waebric.parser.exception.UnexpectedTokenException;
import org.cwi.waebric.scanner.token.WaebricToken;
import org.cwi.waebric.scanner.token.WaebricTokenIterator;
import org.cwi.waebric.scanner.token.WaebricTokenSort;

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
	
	public SiteParser(WaebricTokenIterator tokens, List<ParserException> exceptions) {
		super(tokens, exceptions);
		
		// Construct sub parser
		markupParser = new MarkupParser(tokens, exceptions);
	}
	
	/**
	 * @see Site
	 * @param site
	 */
	public Site parseSite() {
		Site site = new Site();
		
		// Parse mappings
		Mappings mappings = parseMappings();
		site.setMappings(mappings);
		
		if(! next("site end", "site mappings end", WaebricKeyword.END)) {
			return null; // Incorrect site syntax, return empty node
		}
		
		return site;
	}
	
	/**
	 * @see Mappings
	 * @param mappings
	 */
	public Mappings parseMappings() {
		Mappings mappings = new Mappings();
		
		while(tokens.hasNext()) {
			Mapping mapping = parseMapping();
			mappings.add(mapping);
				
			if(tokens.hasNext()) {
				WaebricToken peek = tokens.peek(1);
				if(peek.getLexeme().equals(WaebricKeyword.END)) {
					break; // End token reached, quit parsing mappings
				} else {
					// Expect mapping separator ";" between each mapping
					next("mapping separator", "semicolon", WaebricSymbol.SEMICOLON);
				}
			}
		}
		
		return mappings;
	}
	
	/**
	 * @see Mapping
	 * @param mapping
	 */
	public Mapping parseMapping() {
		Mapping mapping = new Mapping();
		
		// Parse path
		Path path = parsePath();
		mapping.setPath(path);
		
		// Parse colon separator
		if(! next("mapping separator", "path \":\" markup", WaebricSymbol.COLON)) {
			return null; // Invalid mapping syntax, quit parsing
		}
		
		Markup markup = parseMarkup();
		mapping.setMarkup(markup);
		
		return mapping;
	}
	
	/**
	 * @see Path
	 * @param path
	 */
	public Path parsePath() {
		Path path = null; // Determine path type based on look-ahead
		if(tokens.hasNext(2) && tokens.peek(2).getLexeme().equals(WaebricSymbol.SLASH)) {
			// Parse directory name
			DirName name = parseDirName();
			path = new Path.PathWithDir(name);
		} else {
			path = new Path.PathWithoutDir();
		}

		// Parse filename
		FileName file = parseFileName();
		path.setFileName(file);
		
		return path;
	}
	
	/**
	 * @see DirName
	 * @param Directory name
	 */
	public DirName parseDirName() {
		DirName name = new DirName();
		
		// Parse directory
		Directory directory = parseDirectory();
		name.setDirectory(directory);
		
		return name;
	}
	
	/**
	 * @see Directory
	 * @param directory
	 */
	public Directory parseDirectory() {
		Directory directory = new Directory();
		
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
		
		return directory;
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
	 * @see FileName
	 * @param File name
	 */
	public FileName parseFileName() {
		FileName name = new FileName();
		
		// Parse file name
		if(next("file name", "name \".\" extension", WaebricTokenSort.IDCON)) {
			name.setName(new PathElement(current.getLexeme().toString()));
		}
		
		// Parse period separator
		next("period", "name \".\" extension", WaebricSymbol.PERIOD);
		
		// Parse file extension
		if(next("file extension", "name \".\" extension", WaebricTokenSort.IDCON)) {
			name.setExt(new FileExt(current.getLexeme().toString()));
		}
		
		return name;
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