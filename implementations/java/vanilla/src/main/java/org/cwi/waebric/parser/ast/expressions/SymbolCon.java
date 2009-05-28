package org.cwi.waebric.parser.ast.expressions;

import org.cwi.waebric.WaebricSymbol;
import org.cwi.waebric.parser.ast.CharacterLiteral;
import org.cwi.waebric.parser.ast.ISyntaxNode;
import org.cwi.waebric.parser.ast.SyntaxNodeList;

/**
 * "'" SymbolChar* -> SymbolCon
 * @author schagen
 *
 */
public class SymbolCon implements ISyntaxNode {
	
	private SyntaxNodeList<SymbolChar> characters;

	public ISyntaxNode[] getCharacters() {
		return characters.getChildren();
	}

	public boolean addCharacter(SymbolChar character) {
		return characters.add(character);
	}

	public ISyntaxNode[] getChildren() {
		return new ISyntaxNode[] {
			new CharacterLiteral(WaebricSymbol.SQUOTE),
			characters
		};
	}

}