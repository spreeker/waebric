package org.cwi.waebric.parser.ast.module.site;

import org.cwi.waebric.parser.ast.AbstractSyntaxNodeList;
import org.cwi.waebric.parser.ast.INodeVisitor;

/**
 * { Mapping ";" }* -> Mappings
 * 
 * @author Jeroen van Schagen
 * @date 19-05-2009
 */
public class Mappings extends AbstractSyntaxNodeList<Mapping> {
	
	public Mappings() {
		super();
	}

	@Override
	public void accept(INodeVisitor visitor) {
		visitor.visit(this);
	}
	
}