package org.cwi.waebric.parser.ast;

/**
 * Default implementation of ISyntaxNode, which provides basic
 * functionality for all nodes.
 * 
 * @author Jeroen van Schagen
 * @date 01-06-2009
 */
public abstract class AbstractSyntaxNode {
	
	/**
	 * Visit node using a node visitor.
	 * @param visitor
	 * @param arg
	 */
	public abstract void accept(INodeVisitor visitor);
	
	/**
	 * Retrieve nodes children from parse tree.
	 * @return Children
	 */
	public AbstractSyntaxNode[] getChildren() {
		return new AbstractSyntaxNode[] { /* No children */ };
	}
	
	@Override
	public String toString() {
		String name = this.getClass().getSimpleName() + "(";
		
		// Attach children data
		AbstractSyntaxNode[] children = this.getChildren();
		for(int i = 0; i < children.length; i++) {
			if(i != 0) { name += ","; }
			name += children[i].toString();
		}
		
		name += ")";
		return name;
	}

}