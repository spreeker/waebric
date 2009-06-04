package org.cwi.waebric.parser.ast;

/**
 * The abstract syntax tree (AST) provides an intermediate form for storing 
 * programs. All language processors such as, compilers and interpreters
 * work with the AST for completing their processes.
 * 
 * @author Jeroen van Schagen
 * @date 18-05-2009
 */
public class AbstractSyntaxTree {

	private final AbstractSyntaxNode root;
	
	/**
	 * Initialize abstract syntax tree.
	 * 
	 * @param root Start of tree
	 */
	public AbstractSyntaxTree(AbstractSyntaxNode root) {
		this.root = root;
	}
	
	/**
	 * Retrieve root node.
	 * 
	 * @return
	 */
	public final AbstractSyntaxNode getRoot() {
		return root;
	}
	
}