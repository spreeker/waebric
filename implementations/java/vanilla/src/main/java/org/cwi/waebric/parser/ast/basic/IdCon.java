package org.cwi.waebric.parser.ast.basic;

import org.cwi.waebric.lexer.token.Token;
import org.cwi.waebric.parser.ast.SyntaxNode;
import org.cwi.waebric.parser.ast.INodeVisitor;
import org.cwi.waebric.parser.ast.TokenNode;

/**
 * head:[A-Za-Z] tail:[A-Za-Z\-0-9]* -> IdCon {cons("default")}
 * 
 * @author Jeroen van Schagen
 * @date 02-06-2009
 */
public class IdCon extends SyntaxNode {

	/**
	 * Reference to lexical token.
	 */
	private final TokenNode node;
	
	/**
	 * Construct empty identifier.
	 */
	public IdCon() { this(""); }
	
	/**
	 * Construct identifier purely based on string name,
	 * use this constructor when the token is unknown.
	 * @param name
	 */
	public IdCon(String name) {
		this(new Token.IdentifierToken(name, -1, -1));
	}
	
	/**
	 * Construct identifier based on token text, line and
	 * character position. Using this constructor will 
	 * allow more detailed error messages during checking
	 * and interpreting.
	 * @param name Data
	 * @param lineno Line number
	 * @param charno Character number
	 */
	public IdCon(String name, int lineno, int charno) {
		this(new Token.IdentifierToken(name, lineno, charno));
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
		return this.getToken().getLexeme().toString();
	}
	
	@Override
	public String toString() {
		return this.getName();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof IdCon) {
			IdCon id = (IdCon) obj;
			return id.getName().equals(this.getName());
		}
		
		return false;
	}

	@Override
	public SyntaxNode[] getChildren() {
		return new SyntaxNode[] { node };
	}
	
	@Override
	public <T> T accept(INodeVisitor<T> visitor) {
		return visitor.visit(this);
	}

}