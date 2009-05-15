package org.cwi.waebric.lexer;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

/**
 * Convert input stream in tokens.
 * 
 * @author Jeroen van Schagen
 * @date 14-05-2009 10:25AM
 */
public class WaebricLexer {
	
	public WaebricToken[] tokenizeStream(String data) throws IOException {
		WaebricTokenizer tokenizer = new WaebricTokenizer();
		return tokenizer.tokenizeInput(data, 0);
	}
	
	/**
	 * Translate input stream into a string, using UTF-8 encoding.
	 * 
	 * @param stream
	 * @return input
	 * @throws IOException
	 */
	public String parseStream(InputStream is) throws IOException {
		Reader in = new InputStreamReader(is, "UTF-8");
		StringBuilder out = new StringBuilder();
		
		int read;
		do {
			final char[] buffer = new char[0x10000];
			read = in.read(buffer, 0, buffer.length);
			if (read > 0) { out.append(buffer, 0, read); } 
		} while (read >= 0);

		return out.toString();
	}
	
}