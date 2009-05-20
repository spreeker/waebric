package org.cwi.waebric.parser.ast.site;

import org.cwi.waebric.WaebricSymbol;
import org.cwi.waebric.parser.ast.ISyntaxNode;
import org.cwi.waebric.parser.ast.StringLiteral;
import org.cwi.waebric.parser.ast.markup.Markup;

public class Mapping implements ISyntaxNode {

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

	public ISyntaxNode[] getChildren() {
		return new ISyntaxNode[] { 
				path, 
				new StringLiteral("" + WaebricSymbol.COLON), 
				markup
			};
	}

}