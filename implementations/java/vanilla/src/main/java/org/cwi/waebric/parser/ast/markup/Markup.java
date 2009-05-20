package org.cwi.waebric.parser.ast.markup;

import org.cwi.waebric.parser.ast.ISyntaxNode;

/**
 * Markup
 * 
 * @author Jeroen van Schagen
 * @date 20-05-2009
 */
public abstract class Markup implements ISyntaxNode {

	protected Designator designator;
	protected Arguments arguments;
	
	public Markup() {
		arguments = new Arguments();
	}
	
	/**
	 * Retrieve designator
	 * @return
	 */
	public Designator getDesignator() {
		return designator;
	}

	/**
	 * Store designator
	 * @param designator
	 */
	public void setDesignator(Designator designator) {
		this.designator = designator;
	}

	/**
	 * Retrieve arguments
	 * @return
	 */
	public Arguments getArguments() {
		return arguments;
	}

	/**
	 * Add argument
	 * @param argument
	 * @return success
	 */
	public boolean addArgument(Argument argument) {
		return arguments.add(argument);
	}

	public static class MarkupWithArguments extends Markup {

		@Override
		public ISyntaxNode[] getChildren() {
			return new ISyntaxNode[] { arguments, designator };
		}
		
	}
	
	public static class MarkupWithoutArguments extends Markup {

		@Override
		public ISyntaxNode[] getChildren() {
			return new ISyntaxNode[] { designator };
		}
		
	}

}