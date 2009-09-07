package org.cwi.waebric.parser.ast.statement;

import java.util.Collection;
import java.util.List;

import org.cwi.waebric.parser.ast.SyntaxNode;
import org.cwi.waebric.parser.ast.INodeVisitor;
import org.cwi.waebric.parser.ast.SyntaxNodeList;
import org.cwi.waebric.parser.ast.basic.IdCon;
import org.cwi.waebric.parser.ast.basic.StrCon;
import org.cwi.waebric.parser.ast.expression.Expression;
import org.cwi.waebric.parser.ast.markup.Markup;
import org.cwi.waebric.parser.ast.statement.embedding.Embedding;
import org.cwi.waebric.parser.ast.statement.predicate.Predicate;

/**
 * Statements
 * @author Jeroen van Schagen
 * @date 25-05-2009
 */
public abstract class Statement extends SyntaxNode {

	/**
	 * Markup statements are an abstraction for all statements
	 * that start with Markup+
	 * 
	 * @author Jeroen van Schagen
	 * @date 05-06-2009
	 */
	public abstract static class AbstractMarkupStatement extends Statement {
		
		protected SyntaxNodeList<Markup> markups = new SyntaxNodeList<Markup>();
		
		public void addMarkup(Markup markup) {
			markups.add(markup);
		}
		
		public List<Markup> getMarkups() {
			return markups;
		}
		
	}
	
	/**
	 * If-else statements allow their users to control their statement
	 * flow with the use of predicates. In case a predicate is met the
	 * first statement is executed, otherwise nothing is executed.<br><br>
	 * 
	 * Grammar:<br>
	 * <code>
	 * 	"if" "(" Predicate ")" Statement NoElseMayFollow -> Statement
	 * <code>
	 */
	public static class If extends Statement {
		
		protected Predicate predicate;
		protected Statement trueStatement;
		
		public If() { }
		
		public If(Predicate predicate, Statement trueStatement) {
			this.predicate = predicate;
			this.trueStatement = trueStatement;
		}
		
		public Predicate getPredicate() {
			return predicate;
		}
		
		public void setPredicate(Predicate predicate) {
			this.predicate = predicate;
		}
		
		public Statement getTrueStatement() {
			return trueStatement;
		}
		
		public void setTrueStatement(Statement trueStatement) {
			this.trueStatement = trueStatement;
		}

		public SyntaxNode[] getChildren() {
			return new SyntaxNode[] { predicate, trueStatement };
		}
		
		@Override
		public <T> T accept(INodeVisitor<T> visitor) {
			return visitor.visit(this);
		}
		
	}
	
	/**
	 * If-else statements allow their users to control their statement
	 * flow with the use of predicates. In case a predicate is met the
	 * first statement is executed, otherwise the second statement is
	 * executed.<br><br>
	 * 
	 * Grammar:<br>
	 * <code>
	 * 	"if" "(" Predicate ")" Statement "else" Statement -> Statement
	 * </code>
	 */
	public static class IfElse extends If {
		
		private Statement elseStatement;
		
		public IfElse() { }
		
		public IfElse(Predicate predicate, Statement trueStatement, Statement elseStatement) {
			this.predicate = predicate;
			this.trueStatement = trueStatement;
			this.elseStatement = elseStatement;
		}
		
		public Statement getFalseStatement() {
			return elseStatement;
		}
		
		public void setFalseStatement(Statement elseStatement) {
			this.elseStatement = elseStatement;
		}

		public SyntaxNode[] getChildren() {
			return new SyntaxNode[] { predicate, trueStatement,	elseStatement };
		}
		
			@Override
	public <T> T accept(INodeVisitor<T> visitor) {
		return visitor.visit(this);
	}
		
	}

	/**
	 * Each statement allows users to iterate over a collection of
	 * expressions, such as a list or a record. For each element
	 * a local variable will be initiated and a statement executed.<br><br>
	 * 
	 * Grammar:<br>
	 * <code>
	 * 	"each" "(" IdCon ":" Expression ")" Statement -> Statement
	 * </code>
	 */
	public static class Each extends Statement {

		private IdCon var;
		private Expression expression;
		private Statement statement;
		
		public IdCon getVar() {
			return var;
		}

		public void setVar(IdCon var) {
			this.var = var;
		}

		public Expression getExpression() {
			return expression;
		}

		public void setExpression(Expression expression) {
			this.expression = expression;
		}

