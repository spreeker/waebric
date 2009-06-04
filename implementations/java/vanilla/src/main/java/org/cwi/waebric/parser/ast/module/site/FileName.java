package org.cwi.waebric.parser.ast.module.site;

import org.cwi.waebric.WaebricSymbol;
import org.cwi.waebric.parser.ast.AbstractSyntaxNode;
import org.cwi.waebric.parser.ast.StringLiteral;

public class FileName extends AbstractSyntaxNode {

	private PathElement name;
	private FileExt ext;
	
	public PathElement getName() {
		return name;
	}
	
	public void setName(PathElement name) {
		this.name = name;
	}
	
	public FileExt getExt() {
		return ext;
	}
	
	public void setExt(FileExt ext) {
		this.ext = ext;
	}
	
	public AbstractSyntaxNode[] getChildren() {
		return new AbstractSyntaxNode[] {
			name,
			new StringLiteral("" + WaebricSymbol.PERIOD),
			ext
		};
	}

}