package org.cwi.waebric.parser.ast.module;

import org.cwi.waebric.WaebricSymbol;
import org.cwi.waebric.parser.ast.SyntaxNodeList.SyntaxNodeListWithSeparator;
import org.cwi.waebric.parser.ast.basic.IdCon;

public class ModuleId extends SyntaxNodeListWithSeparator<IdCon> {

	public ModuleId() {
		super(WaebricSymbol.PERIOD);
	}

}