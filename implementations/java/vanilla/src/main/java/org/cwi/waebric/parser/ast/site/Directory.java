package org.cwi.waebric.parser.ast.site;

import org.cwi.waebric.WaebricSymbol;
import org.cwi.waebric.parser.ast.SyntaxNodeList.SyntaxNodeListWithSeparator;

public class Directory extends SyntaxNodeListWithSeparator<PathElement> {

	/**
	 * Serial ID
	 */
	private static final long serialVersionUID = -7141207970011776899L;
	
	/**
	 * Separate path elements with a slash /
	 */
	public Directory() {
		super(WaebricSymbol.SLASH);
	}

}
