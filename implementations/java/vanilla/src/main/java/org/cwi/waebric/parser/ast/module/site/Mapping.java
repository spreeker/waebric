package org.cwi.waebric.parser.ast.module.site;

import org.cwi.waebric.WaebricSymbol;
import org.cwi.waebric.parser.ast.AbstractSyntaxNode;
import org.cwi.waebric.parser.ast.INodeVisitor;
import org.cwi.waebric.parser.ast.markup.Markup;
import org.cwi.waebric.parser.ast.token.CharacterLiteral;

/**
 * Path ":" Markup -> Mapping
 * 
 * @author Jeroen van Schagen
 * @date 22-05-2009
 *
 */
public class Mapping extends AbstractSyntaxNode {

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

	public AbstractSyntaxNode[] getChildren() {
		return new AbstractSyntaxNode[] { 
			path, 
			new CharacterLiteral(WaebricSymbol.COLON), 
			markup
		};
	}
	
	@Override
	public void accept(INodeVisitor visitor, Object[] args) {
		visitor.visit(this, args);
	}

}