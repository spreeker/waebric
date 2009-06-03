package org.cwi.waebric.parser.ast.module;

import org.cwi.waebric.WaebricSymbol;
import org.cwi.waebric.parser.ast.AbstractSyntaxNodeList.SyntaxNodeListWithSeparator;
import org.cwi.waebric.parser.ast.basic.IdCon;

/**
 * { IdCon "." }+ -> ModuleId
 * 
 * @author Jeroen van Schagen
 * @date 20-05-2009
 */
public class ModuleId extends SyntaxNodeListWithSeparator<IdCon> {

	public ModuleId() {
		super(WaebricSymbol.PERIOD);
	}

}