package org.cwi.waebric.parser.ast.markup;

import org.cwi.waebric.parser.ast.INodeVisitor;
import org.cwi.waebric.parser.ast.SyntaxNodeList;

/**
 * Collection of attributes
 * 
 * @author Jeroen van Schagen
 * @date 22-05-2009
 */
public class Attributes extends SyntaxNodeList<Attribute> {

	@Override
	public <T> T accept(INodeVisitor<T> visitor) {
		return visitor.visit(this);
	}
	
}
