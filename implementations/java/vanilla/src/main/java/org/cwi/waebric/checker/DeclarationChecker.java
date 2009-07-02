package org.cwi.waebric.checker;

import java.util.List;

import org.cwi.waebric.ModuleRegister;
import org.cwi.waebric.XHTMLTag;
import org.cwi.waebric.interpreter.Environment;
import org.cwi.waebric.parser.ast.DefaultNodeVisitor;
import org.cwi.waebric.parser.ast.basic.IdCon;
import org.cwi.waebric.parser.ast.expression.Expression.VarExpression;
import org.cwi.waebric.parser.ast.markup.Markup;
import org.cwi.waebric.parser.ast.module.Module;
import org.cwi.waebric.parser.ast.module.function.Formals;
import org.cwi.waebric.parser.ast.module.function.FunctionDef;
import org.cwi.waebric.parser.ast.module.site.Mapping;
import org.cwi.waebric.parser.ast.module.site.Site;
import org.cwi.waebric.parser.ast.statement.Assignment;
import org.cwi.waebric.parser.ast.statement.Statement;
import org.cwi.waebric.parser.ast.statement.Assignment.FuncBind;
import org.cwi.waebric.parser.ast.statement.Assignment.VarBind;
import org.cwi.waebric.parser.ast.statement.Statement.Each;
import org.cwi.waebric.parser.ast.statement.Statement.Let;

public class DeclarationChecker extends DefaultNodeVisitor {

	/**
	 * Current environment
	 */
	private Environment environment;
	
	/**
	 * Exceptions
	 */
	private final List<SemanticException> exceptions;

	/**
	 * Construct checker
	 * @param exceptions
	 */
	public DeclarationChecker(List<SemanticException> exceptions) {
		this.exceptions = exceptions;
		environment = new Environment();
	}
	
	@Override
	public void visit(Module module) {
		// Retrieve all dependent modules and store their definitions
		List<Module> dependancies = ModuleRegister.getInstance().loadDependencies(module);
		
		// Store function definitions
		for(Module component: dependancies) {
			for(FunctionDef function: component.getFunctionDefinitions()) {
				if(environment.containsFunction(function.getIdentifier().getName())) {
					// Function is already defined, store exception
					exceptions.add(new DuplicateFunctionDefinition(function));
				} else {
					// Store definition
					environment.storeFunctionDef(function);
				}
			}
		}
		
		for(Module component: dependancies) {
			// Check each function definition for invalid definitions
			for(FunctionDef function: component.getFunctionDefinitions()) {
				function.accept(this);
			}
			
			// Check each site definition for invalid definitions
			for(Site site: component.getSites()) {
				for(Mapping mapping: site.getMappings()) {
					mapping.getMarkup().accept(this);
				}
			}
		}	
	}
	
	@Override
	public void visit(FunctionDef function) {
		// Store formals in local environment
		environment = new Environment(environment);
		for(IdCon identifier: function.getFormals().getIdentifiers()) {
			environment.storeVariable(identifier.getName(), null);
		}

		for(Statement statement: function.getStatements()) {
			statement.accept(this); // Check statements
		}
		
		// Restore previous environment
		environment = environment.getParent();
	}
	
	@Override
	public void visit(Each statement) {
		environment = new Environment(environment); // Store variable in local environment
		environment.storeVariable(statement.getVar().getName(), statement.getExpression());
		statement.getStatement().accept(this); // Visit sub-statement
		environment = environment.getParent(); // Restore previous environment
	}
	
	@Override
	public void visit(Let statement) {
		// Create local environment for each assignment
		for(Assignment assignment: statement.getAssignments()) {
			environment = new Environment(environment);
			assignment.accept(this); // Alter current environment with assignment
		}
		
		// Visit sub-statement(s)
		for(Statement sub: statement.getStatements()) {
			sub.accept(this);
		}
		
		// Restore previous environment
		for(int i = 0; i < statement.getAssignments().size(); i++) {
			environment = environment.getParent();
		}
	}
	
	@Override
	public void visit(FuncBind bind) {
		// Convert function binding in function definition
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

		definition.accept(this); // Check internal function
		environment.storeFunctionDef(definition); // Store definition
	}
	
	@Override
	public void visit(VarBind bind) {
		bind.getExpression().accept(this); // Check expression
		environment.storeVariable(bind.getIdentifier().getName(), bind.getExpression());
	}
	
