package org.cwi.waebric.scanner.token;

import java.util.Iterator;
import java.util.List;

/**
 * Token iterator which allows the iteration over tokens. Also allows
 * k look-ahead functionality, which is useful for language parsing.
 * 
 * @author Jeroen van Schagen
 * @date 19-05-2009
 */
public class WaebricTokenIterator implements Iterator<WaebricToken> {

	private List<WaebricToken> collection;
	private int curr;
	
	/**
	 * Construct token iterator based on token collection. Current
	 * position will by default be set on -1.
	 * 
	 * @param collection
	 */
	public WaebricTokenIterator(List<WaebricToken> collection) {
		this(collection, -1);
	}
	
	/**
	 * Construct token iterator based on a token collection and current position.
	 * 
	 * @param collection
	 * @param curr
	 */
	public WaebricTokenIterator(List<WaebricToken> collection, int curr) {
		this.collection = collection;
		this.curr = curr;
	}
	
	/**
	 * Clone iterator by copying the collection and current position. When 
	 * using the cloned iterator, no changes will be made to the original iterator.
	 * 
	 * @return Clone
	 */
	public WaebricTokenIterator clone() {
		return new WaebricTokenIterator(new java.util.ArrayList<WaebricToken>(collection));
	}
	
	/**
	 * Determine if iterator has next element
	 * 
	 * @return Boolean
	 */
	public boolean hasNext() {
		return hasNext(1);
	}
	
	/**
	 * Determine if iterator has k next element(s)
	 * 
	 * @param k Lookahead
	 * @return Boolean
	 */
	public boolean hasNext(int k) {
		return curr+k >= 0 && curr+k < collection.size();
	}

	/**
	 * Retrieve next token and increment current position.
	 * 
	 * @return Token at current+1
	 */
	public WaebricToken next() {
		curr++; // Increment current position
		return collection.get(curr);
	}
	
	/**
	 * Retrieve token which is positioned current+k.
	 * 
	 * @param k Lookahead
	 * @return Token at current+k
	 */
	public WaebricToken peek(int k) {
		if(! hasNext(k)) { return null; }
		return collection.get(curr+k);
	}
	
	/**
	 * Retrieve current token.
	 * 
	 * @return Current token
	 */
	public WaebricToken current() {
		return collection.get(curr);
	}

	/**
	 * Remove current token from iterator.
	 */
	public void remove() {
		collection.remove(curr);
		curr--; // Decrement current position
	}
	
	/**
	 * Store additional token behind current token.
	 * 
	 * @param token Token
	 */
	public void add(WaebricToken token) {
		collection.add(curr+1, token);
	}
	
	/**
	 * Store collection of tokens behind current token.
	 * 
	 * @param tokens Collection of tokens
	 */
	public void addAll(List<? extends WaebricToken> tokens) {
		collection.addAll(curr+1, tokens);
	}
	
	@Override
	public String toString() {
		return collection.toString();
	}

}