package org.cwi.waebric.interpreter;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Stack;

import org.cwi.waebric.parser.ast.NullVisitor;
import org.cwi.waebric.parser.ast.SyntaxNode;
import org.cwi.waebric.parser.ast.SyntaxNodeList;
import org.cwi.waebric.parser.ast.basic.IdCon;
import org.cwi.waebric.parser.ast.expression.Expression;
import org.cwi.waebric.parser.ast.expression.Expression.Field;
import org.cwi.waebric.parser.ast.markup.Argument;
import org.cwi.waebric.parser.ast.markup.Arguments;
import org.cwi.waebric.parser.ast.markup.Attribute;
import org.cwi.waebric.parser.ast.markup.Markup;
import org.cwi.waebric.parser.ast.markup.Markup.Call;
import org.cwi.waebric.parser.ast.markup.Markup.Tag;
import org.cwi.waebric.parser.ast.module.function.Formals;
import org.cwi.waebric.parser.ast.module.function.FunctionDef;
import org.cwi.waebric.parser.ast.statement.Assignment;
import org.cwi.waebric.parser.ast.statement.Statement;
import org.cwi.waebric.parser.ast.statement.Assignment.FuncBind;
import org.cwi.waebric.parser.ast.statement.Assignment.VarBind;
import org.cwi.waebric.parser.ast.statement.Statement.MarkupStatement;
import org.cwi.waebric.parser.ast.statement.Statement.MarkupsEmbedding;
import org.cwi.waebric.parser.ast.statement.Statement.MarkupsExpression;
import org.cwi.waebric.parser.ast.statement.Statement.MarkupsMarkup;
import org.cwi.waebric.parser.ast.statement.Statement.MarkupsStatement;
import org.cwi.waebric.parser.ast.statement.embedding.Embed;
import org.cwi.waebric.parser.ast.statement.embedding.Embedding;
import org.cwi.waebric.parser.ast.statement.embedding.TextTail;
import org.cwi.waebric.util.Environment;
import org.jdom.CDATA;
import org.jdom.Comment;
import org.jdom.Content;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.Text;

/**
 * Convert AST in JDOM format, the eventual XHTML document is generated using
 * the JDOM libraries. During conversion the visitor pattern is applied to
 * minimize the need for node casts and result in more readable code.
 * @author Jeroen van Schagen
 * @date 11-06-2009
 */
public class WaebricEvaluator extends NullVisitor<Object> {

	private final Document document;
	private Element current;
	private int depth = 0;
	
	private Stack<Yieldable> yield;
	private Environment environment;
	private final Map<FunctionDef, Environment> functionEnvironments;

	private final ExpressionEvaluator expressionEvaluator;
	private final PredicateEvaluator predicateEvaluator;
	
	/**
	 * Construct JDOM visitor
	 * @param document
	 */
	public WaebricEvaluator(Document document) {
		this(document, new Environment());
	}
	
	/**
	 * Construct JDOM visitor
	 * @param document Document
	 */
	public WaebricEvaluator(Document document, Environment environment) {
		this.document = document;
		this.environment = environment;
		
		yield = new Stack<Yieldable>();
		
		functionEnvironments = new HashMap<FunctionDef, Environment>();
		expressionEvaluator = new ExpressionEvaluator(this);
		predicateEvaluator = new PredicateEvaluator(this);
	}

	/**
	 * Attach content to current element, in-case element
	 * does not exist create root element.
	 * @param content
	 */
	private void addContent(Content content) {
		// Update JDOM objects
		if(current == null) {
			if(content instanceof Element) {
				document.setRootElement((Element) content);
			} else {
				createXHTMLRoot(false);
				document.getRootElement().addContent(content);
			}
		} else { current.addContent(content); }

		// Maintain field information
		if(content instanceof Element) {
			current = (Element) content;
			depth++;
		}
	}
	
