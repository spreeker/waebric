package org.cwi.waebric.parser.ast.site;

import org.cwi.waebric.WaebricSymbol;
import org.cwi.waebric.parser.ast.ISyntaxNode;
import org.cwi.waebric.parser.ast.CharacterLiteral;

/**
 * File path
 * 
 * @author Jeroen van Schagen
 * @date 20-05-2009
 */
public abstract class Path implements ISyntaxNode {

	protected FileName fileName;
	protected DirName dirName;

	/**
	 * Retrieve file name
	 * @return
	 */
	public FileName getFileName() {
		return fileName;
	}

	/**
	 * Store file name
	 * @param file
	 */
	public void setFileName(FileName file) {
		this.fileName = file;
	}

	/**
	 * Retrieve directory name
	 * @return
	 */
	public DirName getDirName() {
		return dirName;
	}

	/**
	 * Store directory name
	 * @param dir
	 */
	public void setDirName(DirName dir) {
		this.dirName = dir;
	}

	/**
	 * Path implementation, consisting of a directory name and file name<br><br>
	 * 
	 * Grammar:<br>
	 * <code>
	 * 	Dirname "/" FileName -> Path
	 * </code>
	 * 
	 * @author Jeroen van Schagen
	 * @date 20-05-2009
	 */
	public static class PathWithDir extends Path {
		
		public ISyntaxNode[] getChildren() {
			return new ISyntaxNode[] {
					dirName,
					new CharacterLiteral(WaebricSymbol.SLASH),
					fileName
				};
		}
		
		@Override
		public boolean equals(Object obj) {
			// TODO Auto-generated method stub
			return super.equals(obj);
		}
		
		@Override
		public String toString() {
			// TODO Auto-generated method stub
			return super.toString();
		}
		
	}
	
	/**
	 * Path implementation consisting of only a filename.<br><br>
	 * 
	 * Grammar:<br>
	 * <code>
	 * 	Filename -> Path
	 * </code>
	 * 
	 * @author Jeroen van Schagen
	 * @date 20-05-2009
	 */
	public static class PathWithoutDir extends Path {

		public ISyntaxNode[] getChildren() {
			return new ISyntaxNode[] { fileName };
		}
		
		@Override
		public boolean equals(Object obj) {
			return fileName.equals(obj);
		}
		
		@Override
		public String toString() {
			return fileName.toString();
		}
		
	}
	
}