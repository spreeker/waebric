package org.cwi.waebric.parser.ast.module;

import java.util.ArrayList;

import org.cwi.waebric.parser.ast.ISyntaxNode;

/**
 * Modules is a module collection, which represents a typical Waebric program. 
 * 
 * @author Jeroen van Schagen
 * @date 19-05-2009
 */
public class Modules extends ArrayList<Module> implements ISyntaxNode {

	private static final long serialVersionUID = 2499166908490038058L;

	@Override
	public ISyntaxNode[] getChildren() {
		return toArray(new Module[0]);
	}

}