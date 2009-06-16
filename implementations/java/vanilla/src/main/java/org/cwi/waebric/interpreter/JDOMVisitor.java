package org.cwi.waebric.interpreter;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

import org.cwi.waebric.ModuleRegister;
import org.cwi.waebric.parser.ast.AbstractSyntaxNode;
import org.cwi.waebric.parser.ast.DefaultNodeVisitor;
import org.cwi.waebric.parser.ast.NodeList;
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
import org.cwi.waebric.parser.ast.markup.Attribute;
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
 * Convert AST in JDOM format, the eventual XHTML document is generated using
 * the JDOM libraries. During conversion the visitor pattern is applied to
 * minimize the need for node casts and result in more readable code.
 * @author Jeroen van Schagen
 * @date 11-06-2009
 */
public class JDOMVisitor extends DefaultNodeVisitor {

	/**
	 * Active function definitions
	 */
	private final Map<String, FunctionDef> functions;
	
	/**
	 * Active variables
	 */
	private final Map<String, Expression> variables;
	
	/**
	 * JDOM root element
	 */
	private final Document document;
	
	/**
	 * JDOM current element
	 */
	private Element current;
	
	/**
	 * Current text value
	 */
	private String text = "";
	
	/**
	 * Current yield stack (statement, expression or embedding)
	 */
	private Stack<AbstractSyntaxNode> yield;
	
	/**
	 * Construct JDOM visitor
	 * @param document Document
	 */
	public JDOMVisitor(Document document) {
		this.document = document;
		
		functions = new HashMap<String, FunctionDef>();
		variables = new HashMap<String, Expression>();

		yield = new Stack<AbstractSyntaxNode>();
	}

	public void visit(Module module) {
		// Brand-mark document
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Comment comment = new Comment("Compiled on: " + format.format(new Date()));
		document.addContent(comment);
		
		// Store function definitions
		Modules dependancies = ModuleRegister.getInstance().loadDependancies(module).getRoot();
		for(Module dependancy: dependancies) {
			for(FunctionDef function: dependancy.getFunctionDefinitions()) {
				functions.put(function.getIdentifier().toString(), function);
			}
		}
		
		visit(functions.get("main")); // Start interpreting "main" function
		functions.clear(); // Terminate all function definitions
	}

	public void visit(Site site) {
		// TODO Auto-generated method stub
		
	}

	public void visit(FunctionDef function) {	
		// Construct HTML root element when multiple statements can be root
		if(function.getStatements().size() > 1 && ! document.hasRootElement()) {
			Element html = new Element("html", Namespace.getNamespace("xhtml", "http://www.w3.org/1999/xhtml"));
			html.setAttribute("lang", "en");
			
			// Store HTML tag as root element
			document.setRootElement(html);
			current = html;
		}

		// Process statement(s)
		Element root = current;
		for(Statement statement: function.getStatements()) {
			current = root; // Reset current to root
			statement.accept(this);
		}
		
		// Terminate all function variables
		for(IdCon identifier: function.getFormals().getIdentifiers()) {
			variables.remove(identifier.toString());
		}
	}

	public void visit(Statement.If statement) {
		if(evaluatePredicate(statement.getPredicate())) {
			statement.getStatement().accept(this);
		}
	}

	public void visit(Statement.IfElse statement) {
		if(evaluatePredicate(statement.getPredicate())) {
			statement.getStatement().accept(this);
		} else {
			statement.getElseStatement().accept(this);
		}
	}
	
