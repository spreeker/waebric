package org.cwi.waebric.parser.ast.markup;

import org.cwi.waebric.parser.ast.AbstractSyntaxNodeList;
import org.cwi.waebric.parser.ast.INodeVisitor;

/**
 * "(" { Argument "," }* ")" -> Arguments
 * 
 * @author Jeroen van Schagen
 * @date 19-05-2009
 */
public class Arguments extends AbstractSyntaxNodeList<Argument> {

	public Arguments() {
		super();
	}
	
	@Override
	public void accept(INodeVisitor visitor) {
		visitor.visit(this);
	}
	
}