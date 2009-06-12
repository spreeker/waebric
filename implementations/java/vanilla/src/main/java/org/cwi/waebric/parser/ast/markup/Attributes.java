package org.cwi.waebric.parser.ast.markup;

import org.cwi.waebric.parser.ast.NodeList;
import org.cwi.waebric.parser.ast.INodeVisitor;

/**
 * Collection of attributes
 * 
 * @author Jeroen van Schagen
 * @date 22-05-2009
 */
public class Attributes extends NodeList<Attribute> {

	@Override
	public void accept(INodeVisitor visitor, Object[] args) {
		visitor.visit(this, args);
	}
	
}
