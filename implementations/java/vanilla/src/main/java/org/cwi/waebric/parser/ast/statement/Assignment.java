package org.cwi.waebric.parser.ast.statement;

import java.util.List;

import org.cwi.waebric.parser.ast.SyntaxNode;
import org.cwi.waebric.parser.ast.SyntaxNodeList;
import org.cwi.waebric.parser.ast.INodeVisitor;
import org.cwi.waebric.parser.ast.basic.IdCon;
import org.cwi.waebric.parser.ast.expression.Expression;

/**
 * Assignments
 * 
 * @author Jeroen van Schagen
 * @date 27-05-2009
 */
public abstract class Assignment extends SyntaxNode {

	/**
	 * IdCon "(" {IdCon ","}* ")"  "=" Statement -> Assignment
	 * @author Jeroen van Schagen
	 * @date 26-05-2009
	 */
	public static class FuncBind extends Assignment {
	
		private IdCon identifier;
		private SyntaxNodeList<IdCon> variables;
		private Statement statement;
		
		/**
		 * Construct default function binding.
		 */
		public FuncBind() {
			variables = new SyntaxNodeList<IdCon>();
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
	
		public SyntaxNode[] getChildren() {
			return new SyntaxNode[] { identifier, variables, statement };
		}
		
			@Override
	public <T> T accept(INodeVisitor<T> visitor) {
		return visitor.visit(this);
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
		
		public SyntaxNode[] getChildren() {
			return new SyntaxNode[] { identifier, expression };
		}
		
			@Override
	public <T> T accept(INodeVisitor<T> visitor) {
		return visitor.visit(this);
	}
		
	}

}