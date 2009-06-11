package org.cwi.waebric.parser.ast.basic;

import org.cwi.waebric.parser.ast.AbstractSyntaxNode;
import org.cwi.waebric.parser.ast.INodeVisitor;
import org.cwi.waebric.parser.ast.TokenNode;
import org.cwi.waebric.scanner.token.Token;

/**
 * head:[A-Za-Z] tail:[A-Za-Z\-0-9]* -> IdCon {cons("default")}
 * 
 * @author Jeroen van Schagen
 * @date 02-06-2009
 */
public class IdCon extends AbstractSyntaxNode {

	private TokenNode node;
	
	public IdCon(Token token) {
		this.node = new TokenNode(token);
	}
	
	public Token getToken() {
		return node.getToken();
	}
	
	public String getName() {
		return node.getToken().getLexeme().toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof IdCon) {
			IdCon id = (IdCon) obj;
			return node.getToken().getLexeme().equals(id.getToken().getLexeme());
		}
		
		return false;
	}

	public AbstractSyntaxNode[] getChildren() {
		return new AbstractSyntaxNode[] { node };
	}
	
	@Override
	public void accept(INodeVisitor visitor, Object[] args) {
		visitor.visit(this, args);
	}

}