	/**
	 * Create default XHTML root tag
	 * @return
	 */
	private void createXHTMLRoot(boolean namespace) {
		Element html;
		if(namespace) {
			Namespace XHTML = Namespace.getNamespace("xhtml", "http://www.w3.org/1999/xhtml");
			html = new Element("html", XHTML);
			html.setAttribute("lang", "en");
		} else { html = new Element("html"); }
		document.setRootElement(html);
		current = html;
	}
	
	/**
	 * Delegate interpretation to called function, in case the called function does 
	 * not exists the call is interpreted as a mark-up tag and its arguments as attributes.
	 */
	public Object visit(Call markup) {
		String name = markup.getDesignator().getIdentifier().getName();
		if(environment.isDefinedFunction(name)) { // Call to defined function
			FunctionDef function = environment.getFunction(name); // Retrieve function definition
			
			// Create new environment for function
			Environment previous = environment; // Store previous environment
			environment = new Environment(getEnvironment(function)); 
			
			// Store function variables
			int index = 0; 
			Arguments arguments = markup.getArguments();
			for(IdCon formal: function.getFormals().getIdentifiers()) {
				Expression expression = null;
				
				if(arguments.size() > index) {
					expression = arguments.get(index).getExpression();
				}
				
				environment.defineVariable(formal.getName(), expression);
				index++;
			}
			
			function.accept(this); // Visit function
			
			// Restore parent environment
			environment = previous;
		} else { // Call to undefined function
			// Delegate mark-up as tag
			new Tag(markup.getDesignator()).accept(this);

			// Interpret arguments as attributes
			String value = ""; // Value attribute
			for(Argument argument: markup.getArguments()) {
				if(argument instanceof Argument.RegularArgument) {
					// The combined value of regular arguments are stored as value attribute
					String eval = argument.getExpression().accept(expressionEvaluator);
					if(eval.equals("undef")) { eval = "UNDEFINED"; } // Convert undef to UNDEFINED to match reference impl
					if(! value.equals("") && ! eval.equals("")) { value += " "; } // Attach separator
					value += eval; // Store text in value
				} else if(argument instanceof Argument.Attr) {
					// Attribute arguments are stored as separate attribute
					String attribute = ((Argument.Attr) argument).getIdentifier().getName();
					String eval = argument.getExpression().accept(expressionEvaluator);
					if(attribute.equals("xmlns")) {
						current.setNamespace(Namespace.getNamespace("xhtml", "http://www.w3.org/1999/xhtml"));
					} else {
						addAttributeValue(attribute, eval);
					}
				}
			}
			
			// Store value attribute
			if(! value.equals("")) { current.setAttribute("value", value); }
		}
		
		return null;
	}

	/**
	 * Create next element in JDOM tree structure for tag. Attach additional attributes
	 * when these are specified in the tag mark-up.
	 */
	public Object visit(Tag markup) {
		String name = markup.getDesignator().getIdentifier().getName();
		if(environment.isDefinedFunction(name)) { // Call to defined function
			// Delegate mark-up as call
			new Markup.Call(markup.getDesignator()).accept(this);
		} else {
			Element tag = new Element(name);
			addContent(tag); // Store tag as element in JDOM structure
			visit(markup.getDesignator().getAttributes()); // Process attributes
		}
		
		return null;
	}
	
	/**
	 * Attach attribute value to current element
	 * @param att Attribute name
	 * @param value Added value
	 */
	private void addAttributeValue(String att, String value) {
		org.jdom.Attribute attribute = current.getAttribute(att);
		
		String actual = "";
		if(attribute != null) { actual = attribute.getValue() + " "; }
		current.setAttribute(att, actual + value);
	}

	/**
	 * Store class attribute.
	 */
	public Object visit(Attribute.ClassAttribute attribute) {
		addAttributeValue("class", attribute.getIdentifier().getName());
		return null;
	}
	
	/**
	 * Store id attribute.
	 */
	public Object visit(Attribute.IdAttribute attribute) {
		addAttributeValue("id", attribute.getIdentifier().getName());
		return null;
	}
	
