package org.cwi.waebric;

/**
 * Waebric literal constants
 * 
 * @author Jeroen van Schagen
 * @date 18-05-2009
 */
public enum WaebricKeyword {
	
	// Statement
	IF, EACH, LET, IN, 
	COMMENT, ECHO, CDATA, YIELD,
	
	// Module
	MODULE, IMPORT, DEF, END, SITE,
	
	// Predicates
	LIST, RECORD, STRING;
	
	/**
	 * Retrieve keyword literal
	 * 
	 * @param keyword
	 * @return
	 */
	public static String getLiteral(WaebricKeyword keyword) {
		return keyword.name().toLowerCase();
	}
	
}