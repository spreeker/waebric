package org.cwi.waebric.parser.ast.statement;

import org.cwi.waebric.WaebricSymbol;
import org.cwi.waebric.parser.ast.AbstractSyntaxNode;
import org.cwi.waebric.parser.ast.CharacterLiteral;
import org.cwi.waebric.parser.ast.AbstractSyntaxNodeList.AbstractSeparatedSyntaxNodeList;
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
		private AbstractSeparatedSyntaxNodeList<IdCon> identifiers;
		private Statement statement;
		
		/**
		 * Construct default function binding.
		 */
		public FuncBind() {
			identifiers = new AbstractSeparatedSyntaxNodeList<IdCon>(',');
		}
		
		public IdCon getIdentifier() {
			return identifier;
		}
	
		public void setIdentifier(IdCon identifier) {
			this.identifier = identifier;
		}
	
		public IdCon getIdentifier(int index) {
			return identifiers.get(index);
		}
	
		public void addIdentifier(IdCon identifier) {
			identifiers.add(identifier);
		}
		
		public int getIdentifierCount() {
			return identifiers.size();
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
				identifiers,
				new CharacterLiteral(WaebricSymbol.RPARANTHESIS),
				new CharacterLiteral(WaebricSymbol.EQUAL_SIGN),
				statement
			};
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
		
	}

}