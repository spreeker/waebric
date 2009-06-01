package org.cwi.waebric.parser.ast.markup;

import org.cwi.waebric.WaebricSymbol;
import org.cwi.waebric.parser.ast.ISyntaxNode;
import org.cwi.waebric.parser.ast.StringLiteral;
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
public abstract class Attribute implements ISyntaxNode {
	
	protected StringLiteral symbol;
	
	public StringLiteral getSymbol() {
		return symbol;
	}

	/**
	 * Grammar:<br>
	 * <code>
	 * 	"#" IdCon -> Attribute
	 *  "." IdCon -> Attribute
	 *  "$" IdCOn -> Attribute
	 * </code>
	 */
	public static class AttributeIdCon extends Attribute {

		private IdCon identifier;
		
		public AttributeIdCon(char symbol) {
			this.symbol = new StringLiteral("" + symbol);
		}
		
		public IdCon getIdentifier() {
			return identifier;
		}
		
		public void setIdentifier(IdCon identifier) {
			this.identifier = identifier;
		}
		
		@Override
		public String toString() {
			return "" + symbol + identifier;
		}
		
		public ISyntaxNode[] getChildren() {
			return new ISyntaxNode[] { symbol, identifier };
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
		
		public AttributeNatCon() {
			symbol = new StringLiteral("" + WaebricSymbol.AT_SIGN);
		}
		
		public NatCon getNumber() {
			return number;
		}
		
		public void setNumber(NatCon number) {
			this.number = number;
		}
		
		@Override
		public String toString() {
			return "" + symbol + number;
		}
		
		public ISyntaxNode[] getChildren() {
			return new ISyntaxNode[] { symbol, number };
		}
		
	}
	
	/**
	 * Grammar:<br>
	 * <code>
	 * 	"@" w:NatCon "%" h:NatCon -> Attribute
	 * </code>
	 */
	public static class AttributeDoubleNatCon extends AttributeNatCon {

		private final StringLiteral secondSymbol;
		private NatCon secondNumber;
		
		public AttributeDoubleNatCon() {
			super();
			secondSymbol = new StringLiteral("" + WaebricSymbol.AT_SIGN);
		}
		
		public StringLiteral getSecondSymbol() {
			return secondSymbol;
		}
		
		public NatCon getSecondNumber() {
			return secondNumber;
		}
		
		public void setSecondNumber(NatCon secondNumber) {
			this.secondNumber = secondNumber;
		}
		
		@Override
		public String toString() {
			return "" + symbol + number + secondSymbol + secondNumber;
		}
		
		@Override
		public ISyntaxNode[] getChildren() {
			return new ISyntaxNode[] { symbol, number, secondSymbol, secondNumber };
		}
		
	}
	
}