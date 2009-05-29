package org.cwi.waebric.scanner;

import static org.junit.Assert.assertTrue;

import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import org.cwi.waebric.scanner.exception.ScannerException;
import org.cwi.waebric.scanner.token.Token;
import org.cwi.waebric.scanner.token.TokenSort;
import org.junit.Test;

public class TestTokenizer {

	private ArrayList<Token> tokens = new ArrayList<Token>();
	
	@Test
	public void testTokenizer() throws IOException {
		FileReader reader = new FileReader("src/test/waebric/9000lines.wae");
		ArrayList<ScannerException> exceptions = new ArrayList<ScannerException>();
		WaebricTokenizer tokenizer = new WaebricTokenizer(reader, exceptions);
		
		TokenSort sort = tokenizer.nextToken();
		do {
			if(sort == TokenSort.IDCON) {
				tokens.add(new Token(tokenizer.getStringValue(), TokenSort.IDCON, tokenizer.getLineNumber()));
			} else if(sort == TokenSort.STRCON) {
				tokens.add(new Token(tokenizer.getStringValue(), TokenSort.STRCON, tokenizer.getLineNumber()));
			} else if(sort == TokenSort.SYMBOLCON) {
				tokens.add(new Token(tokenizer.getStringValue(), TokenSort.SYMBOLCON, tokenizer.getLineNumber()));
			} else if(sort == TokenSort.SYMBOLCHAR) {
				tokens.add(new Token(tokenizer.getCharacterValue(), TokenSort.SYMBOLCHAR, tokenizer.getLineNumber()));
			} else if(sort == TokenSort.NATCON) {
				tokens.add(new Token(tokenizer.getIntegerValue(), TokenSort.NATCON, tokenizer.getLineNumber()));
			}
			
			sort = tokenizer.nextToken();
		} while(sort != TokenSort.EOF);
		
		assertTrue(tokens.size() != 0);
	}
	
}
