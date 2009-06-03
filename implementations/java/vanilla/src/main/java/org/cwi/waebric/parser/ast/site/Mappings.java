package org.cwi.waebric.parser.ast.site;

import org.cwi.waebric.WaebricSymbol;
import org.cwi.waebric.parser.ast.AbstractSyntaxNodeList.SyntaxNodeListWithSeparator;

/**
 * Collection of Mapping objects.
 * 
 * @author Jeroen van Schagen
 * @date 19-05-2009
 */
public class Mappings extends SyntaxNodeListWithSeparator<Mapping> {
	
	public Mappings() {
		super(WaebricSymbol.SEMICOLON);
	}

}