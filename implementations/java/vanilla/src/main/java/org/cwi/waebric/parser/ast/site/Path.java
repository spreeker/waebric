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
	 * Grammar:<br>
	 * <code>
	 * 	Dirname "/" FileName -> Path
	 * </code>
	 */
	public static class PathWithDir extends Path {
		
		protected DirName dirName;
		
		public PathWithDir(DirName dirName) {
			this.dirName = dirName;
		}
		
		/**
		 * Retrieve directory name
		 * @return
		 */
		public DirName getDirName() {
			return dirName;
		}
		
		@Override
		public String toString() {
			return dirName.toString() + "/" + fileName.toString();
		}
		
		public ISyntaxNode[] getChildren() {
			return new ISyntaxNode[] {
				dirName,
				new CharacterLiteral(WaebricSymbol.SLASH),
				fileName
			};
		}
		
	}
	
	/**
	 * Grammar:<br>
	 * <code>
	 * 	Filename -> Path
	 * </code>
	 */
	public static class PathWithoutDir extends Path {
		
		@Override
		public boolean equals(Object obj) {
			return fileName.equals(obj);
		}
		
		@Override
		public String toString() {
			return fileName.toString();
		}
		
		public ISyntaxNode[] getChildren() {
			return new ISyntaxNode[] { fileName };
		}
		
	}
	
}