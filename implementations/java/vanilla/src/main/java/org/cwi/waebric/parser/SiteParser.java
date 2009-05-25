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
import org.cwi.waebric.scanner.token.Token;
import org.cwi.waebric.scanner.token.TokenIterator;

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
		
		// Initialise sub parser
		markupParser = new MarkupParser(tokens, exceptions);
	}
	
	public void visit(Site site) {
		Token start = current; // Store site token for error reporting
		visit(site.getMappings());
		
		if(! WaebricParser.isKeyword(current, WaebricKeyword.END)) {
			exceptions.add(new ParserException(start.toString() + " is never closed, use \"end\"."));
			return;
		}
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
		Path path = null; // Initialised later as multiple alternatives are possible
		visit(path);
		mapping.setPath(path);
		
		// Retrieve colon separator
		current = tokens.next();
		if(! current.getLexeme().equals(WaebricSymbol.COLON)) {
			exceptions.add(new ParserException(current.toString() + " is not a valid mapping " +
					"syntax, use: path \":\" markup."));
			return;
		}
		
		Markup markup = null; // Initialised later as multiple alternatives are possible
		visit(mapping);
		mapping.setMarkup(markup);
	}
	
	public void visit(Path path) {
		if(path == null) { // Determine path type based on look-ahead
			if(tokens.hasNext(2) && tokens.peek(1).getLexeme().equals(WaebricSymbol.SLASH)) {
				path = new Path.PathWithDir();
			} else {
				path = new Path.PathWithoutDir();
			}
		}
		
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
			String element = current.getLexeme().toString(); // Store path element
			
			if(! isPathElement(element)) {
				exceptions.add(new ParserException(current.toString() + " is an invalid path element," +
						"refrain from using white spaces, layout symbols, periods and backward slashes."));
				return;
			}
			
			if(! tokens.hasNext() || ! tokens.peek(1).getLexeme().equals(WaebricSymbol.SLASH)) {
				return; // No more separator found, quit parsing directory
			}
			
			tokens.next(); // Skip slash separator
			directory.add(new PathElement(element));
		}
	}
	
	public static boolean isPathElement(String lexeme) {
		return ! lexeme.matches("(.* .*)|(.*\t.*)|(.*\n.*)|(.*\r.*)|(.*/.*)|(.*\\..*)|(.*\\\\.*)");
	}
	
	public void visit(FileName name) {
		current = tokens.next();
		if(current.getLexeme().toString().indexOf(WaebricSymbol.PERIOD) != -1) {
			String[] elements = current.getLexeme().toString().split("\\.");
			if(elements.length == 2) {
				name.setName(new PathElement(elements[0]));
				name.setExt(new FileExt(elements[1]));
			} else {
				exceptions.add(new ParserException(current.toString() + " has too many elements" +
						"to be a valid filename, use name \".\" extension"));
				return;
			}
		} else {
			exceptions.add(new ParserException(current.toString() + " is an invalid filename, " +
				"use name \".\" extension"));
			return;
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