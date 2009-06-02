package org.cwi.waebric.parser.ast.basic;

import org.cwi.waebric.WaebricSymbol;
import org.cwi.waebric.parser.ast.CharacterLiteral;
import org.cwi.waebric.parser.ast.ISyntaxNode;
import org.cwi.waebric.parser.ast.StringLiteral;

/**
 * 
 * @author Jeroen van Schagen
 * @date 20-05-2009
 */
public class Text {
	
	private StringLiteral text;

	public Text(StringLiteral string) {
		this.text = string;
	}
	
	public Text(String string) {
		this.text = new StringLiteral(string);
	}
	
	public StringLiteral getString() {
		return text;
	}

	public void setString(StringLiteral string) {
		this.text = string;
	}
	
	public ISyntaxNode[] getChildren() {
		return new ISyntaxNode[] {
			new CharacterLiteral(WaebricSymbol.DQUOTE),
			text,
			new CharacterLiteral(WaebricSymbol.DQUOTE)
		};
	}
	
}
