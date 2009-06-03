package org.cwi.waebric.parser.ast.site;

import org.cwi.waebric.WaebricSymbol;
import org.cwi.waebric.parser.ast.AbstractSyntaxNodeList.SyntaxNodeListWithSeparator;

public class Directory extends SyntaxNodeListWithSeparator<PathElement> {
	
	/**
	 * Separate path elements with a slash /
	 */
	public Directory() {
		super(WaebricSymbol.SLASH);
	}

}
