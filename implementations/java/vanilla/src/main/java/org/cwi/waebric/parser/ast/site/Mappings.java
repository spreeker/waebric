package org.cwi.waebric.parser.ast.site;

import org.cwi.waebric.WaebricSymbol;
import org.cwi.waebric.parser.ast.SyntaxNodeList.SyntaxNodeListWithSeparator;

/**
 * Collection of Mapping objects.
 * 
 * @author Jeroen van Schagen
 * @date 19-05-2009
 */
public class Mappings extends SyntaxNodeListWithSeparator<Mapping> {
	
	public Mappings() {
		super("" + WaebricSymbol.SEMICOLON);
	}

	/**
	 * Serial ID
	 */
	private static final long serialVersionUID = -6962647667939837646L;

}