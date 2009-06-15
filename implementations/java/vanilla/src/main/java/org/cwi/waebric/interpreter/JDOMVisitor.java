package org.cwi.waebric.interpreter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.cwi.waebric.ModuleRegister;
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
import org.cwi.waebric.parser.ast.markup.Markup;
import org.cwi.waebric.parser.ast.markup.Markup.Call;
import org.cwi.waebric.parser.ast.markup.Markup.Tag;
import org.cwi.waebric.parser.ast.module.Module;
import org.cwi.waebric.parser.ast.module.Modules;
import org.cwi.waebric.parser.ast.module.function.Formals;
import org.cwi.waebric.parser.ast.module.function.FunctionDef;
import org.cwi.waebric.parser.ast.module.site.Site;
import org.cwi.waebric.parser.ast.statement.Assignment;
import org.cwi.waebric.parser.ast.statement.Statement;
import org.cwi.waebric.parser.ast.statement.Assignment.FuncBind;
import org.cwi.waebric.parser.ast.statement.Assignment.VarBind;
import org.cwi.waebric.parser.ast.statement.Statement.MarkupEmbedding;
import org.cwi.waebric.parser.ast.statement.Statement.MarkupExp;
import org.cwi.waebric.parser.ast.statement.Statement.MarkupMarkup;
import org.cwi.waebric.parser.ast.statement.Statement.MarkupStat;
import org.cwi.waebric.parser.ast.statement.Statement.RegularMarkupStatement;
import org.cwi.waebric.parser.ast.statement.embedding.Embedding;
import org.cwi.waebric.parser.ast.statement.predicate.Predicate;

import org.jdom.CDATA;
import org.jdom.Comment;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.Parent;
import org.jdom.Text;

/**
 * Convert AST to JDOM format, allowing it to be parsed into an XHTML document.
 * The conversion is done using the visitor pattern to quit the endless casts
 * on statements and expressions.
 * @author Jeroen van Schagen
 * @date 11-06-2009
 */
@SuppressWarnings("unchecked")
public class JDOMVisitor extends DefaultNodeVisitor {
	
	/**
	 * Store function definitions and interpret "main".
	 * @param args[0] Document as JDOM root element.
	 */
	public void visit(Module module, Object[] args) {
		Document document = (Document) args[0];
		
		// Brand-mark document
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Comment comment = new Comment("Compiled on: " + format.format(new Date()));
		document.addContent(comment);
		
		// Store function definitions
		Map<String, FunctionDef> functions = new HashMap<String, FunctionDef>();
		Modules dependancies = ModuleRegister.getInstance().loadDependancies(module).getRoot();
		for(Module dependancy: dependancies) {
			for(FunctionDef function: dependancy.getFunctionDefinitions()) {
				functions.put(function.getIdentifier().toString(), function);
			}
		}
		
		// Start interpreting "main" function
		FunctionDef main = functions.get("main");
		if(main != null) {
			main.accept(this, new Object[] { 
				document, // Document reference
				functions,
				new Expression[]{} // Main function takes no call values
			});
		}
	}

	public void visit(Site site, Object[] args) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Store call values and delegate statements.
	 * @param args[0] Parent node
	 * @param args[1] Map<String, FunctionDef> for function definitions
	 * @param args[2] Expression[] as function variable values
	 */
	public void visit(FunctionDef function, Object[] args) {	
		// Store function variables with their called expressions
		Map<String, Expression> variables = new HashMap<String, Expression>();
		Expression[] values = (Expression[]) args[2];
		
		int index = 0; 
		for(IdCon identifier: function.getFormals().getIdentifiers()) {
			variables.put(identifier.toString(), values[index]);
			index++;
		}

		// Construct HTML root element when multiple statements can be root
		if(args[0] instanceof Document && function.getStatements().size() > 1) {
			Document document = (Document) args[0];

			// <html xmlns="http://www.w3.org/1999/xhtml" xml:lang="en" lang="en">
			Element html = new Element("html", Namespace.getNamespace("xhtml", "http://www.w3.org/1999/xhtml"));
			html.setAttribute("lang", "en");
			document.setRootElement(html);
			
			args[0] = html; // Make HTML current element
		}
		
		// Process statement(s)
		for(Statement statement: function.getStatements()) {
			statement.accept(this, new Object[] { 
					args[0] // Parent node
				});
		}
	}
	
