package org.cwi.waebric.parser.ast.module.site;

import org.cwi.waebric.parser.ast.ISyntaxNode;
import org.cwi.waebric.parser.ast.StringLiteral;
import org.cwi.waebric.scanner.token.WaebricSymbol;

public class Path implements ISyntaxNode {

	private DirName dir;
	private FileName file;
	
	public DirName getDir() {
		return dir;
	}

	public void setDir(DirName dir) {
		this.dir = dir;
	}

	public FileName getFile() {
		return file;
	}

	public void setFile(FileName file) {
		this.file = file;
	}

	@Override
	public ISyntaxNode[] getChildren() {
		if(dir == null) { return new ISyntaxNode[] { file }; }
		return new ISyntaxNode[] { dir, new StringLiteral("" + WaebricSymbol.SLASH), file };
	}
	
}