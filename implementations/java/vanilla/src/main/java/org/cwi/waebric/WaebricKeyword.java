package org.cwi.waebric;

/**
 * Waebric literal constants
 * 
 * @author Jeroen van Schagen
 * @date 18-05-2009
 */
public enum WaebricKeyword {
	
	// Statement related keywords
	IF, ELSE, EACH, LET, IN, COMMENT, ECHO, CDATA, YIELD,
	
	// Module related keywords
	MODULE, IMPORT, DEF, END, SITE,
	
	// Type related keywords
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