package org.cwi.waebric.parser.ast.expression;

import org.cwi.waebric.parser.ast.AbstractSyntaxNode;
import org.cwi.waebric.parser.ast.basic.IdCon;

/**
 * IdCon -> Var { category("MetaVariables") }
 * @author schagen
 *
 */
public class Var extends AbstractSyntaxNode {

	private IdCon identifier;

	public IdCon getIdentifier() {
		return identifier;
	}

	public void setIdentifier(IdCon identifier) {
		this.identifier = identifier;
	}
	
	public AbstractSyntaxNode[] getChildren() {
		return new AbstractSyntaxNode[] { identifier };
	}
	
}