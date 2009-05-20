package org.cwi.waebric.scanner.token;

import java.util.Iterator;
import java.util.List;

/**
 * Token iterator which allows k lookahead, besides regular iterator
 * functionality.
 * 
 * TODO: Determine which way of storing collections is most efficient.
 * 
 * @author Jeroen van Schagen
 * @date 19-05-2009
 */
public class TokenIterator implements Iterator<Token> {

	private List<Token> collection;
	private int curr = -1;
	
	public TokenIterator(List<Token> collection) {
		this.collection = collection;
	}
	
	public boolean hasNext() {
		return hasNext(1);
	}
	
	public boolean hasNext(int k) {
		return curr+k >= 0 && curr+k < collection.size();
	}

	public Token next() {
		curr++;
		return collection.get(curr);
	}
	
	public Token peek(int k) {
		if(!hasNext(k)) { return null; }
		return collection.get(curr+k);
	}

	public void remove() {
		collection.remove(curr);
		curr--;
	}

}