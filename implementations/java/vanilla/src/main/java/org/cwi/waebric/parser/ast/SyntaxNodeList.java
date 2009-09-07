package org.cwi.waebric.parser.ast;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;

/**
 * Generic syntax node list implementation for syntax nodes that represent a
 * list structure.
 * 
 * @param <E>
 * 
 * @author Jeroen van Schagen
 * @date 20-05-2009
 */
public class SyntaxNodeList<E extends SyntaxNode> extends SyntaxNode implements
		List<E> {

	/**
	 * Element collection
	 */
	protected List<E> list;

	/**
	 * Construct syntax node list.
	 */
	public SyntaxNodeList() {
		list = new ArrayList<E>();
	}

	/**
	 * Construct syntax node list.
	 * 
	 * @param list
	 *            Collection of all elements
	 */
	public SyntaxNodeList(List<E> list) {
		this.list = list;
	}

	public boolean add(E element) {
		if (element == null) {
			return false;
		}
		if (this.contains(element)) {
			return false;
		}
		return list.add(element);
	}

	public void add(int index, E element) {
		list.add(index, element);
	}

	public boolean addAll(Collection<? extends E> c) {
		return list.addAll(c);
	}

	public boolean addAll(int index, Collection<? extends E> c) {
		return list.addAll(index, c);
	}

	public void clear() {
		list.clear();
	}

	@Override
	public SyntaxNodeList<E> clone() {
		return new SyntaxNodeList<E>(new ArrayList<E>(list));
	}

	public boolean contains(Object o) {
		return list.contains(o);
	}

	public boolean containsAll(Collection<?> c) {
		return list.containsAll(c);
	}

	@Override
	public boolean equals(Object arg) {
		if (arg instanceof SyntaxNodeList) {
			SyntaxNodeList<?> nodeList = (SyntaxNodeList<?>) arg;

			// Check if size matches
			if (nodeList.size() != this.size()) {
				return false;
			}

			for (int i = 0; i < this.size(); i++) {
				// Check if each element matches
				if (!this.get(i).equals(nodeList.get(i))) {
					return false;
				}
			}

			return true;
		}

		return false;
	}

	public E get(int index) {
		return list.get(index);
	}

	public SyntaxNode[] getChildren() {
		return list.toArray(new SyntaxNode[0]);
	}

	public int indexOf(Object o) {
		return list.indexOf(o);
	}

	public boolean isEmpty() {
		return list.isEmpty();
	}

	public Iterator<E> iterator() {
		return list.iterator();
	}

	public int lastIndexOf(Object o) {
		return list.lastIndexOf(o);
	}

	public ListIterator<E> listIterator() {
		return list.listIterator();
	}

	public ListIterator<E> listIterator(int index) {
		return list.listIterator();
	}

	public E remove(int index) {
		return list.remove(index);
	}

	public boolean remove(Object o) {
		return list.remove(o);
	}

	public boolean removeAll(Collection<?> c) {
		return list.retainAll(c);
	}

	public boolean retainAll(Collection<?> c) {
		return list.retainAll(c);
	}

	public E set(int index, E element) {
		return list.set(index, element);
	}

	public int size() {
		return list.size();
	}

	public SyntaxNodeList<E> subList(int fromIndex, int toIndex) {
		return new SyntaxNodeList<E>(list.subList(fromIndex, toIndex));
	}

	public Object[] toArray() {
		return list.toArray();
	}

	public <T> T[] toArray(T[] a) {
		return list.toArray(a);
	}

	@Override
	public String toString() {
		String name = "[";

		SyntaxNode[] children = this.getChildren();
		for (int i = 0; i < children.length; i++) {
			if (i != 0) {
				name += ",";
			}
			name += children[i].toString();
		}

		return name + "]";
	}

	@Override
	public <T> T accept(INodeVisitor<T> visitor) {
		return visitor.visit(this);
	}

}