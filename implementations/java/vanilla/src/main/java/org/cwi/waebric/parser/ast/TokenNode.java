package org.cwi.waebric.parser.ast;

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
	public void accept(INodeVisitor visitor) {
		visitor.visit(this);
	}

}