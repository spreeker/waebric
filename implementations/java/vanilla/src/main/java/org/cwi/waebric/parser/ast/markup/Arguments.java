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
public class Arguments implements ISyntaxNode {

	private SyntaxNodeListWithSeparator<Argument> arguments;
	
	public Arguments() {
		arguments = new SyntaxNodeListWithSeparator<Argument>(WaebricSymbol.COMMA);
	}

	@Override
	public ISyntaxNode[] getChildren() {
		return new ISyntaxNode[] {
			new CharacterLiteral(WaebricSymbol.LPARANTHESIS),
			arguments,
			new CharacterLiteral(WaebricSymbol.RPARANTHESIS)
		};
	}

	public boolean add(Argument argument) {
		return arguments.add(argument);
	}
	
	public Argument getElement(int index) {
		return arguments.get(index);
	}
	
	public int getElementCount() {
		return arguments.size();
	}
	
}