package org.cwi.waebric.parser.ast.module;

import org.cwi.waebric.WaebricSymbol;
import org.cwi.waebric.parser.ast.INodeVisitor;
import org.cwi.waebric.parser.ast.SeparatedNodeList;
import org.cwi.waebric.parser.ast.basic.IdCon;

/**
 * { IdCon "." }+ -> ModuleId
 * 
 * @author Jeroen van Schagen
 * @date 20-05-2009
 */
public class ModuleId extends SeparatedNodeList<IdCon> {

	public ModuleId() {
		super(WaebricSymbol.PERIOD);
	}
	
	@Override
	public void accept(INodeVisitor visitor, Object[] args) {
		visitor.visit(this, args);
	}
	
}