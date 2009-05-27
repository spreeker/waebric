package org.cwi.waebric.parser.ast;

import java.util.ArrayList;

/**
 * Generic syntax node list implementation for syntax nodes that
 * represent a list structure.
 * 
 * @param <E>
 * 
 * @author Jeroen van Schagen
 * @date 20-05-2009
 */
public class SyntaxNodeList<E extends ISyntaxNode> extends ArrayList<E> implements ISyntaxNode {

	/**
	 * Serial ID
	 */
	private static final long serialVersionUID = -5624162057293783716L;

	/**
	 * Retrieve elements
	 * 
	 * @return
	 */
	public ISyntaxNode[] getElements() {
		return this.toArray(new ISyntaxNode[0]);
	}
	
	/**
	 * Retrieve children
	 * 
	 * @return
	 */
	public ISyntaxNode[] getChildren() {
		return getElements();
	}
	
	/**
	 * Generic syntax node list implementation for syntax that
	 * represent a list structure, including a separator literal
	 * between each element.
	 * 
	 * @param <E>
	 * 
	 * @author Jeroen van Schagen
	 * @date 20-05-2009
	 */
	public static class SyntaxNodeListWithSeparator<E extends ISyntaxNode> extends SyntaxNodeList<E> {

		/**
		 * Serial ID
		 */
		private static final long serialVersionUID = -2538711881618951310L;
		
		/**
		 * Separation character
		 */
		private final char separator;
		
		public SyntaxNodeListWithSeparator(char separator) {
			this.separator = separator;
		}

		@Override
		public ISyntaxNode[] getChildren() {
			ArrayList<ISyntaxNode> children = new ArrayList<ISyntaxNode>();
			
			for(int i = 0; i < this.size(); i++) {
				children.add(this.get(i));
				
				// Attach separator when element is not last in collection
				if(i < this.size()-1) { children.add(new CharacterLiteral(separator)); }
			}
			
			return children.toArray(new ISyntaxNode[0]);
		}
		
	}

}