	/**
	 * 
	 * @param args[0] Parent node
	 * @param args[1] Map<String, FunctionDef> for function definitions
	 * @param args[2] Map<String, Expression> for variable definitions
	 */
	public void visit(Statement.If statement, Object[] args) {
		if(evaluatePredicate(statement.getPredicate())) {
			statement.getStatement().accept(this, args);
		}
	}

	/**
	 * 
	 * @param args[0] Parent node
	 * @param args[1] Map<String, FunctionDef> for function definitions
	 * @param args[2] Map<String, Expression> for variable definitions
	 */
	public void visit(Statement.IfElse statement, Object[] args) {
		if(evaluatePredicate(statement.getPredicate())) {
			statement.getStatement().accept(this, args);
		} else {
			statement.getElseStatement().accept(this, args);
		}
	}
	
	/**
	 * Evaluate predicate into a boolean value.
	 * @param predicate
	 * @return Boolean
	 */
	private boolean evaluatePredicate(Predicate predicate) {
		if(predicate instanceof Predicate.Is) {
			Predicate.Is is = (Predicate.Is) predicate;
			
			String type = is.getType().getName().toString();
			if(type.equals("string")) {
				return is.getExpression().getClass() == Expression.TextExpression.class;
			} else if(type.equals("list")) {
				return is.getExpression().getClass() == Expression.ListExpression.class;
			} else if(type.equals("record")) {
				return is.getExpression().getClass() == Expression.RecordExpression.class;
			}
			
			return false;
		} else if(predicate instanceof Predicate.And) {
			Predicate.And and = (Predicate.And) predicate;
			return evaluatePredicate(and.getLeft()) && evaluatePredicate(and.getRight());
		} else if(predicate instanceof Predicate.Or) {
			Predicate.Or or = (Predicate.Or) predicate;
			return evaluatePredicate(or.getLeft()) || evaluatePredicate(or.getRight());			
		} else if(predicate instanceof Predicate.Not) {
			Predicate.Not and = (Predicate.Not) predicate;
			return ! evaluatePredicate(and.getPredicate());			
		}
		
		return false;
	}

	/**
	 * 
	 * @param args[0] Parent node
	 * @param args[1] Map<String, FunctionDef> for function definitions
	 * @param args[2] Map<String, Expression> for variable definitions
	 */
	public void visit(Statement.Block statement, Object[] args) {
		for(Statement sub: statement.getStatements()) {
			sub.accept(this, new Object[] { args[0] });
		}
	}

	/**
	 * 
	 * @param args[0] Parent node
	 * @param args[1] Map<String, FunctionDef> for function definitions
	 * @param args[2] Map<String, Expression> for variable definitions
	 */
	public void visit(Statement.CData statement, Object[] args) {
		CDATA data = new CDATA("");
		
		Parent parent = (Parent) args[0];
		parent.getContent().add(data);
		
		args[0] = data; // Change current element to CDATA
		statement.getExpression().accept(this, args);
	}

	/**
	 * 
	 * @param args[0] Parent node
	 * @param args[1] Map<String, FunctionDef> for function definitions
	 * @param args[2] Map<String, Expression> for variable definitions
	 */
	public void visit(Statement.Comment statement, Object[] args) {
		Comment comment = new Comment(statement.getComment().getLiteral().toString());
		
		Parent parent = (Parent) args[0];
		parent.getContent().add(comment);
	}

