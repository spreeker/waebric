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
import org.jdom.Comment;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;

/**
 * Convert AST to JDOM format, allowing it to be parsed into an XHTML document.
 * The conversion is done using the visitor pattern to quit the endless casts
 * on statements and expressions.
 * @author Jeroen van Schagen
 * @date 11-06-2009
 */
public class JDOMVisitor extends DefaultNodeVisitor {
	
	/**
	 * Active function definitions
	 */
	private Map<IdCon, FunctionDef> functions;
	
	/**
	 * Active variables
	 */
	private Map<IdCon, Expression> variables;
	
	/**
	 * Construct JDOM visitor based on modules instance.
	 * @param modules
	 */
	public JDOMVisitor() {
		this.functions = new HashMap<IdCon, FunctionDef>();
		this.variables = new HashMap<IdCon, Expression>();
	}

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
		Modules dependancies = ModuleRegister.getInstance().loadDependancies(module).getRoot();
		for(Module dependancy: dependancies) {
			for(FunctionDef function: dependancy.getFunctionDefinitions()) {
				functions.put(function.getIdentifier(), function);
			}
		}
		
		// Start interpreting "main" function
		FunctionDef main = module.getFunctionDefinition("main");
		if(main != null) {
			main.accept(this, new Object[] { 
				document, // Document reference
				new Expression[]{} // Main function takes no call values
			});
		}
		
		functions.clear(); // Terminate all function definitions
	}

	public void visit(Site site, Object[] args) {
		// TODO Auto-generated method stub
		
	}
	
	/**
	 * Store call values and delegate statements.
	 * @param args[0] Document or Element as parent node
	 * @param args[1] Expression[] as function variable values
	 */
	public void visit(FunctionDef function, Object[] args) {	
		// Store function variables with their called expressions
		Expression[] values = (Expression[]) args[1]; int index = 0; 
		for(IdCon identifier: function.getFormals().getIdentifiers()) {
			variables.put(identifier, values[index]);
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
					args[0] // Parent node, document or element instance
				});
		}
		
		// Terminate function variables
		for(IdCon identifier: function.getFormals().getIdentifiers()) {
			variables.remove(identifier);
		}
	}
	
	public void visit(Statement.If statement, Object[] args) {
		// TODO Auto-generated method stub
		
	}

	public void visit(Statement.IfElse statement, Object[] args) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Call all sub-statements which affecting current element.
	 */
	public void visit(Statement.Block statement, Object[] args) {
		Element parent = (Element) args[0];
		for(Statement sub: statement.getStatements()) {
			sub.accept(this, new Object[] { parent });
		}
	}

	public void visit(Statement.CData statement, Object[] args) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Store new comment content on parent element, but leave current
	 * element untouched.
	 */
	public void visit(Statement.Comment statement, Object[] args) {
		if(args[0] instanceof Element) {
			Comment comment = new Comment(statement.getComment().toString());
			Element parent = (Element) args[0];
			parent.addContent(comment);
		}
	}

	public void visit(Statement.Each statement, Object[] args) {
		// TODO Auto-generated method stub
		
	}

	public void visit(Statement.Echo statement, Object[] args) {
		// TODO Auto-generated method stub
		
	}

	public void visit(Statement.EchoEmbedding statement, Object[] args) {
		// TODO Auto-generated method stub
		
	}

	public void visit(Statement.Let statement, Object[] args) {
		// Process assignments to extend function and variable bindings
		for(Assignment assignment: statement.getAssignments()) {
			assignment.accept(this, args);
		}
		
		// Delegate visit to sub-statements
		Object parent = args[0];
		for(Statement sub: statement.getStatements()) {
			sub.accept(this, new Object[] { parent });
		}
	}

	public void visit(Statement.Yield statement, Object[] args) {
		// TODO Auto-generated method stub
		
	}
	
	public void visit(RegularMarkupStatement statement, Object[] args) {
		statement.getMarkup().accept(this, args);
	}

	public void visit(MarkupMarkup statement, Object[] args) {
		for(Markup markup: statement.getMarkups()) {
			markup.accept(this, args);
		}
	}

	public void visit(MarkupExp statement, Object[] args) {
		// Visit mark-up(s)
		for(Markup markup: statement.getMarkups()) {
			markup.accept(this, args);
		}
		
		// Visit expression
		statement.getExpression().accept(this, args);
	}

	public void visit(MarkupStat statement, Object[] args) {
		// Visit mark-up(s)
		for(Markup markup: statement.getMarkups()) {
			markup.accept(this, args);
		}
		
		// Visit statement
		statement.getStatement().accept(this, args);
	}

	public void visit(MarkupEmbedding statement, Object[] args) {
		// TODO Auto-generated method stub
		
	}

	public void visit(FuncBind bind, Object[] args) {
		// TODO Auto-generated method stub
		
	}

	public void visit(VarBind bind, Object[] args) {
		// Extend variable declarations with bind
		variables.put(bind.getIdentifier(), bind.getExpression());
	}

	public void visit(Call markup, Object[] args) {
		// TODO Retrieve function definition
		// TODO Visit function
	}

	/**
	 * 
	 */
	public void visit(Tag markup, Object[] args) {
		Element tag = new Element(markup.getDesignator().getIdentifier().getName());
		
		// Store tag on parent element (JDOM does not allow document and element to be generalized)
		if(args[0] instanceof Document) {
			Document document = (Document) args[0];
			document.setRootElement(tag);
			args[0] = tag;
		} else if(args[0] instanceof Element) {
			Element parent = (Element) args[0];
			parent.addContent(tag);
			args[0] = tag;
		}
	}

	public void visit(CatExpression expression, Object[] args) {
		
	}

	public void visit(Field expression, Object[] args) {
		// TODO Auto-generated method stub
		
	}

	public void visit(ListExpression expression, Object[] args) {
		// TODO Auto-generated method stub
		
	}

	public void visit(NatExpression expression, Object[] args) {
		// TODO Auto-generated method stub
		
	}

	public void visit(RecordExpression expression, Object[] args) {
		// TODO Auto-generated method stub
		
	}

	public void visit(SymbolExpression expression, Object[] args) {
		// TODO Auto-generated method stub
		
	}

	public void visit(TextExpression expression, Object[] args) {
		Element element = (Element) args[0];
		element.setText(expression.getText().getLiteral().toString());
	}

	public void visit(VarExpression expression, Object[] args) {
		// Delegate visit to expression value of variable
		variables.get(expression.getVar()).accept(this, args);
	}

	public void visit(Embedding embedding, Object[] args) {
		// TODO Auto-generated method stub
		
	}
	
}