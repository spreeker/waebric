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
	
	private StringLiteral literal;
	
	public SymbolCon(StringLiteral literal) {
		this.literal = literal;
	}
	
	public SymbolCon(String literal) {
		this.literal = new StringLiteral(literal);
	}

	public StringLiteral getLiteral() {
		return literal;
	}
	
	@Override
	public boolean equals(Object obj) {
		return literal.equals(obj);
	}

	public AbstractSyntaxNode[] getChildren() {
		return new AbstractSyntaxNode[] {
			new CharacterLiteral(WaebricSymbol.SQUOTE),
			literal
		};
	}
	
	@Override
	public void accept(INodeVisitor visitor) {
		visitor.visit(this);
	}

}