package org.cwi.waebric.parser.ast;

public abstract class DefaultNodeVisitor implements INodeVisitor {

	public void visit(AbstractSyntaxNode node, Object[] args) {
		for(AbstractSyntaxNode child: node.getChildren()) {
			child.accept(this, args); // Recursively call visitor on children
		}
	}

}