		public Statement getStatement() {
			return statement;
		}

		public void setStatement(Statement statement) {
			this.statement = statement;
		}

		public SyntaxNode[] getChildren() {
			return new SyntaxNode[] { var, expression, statement };
		}
		
			@Override
	public <T> T accept(INodeVisitor<T> visitor) {
		return visitor.visit(this);
	}
		
	}
	
	/**
	 * Let statements allow users to make additional variable and function
	 * bindings, which stay alive until the let statement is ended. <br><br>
	 * 
	 * Grammar:<br>
	 * <code>
	 * 	"let" Assignment+ "in" Statement* "end" -> Statement
	 * </code>
	 */
	public static class Let extends Statement {

		private SyntaxNodeList<Assignment> assignments;
		private SyntaxNodeList<Statement> statements;
		
		public Let() {
			assignments = new SyntaxNodeList<Assignment>();
			statements = new SyntaxNodeList<Statement>();
		}
		
		public boolean addAssignment(Assignment assignment) {
			return assignments.add(assignment);
		}
		
		public List<Assignment> getAssignments() {
			return assignments.clone();
		}
		
		public boolean addStatement(Statement statement) {
			return statements.add(statement);
		}
		
		public List<Statement> getStatements() {
			return statements.clone();
		}
		
		public SyntaxNode[] getChildren() {
			return new SyntaxNode[] { assignments, statements };
		}
		
			@Override
	public <T> T accept(INodeVisitor<T> visitor) {
		return visitor.visit(this);
	}
		
	}
	
	/**
	 * Block statements allow the user to define additional statements within
	 * the statement itself.<br><br>
	 * 
	 * Grammar:<br>
	 * <code>
	 * 	"{" Statement* "}"
	 * </code>
	 */
	public static class Block extends Statement {

		private SyntaxNodeList<Statement> statements;
		
		public Block() {
			statements = new SyntaxNodeList<Statement>();
		}
		
		public boolean addStatement(Statement statement) {
			return statements.add(statement);
		}
		
		public List<Statement> getStatements() {
			return statements;
		}
		
		public SyntaxNode[] getChildren() {
			return new SyntaxNode[] { statements };
		}
		
			@Override
	public <T> T accept(INodeVisitor<T> visitor) {
		return visitor.visit(this);
	}
		
	}
	
	/**
	 * Comment statements will be interpreted as HTML comment tags.<br><br>
	 * 
	 * Grammar:<br>
	 * <code>
	 * 	"comment" StrCon ";" -> Statement
	 * </code>
	 */
	public static class Comment extends Statement {

		private StrCon comment;

		public StrCon getComment() {
			return comment;
		}

		public void setComment(StrCon comment) {
			this.comment = comment;
		}

		public SyntaxNode[] getChildren() {
			return new SyntaxNode[] { comment };
		}
		
			@Override
	public <T> T accept(INodeVisitor<T> visitor) {
		return visitor.visit(this);
	}
		
	}
	
	/**
	 * Echo statements allow the user to attach text to the current
	 * XML element.<br><br>
	 * 
	 * Grammar:<br>
	 * <code>
	 * 	"echo" Expression ";" -> Statement
	 * </code>
	 */
	public static class Echo extends Statement {
		
		private Expression expression;

		public Echo() { }
		
		public Echo(Expression expression) {
			this.expression = expression;
		}
		
		public Expression getExpression() {
			return expression;
		}

		public void setExpression(Expression expression) {
			this.expression = expression;
		}
		
		public SyntaxNode[] getChildren() {
			return new SyntaxNode[] { expression };
		}
		
			@Override
	public <T> T accept(INodeVisitor<T> visitor) {
		return visitor.visit(this);
	}
		
	}
	
	/**
	 * "echo" Embedding ";" -> Statement
	 */
	public static class EchoEmbedding extends Statement {
		
		private Embedding embedding;

		public Embedding getEmbedding() {
			return embedding;
		}

		public void setEmbedding(Embedding embedding) {
			this.embedding = embedding;
		}
		
		public SyntaxNode[] getChildren() {
			return new SyntaxNode[] { embedding	};
		}
		
			@Override
	public <T> T accept(INodeVisitor<T> visitor) {
		return visitor.visit(this);
	}
		
	}
	
	/**
	 * CData statements translate directly in regular CDATA tags.<br><br>
	 * 
	 * Grammar:<br>
	 * <code>
	 * 	"cdata" Expression ";" -> Statement
	 * </code>
	 */
	public static class CData extends Statement {

