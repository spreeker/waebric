package org.cwi.waebric.parser.ast.basic;

import org.cwi.waebric.parser.ast.AbstractSyntaxNode;
import org.cwi.waebric.parser.ast.StringLiteral;

/**
 * head:[A-Za-Z] tail:[A-Za-Z\-0-9]* -> IdCon {cons("default")}
 * 
 * @author Jeroen van Schagen
 * @date 02-06-2009
 */
public class IdCon extends AbstractSyntaxNode {

	private StringLiteral literal;
	
	public IdCon(String identifier) {
		this.literal = new StringLiteral(identifier);
	}
	
	public StringLiteral getLiteral() {
		return literal;
	}
	
	@Override
	public boolean equals(Object obj) {
		return literal.equals(obj);
	}

	public AbstractSyntaxNode[] getChildren() {
		return new StringLiteral[] { literal };
	}

}