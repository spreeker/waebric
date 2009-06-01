package org.cwi.waebric.parser.ast.expressions;

import org.cwi.waebric.WaebricSymbol;
import org.cwi.waebric.parser.ast.CharacterLiteral;
import org.cwi.waebric.parser.ast.ISyntaxNode;
import org.cwi.waebric.parser.ast.StringLiteral;

/**
 * "'" SymbolChar* -> SymbolCon
 * @author schagen
 *
 */
public class SymbolCon implements ISyntaxNode {
	
	private StringLiteral symbol;
	
	public SymbolCon(StringLiteral symbol) {
		this.symbol = symbol;
	}

	public StringLiteral getSymbol() {
		return symbol;
	}
	
	@Override
	public boolean equals(Object obj) {
		return symbol.equals(obj);
	}
	
	@Override
	public String toString() {
		return symbol.toString();
	}

	public ISyntaxNode[] getChildren() {
		return new ISyntaxNode[] {
			new CharacterLiteral(WaebricSymbol.SQUOTE),
			symbol
		};
	}

}