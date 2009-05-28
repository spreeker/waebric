package org.cwi.waebric.parser.ast.expressions;

import org.cwi.waebric.WaebricSymbol;
import org.cwi.waebric.parser.ast.CharacterLiteral;
import org.cwi.waebric.parser.ast.ISyntaxNode;
import org.cwi.waebric.parser.ast.StringLiteral;
import org.cwi.waebric.parser.ast.SyntaxNodeList.SyntaxNodeListWithSeparator;
import org.cwi.waebric.parser.ast.basic.IdCon;
import org.cwi.waebric.parser.ast.basic.NatCon;

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
	 * SymbolCon -> Expression
	 * @author schagen
	 *
	 */
	public static class SymbolExpression extends Expression {

		private SymbolCon symbol;
		
		public SymbolCon getSymbol() {
			return symbol;
		}

		public void setSymbol(SymbolCon symbol) {
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

		private NatCon natural;
		
		public NatCon getNatural() {
			return natural;
		}

		public void setNatural(NatCon natural) {
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
	public static class IdConExpression extends Expression {

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
				new CharacterLiteral(WaebricSymbol.PERIOD),
				identifier
			};
		}
		
	}
	
	/**
	 * "[" { Expression "," }* "]" -> Expression
	 * @author schagen
	 *
	 */
	public static class ListExpression extends Expression {

		private SyntaxNodeListWithSeparator<Expression> expressions;
		
		public ListExpression() {
			expressions = new SyntaxNodeListWithSeparator<Expression>(WaebricSymbol.COMMA);
		}
		
		public boolean addExpression(Expression expression) {
			return expressions.add(expression);
		}
		
		public ISyntaxNode[] getElements() {
			return expressions.getElements();
		}
		
		public ISyntaxNode[] getChildren() {
			return new ISyntaxNode[] { 
				new CharacterLiteral(WaebricSymbol.LBRACKET),
				expressions,
				new CharacterLiteral(WaebricSymbol.RBRACKET)
			};
		}
		
	}
	
	/**
	 * "{" { KeyValuePair "," }* "}" -> Expression
	 * @author schagen
	 *
	 */
	public static class RecordExpression extends Expression {

		private SyntaxNodeListWithSeparator<KeyValuePair> pairs;
		
		public RecordExpression() {
			pairs = new SyntaxNodeListWithSeparator<KeyValuePair>(WaebricSymbol.COMMA);
		}
		
		public boolean addKeyValuePair(KeyValuePair pair) {
			return pairs.add(pair);
		}
		
		public ISyntaxNode[] getElements() {
			return pairs.getElements();
		}
		
		public ISyntaxNode[] getChildren() {
			return new ISyntaxNode[] { 
					new CharacterLiteral(WaebricSymbol.LCBRACKET),
					pairs,
					new CharacterLiteral(WaebricSymbol.RCBRACKET)
				};
		}
		
	}

}