		private Expression expression;
		
		public Expression getExpression() {
			return expression;
		}

		public void setExpression(Expression expression) {
			this.expression = expression;
		}

		public SyntaxNode[] getChildren() {
			return new SyntaxNode[] { expression };
		}
		
			@Override
	public <T> T accept(INodeVisitor<T> visitor) {
		return visitor.visit(this);
	}
		
	}
	
	/**
	 * "yield" ";" -> Statement
	 */
	public static class Yield extends Statement {
		
		public SyntaxNode[] getChildren() {
			return new SyntaxNode[] { /* No children */ };
		}
		
			@Override
	public <T> T accept(INodeVisitor<T> visitor) {
		return visitor.visit(this);
	}
		
	}
	
	/**
	 * Markup ";" -> Statement
	 */
	public static class MarkupStatement extends Statement {
		
		private Markup markup;

		public Markup getMarkup() {
			return markup;
		}

		public void setMarkup(Markup markup) {
			this.markup = markup;
		}

		public SyntaxNode[] getChildren() {
			return new SyntaxNode[] { markup };
		}
		
			@Override
	public <T> T accept(INodeVisitor<T> visitor) {
		return visitor.visit(this);
	}
		
	}
	
	/**
	 * Markup+ Markup ";" -> Statement
	 */
	public static class MarkupsMarkup extends AbstractMarkupStatement {

		private Markup markup;

		public MarkupsMarkup() { }

		public MarkupsMarkup(Markup markup) {
			markups.add(markup);
		}
		
		public MarkupsMarkup(Collection<Markup> args) {
			markups.addAll(args);
		}
		
		public Markup getMarkup() {
			return markup;
		}

		public void setMarkup(Markup markup) {
			this.markup = markup;
		}

		public SyntaxNode[] getChildren() {
			return new SyntaxNode[] { markups, markup};
		}
		
			@Override
	public <T> T accept(INodeVisitor<T> visitor) {
		return visitor.visit(this);
	}
		
	}
	
	/**
	 * Markup+ Statement ";" -> Statement
	 */
	public static class MarkupsStatement extends AbstractMarkupStatement {
		
		private Statement statement;
		
		public MarkupsStatement() { }

		public MarkupsStatement(Markup markup) {
			markups.add(markup);
		}
		
		public MarkupsStatement(Collection<Markup> args) {
			markups.addAll(args);
		}
		
		public Statement getStatement() {
			return statement;
		}

		public void setStatement(Statement statement) {
			this.statement = statement;
		}

		public SyntaxNode[] getChildren() {
			return new SyntaxNode[] { markups, statement };
		}
		
			@Override
	public <T> T accept(INodeVisitor<T> visitor) {
		return visitor.visit(this);
	}
		
	}
	
	/**
	 * Markup+ Expression ";" -> Statement
	 */
	public static class MarkupsExpression extends AbstractMarkupStatement {

		private Expression expression;
		
		public MarkupsExpression() { }

		public MarkupsExpression(Markup markup) {
			markups.add(markup);
		}
		
		public MarkupsExpression(Collection<Markup> args) {
			markups.addAll(args);
		}

		public Expression getExpression() {
			return expression;
		}

		public void setExpression(Expression expression) {
			this.expression = expression;
		}
		
		public SyntaxNode[] getChildren() {
			return new SyntaxNode[] { markups, expression };
		}
		
			@Override
	public <T> T accept(INodeVisitor<T> visitor) {
		return visitor.visit(this);
	}
		
	}
	
	/**
	 * Markup+ Embedding ";" -> Statement
	 */
	public static class MarkupsEmbedding extends AbstractMarkupStatement {
		
		private Embedding embedding;
		
		public MarkupsEmbedding() { }

		public MarkupsEmbedding(Markup markup) {
			markups.add(markup);
		}
		
		public MarkupsEmbedding(Collection<Markup> args) {
			markups.addAll(args);
		}

		public Embedding getEmbedding() {
			return embedding;
		}

		public void setEmbedding(Embedding embedding) {
			this.embedding = embedding;
		}
		
		public SyntaxNode[] getChildren() {
			return new SyntaxNode[] { markups, embedding };
		}
		
		@Override
		public <T> T accept(INodeVisitor<T> visitor) {
			return visitor.visit(this);
		}
		
	}

}