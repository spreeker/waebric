package org.cwi.waebric.parser.ast.site;

import java.util.ArrayList;

import org.cwi.waebric.parser.ast.ISyntaxNode;

public class Mappings extends ArrayList<Mapping> implements ISyntaxNode {

	/**
	 * Serial ID
	 */
	private static final long serialVersionUID = -6962647667939837646L;

	public ISyntaxNode[] getChildren() {
		return toArray(new Mapping[0]);
	}

}
