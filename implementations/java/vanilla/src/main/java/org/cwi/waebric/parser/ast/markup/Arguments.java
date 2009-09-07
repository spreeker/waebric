package org.cwi.waebric.parser.ast.markup;

import org.cwi.waebric.parser.ast.SyntaxNodeList;
import org.cwi.waebric.parser.ast.INodeVisitor;

/**
 * "(" { Argument "," }* ")" -> Arguments
 * 
 * @author Jeroen van Schagen
 * @date 19-05-2009
 */
public class Arguments extends SyntaxNodeList<Argument> {

	public Arguments() {
		super();
	}
	
	@Override
	public <T> T accept(INodeVisitor<T> visitor) {
		return visitor.visit(this);
	}
	
}