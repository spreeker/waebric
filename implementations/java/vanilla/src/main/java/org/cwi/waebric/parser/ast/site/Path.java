package org.cwi.waebric.parser.ast.site;

import org.cwi.waebric.parser.ast.ISyntaxNode;
import org.cwi.waebric.parser.ast.StringLiteral;
import org.cwi.waebric.scanner.token.WaebricSymbol;

/**
 * 
 * @author Jeroen van Schagen
 * @date 20-05-2009
 */
public abstract class Path implements ISyntaxNode {

	protected FileName fileName;
	protected DirName dirName;

	public FileName getFileName() {
		return fileName;
	}

	public void setFileName(FileName file) {
		this.fileName = file;
	}

	public DirName getDirName() {
		return dirName;
	}

	public void setDirName(DirName dir) {
		this.dirName = dir;
	}

	/**
	 * Path implementation, consisting of a directory name and file name
	 * 
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
					new StringLiteral("" + WaebricSymbol.SLASH),
					fileName
				};
		}
		
	}
	
	/**
	 * Path implementation consisting of stricly a filename.
	 * 
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
		
	}
	
}