	/**
	 * Evaluate predicate into a boolean value.
	 * @param predicate
	 * @return Boolean
	 */
	public boolean evaluatePredicate(Predicate predicate) {
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
		} else if(predicate instanceof Predicate.RegularPredicate) {
			Predicate.RegularPredicate reg = (Predicate.RegularPredicate) predicate;
			if(reg.getExpression() instanceof Expression.Field) {
				// TODO Figure out how field expressions work
			} else if(reg.getExpression() instanceof Expression.VarExpression) {
				String name = ((Expression.VarExpression) reg.getExpression()).getVar().getName();
				return variables.containsKey(name);
			} else { return true; }
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

	public void visit(Statement.Block statement) {
		Element root = current;
		for(Statement sub: statement.getStatements()) {
			current = root; // Reset current to root
			sub.accept(this);
		}
	}

	public void visit(Statement.CData statement) {
		// Process expression to fill text field
		statement.getExpression().accept(this);
		
		CDATA cdata = new CDATA(text);
		current.addContent(cdata);
	}

	public void visit(Statement.Comment statement) {
		Comment comment = new Comment(statement.getComment().getLiteral().toString());
		current.addContent(comment);
	}

	public void visit(Statement.Each statement) {
		if(statement.getExpression() instanceof Expression.ListExpression) {
			Expression.ListExpression list = (Expression.ListExpression) statement.getExpression();
			
			// Execute statement for each element
			for(Expression expression: list.getExpressions()) {
				variables.put(statement.getVar().getName(), expression);
				statement.getStatement().accept(this);
				variables.remove(statement.getVar().getName());
			}
		}	
	}

	public void visit(Statement.Echo statement) {
		statement.getExpression().accept(this);
		current.setText(current.getText() + text);
	}

	public void visit(Statement.EchoEmbedding statement) {
		// TODO Auto-generated method stub
		
	}

	public void visit(Statement.Let statement) {
		// Extend function and variable definitions with assignments
		for(Assignment assignment: statement.getAssignments()) {
			assignment.accept(this);
		}
		
		// Visit sub-statements
		for(Statement sub: statement.getStatements()) {
			sub.accept(this);
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

	/**
	 * Retrieve first element from yield stack and visit it.
	 */
	public void visit(Statement.Yield statement) {
		AbstractSyntaxNode replacement = yield.pop();
		if(replacement != null) {
			replacement.accept(this);
	
			// Place text value in current element
			if(replacement instanceof Expression || replacement instanceof Embedding) {
				current.setText(text);
			}
		}
	}

	public void visit(RegularMarkupStatement statement) {
		statement.getMarkup().accept(this);
	}

	public void visit(MarkupMarkup statement) {
		for(Markup markup: statement.getMarkups()) {
			markup.accept(this);
			if(markup instanceof Markup.Call) { return; } // Quit interpreting after call
		}
	}

	public void visit(MarkupExp statement) {
		// Visit mark-ups
		Iterator<Markup> markups = statement.getMarkups().iterator();
		while(markups.hasNext()) {
			Markup markup = markups.next();
			boolean containsYield = containsYield(markup);
			
			if(containsYield) {
				// Determine and store yield value
				if(markups.hasNext()) {
					// Remainder of mark-up chain
					NodeList<Markup> remainder = new NodeList<Markup>();
					while(markups.hasNext()) { remainder.add(markups.next()); }
					MarkupExp expr = new MarkupExp(remainder);
					expr.setExpression(statement.getExpression());
					yield.add(expr);
				} else {
					// Single remaining expression
					yield.add(statement.getExpression());
				}
			}

			markup.accept(this); // Visit mark-up

			if(markup instanceof Markup.Call) {
				return; // Quit parsing mark-up expression after first call
			}
		}
		
		// Interpret expression when mark-up chain is call free
		statement.getExpression().accept(this);
		current.setText(text);
	}

	public void visit(MarkupStat statement) {
		// Visit mark-ups
		Iterator<Markup> markups = statement.getMarkups().iterator();
		while(markups.hasNext()) {
			Markup markup = markups.next();
			boolean containsYield = containsYield(markup);
			
			if(containsYield) {
				// Determine and store yield value
				if(markups.hasNext()) {
					// Remainder of mark-up chain
					NodeList<Markup> remainder = new NodeList<Markup>();
					while(markups.hasNext()) { remainder.add(markups.next()); }
					MarkupStat stm = new MarkupStat(remainder);
					stm.setStatement(statement.getStatement());
					yield.add(stm);
				} else {
					// Single remaining statement
					yield.add(statement.getStatement());
				}
			}

			markup.accept(this); // Visit mark-up

			if(markup instanceof Markup.Call) {
				return; // Quit parsing mark-up statement after first call
			}
		}
		
		// Interpret statement when mark-up chain is call free
		statement.getStatement().accept(this);
	}

	public void visit(MarkupEmbedding statement) {
		// Visit mark-ups
		Iterator<Markup> markups = statement.getMarkups().iterator();
		while(markups.hasNext()) {
			Markup markup = markups.next();
			boolean containsYield = containsYield(markup);
			
			if(containsYield) {
				// Determine and store yield value
				if(markups.hasNext()) {
					// Remainder of mark-up chain
					NodeList<Markup> remainder = new NodeList<Markup>();
					while(markups.hasNext()) { remainder.add(markups.next()); }
					MarkupEmbedding embedding = new MarkupEmbedding(remainder);
					embedding.setEmbedding(statement.getEmbedding());
					yield.add(embedding);
				} else {
					// Single remaining embedding
					yield.add(statement.getEmbedding());
				}
			}

			markup.accept(this); // Visit mark-up

			if(markup instanceof Markup.Call) {
				return; // Quit parsing mark-up embedding after first call
			}
		}
		
		// Interpret embedding when mark-up chain is call free
		statement.getEmbedding().accept(this);
		current.setText(text);
	}

	/**
	 * Check if node contains a yield statement, either contained directly in 
	 * the statement collection or indirectly by calling a statement with yield.
	 * @param node
	 * @return
	 */
	private boolean containsYield(AbstractSyntaxNode node) {
		if(node instanceof Statement.Yield) { 
			return true; // Yield found!
		} else if(node instanceof Markup.Call) {
			// Retrieve called function and check if that contains a yield statement
			Markup.Call call = (Markup.Call) node;
			FunctionDef function = functions.get(call.getDesignator().getIdentifier().getName());
			return containsYield(function);
		} else {
			// Delegate check to children
			for(AbstractSyntaxNode child: node.getChildren()) {
				if(containsYield(child)) { return true; }
			}
			
			return false; // Nothing found
		}
	}

	public void visit(FuncBind bind) {
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

	public void visit(VarBind bind) {
		variables.put(bind.getIdentifier().getName(), bind.getExpression());
	}

	public void visit(Call markup) {
		// Retrieve function definition
		FunctionDef function = functions.get(markup.getDesignator().getIdentifier().getName());
		
		// Store function variables and their called values
		int index = 0; 
		for(IdCon identifier: function.getFormals().getIdentifiers()) {
			Expression expression = markup.getArguments().get(index).getExpression();
			variables.put(identifier.toString(), expression);
			index++;
		}
		
		function.accept(this); // Visit function
	}

	public void visit(Tag markup) {
		Element tag = new Element(markup.getDesignator().getIdentifier().getName());
		
		for(Attribute attribute: markup.getDesignator().getAttributes()) {
			if(attribute instanceof Attribute.AttributeIdCon) {
				Attribute.AttributeIdCon id = (Attribute.AttributeIdCon) attribute;
				if(id.getSymbol().toCharacter() == '.') {
					tag.setAttribute("class", id.getIdentifier().getName());
				} else if(id.getSymbol().toCharacter() == '#') {
					tag.setAttribute("id", id.getIdentifier().getName());
				} else if(id.getSymbol().toCharacter() == '$') {
					tag.setAttribute("name", id.getIdentifier().getName());
				} else if(id.getSymbol().toCharacter() == ':') {
					tag.setAttribute("type", id.getIdentifier().getName());
				}
			} else if(attribute instanceof Attribute.AttributeNatCon) {
				Attribute.AttributeNatCon nat = (Attribute.AttributeNatCon) attribute;
				tag.setAttribute("width", nat.getNumber().getLiteral().toString());
			} else if(attribute instanceof Attribute.AttributeDoubleNatCon) {
				Attribute.AttributeDoubleNatCon dnat = (Attribute.AttributeDoubleNatCon) attribute;
				tag.setAttribute("width", dnat.getNumber().getLiteral().toString());
				tag.setAttribute("height", dnat.getSecondNumber().getLiteral().toString());
			}
		}
		
		if(current != null) { current.addContent(tag); }
		else { document.addContent(tag); }
		
		current = tag; // Current element is tag
	}

	public void visit(CatExpression expression) {
		// TODO Auto-generated method stub
		
	}

	/**
	 * Retrieve expression element from record expression, when
	 * undefined or other expression type return "undef".
	 */
	public void visit(Field field) {
		Expression expression = field.getExpression();
		while(expression instanceof Expression.VarExpression) {
			// Browse over variable expressions until a raw type is detected
			Expression.VarExpression var = (Expression.VarExpression) expression;
			expression = variables.get(var.getVar().getName());
		}
		
		if(expression instanceof Expression.RecordExpression) {
			Expression.RecordExpression record = (Expression.RecordExpression) expression;
			
			// Retrieve referenced element from record and visit it
			Expression reference = record.getExpression(field.getIdentifier());
			if(reference == null) { text = "undef"; } // Unknown record element
			reference.accept(this);
		} else { text = "undef"; } // Invalid expression type
	}

	public void visit(ListExpression expression) {
		// TODO Auto-generated method stub
		
	}

	public void visit(NatExpression expression) {
		// TODO Auto-generated method stub
		
	}

	public void visit(RecordExpression expression) {
		// TODO Auto-generated method stub
		
	}

	public void visit(SymbolExpression expression) {
		// TODO Auto-generated method stub
		
	}

	public void visit(TextExpression expression) {
		text = expression.getText().getLiteral().toString();
	}

	public void visit(VarExpression expression) {
		variables.get(expression.getVar().getName()).accept(this);
	}

	public void visit(Embedding embedding) {
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
	
	/**
	 * 
	 * @param name
	 * @param function
	 */
	public void addFunctionDef(String name, FunctionDef function) {
		functions.put(name, function);
	}
	
	/**
	 * 
	 * @param name
	 * @param value
	 */
	public void addVariable(String name, Expression value) {
		variables.put(name, value);
	}
	
}