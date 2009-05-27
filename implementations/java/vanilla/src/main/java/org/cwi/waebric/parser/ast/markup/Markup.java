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
	 * Designator Arguments -> Markup
	 * 
	 * @author schagen
	 *
	 */
	public static class MarkupWithArguments extends Markup {

		protected Arguments arguments;

		public MarkupWithArguments() {
			arguments = new Arguments();
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
		
		public ISyntaxNode[] getChildren() {
			return new ISyntaxNode[] { designator, arguments };
		}
		
	}
	
	/**
	 * Designator -> Argument
	 * @author schagen
	 *
	 */
	public static class MarkupWithoutArguments extends Markup {

		public ISyntaxNode[] getChildren() {
			return new ISyntaxNode[] { designator };
		}
		
	}

}