	/**
	 * Store name attribute.
	 */
	public Object visit(Attribute.NameAttribute attribute) {
		addAttributeValue("name", attribute.getIdentifier().getName());
		return null;
	}
	
	/**
	 * Store type attribute.
	 */
	public Object visit(Attribute.TypeAttribute attribute) {
		addAttributeValue("type", attribute.getIdentifier().getName());
		return null;
	}
	
	/**
	 * Store width attribute.
	 */
	public Object visit(Attribute.WidthAttribute attribute) {
		addAttributeValue("width", "" + attribute.getWidth().getValue());
		return null;
	}
	
	/**
	 * Store width and height attribute.
	 */
	public Object visit(Attribute.WidthHeightAttribute attribute) {
		addAttributeValue("width", "" + attribute.getWidth().getValue());
		addAttributeValue("height", "" + attribute.getHeight().getValue());
		return null;
	}
	
	/**
	 * Determine root element and visit each statement.
	 */
	public Object visit(FunctionDef function) {	
		// Construct XHTML tag when multiple statements can be root
		if(function.getStatements().size() > 1 && ! document.hasRootElement()) { 
			createXHTMLRoot(false);
		}
		
		// Process statement(s)
		int depth = this.depth;
		for(Statement statement: function.getStatements()) {
			statement.accept(this);
			restoreCurrent(depth);
		}
		
		return null;
	}

	/**
	 * Evaluate predicate and potentially execute statement based on outcome.
	 */
	public Object visit(Statement.If statement) {
		Boolean eval = statement.getPredicate().accept(predicateEvaluator);
		if(eval) {	statement.getTrueStatement().accept(this); }
		return null;
	}

	/**
	 * Evaluate predicate and execute either of the statements based on outcome.
	 */
	public Object visit(Statement.IfElse statement) {
		Boolean eval = statement.getPredicate().accept(predicateEvaluator);
		if(eval) { statement.getTrueStatement().accept(this); } 
		else { statement.getFalseStatement().accept(this); }
		return null;
	}
	


	/**
	 * Interpret all sub-statements embedded in block.
	 */
	public Object visit(Statement.Block statement) {
		if(statement.getStatements().size() > 1 && ! document.hasRootElement()) { 
			createXHTMLRoot(false);
		}
		
		int depth = this.depth;
		for(Statement sub: statement.getStatements()) {
			sub.accept(this);
			restoreCurrent(depth);
		}
		
		return null;
	}

	/**
	 * Execute statement for each element in list expression, also
	 * create a local element variable during each loop.
	 */
	public Object visit(Statement.Each statement) {
		if(! document.hasRootElement()) { 
			createXHTMLRoot(false);
		}
		
		Expression expression = statement.getExpression();
		if(expression instanceof Expression.ListExpression) {
			Expression.ListExpression list = (Expression.ListExpression) expression;
			
			// Execute statement for each element
			int depth = this.depth;
			for(Expression e: list.getExpressions()) {
				environment = new Environment(environment);
				environment.defineVariable(statement.getVar().getName(), e);
				statement.getStatement().accept(this);
				environment = environment.getParent();
				restoreCurrent(depth);
			}
		} else if(expression instanceof Expression.VarExpression) {
			Expression.VarExpression var = (Expression.VarExpression) expression;
			
			// Retrieve actual expression from variable cache
			expression = environment.getVariable(var.getId().getName());
			
			// Execute function again
			Statement.Each each = new Statement.Each();
			each.setVar(statement.getVar());
			each.setStatement(statement.getStatement());
			each.setExpression(expression);
			visit(each);
		} else if(expression instanceof Expression.Field) {
			Field field = (Expression.Field) expression;
			expression = ExpressionEvaluator.getFieldExpression(field, environment);
			
			// Execute function again
			Statement.Each each = new Statement.Each();
			each.setVar(statement.getVar());
			each.setStatement(statement.getStatement());
			each.setExpression(expression);
			visit(each);
		}
		
		return null;
	}
	
