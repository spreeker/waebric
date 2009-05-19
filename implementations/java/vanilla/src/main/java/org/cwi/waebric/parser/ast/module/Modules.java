package org.cwi.waebric.parser.ast.module;

import java.util.ArrayList;

import org.cwi.waebric.parser.ast.ISyntaxNode;

public class Modules extends ArrayList<Module> implements ISyntaxNode {

	/**
	 * Serial ID
	 */
	private static final long serialVersionUID = 2499166908490038058L;

	@Override
	public ISyntaxNode[] getChildren() {
		return toArray(new Module[0]);
	}

}