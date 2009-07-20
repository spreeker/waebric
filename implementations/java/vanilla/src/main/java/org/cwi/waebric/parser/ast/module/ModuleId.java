package org.cwi.waebric.parser.ast.module;

import org.cwi.waebric.parser.ast.AbstractSyntaxNodeList;
import org.cwi.waebric.parser.ast.INodeVisitor;
import org.cwi.waebric.parser.ast.basic.IdCon;

/**
 * { IdCon "." }+ -> ModuleId
 * 
 * @author Jeroen van Schagen
 * @date 20-05-2009
 */
public class ModuleId extends AbstractSyntaxNodeList<IdCon> {

	public ModuleId() {
		super();
	}
	
	@Override
	public void accept(INodeVisitor visitor) {
		visitor.visit(this);
	}
	
}