package org.cwi.waebric.parser.ast.markup;

import org.cwi.waebric.parser.ast.AbstractSyntaxNode;
import org.cwi.waebric.parser.ast.ISyntaxNode;

/**
 * Markup
 * 
 * @author Jeroen van Schagen
 * @date 20-05-2009
 */
public abstract class Markup extends AbstractSyntaxNode {

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
	 * @author Jeroen van Schagen
	 * @date 20-05-2009
	 */
	public static class MarkupWithArguments extends Markup {

		protected Arguments arguments;

		public MarkupWithArguments(Arguments arguments) {
			this.arguments = arguments;
		}
		
		/**
		 * Retrieve arguments
		 * @return
		 */
		public Arguments getArguments() {
			return arguments;
		}
		
		public ISyntaxNode[] getChildren() {
			return new ISyntaxNode[] { designator, arguments };
		}
		
	}
	
	/**
	 * Designator -> Argument
	 * @author Jeroen van Schagen
	 * @date 22-05-2009
	 */
	public static class MarkupWithoutArguments extends Markup {

		public ISyntaxNode[] getChildren() {
			return new ISyntaxNode[] { designator };
		}
		
	}

}