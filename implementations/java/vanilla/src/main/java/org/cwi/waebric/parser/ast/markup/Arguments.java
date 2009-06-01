package org.cwi.waebric.parser.ast.markup;

import org.cwi.waebric.WaebricSymbol;
import org.cwi.waebric.parser.ast.CharacterLiteral;
import org.cwi.waebric.parser.ast.ISyntaxNode;
import org.cwi.waebric.parser.ast.SyntaxNodeList.SyntaxNodeListWithSeparator;

/**
 * "(" { Argument "," }* ")" -> Arguments
 * 
 * @author Jeroen van Schagen
 * @date 19-05-2009
 */
public class Arguments extends SyntaxNodeListWithSeparator<Argument> {

	public Arguments() {
		super(WaebricSymbol.COMMA);
	}

	public ISyntaxNode[] getChildren() {
		ISyntaxNode[] children = super.getChildren();
		ISyntaxNode[] actual = new ISyntaxNode[children.length+2];
		actual[0] = new CharacterLiteral(WaebricSymbol.LPARANTHESIS);
		actual[actual.length-1] = new CharacterLiteral(WaebricSymbol.RPARANTHESIS);
		for(int i = 1; i < actual.length-1; i++) {
			actual[i] = children[i-1];
		}
		return actual;
	}
	
}