package org.cwi.waebric.scanner.token;

public class Token {

	private Object data;
	private TokenSort sort;
	private int line;
	
	public Token(Object data, TokenSort sort, int line) {
		this.data = data;
		this.sort = sort;
		this.line = line;
	}

	public Object getData() {
		return data;
	}

	public void setData(Object data) {
		this.data = data;
	}

	public TokenSort getSort() {
		return sort;
	}

	public void setSort(TokenSort sort) {
		this.sort = sort;
	}

	public int getLine() {
		return line;
	}

	public void setLine(int line) {
		this.line = line;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Token) {
			Token token = (Token) obj;
			if(token.getSort() != this.sort) { return false; }
			if(token.getLine() != this.line) { return false; }
			return token.getData().equals(this.data);
		}
		
		return false;
	}
	
}
