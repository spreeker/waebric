package org.cwi.waebric.parser.ast.module.site;

import org.cwi.waebric.WaebricSymbol;
import org.cwi.waebric.parser.ast.INodeVisitor;
import org.cwi.waebric.parser.ast.SeparatedNodeList;

/**
 * { PathElement "/" }+ -> Directory
 * 
 * @author Jeroen van Schagen
 * @date 20-05-2009
 */
public class Directory extends SeparatedNodeList<PathElement> {
	
	/**
	 * Separate path elements with a slash /
	 */
	public Directory() {
		super(WaebricSymbol.SLASH);
	}
	
	@Override
	public void accept(INodeVisitor visitor) {
		visitor.visit(this);
	}

}