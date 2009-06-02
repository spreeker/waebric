package org.cwi.waebric.parser.ast.expressions;

import org.cwi.waebric.WaebricSymbol;
import org.cwi.waebric.parser.ast.AbstractSyntaxNode;
import org.cwi.waebric.parser.ast.CharacterLiteral;
import org.cwi.waebric.parser.ast.ISyntaxNode;
import org.cwi.waebric.parser.ast.StringLiteral;

/**
 * "\"" TextChar* "\"" -> Text<br>
 * ~[\0-\31\<\128-\255] \/ [\n\t\r] -> TextChar<br>
 * 
 * @author Jeroen van Schagen
 * @date 20-05-2009
 */
public class Text extends AbstractSyntaxNode {
	
	private StringLiteral literal;

	public Text(StringLiteral string) {
		this.literal = string;
	}
	
	public Text(String string) {
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
	
}