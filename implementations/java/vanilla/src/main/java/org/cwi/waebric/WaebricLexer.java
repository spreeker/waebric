package org.cwi.waebric;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.StringTokenizer;

/**
 * Convert input stream in tokens.
 * 
 * @author Jeroen van Schagen
 * @date 14-05-2009 10:25AM
 */
public class WaebricLexer {

	public Object[] tokenizeStream(InputStream is) throws IOException {
		String input = parseStream(is);
		String[] words = separateWords(input);
		
		// TODO: Convert words to tokens
		return new Object[0];
	}
	
	/**
	 * Separate string into words.
	 * 
	 * @see java.util.StringTokenizer
	 * @param input
	 * @return words
	 */
	public String[] separateWords(String input) {
		StringTokenizer separator = new StringTokenizer(input);
		String[] tokens =  new String[separator.countTokens()];
		for(int i = 0; separator.hasMoreElements(); i++) {
			tokens[i] = separator.nextToken();
		}
		
		return tokens;
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
			if (read>0) { out.append(buffer, 0, read); } 
		} while (read>=0);

		return out.toString();
	}
	
}