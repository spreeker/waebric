package org.cwi.waebric.parser.ast.markup;

import org.cwi.waebric.parser.ast.AbstractSyntaxNode;
import org.cwi.waebric.parser.ast.INodeVisitor;

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

		protected Arguments arguments = new Arguments();

		public Call(Designator designator) {
			this.designator = designator;
		}
		
		public Call(Arguments arguments) {
			this.arguments.addAll(arguments);
		}
		
		public Call(Designator designator, Arguments arguments) {
			this.designator = designator;
			this.arguments.addAll(arguments);
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
		
		@Override
		public void accept(INodeVisitor visitor) {
			visitor.visit(this);
		}
		
	}
	
	/**
	 * Designator -> Argument
	 * @author Jeroen van Schagen
	 * @date 20-05-2009
	 */
	public static class Tag extends Markup {

		public Tag(Designator designator) {
			this.designator = designator;
		}
		
		public AbstractSyntaxNode[] getChildren() {
			return new AbstractSyntaxNode[] { designator };
		}
		
		@Override
		public void accept(INodeVisitor visitor) {
			visitor.visit(this);
		}
		
	}

}