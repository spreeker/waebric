package org.cwi.waebric.parser.ast.basic;

import org.cwi.waebric.WaebricSymbol;
import org.cwi.waebric.parser.ast.CharacterLiteral;
import org.cwi.waebric.parser.ast.ISyntaxNode;
import org.cwi.waebric.parser.ast.StringLiteral;

public class StrCon implements ISyntaxNode {
	
	private StringLiteral string;

	public StrCon(StringLiteral string) {
		this.string = string;
	}
	
	public StrCon(String string) {
		this.string = new StringLiteral(string);
	}
	
	public StringLiteral getString() {
		return string;
	}

	public void setString(StringLiteral string) {
		this.string = string;
	}
	
	public ISyntaxNode[] getChildren() {
		return new ISyntaxNode[] {
			new CharacterLiteral(WaebricSymbol.DQUOTE),
			string,
			new CharacterLiteral(WaebricSymbol.DQUOTE)
		};
	}

}