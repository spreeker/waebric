package org.cwi.waebric.interpreter;

import java.util.HashMap;
import java.util.Map;

import org.cwi.waebric.parser.ast.DefaultNodeVisitor;
import org.cwi.waebric.parser.ast.basic.IdCon;
import org.cwi.waebric.parser.ast.expression.Expression;
import org.cwi.waebric.parser.ast.expression.Expression.CatExpression;
import org.cwi.waebric.parser.ast.expression.Expression.Field;
import org.cwi.waebric.parser.ast.expression.Expression.ListExpression;
import org.cwi.waebric.parser.ast.expression.Expression.NatExpression;
import org.cwi.waebric.parser.ast.expression.Expression.RecordExpression;
import org.cwi.waebric.parser.ast.expression.Expression.SymbolExpression;
import org.cwi.waebric.parser.ast.expression.Expression.TextExpression;
import org.cwi.waebric.parser.ast.expression.Expression.VarExpression;
import org.cwi.waebric.parser.ast.markup.Markup.Call;
import org.cwi.waebric.parser.ast.markup.Markup.Tag;
import org.cwi.waebric.parser.ast.module.function.Formals;
import org.cwi.waebric.parser.ast.module.function.FunctionDef;
import org.cwi.waebric.parser.ast.statement.Assignment;
import org.cwi.waebric.parser.ast.statement.Statement;
import org.cwi.waebric.parser.ast.statement.Assignment.FuncBind;
import org.cwi.waebric.parser.ast.statement.Assignment.VarBind;
import org.cwi.waebric.parser.ast.statement.embedding.Embedding;
import org.jdom.Comment;
import org.jdom.Document;
import org.jdom.Element;

/**
 * Convert AST to JDOM format.
 * @author Jeroen van Schagen
 * @date 11-06-2009
 */
@SuppressWarnings("unchecked")
public class JDOMVisitor extends DefaultNodeVisitor {

	/**
	 * Document reference
	 */
	private final Document document;
	
	/**
	 * Construct JDOMVisitor based on document
	 * @param document XML Document
	 */
	public JDOMVisitor(Document document) {
		this.document = document;
	}
	
	@Override
	public void visit(FunctionDef function, Object[] args) {
		Map<IdCon, Expression> variables = new HashMap<IdCon, Expression>();
		
		if(args != null && args.length != 0) {
			function.getFormals().accept(this, new Object[] { variables, args });
		}

		for(Statement statement: function.getStatements()) {
			statement.accept(this, new Object[] { variables, null });
		}
	}
	
	@Override
	public void visit(Formals.RegularFormal formals, Object[] args) {
		// Store variable values using names specified in formals
		Map<IdCon, Expression> variables = (Map<IdCon, Expression>) args[0];
		Expression[] values = (Expression[]) args[1];
		
		int index = 0;
		for(IdCon identifier: formals.getIdentifiers()) {
			variables.put(identifier, values[index]);
			index++;
		}
	}
	
	@Override
	public void visit(Statement.If statement, Object[] args) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(Statement.IfElse statement, Object[] args) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(Statement.Block statement, Object[] args) {
		for(Statement sub: statement.getStatements()) {
			sub.accept(this, args);
		}
	}

	@Override
	public void visit(Statement.CData statement, Object[] args) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(Statement.Comment statement, Object[] args) {
		// Attach JDOM comment to document
		Comment comment = new Comment(statement.getComment().toString());
		document.addContent(comment);
	}

	@Override
	public void visit(Statement.Each statement, Object[] args) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(Statement.Echo statement, Object[] args) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(Statement.EchoEmbedding statement, Object[] args) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(Statement.Let statement, Object[] args) {
		// Process assignments to extend function and variable bindings
		for(Assignment assignment: statement.getAssignments()) {
			assignment.accept(this, args);
		}
		
		// Delegate visit to sub-statements
		for(Statement sub: statement.getStatements()) {
			sub.accept(this, args);
		}
	}

	@Override
	public void visit(Statement.Yield statement, Object[] args) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(FuncBind bind, Object[] args) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(VarBind bind, Object[] args) {
		// Extend variable declarations with bind
		Map<IdCon, Expression> variables = (Map<IdCon, Expression>) args[0];
		variables.put(bind.getIdentifier(), bind.getExpression());
	}

	@Override
	public void visit(Call markup, Object[] args) {
		// TODO Retrieve function definition
		// TODO Visit function
	}

	@Override
	public void visit(Tag markup, Object[] args) {
		Element tag = new Element(markup.getDesignator().getIdentifier().getName());
		if(args[1] == null) {
			document.setRootElement(tag);
			args[1] = tag;
		} else {
			Element parent = (Element) args[1];
			parent.addContent(tag);
			args[1] = tag;
		}
	}

	@Override
	public void visit(CatExpression expression, Object[] args) {
		
	}

	@Override
	public void visit(Field expression, Object[] args) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(ListExpression expression, Object[] args) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(NatExpression expression, Object[] args) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(RecordExpression expression, Object[] args) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(SymbolExpression expression, Object[] args) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(TextExpression expression, Object[] args) {
		Element element = (Element) args[1];
		element.setText(expression.getText().getLiteral().toString());
	}

	@Override
	public void visit(VarExpression expression, Object[] args) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(Embedding embedding, Object[] args) {
		// TODO Auto-generated method stub
		
	}
	
}