package org.cwi.waebric.parser.ast.markup;

import org.cwi.waebric.parser.ast.SyntaxNode;
import org.cwi.waebric.parser.ast.INodeVisitor;

/**
 * Markup
 * @author Jeroen van Schagen
 * @date 22-05-2009
 */
public abstract class Markup extends SyntaxNode {
	
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

		public Call(Designator designator) {
			this(designator, new Arguments());
		}

		public Call(Designator designator, Arguments arguments) {
			this.designator = designator;
			this.arguments = arguments;
		}
		
		/**
		 * Retrieve arguments
		 * @return
		 */
		public Arguments getArguments() {
			return arguments;
		}
		
		public void setArguments(Arguments arguments) {
			this.arguments = arguments;
		}
		
		public SyntaxNode[] getChildren() {
			return new SyntaxNode[] { designator, arguments };
		}
		
			@Override
	public <T> T accept(INodeVisitor<T> visitor) {
		return visitor.visit(this);
	}
		
	}
	
	/**
	 * Designator -> Markup
	 * @author Jeroen van Schagen
	 * @date 20-05-2009
	 */
	public static class Tag extends Markup {

		public Tag(Designator designator) {
			this.designator = designator;
		}
		
		public SyntaxNode[] getChildren() {
			return new SyntaxNode[] { designator };
		}
		
			@Override
	public <T> T accept(INodeVisitor<T> visitor) {
		return visitor.visit(this);
	}
		
	}

}