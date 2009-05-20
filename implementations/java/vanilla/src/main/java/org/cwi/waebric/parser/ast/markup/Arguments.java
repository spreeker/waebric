package org.cwi.waebric.parser.ast.markup;

import org.cwi.waebric.parser.ast.SyntaxNodeList.SyntaxNodeListWithSeparator;
import org.cwi.waebric.scanner.token.WaebricSymbol;

/**
 * Collection of Argument objects.
 * 
 * @author Jeroen van Schagen
 * @date 19-05-2009
 */
public class Arguments extends SyntaxNodeListWithSeparator<Argument> {

	public Arguments() {
		super("" + WaebricSymbol.COMMA);
	}

	/**
	 * Serial ID
	 */
	private static final long serialVersionUID = -8582049249913945792L;
	
}