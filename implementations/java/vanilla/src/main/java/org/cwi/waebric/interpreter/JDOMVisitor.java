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

/**
 * Convert AST to JDOM format, allowing it to be parsed into an XHTML document.
 * The conversion is done using the visitor pattern to quit the endless casts
 * on statements and expressions.
 * @author Jeroen van Schagen
 * @date 11-06-2009
 */
public class JDOMVisitor extends DefaultNodeVisitor {

	/**
	 * Current defined functions
	 */
	private final Map<String, FunctionDef> functions;
	
	/**
	 * Current defined variables
	 */
	private final Map<String, Expression> variables;
	
	/**
	 * JDOM document
	 */
	private final Document document;
	
	/**
	 * Current element
	 */
	private Element current;
	
	/**
	 * Current text
	 */
	private String text = "";
	
	/**
	 * Construct JDOM visitor
	 * @param document Document
	 */
	public JDOMVisitor(Document document) {
		this.document = document;
		
		// Initiate mappings
		variables = new HashMap<String, Expression>();
		functions = new HashMap<String, FunctionDef>();
	}
	
	/**
	 * Construct JDOM visitor
	 * @param document Document
	 * @param functions Declared functions
	 * @param variables Defined variables
	 */
	public JDOMVisitor(Document document, Map<String, FunctionDef> functions, 
			Map<String, Expression> variables) {
		this.document = document;
		this.functions = functions;
		this.variables = variables;
	}

	public void visit(Module module, Object[] args) {
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
			main.accept(this, args);
		}
		
		functions.clear(); // Terminate all function definitions
	}

	public void visit(Site site, Object[] args) {
		// TODO Auto-generated method stub
		
	}

	public void visit(FunctionDef function, Object[] args) {	
		// Construct HTML root element when multiple statements can be root
		if(function.getStatements().size() > 1) {
			Element html = new Element("html", Namespace.getNamespace("xhtml", "http://www.w3.org/1999/xhtml"));
			html.setAttribute("lang", "en");
			
			// Store HTML tag as root element
			document.setRootElement(html);
			current = html;
		}

		// Process statement(s)
		Element root = current;
		for(Statement statement: function.getStatements()) {
			statement.accept(this, args);
			current = root; // Reset current to root
		}
		
		// Terminate all function variables
		for(IdCon identifier: function.getFormals().getIdentifiers()) {
			variables.remove(identifier.toString());
		}
	}

	public void visit(Statement.If statement, Object[] args) {
		if(evaluatePredicate(statement.getPredicate())) {
			statement.getStatement().accept(this, args);
		}
	}

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

	public void visit(Statement.Block statement, Object[] args) {
		Element root = current;
		for(Statement sub: statement.getStatements()) {
			sub.accept(this, args);
			current = root; // Reset current to root
		}
	}

	public void visit(Statement.CData statement, Object[] args) {
		// Process expression to fill data field
		statement.getExpression().accept(this, args);
		
		CDATA cdata = new CDATA(text);
		current.addContent(cdata);
	}

	public void visit(Statement.Comment statement, Object[] args) {
		Comment comment = new Comment(statement.getComment().getLiteral().toString());
		current.addContent(comment);
	}

	public void visit(Statement.Each statement, Object[] args) {
		// TODO Auto-generated method stub
		
	}

	public void visit(Statement.Echo statement, Object[] args) {
		statement.getExpression().accept(this, args);
	}

	public void visit(Statement.EchoEmbedding statement, Object[] args) {
		// TODO Auto-generated method stub
		
	}

	public void visit(Statement.Let statement, Object[] args) {
		// Extend function and variable definitions with assignments
		for(Assignment assignment: statement.getAssignments()) {
			assignment.accept(this, args);
		}
		
		// Visit sub-statements
		for(Statement sub: statement.getStatements()) {
			sub.accept(this, args);
		}
		
		// Destroy let assignments
		for(Assignment assignment: statement.getAssignments()) {
			if(assignment instanceof VarBind) {
				variables.remove(((VarBind) assignment).getIdentifier().getName());
			} else if(assignment instanceof FuncBind) {
				functions.remove(((FuncBind) assignment).getIdentifier().getName());
			}
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
		current.setText(text);
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
		// Construct new function definition based on bind data
		FunctionDef definition = new FunctionDef();
		
		definition.setIdentifier(bind.getIdentifier());
		definition.addStatement(bind.getStatement());
		
		if(bind.getVariables().size() == 0) {
			definition.setFormals(new Formals.EmptyFormal());
		} else {
			Formals.RegularFormal formals = new Formals.RegularFormal();
			for(IdCon variable : bind.getVariables()) {
				formals.addIdentifier(variable);
			}
		}

		functions.put(bind.getIdentifier().getName(), definition);
	}

	public void visit(VarBind bind, Object[] args) {
		variables.put(bind.getIdentifier().getName(), bind.getExpression());
	}

	public void visit(Call markup, Object[] args) {
		// Retrieve function definition
		FunctionDef function = functions.get(markup.getDesignator().getIdentifier().getName());
		
		// Store function variables and their called values
		int index = 0; 
		for(IdCon identifier: function.getFormals().getIdentifiers()) {
			Expression expression = markup.getArguments().get(index).getExpression();
			variables.put(identifier.toString(), expression);
			index++;
		}
	}

	public void visit(Tag markup, Object[] args) {
		Element tag = new Element(markup.getDesignator().getIdentifier().getName());
		current.addContent(tag);
		current = tag; // Current element is tag
	}

	public void visit(CatExpression expression, Object[] args) {
		// TODO Auto-generated method stub
		
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
		text = expression.getText().getLiteral().toString();
	}

	public void visit(VarExpression expression, Object[] args) {
		variables.get(expression.getVar()).accept(this, args);
	}

	public void visit(Embedding embedding, Object[] args) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * 
	 * @return
	 */
	public Element getCurrent() {
		return current;
	}
	
	/**
	 * 
	 * @param current
	 */
	public void setCurrent(Element current) {
		this.current = current;
	}
	
	/**
	 * 
	 * @return
	 */
	public String getText() {
		return text;
	}
	
	/**
	 * 
	 * @param data
	 */
	public void setText(String text) {
		if(text != null) { this.text = text; } 
	}
	
	/**
	 * 
	 * @param name
	 * @return
	 */
	public FunctionDef getFunction(String name) {
		return functions.get(name);
	}
	
	/**
	 * 
	 * @param name
	 * @return
	 */
	public Expression getVariable(String name) {
		return variables.get(name);
	}
	
}