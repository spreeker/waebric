package org.cwi.waebric.interpreter;

import java.util.HashMap;
import java.util.Map;

import org.cwi.waebric.parser.ast.DefaultNodeVisitor;
import org.cwi.waebric.parser.ast.basic.IdCon;
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
import org.cwi.waebric.parser.ast.statement.Statement;
import org.cwi.waebric.parser.ast.statement.Assignment.FuncBind;
import org.cwi.waebric.parser.ast.statement.Assignment.VarBind;
import org.cwi.waebric.parser.ast.statement.Statement.Block;
import org.cwi.waebric.parser.ast.statement.Statement.CData;
import org.cwi.waebric.parser.ast.statement.Statement.Comment;
import org.cwi.waebric.parser.ast.statement.Statement.Each;
import org.cwi.waebric.parser.ast.statement.Statement.Echo;
import org.cwi.waebric.parser.ast.statement.Statement.EchoEmbedding;
import org.cwi.waebric.parser.ast.statement.Statement.If;
import org.cwi.waebric.parser.ast.statement.Statement.IfElse;
import org.cwi.waebric.parser.ast.statement.Statement.Let;
import org.cwi.waebric.parser.ast.statement.Statement.MarkupEmbedding;
import org.cwi.waebric.parser.ast.statement.Statement.MarkupExp;
import org.cwi.waebric.parser.ast.statement.Statement.MarkupMarkup;
import org.cwi.waebric.parser.ast.statement.Statement.MarkupStat;
import org.cwi.waebric.parser.ast.statement.Statement.RegularMarkupStatement;
import org.cwi.waebric.parser.ast.statement.Statement.Yield;
import org.jdom.Document;
import org.jdom.Element;

/**
 * Convert AST to JDOM format.
 * @author Jeroen van Schagen
 * @date 11-06-2009
 */
public class JDOMVisitor extends DefaultNodeVisitor {

	private Document document;
	
	public JDOMVisitor(Document document) {
		this.document = document;
	}
	
	@Override
	public void visit(FunctionDef function, Object[] args) {
		Map<IdCon, Object> variables = new HashMap<IdCon, Object>();
		
		if(args != null && args.length != 0) {
			// Store variable values using names specified in formals
			function.getFormals().accept(this, new Object[] { variables, args });
		}
		
		for(Statement statement: function.getStatements()) {
			statement.accept(this, new Object[] { variables });
		}
	}
	
	@Override
	@SuppressWarnings("unchecked")
	public void visit(Formals.RegularFormal formals, Object[] args) {
		Map<IdCon, Object> variables = (Map<IdCon, Object>) args[0];
		Object[] values = (Object[]) args[1];
		
		int index = 0;
		for(IdCon identifier: formals.getIdentifiers()) {
			variables.put(identifier, values[index]);
			index++;
		}
	}
	
	@Override
	public void visit(If statement, Object[] args) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(IfElse statement, Object[] args) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(RegularMarkupStatement statement, Object[] args) {
		statement.getMarkup().accept(this, args);
	}

	@Override
	public void visit(Block statement, Object[] args) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(CData statement, Object[] args) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(Comment statement, Object[] args) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(Each statement, Object[] args) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(Echo statement, Object[] args) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(EchoEmbedding statement, Object[] args) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(Let statement, Object[] args) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(MarkupEmbedding statement, Object[] args) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(MarkupExp statement, Object[] args) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(MarkupMarkup statement, Object[] args) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(MarkupStat statement, Object[] args) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(Yield statement, Object[] args) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(FuncBind bind, Object[] args) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(VarBind bind, Object[] args) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(Call markup, Object[] args) {
		
	}

	@Override
	public void visit(Tag markup, Object[] args) {
		Element tag = new Element(markup.getDesignator().getIdentifier().getName());
		if(document.getContentSize() == 0) { document.setRootElement(tag); }
		else { document.addContent(tag); };
	}

	@Override
	public void visit(CatExpression expression, Object[] args) {
		// TODO Retrieve function definition
		// TODO Visit function
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
		// TODO Auto-generated method stub
		
	}

	@Override
	public void visit(VarExpression expression, Object[] args) {
		// TODO Auto-generated method stub
		
	}
	
}