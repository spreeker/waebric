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
import org.cwi.waebric.scanner.token.TokenIterator;
import org.cwi.waebric.scanner.token.TokenSort;

/**
 * Site parser
 * 
 * @author Jeroen van Schagen
 * @date 20-05-2009
 */
public class SiteParser extends AbstractParser {

	private final MarkupParser markupParser;
	
	public SiteParser(TokenIterator tokens, List<ParserException> exceptions) {
		super(tokens, exceptions);
		
		// Construct sub parser
		markupParser = new MarkupParser(tokens, exceptions);
	}
	
	public void visit(Site site) {
		visit(site.getMappings()); // Parse mappings
		next("site end", "site mappings end", "" + WaebricKeyword.END);
	}
	
	public void visit(Mappings mappings) {
		while(tokens.hasNext()) {
			Mapping mapping = new Mapping();
			visit(mapping);
			mappings.add(mapping);
			
			// Retrieve separator
			current = tokens.next();
			if(WaebricParser.isKeyword(current, WaebricKeyword.END)) {
				break; // End token reached, stop parsing mappings
			} else if(! current.getLexeme().equals(WaebricSymbol.SEMICOLON)) {
				exceptions.add(new UnexpectedTokenException(current, "mapping separator", ";"));
			}
		}
	}
	
	public void visit(Mapping mapping) {
		Path path = null;
		// Determine path type based on look-ahead
		if(tokens.hasNext(2) && tokens.peek(2).getLexeme().equals(WaebricSymbol.SLASH)) {
			path = new Path.PathWithDir();
		} else {
			path = new Path.PathWithoutDir();
		}
		visit(path);
		mapping.setPath(path);
		
		// Retrieve colon separator
		next("mapping separator", "path \":\" markup", "" + WaebricSymbol.COLON);
		
		Markup markup = null;
		// Determine mark-up type based on look-ahead
		if(tokens.hasNext(2) && tokens.peek(2).getLexeme().equals(WaebricSymbol.LPARANTHESIS)) {
			markup = new Markup.MarkupWithArguments();
		} else {
			markup = new Markup.MarkupWithoutArguments();
		}
		visit(markup);
		mapping.setMarkup(markup);
	}
	
	public void visit(Path path) {
		if(path instanceof Path.PathWithDir) {
			DirName dir = new DirName();
			visit(dir);
			path.setDirName(dir);
		}
		
		FileName file = new FileName();
		visit(file);
		path.setFileName(file);
	}
	
	public void visit(DirName name) {
		Directory directory = new Directory();
		visit(directory); // Delegate to directory visit
		name.setDirectory(directory);
	}
	
	public void visit(Directory directory) {
		while(tokens.hasNext()) {
			// Parse path separator (slash)
			if(directory.getElements().length != 0) {
				next("directory separator", "slash", "" + WaebricSymbol.SLASH);
			}
			
			if(tokens.hasNext(2) && tokens.peek(2).getLexeme().equals(WaebricSymbol.PERIOD)) {
				break; // File name identified, stop parsing directory
			}
			
			// Parse path element
			current = tokens.next();
			String element = current.getLexeme().toString();
			if(isPathElement(element)) {
				directory.add(new PathElement(element));
			} else {
				exceptions.add(new UnexpectedTokenException(current, " path element,", 
						"identifier without white spaces, layout symbols, periods and backward slashes"));
				return;
			}
		}
	}
	
	public static boolean isPathElement(String lexeme) {
		return ! lexeme.matches("(.* .*)|(.*\t.*)|(.*\n.*)|(.*\r.*)|(.*/.*)|(.*\\..*)|(.*\\\\.*)");
	}
	
	public void visit(FileName fileName) {
		// Parse name
		if(next("file name", "name \".\" extension", TokenSort.IDENTIFIER)) {
			fileName.setName(new PathElement(current.getLexeme().toString()));
		}
		
		// Parse period separator
		next("period", "name \".\" extension", "" + WaebricSymbol.PERIOD);
		
		// Parse extension
		if(next("file extension", "name \".\" extension", TokenSort.IDENTIFIER)) {
			fileName.setExt(new FileExt(current.getLexeme().toString()));
		}
	}
	
	/**
	 * @see org.cwi.waebric.parser.MarkupParser
	 * @param markup
	 */
	public void visit(Markup markup) {
		markupParser.visit(markup);
	}

}