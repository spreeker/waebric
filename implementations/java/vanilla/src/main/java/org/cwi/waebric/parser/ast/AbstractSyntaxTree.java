package org.cwi.waebric.parser.ast;

import org.cwi.waebric.parser.ast.module.Modules;

/**
 * The abstract syntax tree (AST) provides an intermediate form for storing 
 * Waebric programs. All further language processes, e.g. checker, compiler
 * and interpreter interface with the AST.
 * 
 * @author Jeroen van Schagen
 * @date 18-05-2009
 */
public class AbstractSyntaxTree {

	private final Modules root;
	
	/**
	 * Construct AST with empty root node.
	 * @param root Start of tree
	 */
	public AbstractSyntaxTree() {
		this(new Modules());
	}
	
	/**
	 * Construct AST based on root node.
	 * @param root Modules
	 */
	public AbstractSyntaxTree(Modules root) {
		this.root = root;
	}
	
	/**
	 * Retrieve root node. 
	 * @return
	 */
	public final Modules getRoot() {
		return root;
	}
	
	@Override
	public final String toString() {
		return root.toString();
	}
	
}