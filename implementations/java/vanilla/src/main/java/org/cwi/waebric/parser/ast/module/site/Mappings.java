package org.cwi.waebric.parser.ast.module.site;

import org.cwi.waebric.WaebricSymbol;
import org.cwi.waebric.parser.ast.INodeVisitor;
import org.cwi.waebric.parser.ast.SeparatedNodeList;

/**
 * { Mapping ";" }* -> Mappings
 * 
 * @author Jeroen van Schagen
 * @date 19-05-2009
 */
public class Mappings extends SeparatedNodeList<Mapping> {
	
	public Mappings() {
		super(WaebricSymbol.SEMICOLON);
	}

	@Override
	public void accept(INodeVisitor visitor, Object[] args) {
		visitor.visit(this, args);
	}
	
}