package org.cwi.waebric.parser.ast;

/**
 * Default implementation of ISyntaxNode, which provides basic
 * functionality for all nodes.
 * 
 * @author Jeroen van Schagen
 * @date 01-06-2009
 */
public abstract class AbstractSyntaxNode implements ISyntaxNode {
	
	/**
	 * Convert node to string, used for debugging.
	 */
	@Override
	public String toString() {
		String name = this.getClass().getSimpleName();
		
		// Attach children data
		name += "(";
		ISyntaxNode[] children = this.getChildren();
		for(int i = 0; i < children.length; i++) {
			if(i != 0) { name += ","; }
			name += children[i].toString();
		}
		name += ")";
		
		return name;
	}

}