	/**
	 * 
	 * @param args[0] Parent node
	 * @param args[1] Map<String, FunctionDef> for function definitions
	 * @param args[2] Map<String, Expression> for variable definitions
	 */
	public void visit(Statement.Each statement, Object[] args) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * 
	 * @param args[0] Parent node
	 * @param args[1] Map<String, FunctionDef> for function definitions
	 * @param args[2] Map<String, Expression> for variable definitions
	 */
	public void visit(Statement.Echo statement, Object[] args) {
		statement.getExpression().accept(this, args);
	}

	/**
	 * 
	 * @param args[0] Parent node
	 * @param args[1] Map<String, FunctionDef> for function definitions
	 * @param args[2] Map<String, Expression> for variable definitions
	 */
	public void visit(Statement.EchoEmbedding statement, Object[] args) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * 
	 * @param args[0] Parent node
	 * @param args[1] Map<String, FunctionDef> for function definitions
	 * @param args[2] Map<String, Expression> for variable definitions
	 */
	public void visit(Statement.Let statement, Object[] args) {
		// Extend function and variable definitions with assignments
		for(Assignment assignment: statement.getAssignments()) {
			assignment.accept(this, args);
		}
		
		// Visit sub-statements
		for(Statement sub: statement.getStatements()) {
			sub.accept(this, new Object[] { args[0] });
		}
	}

	/**
	 * 
	 * @param args[0] Parent node
	 * @param args[1] Map<String, FunctionDef> for function definitions
	 * @param args[2] Map<String, Expression> for variable definitions
	 */
	public void visit(Statement.Yield statement, Object[] args) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * 
	 * @param args[0] Parent node
	 * @param args[1] Map<String, FunctionDef> for function definitions
	 * @param args[2] Map<String, Expression> for variable definitions
	 */
	public void visit(RegularMarkupStatement statement, Object[] args) {
		statement.getMarkup().accept(this, args);
	}

	/**
	 * 
	 * @param args[0] Parent node
	 * @param args[1] Map<String, FunctionDef> for function definitions
	 * @param args[2] Map<String, Expression> for variable definitions
	 */
	public void visit(MarkupMarkup statement, Object[] args) {
		for(Markup markup: statement.getMarkups()) {
			markup.accept(this, args);
		}
	}

	/**
	 * 
	 * @param args[0] Parent node
	 * @param args[1] Map<String, FunctionDef> for function definitions
	 * @param args[2] Map<String, Expression> for variable definitions
	 */
	public void visit(MarkupExp statement, Object[] args) {
		// Visit mark-up(s)
		for(Markup markup: statement.getMarkups()) {
			markup.accept(this, args);
		}
		
		// Visit expression
		statement.getExpression().accept(this, args);
	}

	/**
	 * 
	 * @param args[0] Parent node
	 * @param args[1] Map<String, FunctionDef> for function definitions
	 * @param args[2] Map<String, Expression> for variable definitions
	 */
	public void visit(MarkupStat statement, Object[] args) {
		// Visit mark-up(s)
		for(Markup markup: statement.getMarkups()) {
			markup.accept(this, args);
		}
		
		// Visit statement
		statement.getStatement().accept(this, args);
	}

	/**
	 * 
	 * @param args[0] Parent node
	 * @param args[1] Map<String, FunctionDef> for function definitions
	 * @param args[2] Map<String, Expression> for variable definitions
	 */
	public void visit(MarkupEmbedding statement, Object[] args) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Extend function declarations with bind
	 * @param args[0] Parent node
	 * @param args[1] Map<String, FunctionDef> for function definitions
	 * @param args[2] Map<String, Expression> for variable definitions
	 */
	public void visit(FuncBind bind, Object[] args) {
		Map<String, FunctionDef> functions = (Map<String, FunctionDef>) args[1];
		
		// Construct new function definition based on bind data
		FunctionDef definition = new FunctionDef();
		definition.setIdentifier(bind.getIdentifier());
		if(bind.getVariables().size() == 0) {
			definition.setFormals(new Formals.EmptyFormal());
		} else {
			Formals.RegularFormal formals = new Formals.RegularFormal();
			for(IdCon variable : bind.getVariables()) {
				formals.addIdentifier(variable);
			}
		}
		definition.addStatement(bind.getStatement());
		
		functions.put(bind.getIdentifier().getName(), definition);
	}

