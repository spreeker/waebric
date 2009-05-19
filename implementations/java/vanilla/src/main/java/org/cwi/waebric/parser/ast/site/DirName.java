package org.cwi.waebric.parser.ast.site;

import org.cwi.waebric.parser.ast.ISyntaxNode;

public class DirName implements ISyntaxNode {

	private Directory directory;

	public Directory getDirectory() {
		return directory;
	}

	public void setDirectory(Directory directory) {
		this.directory = directory;
	}

	@Override
	public ISyntaxNode[] getChildren() {
		return new ISyntaxNode[] { directory };
	}

}
