package org.cwi.waebric.parser.ast;

/**
 * Syntax nodes are elements of the syntax tree.
 * 
 * @see org.cwi.waebric.parser.ast.SyntaxTree
 * 
 * @author Jeroen van Schagen
 * @date 19-05-2009
 */
public interface ISyntaxNode {

	/**
	 * Retrieve children.
	 * 
	 * @return children
	 */
	public ISyntaxNode[] getChildren();
	
}
