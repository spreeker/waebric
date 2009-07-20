package org.cwi.waebric.parser.ast.basic;

import org.cwi.waebric.parser.ast.AbstractSyntaxNode;
import org.cwi.waebric.parser.ast.INodeVisitor;

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
	private String text;

	/**
	 * Construct empty string.
	 */
	public StrCon() { 
		this("");
	}
	
	/**
	 * Construct string based on java string instance.
	 * @param string
	 */
	public StrCon(String text) {
		this.text = text;
	}
	
	/**
	 * Retrieve literal.
	 * @return
	 */
	public String getLiteral() {
		return text;
	}
	
	@Override
	public void accept(INodeVisitor visitor) {
		visitor.visit(this);
	}

}