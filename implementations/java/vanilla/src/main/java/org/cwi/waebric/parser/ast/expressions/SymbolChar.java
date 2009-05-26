package org.cwi.waebric.parser.ast.expressions;

import org.cwi.waebric.parser.ast.CharacterLiteral;
import org.cwi.waebric.parser.ast.ISyntaxNode;

/**
 * ~[\0-\31)\ \t\n\r\;\,\>\127-\255] -> SymbolChar
 * @author schagen
 *
 */
public class SymbolChar implements ISyntaxNode {

	private CharacterLiteral literal;
	
	public SymbolChar(char c) {
		this.literal = new CharacterLiteral(c);
	}
	
	public CharacterLiteral getLiteral() {
		return literal;
	}
	
	@Override
	public boolean equals(Object obj) {
		return literal.equals(obj);
	}
	
	@Override
	public String toString() {
		return literal.toString();
	}

	public ISyntaxNode[] getChildren() {
		return new ISyntaxNode[] { literal };
	}
	
}