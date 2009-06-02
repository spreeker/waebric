package org.cwi.waebric.parser.ast.basic;

import org.cwi.waebric.WaebricSymbol;
import org.cwi.waebric.parser.ast.AbstractSyntaxNode;
import org.cwi.waebric.parser.ast.CharacterLiteral;
import org.cwi.waebric.parser.ast.ISyntaxNode;
import org.cwi.waebric.parser.ast.StringLiteral;

/**
 * [\"] StringChar* [\"] -> StrCon<br>
 * ~[\0-\31\n\t\"\\] -> StrChar<br>
 * "\\n" -> StrChar { cons("newline") }<br>
 * "\\t" -> StrChar { cons("tab") }<br>
 * "\\\"" -> StrChar { cons("quote") }<br>
 * "\\\\" -> StrChar { cons("backslash") }<br>
 * 
 * @author Jeroen van Schagen
 * @date 20-05-2009
 */
public class StrCon extends AbstractSyntaxNode {
	
	private StringLiteral literal;

	public StrCon(StringLiteral string) {
		this.literal = string;
	}
	
	public StrCon(String string) {
		this.literal = new StringLiteral(string);
	}
	
	public StringLiteral getLiteral() {
		return literal;
	}
	
	public ISyntaxNode[] getChildren() {
		return new ISyntaxNode[] {
			new CharacterLiteral(WaebricSymbol.DQUOTE),
			literal,
			new CharacterLiteral(WaebricSymbol.DQUOTE)
		};
	}
	
	@Override
	public boolean equals(Object obj) {
		return literal.equals(obj);
	}

}