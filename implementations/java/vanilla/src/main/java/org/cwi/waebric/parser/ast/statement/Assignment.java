package org.cwi.waebric.parser.ast.statement;

import java.util.List;

import org.cwi.waebric.WaebricSymbol;
import org.cwi.waebric.parser.ast.AbstractSyntaxNode;
import org.cwi.waebric.parser.ast.INodeVisitor;
import org.cwi.waebric.parser.ast.SeparatedNodeList;
import org.cwi.waebric.parser.ast.basic.IdCon;
import org.cwi.waebric.parser.ast.expression.Expression;
import org.cwi.waebric.parser.ast.token.CharacterLiteral;

/**
 * Assignments
 * 
 * @author Jeroen van Schagen
 * @date 27-05-2009
 */
public abstract class Assignment extends AbstractSyntaxNode {

	/**
	 * IdCon "(" {IdCon ","}* ")"  "=" Statement -> Assignment
	 * @author Jeroen van Schagen
	 * @date 26-05-2009
	 */
	public static class FuncBind extends Assignment {
	
		private IdCon identifier;
		private SeparatedNodeList<IdCon> variables;
		private Statement statement;
		
		/**
		 * Construct default function binding.
		 */
		public FuncBind() {
			variables = new SeparatedNodeList<IdCon>(',');
		}
		
		public IdCon getIdentifier() {
			return identifier;
		}
	
		public void setIdentifier(IdCon identifier) {
			this.identifier = identifier;
		}
	
		public boolean addVariable(IdCon variable) {
			return variables.add(variable);
		}
	
		public List<IdCon> getVariables() {
			return variables.clone();
		}
	
		public Statement getStatement() {
			return statement;
		}
	
		public void setStatement(Statement statement) {
			this.statement = statement;
		}
	
		public AbstractSyntaxNode[] getChildren() {
			return new AbstractSyntaxNode[] {
				identifier,
				new CharacterLiteral(WaebricSymbol.LPARANTHESIS),
				variables,
				new CharacterLiteral(WaebricSymbol.RPARANTHESIS),
				new CharacterLiteral(WaebricSymbol.EQUAL_SIGN),
				statement
			};
		}
		
		@Override
		public void accept(INodeVisitor visitor, Object[] args) {
			visitor.visit(this, args);
		}
	
	}
	
	/**
	 * IdCon "=" Expression -> Assignment
	 * @author Jeroen van Schagen
	 * @date 26-05-2009
	 */
	public static class VarBind extends Assignment {
		
		private IdCon identifier;
		private Expression expression;
		
		public IdCon getIdentifier() {
			return identifier;
		}
		
		public void setIdentifier(IdCon identifier) {
			this.identifier = identifier;
		}
		
		public Expression getExpression() {
			return expression;
		}
		
		public void setExpression(Expression expression) {
			this.expression = expression;
		}
		
		public AbstractSyntaxNode[] getChildren() {
			return new AbstractSyntaxNode[] {
				identifier,
				new CharacterLiteral('='),
				expression
			};
		}
		
		@Override
		public void accept(INodeVisitor visitor, Object[] args) {
			visitor.visit(this, args);
		}
		
	}

}