package org.cwi.waebric.parser.ast.module.site;

import org.cwi.waebric.parser.ast.SyntaxNodeList;
import org.cwi.waebric.parser.ast.INodeVisitor;

/**
 * { Mapping ";" }* -> Mappings
 * 
 * @author Jeroen van Schagen
 * @date 19-05-2009
 */
public class Mappings extends SyntaxNodeList<Mapping> {
	
	public Mappings() {
		super();
	}

	@Override
	public <T> T accept(INodeVisitor<T> visitor) {
		return visitor.visit(this);
	}
	
}