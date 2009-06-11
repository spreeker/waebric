package org.cwi.waebric.parser.ast.module.site;

import org.cwi.waebric.WaebricSymbol;
import org.cwi.waebric.parser.ast.AbstractSyntaxNode;
import org.cwi.waebric.parser.ast.CharacterLiteral;
import org.cwi.waebric.parser.ast.INodeVisitor;

/**
 * File path
 * 
 * @author Jeroen van Schagen
 * @date 20-05-2009
 */
public abstract class Path extends AbstractSyntaxNode {

	protected FileName fileName;

	public FileName getFileName() {
		return fileName;
	}

	public void setFileName(FileName file) {
		this.fileName = file;
	}

	/**
	 * 	DirName "/" FileName -> Path
	 */
	public static class PathWithDir extends Path {
		
		protected DirName dirName;
		
		public PathWithDir(DirName dirName) {
			this.dirName = dirName;
		}
		
		public DirName getDirName() {
			return dirName;
		}
		
		@Override
		public AbstractSyntaxNode[] getChildren() {
			return new AbstractSyntaxNode[] {
				dirName,
				new CharacterLiteral(WaebricSymbol.SLASH),
				fileName
			};
		}
		
		@Override
		public void accept(INodeVisitor visitor, Object[] args) {
			visitor.visit(this, args);
		}
		
	}
	
	/**
	 * FileName -> Path
	 */
	public static class PathWithoutDir extends Path {
		
		@Override
		public AbstractSyntaxNode[] getChildren() {
			return new AbstractSyntaxNode[] { fileName };
		}
		
		@Override
		public void accept(INodeVisitor visitor, Object[] args) {
			visitor.visit(this, args);
		}
		
	}
	
}