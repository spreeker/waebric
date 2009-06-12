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
	
	public SymbolCon(StringLiteral symbol) {
		this.literal = symbol;
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
	public void accept(INodeVisitor visitor, Object[] args) {
		visitor.visit(this, args);
	}

}