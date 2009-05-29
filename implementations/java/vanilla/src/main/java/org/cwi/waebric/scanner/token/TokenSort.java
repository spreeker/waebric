package org.cwi.waebric.scanner.token;

/**
 * Sorts of available tokens
 * 
 * @author Jeroen van Schagen
 * @date 18-05-2009
 */
public enum TokenSort {
	
	/**
	 * Identifier tokens are non-keyword words in a structured text.
	 * 
	 * For example, in the below text:<br>
	 * 	<code>module helloworld</code><br>
	 * helloworld is the identifier
	 */
	IDCON,
	
	/**
	 * Number tokens include all characters that represents natural numbers.
	 * @see java.lang.Integer
	 */
	NATCON, 
	
	/**
	 * Text tokens represent all characters stored between double quotes.<br><br>
	 * 
	 * For example:<br>
	 * "This is text"
	 */
	STRCON, 
	
	/**
	 * "'" SymbolChar
	 */
	SYMBOLCON,
	
	/**
	 * Symbol char tokens are all regular ASCII symbols between the decimal range
	 * of 32..126
	 */
	SYMBOLCHAR,
	
	/**
	 * Keyword tokens are words that are literally part of the language grammar.<br><br>
	 * 
	 * For example, in the below text:<br>
	 * 	<code>module helloworld</code><br>
	 * module is the keyword.
	 */
	KEYWORD,
	
	/**
	 * End of file
	 */
	EOF,
	
	/**
	 * Unknown token
	 */
	UNKNOWN;
	
}