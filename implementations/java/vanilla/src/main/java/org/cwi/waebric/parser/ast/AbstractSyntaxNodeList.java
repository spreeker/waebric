package org.cwi.waebric.parser.ast;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Generic syntax node list implementation for syntax nodes that
 * represent a list structure.
 * 
 * @param <E>
 * 
 * @author Jeroen van Schagen
 * @date 20-05-2009
 */
public class AbstractSyntaxNodeList<E extends AbstractSyntaxNode> extends AbstractSyntaxNode implements Iterable<E> {

	protected ArrayList<E> list = new ArrayList<E>();
	
	/**
	 * Retrieve size
	 * 
	 * @return
	 */
	public int size() {
		return list.size();
	}
	
	/**
	 * Retrieve node
	 * 
	 * @param index
	 * @return
	 */
	public E get(int index) {
		return list.get(index);
	}
	
	/**
	 * Add node
	 * 
	 * @param element
	 * @return
	 */
	public boolean add(E element) {
		if(element == null) { return false; }
		return list.add(element);
	}
	
	/**
	 * Remove node at specified index.
	 * 
	 * @param index Position of removed node
	 * @return Removed node
	 */
	public E remove(int index) {
		return list.remove(index);
	}
	
	/**
	 * Remove all nodes from list.
	 */
	public void clear() {
		list.clear();
	}
	
	/**
	 * Retrieve actual node elements in list.
	 * 
	 * @return Elements
	 */
	public AbstractSyntaxNode[] getElements() {
		return list.toArray(new AbstractSyntaxNode[0]);
	}
	
	/**
	 * Retrieve children
	 * 
	 * @return Children
	 */
	public AbstractSyntaxNode[] getChildren() {
		return getElements();
	}
	
	/**
	 * Create iterator
	 * 
	 * @return Iterator
	 */
	public Iterator<E> iterator() {
		return list.iterator();
	}
	
	@Override
	public boolean equals(Object arg) {
		if(arg instanceof AbstractSyntaxNodeList) {
			AbstractSyntaxNodeList<?> nodeList = (AbstractSyntaxNodeList<?>) arg;
			return this.getElements() == nodeList.getElements();
		}
		
		return false;
	}
	
	@Override
	public String toString() {
		String name = "[";
		
		AbstractSyntaxNode[] children = this.getChildren();
		for(int i = 0; i < children.length; i++) {
			if(i != 0) { name += ","; }
			name += children[i].toString();
		}

		return name + "]";
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
		public final char separator;
		
		public AbstractSeparatedSyntaxNodeList(char separator) {
			this.separator = separator;
		}

		@Override
		public AbstractSyntaxNode[] getChildren() {
			AbstractSyntaxNode[] elements = this.getElements();
			
			int length = elements.length > 0 ? (elements.length * 2) - 1 : 0;
			AbstractSyntaxNode[] children = new AbstractSyntaxNode[length];
			
			for(int i = 0; i < children.length; i++) {
				if(i % 2 == 0) {
					// Even index are reserved to elements
					children[i] = elements[i/2];
				} else {
					// Uneven index are for separators
					children[i] = new CharacterLiteral(separator);
				}
			}

			return children;
		}
		
		@Override
		public boolean equals(Object arg) {
			if(arg instanceof AbstractSeparatedSyntaxNodeList) {
				AbstractSeparatedSyntaxNodeList<?> nodeList = (AbstractSeparatedSyntaxNodeList<?>) arg;
				return this.separator == nodeList.separator && super.equals(arg);
			}
			
			return false;
		}
		
	}

}