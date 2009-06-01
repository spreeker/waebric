package org.cwi.waebric.parser.ast.text;

import org.cwi.waebric.WaebricSymbol;
import org.cwi.waebric.parser.ast.CharacterLiteral;
import org.cwi.waebric.parser.ast.ISyntaxNode;
import org.cwi.waebric.parser.ast.StringLiteral;

/**
 * " Char* " -> Text
 * 
 * @author Jeroen van Schagen
 * @date 01-06-2009
 */
public class Text implements ISyntaxNode {

	private StringLiteral text;
	
	public Text(StringLiteral text) {
		this.text = text;
	}
	
	public StringLiteral getText() {
		return text;
	}
	
	@Override
	public String toString() {
		return text.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		return text.equals(obj);
	}

	public ISyntaxNode[] getChildren() {
		return new ISyntaxNode[] {
			new CharacterLiteral(WaebricSymbol.DQUOTE),
			text,
			new CharacterLiteral(WaebricSymbol.DQUOTE)
		};
	}

}