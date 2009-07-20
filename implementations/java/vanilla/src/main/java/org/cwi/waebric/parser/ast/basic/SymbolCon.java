package org.cwi.waebric.parser.ast.basic;

import org.cwi.waebric.parser.ast.AbstractSyntaxNode;
import org.cwi.waebric.parser.ast.INodeVisitor;

/**
 * "'" SymbolChar* -> SymbolCon
 * @author schagen
 *
 */
public class SymbolCon extends AbstractSyntaxNode {
	
	/**
	 * Symbol name literal.
	 */
	private String name;
	
	/**
	 * Construct empty symbol.
	 */
	public SymbolCon() {
		this("");
	}
	
	/**
	 * Construct symbol based on string value.
	 * @param literal
	 */
	public SymbolCon(String literal) {
		this.name = literal;
	}
	
	/**
	 * Retrieve symbol name.
	 * @return
	 */
	public String getName() {
		return name;
	}
	
	@Override
	public void accept(INodeVisitor visitor) {
		visitor.visit(this);
	}

}