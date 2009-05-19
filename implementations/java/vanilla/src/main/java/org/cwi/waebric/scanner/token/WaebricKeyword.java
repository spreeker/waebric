package org.cwi.waebric.scanner.token;

/**
 * Waebric literal constants
 * 
 * @author Jeroen van Schagen
 * @date 18-05-2009
 */
public enum WaebricKeyword {
	
	// Statement
	IF, COMMENT, ECHO, CDATA, EACH, LET, YIELD,
	
	// Module
	MODULE, IMPORT, DEF, END, SITE,
	
	// Predicates
	LIST, RECORD, STRING;
	
}