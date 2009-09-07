package org.cwi.waebric.parser.ast.module.site;

import org.cwi.waebric.parser.ast.SyntaxNode;
import org.cwi.waebric.parser.ast.INodeVisitor;
import org.cwi.waebric.parser.ast.markup.Markup;

/**
 * Path ":" Markup -> Mapping
 * 
 * @author Jeroen van Schagen
 * @date 22-05-2009
 *
 */
public class Mapping extends SyntaxNode {

	private Path path;
	private Markup markup;
	
	public Path getPath() {
		return path;
	}

	public void setPath(Path path) {
		this.path = path;
	}

	public Markup getMarkup() {
		return markup;
	}

	public void setMarkup(Markup markup) {
		this.markup = markup;
	}

	public SyntaxNode[] getChildren() {
		return new SyntaxNode[] { path, markup };
	}
	
	@Override
	public <T> T accept(INodeVisitor<T> visitor) {
		return visitor.visit(this);
	}

}