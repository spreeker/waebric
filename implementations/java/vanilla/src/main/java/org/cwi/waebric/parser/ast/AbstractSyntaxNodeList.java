package org.cwi.waebric.parser.ast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Generic syntax node list implementation for syntax nodes that
 * represent a list structure.
 * 
 * @param <E>
 * 
 * @author Jeroen van Schagen
 * @date 20-05-2009
 */
public class AbstractSyntaxNodeList<E extends AbstractSyntaxNode> extends AbstractSyntaxNode implements List<E> {

	/**
	 * Element collection
	 */
	protected List<E> list;
	
	/**
	 * Construct syntax node list.
	 */
	public AbstractSyntaxNodeList() {
		list =  new ArrayList<E>();
	}

	/**
	 * Construct syntax node list.
	 * @param list Collection of all elements
	 */
	public AbstractSyntaxNodeList(List<E> list) {
		this.list = list;
	}
	
	@Override
	public boolean add(E element) {
		if(element == null) { return false; }
		if(this.contains(element)) { return false; }
		return list.add(element);
	}
	
	@Override
	public void add(int index, E element) {
		list.add(index, element);
	}
	
	@Override
	public boolean addAll(Collection<? extends E> c) {
		return list.addAll(c);
	}
	
	@Override
	public boolean addAll(int index, Collection<? extends E> c) {
		return list.addAll(index, c);
	}
	
	@Override
	public void clear() {
		list.clear();
	}
	
	@Override
	public AbstractSyntaxNodeList<E> clone() {
		return new AbstractSyntaxNodeList<E>(new ArrayList<E>(list));
	}
	
	@Override
	public boolean contains(Object o) {
		return list.contains(o);
	}
	
	@Override
	public boolean containsAll(Collection<?> c) {
		return list.containsAll(c);
	}
	
	@Override
	public boolean equals(Object arg) {
		if(arg instanceof AbstractSyntaxNodeList) {
			AbstractSyntaxNodeList<?> nodeList = (AbstractSyntaxNodeList<?>) arg;
			if(nodeList.size() != this.size()) { return false; }
			for(int i = 0; i < this.size(); i++) {
				if(! this.get(i).equals(nodeList.get(i))) { return false; }
			}
			
			return true;
		}
		
		return false;
	}
	
	@Override
	public E get(int index) {
		return list.get(index);
	}

	@Override
	public AbstractSyntaxNode[] getChildren() {
		return list.toArray(new AbstractSyntaxNode[0]);
	}

	@Override
	public int indexOf(Object o) {
		return list.indexOf(o);
	}

	@Override
	public boolean isEmpty() {
		return list.isEmpty();
	}

	@Override
	public Iterator<E> iterator() {
		return list.iterator();
	}

	@Override
	public int lastIndexOf(Object o) {
		return list.lastIndexOf(o);
	}

	@Override
	public ListIterator<E> listIterator() {
		return list.listIterator();
	}

	@Override
	public ListIterator<E> listIterator(int index) {
		return list.listIterator();
	}

	@Override
	public E remove(int index) {
		return list.remove(index);
	}

	@Override
	public boolean remove(Object o) {
		return list.remove(o);
	}

	@Override
	public boolean removeAll(Collection<?> c) {
		return list.retainAll(c);
	}

	@Override
	public boolean retainAll(Collection<?> c) {
		return list.retainAll(c);
	}

	@Override
	public E set(int index, E element) {
		return list.set(index, element);
	}

	@Override
	public int size() {
		return list.size();
	}

	@Override
	public List<E> subList(int fromIndex, int toIndex) {
		return list.subList(fromIndex, toIndex);
	}

	@Override
	public Object[] toArray() {
		return list.toArray();
	}

	@Override
	public <T> T[] toArray(T[] a) {
		return list.toArray(a);
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
	 * Construct a combined list of this and e.
	 * @param element Element
	 * @return Union
	 */
	public AbstractSyntaxNodeList<E> union(E element) {
		AbstractSyntaxNodeList<E> clone = this.clone();
		clone.add(element);
		return clone;
	}
	
	/**
	 * Construct a combined list of this and c.
	 * @param c Collection
	 * @return Union
	 */
	public AbstractSyntaxNodeList<E> union(Collection<? extends E> c) {
		AbstractSyntaxNodeList<E> clone = this.clone();
		clone.addAll(c);
		return clone;
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
		
		/**
		 * Construct separated list.
		 * @param separator Separation character
		 */
		public AbstractSeparatedSyntaxNodeList(char separator) {
			this.separator = separator;
		}
		
		/**
		 * Construct separated list.
		 * @param list List containing all elements.
		 * @param separator Separation character
		 */
		public AbstractSeparatedSyntaxNodeList(List<E> list, char separator) {
			super(list);
			this.separator = separator;
		}

		@Override
		public AbstractSeparatedSyntaxNodeList<E> clone() {
			return new AbstractSeparatedSyntaxNodeList<E>(
				new AbstractSyntaxNodeList<E>(new ArrayList<E>(list)), 
				separator
			);
		}

		@Override
		public AbstractSyntaxNode[] getChildren() {
			AbstractSyntaxNode[] elements = super.getChildren();
			
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
				AbstractSeparatedSyntaxNodeList<?> list = (AbstractSeparatedSyntaxNodeList<?>) arg;
				return list.separator == this.separator && super.equals(arg);
			}
			
			return false;
		}

	}

}