	/**
	 * Extend variable declarations with bind
	 * @param args[0] Parent node
	 * @param args[1] Map<String, FunctionDef> for function definitions
	 * @param args[2] Map<String, Expression> for variable definitions
	 */
	public void visit(VarBind bind, Object[] args) {
		Map<String, Expression> variables = (Map<String, Expression>) args[2];
		variables.put(bind.getIdentifier().getName(), bind.getExpression());
	}

	/**
	 * 
	 * @param args[0] Parent node
	 * @param args[1] Map<String, FunctionDef> for function definitions
	 * @param args[2] Map<String, Expression> for variable definitions
	 */
	public void visit(Call markup, Object[] args) {
		// TODO Retrieve function definition
		// TODO Visit function
	}

	/**
	 * 
	 * @param args[0] Parent node
	 * @param args[1] Map<String, FunctionDef> for function definitions
	 * @param args[2] Map<String, Expression> for variable definitions
	 */
	public void visit(Tag markup, Object[] args) {
		Element tag = new Element(markup.getDesignator().getIdentifier().getName());
		
		Parent parent = (Parent) args[0];
		parent.getContent().add(tag);
		
		args[0] = tag; // Current element is tag
	}

	/**
	 * 
	 * @param args[0] Parent node
	 * @param args[1] Map<String, FunctionDef> for function definitions
	 * @param args[2] Map<String, Expression> for variable definitions
	 */
	public void visit(CatExpression expression, Object[] args) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * 
	 * @param args[0] Parent node
	 * @param args[1] Map<String, FunctionDef> for function definitions
	 * @param args[2] Map<String, Expression> for variable definitions
	 */
	public void visit(Field expression, Object[] args) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * 
	 * @param args[0] Parent node
	 * @param args[1] Map<String, FunctionDef> for function definitions
	 * @param args[2] Map<String, Expression> for variable definitions
	 */
	public void visit(ListExpression expression, Object[] args) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * 
	 * @param args[0] Parent node
	 * @param args[1] Map<String, FunctionDef> for function definitions
	 * @param args[2] Map<String, Expression> for variable definitions
	 */
	public void visit(NatExpression expression, Object[] args) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * 
	 * @param args[0] Parent node
	 * @param args[1] Map<String, FunctionDef> for function definitions
	 * @param args[2] Map<String, Expression> for variable definitions
	 */
	public void visit(RecordExpression expression, Object[] args) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * 
	 * @param args[0] Parent node
	 * @param args[1] Map<String, FunctionDef> for function definitions
	 * @param args[2] Map<String, Expression> for variable definitions
	 */
	public void visit(SymbolExpression expression, Object[] args) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * 
	 * @param args[0] Parent content
	 * @param args[1] Map<String, FunctionDef> for function definitions
	 * @param args[2] Map<String, Expression> for variable definitions
	 */
	public void visit(TextExpression expression, Object[] args) {
		if(args[0] instanceof Element) {
			Element element = (Element) args[0];
			element.setText(expression.getText().getLiteral().toString());
		} else if(args[0] instanceof Text) {
			Text text = (Text) args[0];
			text.setText(expression.getText().getLiteral().toString());
		}
	}

	/**
	 * Delegate visit to expression value of variable
	 * @param args[0] Parent content
	 * @param args[1] Map<String, FunctionDef> for function definitions
	 * @param args[2] Map<String, Expression> for variable definitions
	 */
	public void visit(VarExpression expression, Object[] args) {
		Map<String, Expression> variables = (Map<String, Expression>) args[2];
		variables.get(expression.getVar()).accept(this, args);
	}

	/**
	 * 
	 * @param args[0] Parent content
	 * @param args[1] Map<String, FunctionDef> for function definitions
	 * @param args[2] Map<String, Expression> for variable definitions
	 */
	public void visit(Embedding embedding, Object[] args) {
		// TODO Auto-generated method stub
		
	}
	
}