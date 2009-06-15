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
	 * 	"#" IdCon -> Attribute<br>
	 *  "." IdCon -> Attribute<br>
	 *  "$" IdCOn -> Attribute
	 * </code>
	 */
	public static class AttributeIdCon extends Attribute {

		private final CharacterLiteral symbol;
		private IdCon identifier;
		
		public AttributeIdCon(char symbol) {
			this.symbol = new CharacterLiteral(symbol);
		}
		
		public IdCon getIdentifier() {
			return identifier;
		}
		
		public void setIdentifier(IdCon identifier) {
			this.identifier = identifier;
		}

		public CharacterLiteral getSymbol() {
			return symbol;
		}
		
		@Override
		public String toString() {
			return "" + symbol + identifier;
		}
		
		public AbstractSyntaxNode[] getChildren() {
			return new AbstractSyntaxNode[] { symbol, identifier };
		}
		
		@Override
		public void accept(INodeVisitor visitor) {
			visitor.visit(this);
		}
		
	}
	
	/**
	 * Grammar:<br>
	 * <code>
	 * 	"@" w:NatCon -> Attribute
	 * </code>
	 */
	public static class AttributeNatCon extends Attribute {

		protected NatCon number;
		
		public NatCon getNumber() {
			return number;
		}
		
		public void setNumber(NatCon number) {
			this.number = number;
		}
		
		@Override
		public String toString() {
			return "@" + number;
		}
		
		public AbstractSyntaxNode[] getChildren() {
			return new AbstractSyntaxNode[] { 
					new CharacterLiteral(WaebricSymbol.AT_SIGN), 
					number
				};
		}
		
		@Override
		public void accept(INodeVisitor visitor) {
			visitor.visit(this);
		}
		
	}
	
	/**
	 * Grammar:<br>
	 * <code>
	 * 	"@" w:NatCon "%" h:NatCon -> Attribute
	 * </code>
	 */
	public static class AttributeDoubleNatCon extends AttributeNatCon {

		private NatCon secondNumber;
		
		public NatCon getSecondNumber() {
			return secondNumber;
		}
		
		public void setSecondNumber(NatCon secondNumber) {
			this.secondNumber = secondNumber;
		}
		
		@Override
		public String toString() {
			return "@" + number + '%' + secondNumber;
		}
		
		@Override
		public AbstractSyntaxNode[] getChildren() {
			return new AbstractSyntaxNode[] { 
					new CharacterLiteral(WaebricSymbol.AT_SIGN), number, 
					new CharacterLiteral(WaebricSymbol.PERCENT_SIGN), secondNumber 
				};
		}
		
		@Override
		public void accept(INodeVisitor visitor) {
			visitor.visit(this);
		}
		
	}
	
}