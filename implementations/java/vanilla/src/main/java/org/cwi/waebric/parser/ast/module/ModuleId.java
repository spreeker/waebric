package org.cwi.waebric.parser.ast.module;

import org.cwi.waebric.parser.ast.SyntaxNodeList;
import org.cwi.waebric.parser.ast.INodeVisitor;
import org.cwi.waebric.parser.ast.basic.IdCon;

/**
 * { IdCon "." }+ -> ModuleId
 * 
 * @author Jeroen van Schagen
 * @date 20-05-2009
 */
public class ModuleId extends SyntaxNodeList<IdCon> {

	public ModuleId() {
		super();
	}
	
	@Override
	public <T> T accept(INodeVisitor<T> visitor) {
		return visitor.visit(this);
	}
	
}