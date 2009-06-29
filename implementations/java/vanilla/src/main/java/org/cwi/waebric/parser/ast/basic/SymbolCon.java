package org.cwi.waebric.parser.ast.basic;

import org.cwi.waebric.WaebricSymbol;
import org.cwi.waebric.parser.ast.AbstractSyntaxNode;
import org.cwi.waebric.parser.ast.INodeVisitor;
import org.cwi.waebric.parser.ast.token.CharacterLiteral;
import org.cwi.waebric.parser.ast.token.StringLiteral;

/**
 * "'" SymbolChar* -> SymbolCon
 * @author schagen
 *
 */
public class SymbolCon extends AbstractSyntaxNode {
	
	/**
	 * Symbol name literal.
	 */
	private StringLiteral name;
	
	/**
	 * Construct empty symbol.
	 */
	public SymbolCon() { this(""); }
	
	/**
	 * Construct symbol based on string value.
	 * @param literal
	 */
	public SymbolCon(String literal) {
		this.name = new StringLiteral(literal);
	}
	
	/**
	 * Construct symbol based on literal.
	 * @param literal
	 */
	public SymbolCon(StringLiteral literal) {
		this.name = literal;
	}
	
	/**
	 * Retrieve symbol name.
	 * @return
	 */
	public StringLiteral getName() {
		return name;
	}
	
	@Override
	public boolean equals(Object obj) {
		return name.equals(obj);
	}

	public AbstractSyntaxNode[] getChildren() {
		return new AbstractSyntaxNode[] {
			new CharacterLiteral(WaebricSymbol.SQUOTE),
			name
		};
	}
	
	@Override
	public void accept(INodeVisitor visitor) {
		visitor.visit(this);
	}

}