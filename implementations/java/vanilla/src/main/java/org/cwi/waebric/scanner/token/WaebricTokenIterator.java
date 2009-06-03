package org.cwi.waebric.scanner.token;

import java.util.ArrayList;
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
	 * Clone iterator
	 */
	public WaebricTokenIterator clone() {
		return new WaebricTokenIterator(new ArrayList<WaebricToken>(collection));
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
	 * Retrieve next token and increment current position.
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
	 * @return
	 */
	public WaebricToken peek(int k) {
		if(! hasNext(k)) { return null; }
		return collection.get(curr+k);
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
		return "Position: " + curr + ", Collection: " + collection.toString();
	}

}