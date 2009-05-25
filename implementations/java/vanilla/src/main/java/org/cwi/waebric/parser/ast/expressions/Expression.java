package org.cwi.waebric.parser.ast.expressions;

import org.cwi.waebric.WaebricSymbol;
import org.cwi.waebric.parser.ast.ISyntaxNode;
import org.cwi.waebric.parser.ast.IntegerLiteral;
import org.cwi.waebric.parser.ast.StringLiteral;
import org.cwi.waebric.parser.ast.SyntaxNodeList.SyntaxNodeListWithSeparator;
import org.cwi.waebric.parser.ast.basic.IdCon;

public abstract class Expression implements ISyntaxNode {

	/**
	 * Text -> Expression
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
	 * Var -> Expression
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
	 * Symbolcon -> Expression
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
	 * NatCon -> Expression
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
	 * Expression "." IdCon -> Expression
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
	
	/**
	 * "[" { Expression "," }* "]" -> Expression
	 * @author schagen
	 *
	 */
	public static class BracedExpressions extends Expression {

		private SyntaxNodeListWithSeparator<Expression> expressions;
		
		public BracedExpressions() {
			expressions = new SyntaxNodeListWithSeparator<Expression>("" + WaebricSymbol.COMMA);
		}
		
		public boolean addExpression(Expression expression) {
			return expressions.add(expression);
		}
		
		public ISyntaxNode[] getElements() {
			return expressions.getElements();
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
	
	/**
	 * "{" { KeyValuePair "," }* "}" -> Expression
	 * @author schagen
	 *
	 */
	public static class BracedKeyValuePairs extends Expression {

		private SyntaxNodeListWithSeparator<KeyValuePair> keyvaluepairs;
		
		public BracedKeyValuePairs() {
			keyvaluepairs = new SyntaxNodeListWithSeparator<KeyValuePair>("" + WaebricSymbol.COMMA);
		}
		
		public boolean addKeyValuePair(KeyValuePair keyvaluepair) {
			return keyvaluepairs.add(keyvaluepair);
		}
		
		public ISyntaxNode[] getElements() {
			return keyvaluepairs.getElements();
		}
		
		public ISyntaxNode[] getChildren() {
			final ISyntaxNode[] elements = keyvaluepairs.getElements();
			
			ISyntaxNode[] children = new ISyntaxNode[elements.length + 2];
			children[0] = new StringLiteral("" + WaebricSymbol.LCBRACKET);
			children[children.length-1] = new StringLiteral("" + WaebricSymbol.RCBRACKET);
			
			for(int i = 1; i < elements.length; i++) {
				children[i] = elements[i-1]; // Transfer expressions to children array
			}
			
			return children;
		}
		
	}

}