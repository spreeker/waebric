package org.cwi.waebric.parser.ast.statements;

import org.cwi.waebric.WaebricSymbol;
import org.cwi.waebric.parser.ast.AbstractSyntaxNode;
import org.cwi.waebric.parser.ast.AbstractSyntaxNodeList;
import org.cwi.waebric.parser.ast.CharacterLiteral;
import org.cwi.waebric.parser.ast.embedding.Embedding;
import org.cwi.waebric.parser.ast.expressions.Expression;
import org.cwi.waebric.parser.ast.markup.Markup;

/**
 * All statements that start with Markup+
 * 
 * @author Jeroen van Schagen
 * @date 04-06-2009
 */
public abstract class MarkupsStatement extends Statement {
	
	protected AbstractSyntaxNodeList<Markup> markups;
	
	public MarkupsStatement(AbstractSyntaxNodeList<Markup> markups) {
		this.markups = markups;
	}
	
	public Markup getMarkup(int index) {
		return markups.get(index);
	}
	
	public int getMarkupCount() {
		return markups.size();
	}
	
	/**
	 * Markup+ Markup ";" -> Statement
	 */
	public static class MarkupMarkupsStatement extends MarkupsStatement {

		private Markup markup;
		
		public Markup getMarkup() {
			return markup;
		}

		public void setMarkup(Markup markup) {
			this.markup = markup;
		}

		public MarkupMarkupsStatement(AbstractSyntaxNodeList<Markup> markups) {
			super(markups);
		}

		public AbstractSyntaxNode[] getChildren() {
			return new AbstractSyntaxNode[] {
				markups,
				markup,
				new CharacterLiteral(WaebricSymbol.SEMICOLON)
			};
		}
		
	}
	
	/**
	 * Markup+ Statement ";" -> Statement
	 */
	public static class StatementMarkupsStatement extends MarkupsStatement {
		
		private Statement statement;
		
		public StatementMarkupsStatement(AbstractSyntaxNodeList<Markup> markups) {
			super(markups);
		}

		public Statement getStatement() {
			return statement;
		}

		public void setStatement(Statement statement) {
			this.statement = statement;
		}

		public AbstractSyntaxNode[] getChildren() {
			return new AbstractSyntaxNode[] {
				markups,
				statement,
				new CharacterLiteral(WaebricSymbol.SEMICOLON)
			};
		}
		
	}
	
	/**
	 * Markup+ Expression ";" -> Statement
	 */
	public static class ExpressionMarkupsStatement extends MarkupsStatement {

		private Expression expression;
		
		public ExpressionMarkupsStatement(AbstractSyntaxNodeList<Markup> markups) {
			super(markups);
		}

		public Expression getExpression() {
			return expression;
		}

		public void setExpression(Expression expression) {
			this.expression = expression;
		}
		
		public AbstractSyntaxNode[] getChildren() {
			return new AbstractSyntaxNode[] {
				markups,
				expression,
				new CharacterLiteral(WaebricSymbol.SEMICOLON)
			};
		}
		
	}
	
	/**
	 * Markup+ Embedding ";" -> Statement
	 */
	public static class EmbeddingMarkupsStatement extends MarkupsStatement {
		
		private Embedding embedding;
		
		public EmbeddingMarkupsStatement(AbstractSyntaxNodeList<Markup> markups) {
			super(markups);
		}

		public Embedding getEmbedding() {
			return embedding;
		}

		public void setEmbedding(Embedding embedding) {
			this.embedding = embedding;
		}
		
		public AbstractSyntaxNode[] getChildren() {
			return new AbstractSyntaxNode[] {
				markups,
				embedding,
				new CharacterLiteral(WaebricSymbol.SEMICOLON)
			};
		}
	}
	
}