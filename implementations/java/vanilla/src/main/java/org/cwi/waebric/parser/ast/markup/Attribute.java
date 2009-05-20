package org.cwi.waebric.parser.ast.markup;

import org.cwi.waebric.WaebricSymbol;
import org.cwi.waebric.parser.ast.ISyntaxNode;
import org.cwi.waebric.parser.ast.StringLiteral;
import org.cwi.waebric.parser.ast.basic.IdCon;
import org.cwi.waebric.parser.ast.basic.NatCon;

public abstract class Attribute implements ISyntaxNode {
	
	protected StringLiteral symbol;
	
	public StringLiteral getSymbol() {
		return symbol;
	}

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
		
		public ISyntaxNode[] getChildren() {
			return new ISyntaxNode[] { symbol, identifier };
		}
		
	}
	
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
		
		public ISyntaxNode[] getChildren() {
			return new ISyntaxNode[] { symbol, number };
		}
		
	}
	
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
		public ISyntaxNode[] getChildren() {
			return new ISyntaxNode[] { symbol, number, secondSymbol, secondNumber };
		}
		
	}
	
}