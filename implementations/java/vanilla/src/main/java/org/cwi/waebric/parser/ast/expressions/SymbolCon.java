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
		ISyntaxNode[] chars = characters.getChildren();
		ISyntaxNode[] children = new ISyntaxNode[chars.length+1];
		
		children[0] = new CharacterLiteral(WaebricSymbol.SQUOTE);
		for(int i = 0; i < children.length; i++) {
			children[i+1] = chars[i];
		}
		
		return children;
	}

}