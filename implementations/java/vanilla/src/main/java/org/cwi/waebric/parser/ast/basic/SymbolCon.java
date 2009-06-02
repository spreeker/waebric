package org.cwi.waebric.parser.ast.basic;

import org.cwi.waebric.WaebricSymbol;
import org.cwi.waebric.parser.ast.AbstractSyntaxNode;
import org.cwi.waebric.parser.ast.CharacterLiteral;
import org.cwi.waebric.parser.ast.ISyntaxNode;
import org.cwi.waebric.parser.ast.StringLiteral;

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

	public ISyntaxNode[] getChildren() {
		return new ISyntaxNode[] {
			new CharacterLiteral(WaebricSymbol.SQUOTE),
			literal
		};
	}

}