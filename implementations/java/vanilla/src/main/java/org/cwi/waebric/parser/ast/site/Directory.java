package org.cwi.waebric.parser.ast.site;

import java.util.ArrayList;

import org.cwi.waebric.parser.ast.ISyntaxNode;
import org.cwi.waebric.parser.ast.StringLiteral;
import org.cwi.waebric.scanner.token.WaebricSymbol;

public class Directory extends ArrayList<PathElement> implements ISyntaxNode {

	/**
	 * Serial ID
	 */
	private static final long serialVersionUID = -7141207970011776899L;

	@Override
	public ISyntaxNode[] getChildren() {
		ArrayList<ISyntaxNode> clone = new ArrayList<ISyntaxNode>();

		for(int i = 0; i < this.size(); i++) {
			clone.add(this.get(i));
			if(i != this.size()-1) { clone.add(new StringLiteral("" + WaebricSymbol.SLASH)); }
		}
		
		return clone.toArray(new ISyntaxNode[0]);
	}

}
