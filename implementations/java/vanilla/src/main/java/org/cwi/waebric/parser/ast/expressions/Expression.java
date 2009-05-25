package org.cwi.waebric.parser.ast.expressions;

import org.cwi.waebric.WaebricSymbol;
import org.cwi.waebric.parser.ast.ISyntaxNode;
import org.cwi.waebric.parser.ast.IntegerLiteral;
import org.cwi.waebric.parser.ast.StringLiteral;
import org.cwi.waebric.parser.ast.SyntaxNodeList.SyntaxNodeListWithSeparator;
import org.cwi.waebric.parser.ast.basic.IdCon;

public abstract class Expression implements ISyntaxNode {

	/**
	 * text -> expression
	 * @author schagen
	 *
	 */
	public static class TextExpression extends Expression {

		private StringLiteral text;

		public StringLiteral getText() {
			return text;
		}

		public void setText(StringLiteral text) {
			this.text = text;
		}

		public ISyntaxNode[] getChildren() {
			return new ISyntaxNode[] { text };
		}
		
	}
	
	/**
	 * var -> expression
	 * @author schagen
	 *
	 */
	public static class VarExpression extends Expression {

		private Var var;
		
		public Var getVar() {
			return var;
		}

		public void setVar(Var var) {
			this.var = var;
		}

		public ISyntaxNode[] getChildren() {
			return new ISyntaxNode[] { var };
		}
		
	}
	
	/**
	 * symbolcon -> expression
	 * @author schagen
	 *
	 */
	public static class SymbolExpression extends Expression {

		private StringLiteral symbol;
		
		public StringLiteral getSymbol() {
			return symbol;
		}

		public void setSymbol(StringLiteral symbol) {
			this.symbol = symbol;
		}

		public ISyntaxNode[] getChildren() {
			return new ISyntaxNode[] { symbol };
		}
		
	}
	
	/**
	 * natcon -> expression
	 * @author schagen
	 *
	 */
	public static class NatExpression extends Expression {

		private IntegerLiteral natural;
		
		public IntegerLiteral getNatural() {
			return natural;
		}

		public void setNatural(IntegerLiteral natural) {
			this.natural = natural;
		}

		public ISyntaxNode[] getChildren() {
			return new ISyntaxNode[] { natural };
		}
		
	}
	
	/**
	 * expression "." idcon -> expression
	 * @author schagen
	 *
	 */
	public static class ExpressionWithIdCon extends Expression {

		private Expression expression;
		private IdCon identifier;
		
		public Expression getExpression() {
			return expression;
		}

		public void setExpression(Expression expression) {
			this.expression = expression;
		}

		public IdCon getIdentifier() {
			return identifier;
		}

		public void setIdentifier(IdCon identifier) {
			this.identifier = identifier;
		}

		public ISyntaxNode[] getChildren() {
			return new ISyntaxNode[] {
					expression,
					new StringLiteral("" + WaebricSymbol.PERIOD),
					identifier
			};
		}
		
	}
	
	public static class BracedExpressions extends Expression {

		private SyntaxNodeListWithSeparator<Expression> expressions;
		
		public BracedExpressions() {
			expressions = new SyntaxNodeListWithSeparator<Expression>("" + WaebricSymbol.COMMA);
		}
		
		public boolean addExpression(Expression expression) {
			return expressions.add(expression);
		}
		
		public ISyntaxNode[] getChildren() {
			final ISyntaxNode[] elements = expressions.getElements();
			
			ISyntaxNode[] children = new ISyntaxNode[elements.length + 2];
			children[0] = new StringLiteral("" + WaebricSymbol.LBRACKET);
			children[children.length-1] = new StringLiteral("" + WaebricSymbol.RBRACKET);
			
			for(int i = 1; i < elements.length; i++) {
				children[i] = elements[i-1]; // Transfer expressions to children array
			}
			
			return children;
		}
		
	}
	
	public static class BracedKeyValuePairs extends Expression {

		public ISyntaxNode[] getChildren() {
			// TODO Auto-generated method stub
			return null;
		}
		
	}

}