	/**
	 * Attach <!-- COMMENT --> to current element.
	 */
	public Comment visit(Statement.Comment statement) {
		Comment comment = new Comment(statement.getComment().getLiteral().toString());
		addContent(comment);
		return comment;
	}
	
	/**
	 * Attach <-CDATA TEXT -> to current element.
	 */
	public CDATA visit(Statement.CData statement) {
		String eval = statement.getExpression().accept(expressionEvaluator);
		CDATA cdata = new CDATA(eval);
		addContent(cdata);
		return cdata;
	}

	/**
	 * Attach text to current element.
	 */
	public Object visit(Statement.Echo statement) {
		String eval = statement.getExpression().accept(expressionEvaluator);
		addContent(new Text(eval));
		return null;
	}

	/**
	 * Attach text to current element.
	 */
	public Object visit(Statement.EchoEmbedding statement) {
		statement.getEmbedding().accept(this);
		return null;
	}

	/**
	 * Extend definition with assignments and execute statements, remove
	 * definitions after completion of the let statement.
	 */
	public Object visit(Statement.Let statement) {
		// Create new environment for each assignment
		for(Assignment assignment: statement.getAssignments()) {
			environment = new Environment(environment);
			assignment.accept(this);
		}
		
		if(statement.getStatements().size() > 1 && ! document.hasRootElement()) { 
			createXHTMLRoot(false);
		}
		
		// Visit sub-statements
		int depth = this.depth;
		for(Statement sub: statement.getStatements()) {
			sub.accept(this);
			restoreCurrent(depth);
		}
		
		// Restore previous state by removing each assignment environment
		for(int i = 0; i < statement.getAssignments().size(); i++) {
			environment = environment.getParent();
		}
		
		return null;
	}

	/**
	 * Retrieve first element from yield stack and visit it.
	 */
	public Object visit(Statement.Yield statement) {
		if(yield.isEmpty()) { return null; }
		Yieldable e = yield.pop(); // Retrieve replacement

		if(e != null && e.root != null) {
			// Clone yield stack
			Stack<Yieldable> clone = new Stack<Yieldable>();
			clone.addAll(yield);
			
			Environment actual = environment.clone();
		    environment = e.environment;

		    // Visit replacement
			if(e.root instanceof Expression) {
				String eval = e.root.accept(expressionEvaluator);
				addContent(new Text(eval));
			} else { e.root.accept(this); }
			
			environment = actual; // Restore environment
			yield = clone; // Restore yield stack
		}
		
		return null;
	}

	/**
	 * Interpret mark-up embedded in statement.
	 */
	public Object visit(MarkupStatement statement) {
		if(isCall(statement.getMarkup())) {
			String name = statement.getMarkup().getDesignator().getIdentifier().getName();
			if(containsYield(environment.getFunction(name))) {
				yield.add(null); // Place empty yield element in stack
			}
		}
		
		statement.getMarkup().accept(this);
		return null;
	}

	/**
	 * Interpret mark-ups embedded in statement.
	 */
	public Object visit(MarkupsMarkup statement) {
		Iterator<Markup> markups = statement.getMarkups().iterator();
		while(markups.hasNext()) {
			Markup markup = markups.next(); 
			
			if(isCall(markup)) {
				// Calls to functions with yield, store the remainder of mark-up chain as argument
				String name = markup.getDesignator().getIdentifier().getName();
				if(containsYield(environment.getFunction(name))) {
					// Remainder of mark-up chain is stored as yield statement
					SyntaxNodeList<Markup> remainder = new SyntaxNodeList<Markup>();
					while(markups.hasNext()) { remainder.add(markups.next()); }
					MarkupsMarkup replacement = new MarkupsMarkup(remainder);
					replacement.setMarkup(statement.getMarkup());
					addYield(replacement);
				}
				markup.accept(this); // Interpret call
				return null; // Quit interpreting after call
			} else {
				markup.accept(this); // Interpret tag
			}
		}
		
		// Interpret element when mark-up chain is call free
		statement.getMarkup().accept(this);
		return null;
	}
	
