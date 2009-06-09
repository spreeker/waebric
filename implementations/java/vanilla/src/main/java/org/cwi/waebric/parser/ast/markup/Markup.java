package org.cwi.waebric.parser.ast.markup;

import org.cwi.waebric.parser.ast.AbstractSyntaxNode;

/**
 * Markup
 * @author Jeroen van Schagen
 * @date 22-05-2009
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
	 * @author Jeroen van Schagen
	 * @date 20-05-2009
	 */
	public static class Call extends Markup {

		protected Arguments arguments;

		public Call(Arguments arguments) {
			this.arguments = arguments;
		}
		
		/**
		 * Retrieve arguments
		 * @return
		 */
		public Arguments getArguments() {
			return arguments;
		}
		
		public AbstractSyntaxNode[] getChildren() {
			return new AbstractSyntaxNode[] { designator, arguments };
		}
		
	}
	
	/**
	 * Designator -> Argument
	 * @author Jeroen van Schagen
	 * @date 20-05-2009
	 */
	public static class Regular extends Markup {

		public AbstractSyntaxNode[] getChildren() {
			return new AbstractSyntaxNode[] { designator };
		}
		
	}

}