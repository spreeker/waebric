package org.cwi.waebric.scanner.token;

import java.util.Iterator;
import java.util.List;

public class TokenIterator implements Iterator<Token> {

	private List<Token> collection;
	private int curr = -1;
	
	public TokenIterator(List<Token> collection) {
		this.collection = collection;
	}
	
	@Override
	public boolean hasNext() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Token next() {
		// TODO Auto-generated method stub
		return null;
	}
	
	public Token peek() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void remove() {
		// TODO Auto-generated method stub
		
	}

}
