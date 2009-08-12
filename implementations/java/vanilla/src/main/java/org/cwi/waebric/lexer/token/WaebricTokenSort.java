package org.cwi.waebric.lexer.token;

/**
 * Sorts of available tokens
 * 
 * @author Jeroen van Schagen
 * @date 18-05-2009
 */
public enum WaebricTokenSort {
	
	/**
	 * Identifier tokens are non-keyword words in a structured text.<br><br>
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
	 * Symbol tokens contain of a single quote followed by characters.<br><br>
	 * 
	 * For example, in the below text:<br>
	 * 	<code>'@bc</code><br>
	 * @bc is the symbol
	 */
	SYMBOLCON,
	
	/**
	 * Text token contains characters between two double quotes.<br><br>
	 * 
	 * For example:<br>
	 * 	<code>"This is a text"</code>
	 */
	TEXT,
	
	/**
	 * Text token contains characters between two double quotes, 
	 * always placed after a comment keyword.<br><br>
	 * 
	 * For example:<br>
	 * 	<code>comment "This is a text"</code>
	 */
	STRING,
	
	/**
	 * Embedding tokens are an extension to text tokens, which include mark-up and
	 * sometimes an expression inside their structure.<br><br>
	 * 
	 * For example:<br>
	 * 	<code>"Text < call(args) tag "expr" > Text"</code>
	 */
	EMBEDDING,
	
	/**
	 * Character tokens represent a single character from the ASCII language.
	 */
	CHARACTER,
	
	/**
	 * Keywords are words that are literally part of a language's grammar.<br><br>
	 * 
	 * For example, in the below text:<br>
	 * 	<code>module helloworld</code><br>
	 * module is the keyword.
	 */
	KEYWORD;
	
}