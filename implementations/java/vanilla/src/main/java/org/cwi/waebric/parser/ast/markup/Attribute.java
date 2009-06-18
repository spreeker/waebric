package org.cwi.waebric.parser.ast.markup;

import org.cwi.waebric.WaebricSymbol;
import org.cwi.waebric.parser.ast.AbstractSyntaxNode;
import org.cwi.waebric.parser.ast.INodeVisitor;
import org.cwi.waebric.parser.ast.basic.IdCon;
import org.cwi.waebric.parser.ast.basic.NatCon;
import org.cwi.waebric.parser.ast.token.CharacterLiteral;

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
public abstract class Attribute extends AbstractSyntaxNode {

	/**
	 * Grammar:<br>
	 * <code>
	 *  "." IdCon -> Attribute<br>
	 * </code>
	 */
	public static class ClassAttribute extends Attribute {
		
		private IdCon identifier;
		
		public IdCon getIdentifier() {
			return identifier;
		}
		
		public void setIdentifier(IdCon identifier) {
			this.identifier = identifier;
		}

		@Override
		public void accept(INodeVisitor visitor) {
			visitor.visit(this);
		}

		@Override
		public AbstractSyntaxNode[] getChildren() {
			return new AbstractSyntaxNode[] { 
					new CharacterLiteral(WaebricSymbol.PERIOD), 
					identifier
				};
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
		
		public IdCon getIdentifier() {
			return identifier;
		}
		
		public void setIdentifier(IdCon identifier) {
			this.identifier = identifier;
		}

		@Override
		public void accept(INodeVisitor visitor) {
			visitor.visit(this);
		}

		@Override
		public AbstractSyntaxNode[] getChildren() {
			return new AbstractSyntaxNode[] { 
					new CharacterLiteral(WaebricSymbol.NUMBER_SIGN), 
					identifier
				};
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
		
		public IdCon getIdentifier() {
			return identifier;
		}
		
		public void setIdentifier(IdCon identifier) {
			this.identifier = identifier;
		}

		@Override
		public void accept(INodeVisitor visitor) {
			visitor.visit(this);
		}

		@Override
		public AbstractSyntaxNode[] getChildren() {
			return new AbstractSyntaxNode[] { 
					new CharacterLiteral(WaebricSymbol.DOLLAR_SIGN), 
					identifier
				};
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
		
		public IdCon getIdentifier() {
			return identifier;
		}
		
		public void setIdentifier(IdCon identifier) {
			this.identifier = identifier;
		}

		@Override
		public void accept(INodeVisitor visitor) {
			visitor.visit(this);
		}

		@Override
		public AbstractSyntaxNode[] getChildren() {
			return new AbstractSyntaxNode[] { 
					new CharacterLiteral(WaebricSymbol.COLON), 
					identifier
				};
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
		
		public NatCon getWidth() {
			return width;
		}
		
		public void setWidth(NatCon width) {
			this.width = width;
		}

		@Override
		public void accept(INodeVisitor visitor) {
			visitor.visit(this);
		}

		@Override
		public AbstractSyntaxNode[] getChildren() {
			return new AbstractSyntaxNode[] { 
					new CharacterLiteral(WaebricSymbol.AT_SIGN), 
					width
				};
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
		public void accept(INodeVisitor visitor) {
			visitor.visit(this);
		}

		@Override
		public AbstractSyntaxNode[] getChildren() {
			return new AbstractSyntaxNode[] { 
					new CharacterLiteral(WaebricSymbol.AT_SIGN), 
					width,
					new CharacterLiteral(WaebricSymbol.PERCENT_SIGN),
					height
				};
		}
		
	}
	
}