	/**
	 * Check if mark-up is a call, and calls to a valid function.
	 * @param markup
	 */
	public boolean isCall(Markup markup) {
		String name = markup.getDesignator().getIdentifier().getName();
		return environment.getFunction(name) != null;
	}
	
	/**
	 * Check if node contains a yield statement, either contained directly in 
	 * the statement collection or indirectly by calling a statement with yield.
	 * @param node
	 * @return
	 */
	private boolean containsYield(SyntaxNode node) {
		if(node instanceof Statement.Yield) { return true; }
		else {
			// Delegate check to children
			for(SyntaxNode child: node.getChildren()) {
				if(containsYield(child)) { return true; }
			} return false; // Nothing found
		}
	}

	/**
	 * Interpret mark-ups and expression embedded in statement.
	 */
	public Object visit(MarkupsExpression statement) {
		Iterator<Markup> markups = statement.getMarkups().iterator();
		while(markups.hasNext()) {
			Markup markup = markups.next(); 
			
			if(isCall(markup)) {
				// Calls to functions with yield, store the remainder of mark-up chain as argument
				String name = markup.getDesignator().getIdentifier().getName();
				if(containsYield(environment.getFunction(name))) {
					// Remainder of mark-up chain is stored as yield statement
					SyntaxNodeList<Markup> remainder = new SyntaxNodeList<Markup>();
					while(markups.hasNext()) { remainder.add(markups.next()); }
					MarkupsExpression replacement = new MarkupsExpression(remainder);
					replacement.setExpression(statement.getExpression());
					addYield(replacement);
				}
				markup.accept(this); // Interpret call
				return null; // Quit interpreting after call
			} else {
				markup.accept(this); // Interpret tag
			}
		}
		
		// Interpret element when mark-up chain is call free
		String eval = statement.getExpression().accept(expressionEvaluator);
		addContent(new Text(eval));
		return null;
	}

	/**
	 * Interpret mark-ups and sub-statement embedded in statement.
	 */
	public Object visit(MarkupsStatement statement) {
		Iterator<Markup> markups = statement.getMarkups().iterator();
		while(markups.hasNext()) {
			Markup markup = markups.next(); 
			
			if(isCall(markup)) {
				// Calls to functions with yield, store the remainder of mark-up chain as argument
				String name = markup.getDesignator().getIdentifier().getName();
				if(containsYield(environment.getFunction(name))) {
					// Remainder of mark-up chain is stored as yield statement
					SyntaxNodeList<Markup> remainder = new SyntaxNodeList<Markup>();
					while(markups.hasNext()) { remainder.add(markups.next()); }
					MarkupsStatement replacement = new MarkupsStatement(remainder);
					replacement.setStatement(statement.getStatement());
					addYield(replacement);
				}
				markup.accept(this); // Interpret call
				return null; // Quit interpreting after call
			} else {
				markup.accept(this); // Interpret tag
			}
		}
		
		// Interpret element when mark-up chain is call free
		statement.getStatement().accept(this);
		return null;
	}

	/**
	 * Interpret mark-ups and embedding embedded in statement.
	 */
	public Object visit(MarkupsEmbedding statement) {
		Iterator<Markup> markups = statement.getMarkups().iterator();
		while(markups.hasNext()) {
			Markup markup = markups.next(); 
			
			if(isCall(markup)) {
				// Calls to functions with yield, store the remainder of mark-up chain as argument
				String name = markup.getDesignator().getIdentifier().getName();
				if(containsYield(environment.getFunction(name))) {
					// Remainder of mark-up chain is stored as yield statement
					SyntaxNodeList<Markup> remainder = new SyntaxNodeList<Markup>();
					while(markups.hasNext()) { remainder.add(markups.next()); }
					MarkupsEmbedding replacement = new MarkupsEmbedding(remainder);
					replacement.setEmbedding(statement.getEmbedding());
					addYield(replacement);
				}
				markup.accept(this); // Interpret call
				return null; // Quit interpreting after call
			} else {
				markup.accept(this); // Interpret tag
			}
		}
		
		// Interpret element when mark-up chain is call free
		statement.getEmbedding().accept(this);
		return null;
	}

