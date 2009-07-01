package org.cwi.waebric.parser.ast.module.site;

import org.cwi.waebric.parser.ast.AbstractSyntaxNode;
import org.cwi.waebric.parser.ast.INodeVisitor;
import org.cwi.waebric.parser.ast.token.StringLiteral;

/**
 * File path
 * 
 * @author Jeroen van Schagen
 * @date 20-05-2009
 */
public class Path extends AbstractSyntaxNode {

	private StringLiteral path;
	
	/**
	 * Default constructor
	 */
	public Path() { }
	
	/**
	 * Construct path based on string value
	 * @param value
	 */
	public Path(String value) { path = new StringLiteral(value); }
	
	public StringLiteral getValue() {
		return path;
	}
	
	public void setValue(StringLiteral path) {
		this.path = path;
	}
	
	@Override
	public void accept(INodeVisitor visitor) {
		this.accept(visitor);
	}
	
	@Override
	public AbstractSyntaxNode[] getChildren() {
		return new AbstractSyntaxNode[] { path };
	}
	
}