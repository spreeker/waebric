package org.cwi.waebric.parser.ast.module.site;

import org.cwi.waebric.WaebricSymbol;
import org.cwi.waebric.parser.ast.AbstractSyntaxNodeList.AbstractSeparatedSyntaxNodeList;

/**
 * { Mapping ";" }* -> Mappings
 * 
 * @author Jeroen van Schagen
 * @date 19-05-2009
 */
public class Mappings extends AbstractSeparatedSyntaxNodeList<Mapping> {
	
	public Mappings() {
		super(WaebricSymbol.SEMICOLON);
	}

}