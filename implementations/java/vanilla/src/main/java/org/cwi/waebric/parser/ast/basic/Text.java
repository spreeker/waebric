package org.cwi.waebric.parser.ast.basic;

import org.cwi.waebric.parser.ast.AbstractSyntaxNode;
import org.cwi.waebric.parser.ast.INodeVisitor;

/**
 * "\"" TextChar* "\"" -> Text<br>
 * ~[\0-\31\<\128-\255] \/ [\n\t\r] -> TextChar<br>
 * 
 * @author Jeroen van Schagen
 * @date 20-05-2009
 */
public class Text extends AbstractSyntaxNode {
	
	private String value;

	public Text(String value) {
		this.value = value;
	}
	
	public String getLiteral() {
		return value;
	}
	
	@Override
	public void accept(INodeVisitor visitor) {
		visitor.visit(this);
	}
	
}