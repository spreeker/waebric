package org.cwi.waebric.parser.ast.expression;

import org.cwi.waebric.WaebricSymbol;
import org.cwi.waebric.parser.ast.AbstractSyntaxNode;
import org.cwi.waebric.parser.ast.CharacterLiteral;
import org.cwi.waebric.parser.ast.AbstractSyntaxNodeList.AbstractSeparatedSyntaxNodeList;
import org.cwi.waebric.parser.ast.basic.IdCon;
import org.cwi.waebric.parser.ast.basic.NatCon;
import org.cwi.waebric.parser.ast.basic.SymbolCon;

/**
 * Expression
 * 
 * @author Jeroen van Schagen
 * @date 25-05-2009
 */
public abstract class Expression extends AbstractSyntaxNode {

	/**
	 * Text -> Expression
	 */
	public static class TextExpression extends Expression {

		private Text text;

		public Text getText() {
			return text;
		}

		public void setText(Text text) {
			this.text = text;
		}

		public AbstractSyntaxNode[] getChildren() {
			return new AbstractSyntaxNode[] { text };
		}
		
	}
	
	/**
	 * Var -> Expression
	 */
	public static class VarExpression extends Expression {

		private Var var;
		
		public Var getVar() {
			return var;
		}

		public void setVar(Var var) {
			this.var = var;
		}

		public AbstractSyntaxNode[] getChildren() {
			return new AbstractSyntaxNode[] { var };
		}
		
	}
	
	/**
	 * SymbolCon -> Expression
	 */
	public static class SymbolExpression extends Expression {

		private SymbolCon symbol;
		
		public SymbolCon getSymbol() {
			return symbol;
		}

		public void setSymbol(SymbolCon symbol) {
			this.symbol = symbol;
		}

		public AbstractSyntaxNode[] getChildren() {
			return new AbstractSyntaxNode[] { symbol };
		}
		
	}
	
	/**
	 * NatCon -> Expression
	 */
	public static class NatExpression extends Expression {

		private NatCon natural;
		
		public NatCon getNatural() {
			return natural;
		}

		public void setNatural(NatCon natural) {
			this.natural = natural;
		}

		public AbstractSyntaxNode[] getChildren() {
			return new AbstractSyntaxNode[] { natural };
		}
		
	}
	
	/**
	 * Expression "." IdCon -> Expression
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

		public AbstractSyntaxNode[] getChildren() {
			return new AbstractSyntaxNode[] {
				expression,
				new CharacterLiteral(WaebricSymbol.PERIOD),
				identifier
			};
		}
		
	}
	
	/**
	 * "[" { Expression "," }* "]" -> Expression
	 */
	public static class ListExpression extends Expression {

		private AbstractSeparatedSyntaxNodeList<Expression> expressions;
		
		public ListExpression() {
			expressions = new AbstractSeparatedSyntaxNodeList<Expression>(WaebricSymbol.COMMA);
		}
		
		public boolean addExpression(Expression expression) {
			return expressions.add(expression);
		}
		
		public AbstractSyntaxNode[] getElements() {
			return expressions.getElements();
		}
		
		public AbstractSyntaxNode[] getChildren() {
			return new AbstractSyntaxNode[] { 
				new CharacterLiteral(WaebricSymbol.LBRACKET),
				expressions,
				new CharacterLiteral(WaebricSymbol.RBRACKET)
			};
		}
		
	}
	
	/**
	 * "{" { KeyValuePair "," }* "}" -> Expression
	 */
	public static class RecordExpression extends Expression {

		private AbstractSeparatedSyntaxNodeList<KeyValuePair> pairs;
		
		public RecordExpression() {
			pairs = new AbstractSeparatedSyntaxNodeList<KeyValuePair>(WaebricSymbol.COMMA);
		}
		
		public boolean addKeyValuePair(KeyValuePair pair) {
			return pairs.add(pair);
		}
		
		public AbstractSyntaxNode[] getElements() {
			return pairs.getElements();
		}
		
		public AbstractSyntaxNode[] getChildren() {
			return new AbstractSyntaxNode[] { 
				new CharacterLiteral(WaebricSymbol.LCBRACKET),
				pairs,
				new CharacterLiteral(WaebricSymbol.RCBRACKET)
			};
		}
		
	}

}