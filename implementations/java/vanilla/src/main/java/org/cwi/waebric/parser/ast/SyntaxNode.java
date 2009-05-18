package org.cwi.waebric.parser.ast;

import java.util.ArrayList;
import java.util.List;

public class SyntaxNode {

	private final SyntaxNode parent;
	private List<SyntaxNode> children;
	
	public SyntaxNode(SyntaxNode parent) {
		children = new ArrayList<SyntaxNode>();
		this.parent = parent;
	}
	
	public SyntaxNode getParent() {
		return parent;
	}
	
	public SyntaxNode[] getChildren() {
		return children.toArray(new SyntaxNode[0]);
	}
	
}