package org.cwi.waebric.parser.ast.module.site;

import org.cwi.waebric.parser.ast.SyntaxNode;
import org.cwi.waebric.parser.ast.INodeVisitor;

/**
 * File path
 * 
 * @author Jeroen van Schagen
 * @date 20-05-2009
 */
public class Path extends SyntaxNode {

	private String path;
	
	/**
	 * Default constructor
	 */
	public Path() { }
	
	/**
	 * Construct path based on string value
	 * @param value
	 */
	public Path(String path) { 
		this.path = path;
	}
	
	public String getValue() {
		return path;
	}
	
	public void setValue(String path) {
		this.path = path;
	}
	
	@Override
	public <T> T accept(INodeVisitor<T> visitor) {
		return visitor.visit(this);
	}
	
}