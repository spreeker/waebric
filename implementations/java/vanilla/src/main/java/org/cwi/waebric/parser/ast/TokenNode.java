package org.cwi.waebric.parser.ast;

import org.cwi.waebric.lexer.token.Token;

public class TokenNode extends SyntaxNode {

	private Token token;

	public TokenNode(Token token) {
		this.token = token;
	}

	public Token getToken() {
		return token;
	}

	@Override
	public String toString() {
		if (token.getLexeme() == null) {
			return super.toString();
		}
		return token.getLexeme().toString();
	}

	@Override
	public SyntaxNode[] getChildren() {
		return new SyntaxNode[] {};
	}

	@Override
	public <T> T accept(INodeVisitor<T> visitor) {
		return visitor.visit(this);
	}

}