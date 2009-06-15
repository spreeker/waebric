package org.cwi.waebric.parser.ast.basic;

import org.cwi.waebric.parser.ast.AbstractSyntaxNode;
import org.cwi.waebric.parser.ast.INodeVisitor;
import org.cwi.waebric.parser.ast.token.TokenNode;
import org.cwi.waebric.scanner.token.Token;
import org.cwi.waebric.scanner.token.WaebricTokenSort;

/**
 * head:[A-Za-Z] tail:[A-Za-Z\-0-9]* -> IdCon {cons("default")}
 * 
 * @author Jeroen van Schagen
 * @date 02-06-2009
 */
public class IdCon extends AbstractSyntaxNode {

	/**
	 * Reference to lexical token.
	 */
	private final TokenNode node;
	
	/**
	 * Construct identifier purely based on string name,
	 * use this constructor when the token is unknown.
	 * @param name
	 */
	public IdCon(String name) {
		this(new Token(name, WaebricTokenSort.IDCON, -1, -1));
	}
	
	/**
	 * Construct identifier using the token object, by
	 * providing a token reference detailed error messages
	 * can be generated.
	 * @param token
	 */
	public IdCon(Token token) {
		this.node = new TokenNode(token);
	}
	
	/**
	 * Retrieve token
	 * @return
	 */
	public Token getToken() {
		return node.getToken();
	}
	
	/**
	 * Retrieve name
	 * @return
	 */
	public String getName() {
		return node.getToken().getLexeme().toString();
	}
	
	@Override
	public String toString() {
		return node.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof IdCon) {
			IdCon id = (IdCon) obj;
			return id.getToken().getLexeme().equals(this.getToken().getLexeme());
		}
		
		return false;
	}

	@Override
	public AbstractSyntaxNode[] getChildren() {
		return new AbstractSyntaxNode[] { node };
	}
	
	@Override
	public void accept(INodeVisitor visitor, Object[] args) {
		visitor.visit(this, args);
	}

}