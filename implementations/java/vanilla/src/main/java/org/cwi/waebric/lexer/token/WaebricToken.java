package org.cwi.waebric.lexer.token;

public class WaebricToken {

	private WaebricTokenIdentifier token;
	private String data;
	
	private int beginLine = -1;
	private int endLine = -1;
	private int beginCharacter = -1;
	private int endCharacter = -1;
	
	public WaebricToken(WaebricTokenIdentifier token, String data) {
		this.token = token;
		this.data = data;
	}
	
	public WaebricToken(WaebricTokenIdentifier token, String data, int beginCharacter, int endCharacter) {
		this(token, data);
		this.beginCharacter = beginCharacter;
		this.endCharacter = endCharacter;
	}

	public WaebricTokenIdentifier getToken() {
		return token;
	}

	public void setToken(WaebricTokenIdentifier token) {
		this.token = token;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}
	
	public int getBeginLine() {
		return beginLine;
	}

	public void setBeginLine(int beginLine) {
		this.beginLine = beginLine;
	}

	public int getEndLine() {
		return endLine;
	}

	public void setEndLine(int endLine) {
		this.endLine = endLine;
	}

	public int getBeginCharacter() {
		return beginCharacter;
	}

	public void setBeginCharacter(int beginCharacter) {
		this.beginCharacter = beginCharacter;
	}

	public int getEndCharacter() {
		return endCharacter;
	}

	public void setEndCharacter(int endCharacter) {
		this.endCharacter = endCharacter;
	}

	@Override
	public String toString() {
		return "'" + data + "' = " + token.name();
	}
	
}