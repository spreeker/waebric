package org.cwi.waebric.parser.ast.module.site;

import org.cwi.waebric.parser.ast.AbstractSyntaxNode;
import org.cwi.waebric.parser.ast.INodeVisitor;

/**
 * Directory -> DirName
 * 
 * @author Jeroen van Schagen
 * @date 20-05-2009
 */
public class DirName extends AbstractSyntaxNode {

	private Directory directory;

	public Directory getDirectory() {
		return directory;
	}

	public void setDirectory(Directory directory) {
		this.directory = directory;
	}

	public AbstractSyntaxNode[] getChildren() {
		return new AbstractSyntaxNode[] { directory };
	}
	
	@Override
	public String toString() {
		return directory.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		return directory.equals(obj);
	}
	
	@Override
	public void accept(INodeVisitor visitor) {
		visitor.visit(this);
	}

}