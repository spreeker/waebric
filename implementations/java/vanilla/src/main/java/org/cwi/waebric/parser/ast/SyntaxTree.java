package org.cwi.waebric.parser.ast;

/**
 * The abstract syntax tree (AST) provides an intermediate form to store
 * a program. Both compilers as interpreters use the AST for their processes.
 * 
 * @author Jeroen van Schagen
 * @date 18-05-2009
 */
public class SyntaxTree {

	private final SyntaxNode start;
	
	/**
	 * Initialize tree.
	 * 
	 * @param start Start node
	 */
	public SyntaxTree(SyntaxNode start) {
		this.start = start;
	}
	
	/**
	 * Retrieve start node.
	 * 
	 * @return
	 */
	public final SyntaxNode getStart() {
		return start;
	}
	
}