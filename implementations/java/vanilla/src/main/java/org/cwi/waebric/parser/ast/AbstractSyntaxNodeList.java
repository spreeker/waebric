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
public class AbstractSyntaxNodeList<E extends AbstractSyntaxNode> extends AbstractSyntaxNode {

	protected ArrayList<E> list = new ArrayList<E>();
	
	/**
	 * Retrieve size
	 * @return
	 */
	public int size() {
		return list.size();
	}
	
	/**
	 * Retrieve node
	 * @param index
	 * @return
	 */
	public E get(int index) {
		return list.get(index);
	}
	
	/**
	 * Add node
	 * @param element
	 * @return
	 */
	public boolean add(E element) {
		return list.add(element);
	}
	
	/**
	 * Remove node
	 * @param index
	 * @return
	 */
	public E remove(int index) {
		return list.remove(index);
	}
	
	/**
	 * Remove all nodes
	 */
	public void clear() {
		list.clear();
	}
	
	/**
	 * Retrieve elements
	 * 
	 * @return
	 */
	public AbstractSyntaxNode[] getElements() {
		return list.toArray(new AbstractSyntaxNode[0]);
	}
	
	/**
	 * Retrieve children
	 * 
	 * @return
	 */
	public AbstractSyntaxNode[] getChildren() {
		return getElements();
	}
	
	@Override
	public String toString() {
		String name = this.getClass().getSimpleName();
		
		// Attach children data
		name += "[";
		AbstractSyntaxNode[] children = this.getChildren();
		for(int i = 0; i < children.length; i++) {
			if(i != 0) { name += ","; }
			name += children[i].toString();
		}
		name += "]";
		
		return name;
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
	public static class AbstractSeparatedSyntaxNodeList<E extends AbstractSyntaxNode> extends AbstractSyntaxNodeList<E> {
		
		/**
		 * Separation character
		 */
		private final char separator;
		
		public AbstractSeparatedSyntaxNodeList(char separator) {
			this.separator = separator;
		}

		@Override
		public AbstractSyntaxNode[] getChildren() {
			ArrayList<AbstractSyntaxNode> children = new ArrayList<AbstractSyntaxNode>();
			
			for(int i = 0; i < list.size(); i++) {
				if(i != 0) { children.add(new CharacterLiteral(separator)); }
				children.add(list.get(i));
			}
			
			return children.toArray(new AbstractSyntaxNode[0]);
		}
		
	}

}