package org.cwi.waebric.parser.ast.expression;

import java.util.List;

import org.cwi.waebric.parser.ast.AbstractSyntaxNode;
import org.cwi.waebric.parser.ast.AbstractSyntaxNodeList;
import org.cwi.waebric.parser.ast.INodeVisitor;
import org.cwi.waebric.parser.ast.basic.IdCon;
import org.cwi.waebric.parser.ast.basic.NatCon;
import org.cwi.waebric.parser.ast.basic.SymbolCon;
import org.cwi.waebric.parser.ast.basic.Text;

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
		
		public TextExpression(Text text) {
			this.text = text;
		}
		
		public TextExpression(String text) {
			this.text = new Text(text);
		}
		
		public Text getText() {
			return text;
		}

		public AbstractSyntaxNode[] getChildren() {
			return new AbstractSyntaxNode[] { text };
		}
		
		@Override
		public void accept(INodeVisitor visitor) {
			visitor.visit(this);
		}
		
	}
	
	/**
	 * IdCon -> Expression
	 */
	public static class VarExpression extends Expression {

		private IdCon identifier;
		
		public VarExpression(IdCon identifier) {
			this.identifier = identifier;
		}
		
		public IdCon getId() {
			return identifier;
		}

		public AbstractSyntaxNode[] getChildren() {
			return new AbstractSyntaxNode[] { identifier };
		}
		
		@Override
		public void accept(INodeVisitor visitor) {
			visitor.visit(this);
		}
		
	}
	
	/**
	 * SymbolCon -> Expression
	 */
	public static class SymbolExpression extends Expression {

		private SymbolCon symbol;
		
		public SymbolExpression() { }
		
		public SymbolExpression(SymbolCon symbol) {
			this.symbol = symbol;
		}
		
		public SymbolCon getSymbol() {
			return symbol;
		}

		public void setSymbol(SymbolCon symbol) {
			this.symbol = symbol;
		}

		public AbstractSyntaxNode[] getChildren() {
			return new AbstractSyntaxNode[] { symbol };
		}
		
		@Override
		public void accept(INodeVisitor visitor) {
			visitor.visit(this);
		}
		
	}
	
	/**
	 * NatCon -> Expression
	 */
	public static class NatExpression extends Expression {

		private NatCon natural;
		
		public NatExpression(NatCon natural) {
			this.natural = natural;
		}
		
		public NatExpression(int natural) {
			this.natural = new NatCon(natural);
		}
		
		public NatCon getNatural() {
			return natural;
		}

		public AbstractSyntaxNode[] getChildren() {
			return new AbstractSyntaxNode[] { natural };
		}
		
		@Override
		public void accept(INodeVisitor visitor) {
			visitor.visit(this);
		}
		
	}
	
	/**
	 * "[" { Expression "," }* "]" -> Expression
	 */
	public static class ListExpression extends Expression {

		private AbstractSyntaxNodeList<Expression> expressions;
		
		public ListExpression() {
			expressions = new AbstractSyntaxNodeList<Expression>();
		}
		
		public boolean addExpression(Expression expression) {
			return expressions.add(expression);
		}
		
		public List<Expression> getExpressions() {
			return expressions;
		}
		
		public AbstractSyntaxNode[] getChildren() {
			return new AbstractSyntaxNode[] { expressions };
		}
		
		@Override
		public void accept(INodeVisitor visitor) {
			visitor.visit(this);
		}
		
	}
	
	/**
	 * "{" { KeyValuePair "," }* "}" -> Expression
	 */
	public static class RecordExpression extends Expression {

		private AbstractSyntaxNodeList<KeyValuePair> pairs;
		
		public RecordExpression() {
			pairs = new AbstractSyntaxNodeList<KeyValuePair>();
		}
		
		public boolean addKeyValuePair(KeyValuePair pair) {
			return pairs.add(pair);
		}
		
		public List<KeyValuePair> getPairs() {
			return pairs;
		}
		
		public Expression getExpression(IdCon identifier) {
			for(KeyValuePair pair: pairs) {
				if(identifier.equals(pair.getIdentifier())) {
					return pair.getExpression();
				}
			}
			
			return null;
		}
		
		public AbstractSyntaxNode[] getChildren() {
			return new AbstractSyntaxNode[] { pairs	};
		}
		
		@Override
		public void accept(INodeVisitor visitor) {
			visitor.visit(this);
		}
		
	}
	
	/**
	 * Expression "+" Expression -> Expression
	 */
	public static class CatExpression extends Expression {
		
		private Expression left;
		private Expression right;
		
		public CatExpression() { }
		
		public CatExpression(Expression left, Expression right) {
			this.left = left;
			this.right = right;
		}
		
		public Expression getLeft() {
			return left;
		}
		
		public void setLeft(Expression left) {
			this.left = left;
		}
		
		public Expression getRight() {
			return right;
		}
		
		public void setRight(Expression right) {
			this.right = right;
		}
		
		public AbstractSyntaxNode[] getChildren() {
			return new AbstractSyntaxNode[] { left, right };
		}
		
		@Override
		public void accept(INodeVisitor visitor) {
			visitor.visit(this);
		}
		
	}
	
	/**
	 * Expression "." IdCon -> Expression
	 */
	public static class Field extends Expression {

		private Expression expression;
		private IdCon identifier;
		
		public Field() { }
		
		public Field(Expression expression, IdCon identifier) {
			this.expression = expression;
			this.identifier = identifier;
		}
		
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
			return new AbstractSyntaxNode[] { expression, identifier };
		}
		
		@Override
		public void accept(INodeVisitor visitor) {
			visitor.visit(this);
		}
		
	}

}