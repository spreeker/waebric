package org.cwi.waebric.parser.ast.statement.embedding;

import org.cwi.waebric.WaebricSymbol;
import org.cwi.waebric.parser.ast.AbstractSyntaxNode;
import org.cwi.waebric.parser.ast.CharacterLiteral;
import org.cwi.waebric.parser.ast.INodeVisitor;
import org.cwi.waebric.parser.ast.StringLiteral;

/**
 * ">" TextChar* "\""
 * 
 * @author Jeroen van Schagen 
 * @date 02-06-2009
 */
public class PostText extends AbstractSyntaxNode {

	private StringLiteral text;
	
	public StringLiteral getText() {
		return text;
	}

	public void setText(StringLiteral text) {
		this.text = text;
	}

	public AbstractSyntaxNode[] getChildren() {
		return new AbstractSyntaxNode[] {
			new CharacterLiteral(WaebricSymbol.GREATER_THAN),
			text,
			new CharacterLiteral(WaebricSymbol.DQUOTE)
		};
	}
	
	@Override
	public void accept(INodeVisitor visitor, Object[] args) {
		visitor.visit(this, args);
	}

}
