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
import org.cwi.waebric.parser.exception.MissingTokenException;
import org.cwi.waebric.parser.exception.ParserException;
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
				exceptions.add(new ParserException(current.toString() + " is not a valid " +
						"mapping separator, use \";\""));
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
			
			current = tokens.next(); // Skip slash symbol
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
		if(! tokens.hasNext()) {
			return; // Empty directory
		}
		
		while(tokens.hasNext()) {
			current = tokens.next(); // Retrieve token
			
			// Attempt parsing and storing path element
			String element = current.getLexeme().toString();
			if(isPathElement(element)) {
				directory.add(new PathElement(element));
			} else {
				exceptions.add(new ParserException(current.toString() + " is an invalid path element," +
						"refrain from using white spaces, layout symbols, periods and backward slashes."));
				return;
			}
			
			if(! tokens.hasNext(2) || isFileName(tokens.peek(2).getLexeme().toString())) {
				return; // File name is next, thus directory has ended
			}
			
			tokens.next(); // Skip slash separator
		}
	}
	
	public static boolean isPathElement(String lexeme) {
		return ! lexeme.matches("(.* .*)|(.*\t.*)|(.*\n.*)|(.*\r.*)|(.*/.*)|(.*\\..*)|(.*\\\\.*)");
	}
	
	public static boolean isFileName(String lexeme) {
		return lexeme.matches("(.*\\..*)");
	}
	
	public void visit(FileName fileName) {
		if(! tokens.hasNext()) {
			exceptions.add(new MissingTokenException(current, "file name", "name \".\" extension"));
		}
		
		// Build file name
		String name = "";
		while(tokens.hasNext(2) && tokens.peek(2).getLexeme().equals(WaebricSymbol.PERIOD)) {
			current = tokens.next();
			if(! name.equals("")) { name += WaebricSymbol.PERIOD; }
			name += current.getLexeme().toString();
		}
		
		// Parse file name
		fileName.setName(new PathElement(current.getLexeme().toString()));
		
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