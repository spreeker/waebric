package org.cwi.waebric.parser.ast.expressions;

import org.cwi.waebric.WaebricSymbol;
import org.cwi.waebric.parser.ast.ISyntaxNode;
import org.cwi.waebric.parser.ast.StringLiteral;
import org.cwi.waebric.parser.ast.basic.IdCon;

/**
 * IdCon ":" Expression -> Expression
 * @author schagen
 *
 */
public class KeyValuePair implements ISyntaxNode {

	private IdCon identifier;
	private Expression expression;
	
	public IdCon getIdentifier() {
		return identifier;
	}

	public void setIdentifier(IdCon identifier) {
		this.identifier = identifier;
	}

	public Expression getExpression() {
		return expression;
	}

	public void setExpression(Expression expression) {
		this.expression = expression;
	}

	public ISyntaxNode[] getChildren() {
		return new ISyntaxNode[] { 
				identifier,
				new StringLiteral("" + WaebricSymbol.COLON),
				expression
			};
	}

}