package org.cwi.waebric.scanner.token;

import java.util.Iterator;
import java.util.List;

/**
 * Token iterator which allows k lookahead, besides regular iterator
 * functionality.
 * 
 * @author Jeroen van Schagen
 * @date 19-05-2009
 */
public class WaebricTokenIterator implements Iterator<WaebricToken> {

	private List<WaebricToken> collection;
	private int curr = -1;
	
	/**
	 * Construct iterator based
	 * 
	 * @param collection
	 */
	public WaebricTokenIterator(List<WaebricToken> collection) {
		this.collection = collection;
	}
	
	/**
	 * Determine if iterator has next element
	 */
	public boolean hasNext() {
		return hasNext(1);
	}
	
	/**
	 * Determine if iterator has k next element(s)
	 * 
	 * @param k Lookahead
	 * @return
	 */
	public boolean hasNext(int k) {
		return curr+k >= 0 && curr+k < collection.size();
	}

	/**
	 * Retrieve next token by incrementing current position.
	 * 
	 * @return Next token
	 */
	public WaebricToken next() {
		curr++; // Increment current position
		return collection.get(curr);
	}
	
	/**
	 * Retrieve token which is positioned current+k, no alterations
	 * are made to the current position of iterator.
	 * 
	 * @param k Lookahead
	 * @return Token at curr+k
	 */
	public WaebricToken peek(int k) {
		if(! hasNext(k)) { return null; }
		return collection.get(curr+k);
	}
	
	/**
	 * Retrieve current token, executing this function does not alter
	 * the current position.
	 * 
	 * @return Current token
	 */
	public WaebricToken current() {
		return collection.get(curr);
	}

	/**
	 * Remove current token and decrement current position.
	 */
	public void remove() {
		collection.remove(curr);
		curr--; // Decrement current position
	}
	
	/**
	 * Store additional token behind current position.
	 * 
	 * @param token Token
	 */
	public void add(WaebricToken token) {
		collection.add(curr+1, token);
	}
	
	/**
	 * Store collection of additional tokens behind current position.
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