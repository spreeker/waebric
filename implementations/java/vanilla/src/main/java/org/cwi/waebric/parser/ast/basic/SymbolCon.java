package org.cwi.waebric.parser.ast.basic;

import org.cwi.waebric.parser.ast.CharacterLiteral;
import org.cwi.waebric.parser.ast.ISyntaxNode;
import org.cwi.waebric.parser.ast.SyntaxNodeList;

public class SymbolCon implements ISyntaxNode {

	private SyntaxNodeList<CharacterLiteral> characters;
	
	public SymbolCon() {
		characters = new SyntaxNodeList<CharacterLiteral>();
	}
	
	public boolean addCharacter(CharacterLiteral character) {
		return characters.add(character);
	}
	
	public CharacterLiteral getCharacter(int index) {
		return characters.get(index);
	}
	
	public int getCharacterCount() {
		return characters.size();
	}

	public ISyntaxNode[] getChildren() {
		return new ISyntaxNode[] {
			new CharacterLiteral('\''),
			characters
		};
	}
	
}