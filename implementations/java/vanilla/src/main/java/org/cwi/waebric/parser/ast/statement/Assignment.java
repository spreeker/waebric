package org.cwi.waebric.parser.ast.statement;

import java.util.List;

import org.cwi.waebric.parser.ast.AbstractSyntaxNode;
import org.cwi.waebric.parser.ast.AbstractSyntaxNodeList;
import org.cwi.waebric.parser.ast.INodeVisitor;
import org.cwi.waebric.parser.ast.basic.IdCon;
import org.cwi.waebric.parser.ast.expression.Expression;

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
		private AbstractSyntaxNodeList<IdCon> variables;
		private Statement statement;
		
		/**
		 * Construct default function binding.
		 */
		public FuncBind() {
			variables = new AbstractSyntaxNodeList<IdCon>();
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
			return new AbstractSyntaxNode[] { identifier, variables, statement };
		}
		
		@Override
		public void accept(INodeVisitor visitor) {
			visitor.visit(this);
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
			return new AbstractSyntaxNode[] { identifier, expression };
		}
		
		@Override
		public void accept(INodeVisitor visitor) {
			visitor.visit(this);
		}
		
	}

}