package org.cwi.waebric.parser.ast.basic;

import org.cwi.waebric.WaebricSymbol;
import org.cwi.waebric.parser.ast.AbstractSyntaxNode;
import org.cwi.waebric.parser.ast.INodeVisitor;
import org.cwi.waebric.parser.ast.token.CharacterLiteral;
import org.cwi.waebric.parser.ast.token.StringLiteral;

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
	
	/**
	 * String literal value.
	 */
	private StringLiteral text;

	/**
	 * Construct empty string.
	 */
	public StrCon() { this(""); }
	
	/**
	 * Construct string based on java string instance.
	 * @param string
	 */
	public StrCon(String string) {
		this.text = new StringLiteral(string);
	}
	
	/**
	 * Construct string based on literal.
	 * @param string
	 */
	public StrCon(StringLiteral string) {
		this.text = string;
	}
	
	/**
	 * Retrieve literal.
	 * @return
	 */
	public StringLiteral getLiteral() {
		return text;
	}
	
	public AbstractSyntaxNode[] getChildren() {
		return new AbstractSyntaxNode[] {
			new CharacterLiteral(WaebricSymbol.DQUOTE),
			text,
			new CharacterLiteral(WaebricSymbol.DQUOTE)
		};
	}
	
	@Override
	public boolean equals(Object obj) {
		return text.equals(obj);
	}
	
	@Override
	public void accept(INodeVisitor visitor) {
		visitor.visit(this);
	}

}