package org.cwi.waebric.parser.ast.markup;

import org.cwi.waebric.parser.ast.SyntaxNode;
import org.cwi.waebric.parser.ast.INodeVisitor;
import org.cwi.waebric.parser.ast.basic.IdCon;
import org.cwi.waebric.parser.ast.basic.NatCon;

/**
 * Waebric provides shorthand notation for common XHTML attributes.<br><br>
 * 
 * Meaning of attribute shorthands:<br>
 * <code>
 * 	div.x == div(class=x)<br>
 * 	div#x == div(id=x)<br>
 * 	input$x == input(name=x)<br>
 * 	input:x == input(type=x)<br>
 * 	img@w%h == img(width=w,height=h)
 * </code>
 * 
 * @author Jeroen van Schagen
 * @date 25-05-2009
 */
public abstract class Attribute extends SyntaxNode {

	/**
	 * Grammar:<br>
	 * <code>
	 *  "." IdCon -> Attribute<br>
	 * </code>
	 */
	public static class ClassAttribute extends Attribute {
		
		private IdCon identifier;
		
		public ClassAttribute() { }
		
		public ClassAttribute(IdCon identifier) {
			this.identifier = identifier;
		}
		
		public IdCon getIdentifier() {
			return identifier;
		}
		
		public void setIdentifier(IdCon identifier) {
			this.identifier = identifier;
		}

			@Override
	public <T> T accept(INodeVisitor<T> visitor) {
		return visitor.visit(this);
	}

		@Override
		public SyntaxNode[] getChildren() {
			return new SyntaxNode[] { identifier };
		}

	}
	
	/**
	 * Grammar:<br>
	 * <code>
	 *  "#" IdCon -> Attribute<br>
	 * </code>
	 */
	public static class IdAttribute extends Attribute {
		
		private IdCon identifier;
		
		public IdAttribute() { }
		
		public IdAttribute(IdCon identifier) {
			this.identifier = identifier;
		}
		
		public IdCon getIdentifier() {
			return identifier;
		}
		
		public void setIdentifier(IdCon identifier) {
			this.identifier = identifier;
		}

			@Override
	public <T> T accept(INodeVisitor<T> visitor) {
		return visitor.visit(this);
	}

		@Override
		public SyntaxNode[] getChildren() {
			return new SyntaxNode[] { identifier };
		}
		
	}
	
	/**
	 * Grammar:<br>
	 * <code>
	 *  "$" IdCon -> Attribute<br>
	 * </code>
	 */
	public static class NameAttribute extends Attribute {
		
		private IdCon identifier;
		
		public NameAttribute() { }
		
		public NameAttribute(IdCon identifier) {
			this.identifier = identifier;
		}
		
		public IdCon getIdentifier() {
			return identifier;
		}
		
		public void setIdentifier(IdCon identifier) {
			this.identifier = identifier;
		}

			@Override
	public <T> T accept(INodeVisitor<T> visitor) {
		return visitor.visit(this);
	}

		@Override
		public SyntaxNode[] getChildren() {
			return new SyntaxNode[] { identifier };
		}
		
	}
	
	/**
	 * Grammar:<br>
	 * <code>
	 *  ":" IdCon -> Attribute<br>
	 * </code>
	 */
	public static class TypeAttribute extends Attribute {
		
		private IdCon identifier;
		
		public TypeAttribute() { }
		
		public TypeAttribute(IdCon identifier) {
			this.identifier = identifier;
		}
		
		public IdCon getIdentifier() {
			return identifier;
		}
		
		public void setIdentifier(IdCon identifier) {
			this.identifier = identifier;
		}

			@Override
	public <T> T accept(INodeVisitor<T> visitor) {
		return visitor.visit(this);
	}

		@Override
		public SyntaxNode[] getChildren() {
			return new SyntaxNode[] { identifier };
		}
		
	}
	
	/**
	 * Grammar:<br>
	 * <code>
	 *  "@" NatCon -> Attribute<br>
	 * </code>
	 */
	public static class WidthAttribute extends Attribute {
		
		private NatCon width;
		
		public WidthAttribute() { }
		
		public WidthAttribute(NatCon width) {
			this.width = width;
		}
		
		public NatCon getWidth() {
			return width;
		}
		
		public void setWidth(NatCon width) {
			this.width = width;
		}

			@Override
	public <T> T accept(INodeVisitor<T> visitor) {
		return visitor.visit(this);
	}

		@Override
		public SyntaxNode[] getChildren() {
			return new SyntaxNode[] { width	};
		}
		
	}
	
	/**
	 * Grammar:<br>
	 * <code>
	 *  "@" NatCon "%" NatCon -> Attribute<br>
	 * </code>
	 */
	public static class WidthHeightAttribute extends Attribute {
		
		private NatCon width;
		private NatCon height;
		
		public WidthHeightAttribute() { }
		
		public WidthHeightAttribute(NatCon width, NatCon height) {
			this.width = width;
			this.height = height;
		}
		
		public NatCon getWidth() {
			return width;
		}
		
		public void setWidth(NatCon width) {
			this.width = width;
		}
		
		public NatCon getHeight() {
			return height;
		}
		
		public void setHeight(NatCon height) {
			this.height = height;
		}

			@Override
	public <T> T accept(INodeVisitor<T> visitor) {
		return visitor.visit(this);
	}

		@Override
		public SyntaxNode[] getChildren() {
			return new SyntaxNode[] { width, height	};
		}
		
	}
	
}