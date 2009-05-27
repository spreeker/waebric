package org.cwi.waebric.parser.ast.statements;

import org.cwi.waebric.WaebricSymbol;
import org.cwi.waebric.parser.ast.CharacterLiteral;
import org.cwi.waebric.parser.ast.ISyntaxNode;
import org.cwi.waebric.parser.ast.basic.IdCon;
import org.cwi.waebric.parser.ast.expressions.Expression;
import org.cwi.waebric.parser.ast.expressions.Var;

/**
 * Assignments
 * 
 * @author Jeroen van Schagen
 * @date 27-05-2009
 */
public abstract class Assignment implements ISyntaxNode {

	/**
	 * IdCon Formals "=" Statement -> Assignment
	 * @author schagen
	 *
	 */
	public static class IdConAssignment extends Assignment {
	
		private IdCon identifier;
		private Formals formals;
		private Statement statement;
		
		public IdCon getIdentifier() {
			return identifier;
		}
	
		public void setIdentifier(IdCon identifier) {
			this.identifier = identifier;
		}
	
		public Formals getFormals() {
			return formals;
		}
	
		public void setFormals(Formals formals) {
			this.formals = formals;
		}
	
		public Statement getStatement() {
			return statement;
		}
	
		public void setStatement(Statement statement) {
			this.statement = statement;
		}
	
		public ISyntaxNode[] getChildren() {
			return new ISyntaxNode[] {
				identifier,
				formals,
				new CharacterLiteral(WaebricSymbol.EQUAL_SIGN),
				statement
			};
		}
	
	}
	
	/**
	 * Var "=" Expression -> Assignment
	 * @author schagen
	 *
	 */
	public static class VarAssignment extends Assignment {
		
		private Var var;
		private Expression expression;
		
		public Var getVar() {
			return var;
		}
		
		public void setVar(Var var) {
			this.var = var;
		}
		
		public Expression getExpression() {
			return expression;
		}
		
		public void setExpression(Expression expression) {
			this.expression = expression;
		}
		
		public ISyntaxNode[] getChildren() {
			return new ISyntaxNode[] {
				var,
				new CharacterLiteral('='),
				expression
			};
		}
		
	}

}