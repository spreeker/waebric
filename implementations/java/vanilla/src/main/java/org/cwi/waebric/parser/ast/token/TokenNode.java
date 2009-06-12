package org.cwi.waebric.parser.ast.token;

import org.cwi.waebric.parser.ast.AbstractSyntaxNode;
import org.cwi.waebric.parser.ast.INodeVisitor;
import org.cwi.waebric.scanner.token.Token;

public class TokenNode extends AbstractSyntaxNode {

	private Token token;

	public TokenNode(Token token) {
		this.token = token;
	}
	
	public Token getToken() {
		return token;
	}
	
	@Override
	public String toString() {
		if(token.getLexeme() == null) { return super.toString(); }
		return token.getLexeme().toString();
	}
	
	@Override
	public AbstractSyntaxNode[] getChildren() {
		return new AbstractSyntaxNode[] { };
	}
	
	@Override
	public void accept(INodeVisitor visitor, Object[] args) {
		visitor.visit(this, args);
	}

}