	/**
	 * Extend function definitions with binding.
	 */
	public Object visit(FuncBind bind) {
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
			definition.setFormals(formals);
		}

		// Store hard-copy of current environment
		functionEnvironments.put(definition, environment.clone()); 
		
		// Extend current environment with new function definition
		environment.defineFunction(definition);
		return null;
	}

	/**
	 * Extend current variable definition with binding, in case variable
	 * already exists its value will be overwritten.
	 */
	public Object visit(VarBind bind) {
		environment.defineVariable(bind.getIdentifier().getName(), bind.getExpression());
		return null;
	}

	/**
	 * Attach pretext to current element and delegate interpret to embed and tail.
	 */
	public Object visit(Embedding embedding) {
		// Attach pretext to current element
		Text pre = new Text(embedding.getPre().getText().toString());
		addContent(pre);
		
		embedding.getEmbed().accept(this); // Delegate embed
		embedding.getTail().accept(this); // Delegate tail
		
		return null;
	}
	
	/**
	 * Interpret mark-ups and expression.
	 * @param embed
	 */
	public Object visit(Embed.ExpressionEmbed embed) {
		int depth = this.depth;

		// Interpret similar to mark-up expression
		Statement.MarkupsExpression stm = new Statement.MarkupsExpression(embed.getMarkups());
		stm.setExpression(embed.getExpression());
		stm.accept(this);
		
		restoreCurrent(depth);
		return null;
	}
	
	/**
	 * Interpret mark-ups embedded in embed.
	 * @param embed
	 */
	public Object visit(Embed.MarkupEmbed embed) {
		int depth = this.depth;

		// Interpret similar to mark-up mark-up
		Statement.MarkupsMarkup stm = new Statement.MarkupsMarkup(embed.getMarkups().subList(1, embed.getMarkups().size()));
		stm.setMarkup(embed.getMarkups().get(0));
		stm.accept(this);
		
		restoreCurrent(depth);
		return null;
	}
	
	/**
	 * Interpret mid text while visiting embed and tail.
	 */
	public Object visit(TextTail.MidTail tail) {
		// Attach mid text to current element
		Text mid = new Text(tail.getMid().getText().toString());
		addContent(mid);
		
		tail.getEmbed().accept(this);
		tail.getTail().accept(this);
		return null;
	}
	
	/**
	 * Store post text.
	 */
	public Object visit(TextTail.PostTail tail) {
		// Attach post text to current element
		Text post = new Text(tail.getPost().getText().toString());
		addContent(post);
		return null;
	}
	
	/**
	 * Retrieve JDOM document.
	 */
	public Document getDocument() {
		return document;
	}

	/**
	 * Retrieve current JDOM element.
	 * @return
	 */
	public Element getCurrent() {
		return current;
	}
	
	/**
	 * Modify current JDOM element.
	 * @param current
	 */
	public void setCurrent(Element current) {
		if(! document.hasRootElement()) { document.setRootElement(current); }
		this.current = current;
	}
	
	/**
	 * 
	 * @param statement
	 */
	public void addYield(SyntaxNode node) {
		Yieldable element = new Yieldable();
		element.root = node;
		element.environment = this.environment.clone();
		yield.push(element);
	}
	
	/**
	 * Return environment.
	 * @return
	 */
	public Environment getEnvironment() {
		return environment;
	}
	
	private void restoreCurrent(int arg) {
		for(int i = 0; depth > arg; i++) {
			current = current.getParentElement();
			depth--;
		}
	}
	
	/**
	 * Return environment specific to function.
	 * @param function
	 * @return
	 */
	private Environment getEnvironment(FunctionDef function) {
		if(functionEnvironments.containsKey(function)) { return functionEnvironments.get(function); }
		return environment;
	}
	
}