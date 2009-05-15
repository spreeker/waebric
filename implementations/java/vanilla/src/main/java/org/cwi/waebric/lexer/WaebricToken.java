package org.cwi.waebric.lexer;

public class WaebricToken {

	private WaebricTokenType token;
	private String data;
	private int line;
	private int chr;
	
	public WaebricToken(WaebricTokenType token, String data) {
		this.token = token;
		this.data = data;
		line = -1;
		chr = -1;
	}

	public WaebricTokenType getToken() {
		return token;
	}

	public void setToken(WaebricTokenType token) {
		this.token = token;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public int getLine() {
		return line;
	}

	public void setLine(int line) {
		this.line = line;
	}

	public int getChr() {
		return chr;
	}

	public void setChr(int chr) {
		this.chr = chr;
	}
	
	@Override
	public String toString() {
		return "'" + data + "' = " + token.name();
	}
	
}