	@Override
	public void visit(VarExpression expression) {
		if(! environment.containsVariable(expression.getVar().getName())) {
			exceptions.add(new UndefinedVariableException(expression.getVar()));
		}
	}
	
	@Override
	public void visit(Markup.Tag tag) {
		if(environment.containsFunction(tag.getDesignator().getIdentifier().getName())) {
			new Markup.Call(tag.getDesignator()).accept(this);
		}
	}
	
	@Override
	public void visit(Markup.Call call) {
		String name = call.getDesignator().getIdentifier().getName();
		
		// Check if call is made to a defined function
		if(environment.containsFunction(name)) {
			FunctionDef definition = environment.getFunction(name);
			
			// Determine expected arguments by counting function formals
			int expectedArguments = definition.getFormals().getIdentifiers().size();
			
			// Compare expected to actual argument count in call
			if(call.getArguments().size() != expectedArguments) {
				exceptions.add(new ArityMismatchException(call));
			}
		} else {
			// When called function is not defined and is not a XHTML tag, report undefined function
			if(! isXHTMLTag(call.getDesignator().getIdentifier())) {
				exceptions.add(new UndefinedFunctionException(call));
			}
		}
	}
	
	public Environment getEnvironment() {
		return environment;
	}
	
	public static boolean isXHTMLTag(IdCon identifier) {
		if(identifier.getToken() == null || identifier.getToken().getLexeme() == null) { return false; }
		String tag = identifier.getToken().getLexeme().toString();
		try {
			return XHTMLTag.valueOf(tag.toUpperCase()) != null;
		} catch(IllegalArgumentException e) {
			return false;
		}
	}
	
	/**
	 * If a function is called with an incorrect number of arguments 
	 * (as follows from its definition), this is an error.
	 * 
	 * @author Jeroen van Schagen
	 * @date 09-06-2009
	 */
	public class ArityMismatchException extends SemanticException {

		/**
		 * Generated serial ID
		 */
		private static final long serialVersionUID = -954167103131401047L;

		public ArityMismatchException(Markup.Call call) {
			super("Call \"" + call.getDesignator().getIdentifier().getToken().getLexeme().toString()
					+ "\" at line " + call.getDesignator().getIdentifier().getToken().getLine()
					+ ", is an arity mismatch.");
		}
		
	}
	
	/**
	 * Multiple function definitions with the same name are disallowed.
	 * 
	 * @author Jeroen van Schagen
	 * @date 09-06-2009
	 */
	public class DuplicateFunctionDefinition extends SemanticException {

		/**
		 * Generated serial ID
		 */
		private static final long serialVersionUID = -8833578229100261366L;

		public DuplicateFunctionDefinition(FunctionDef def) {
			super("Function \"" + def.getIdentifier().getToken().getLexeme().toString()
					+ "\" at line " + def.getIdentifier().getToken().getLine()
					+ " has a duplicate definition.");
		}
		
	}
	
	/**
	 * If for a markup-call (f) no function definition can be found in 
	 * the current module or one of its (transitive) imports, and, if 
	 * f is not a tag defined in the XHTML 1.0 Transitional standard, 
	 * then this is an error. [f will be interpreted as if it was part 
	 * of XHTML 1.0 transitional.]
	 * 
	 * @author Jeroen van Schagen
	 * @date 09-06-2009
	 */
	public class UndefinedFunctionException extends SemanticException {

		/**
		 * Generated serial ID
		 */
		private static final long serialVersionUID = -4467095005921534334L;

		public UndefinedFunctionException(Markup.Call call) {
			super("Call \"" + call.getDesignator().getIdentifier().getToken().getLexeme().toString()
					+ "\" at line " + call.getDesignator().getIdentifier().getToken().getLine()
					+ ", is made to an undefined function.");
		}
		
	}
	
	/**
	 * If a variable reference x cannot be traced back to an enclosing 
	 * let-definition or function parameter, this is an error. [The 
	 * variable x will be undefined and evaluate to the string “undef”.]
	 * 
	 * @author Jeroen van Schagen
	 * @date 09-06-2009
	 */
	public class UndefinedVariableException extends SemanticException {

		/**
		 * Generated serial ID
		 */
		private static final long serialVersionUID = 3043727441105977011L;

		public UndefinedVariableException(IdCon var) {
			super("Variable \"" + var.getToken().getLexeme().toString()
					+ "\" at line " + var.getToken().getLine()
					+ " is not defined.");
		}
		
	}
	
}