package org.cwi.waebric.checker;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.cwi.waebric.XHTMLTag;
import org.cwi.waebric.checker.exception.ArityMismatchException;
import org.cwi.waebric.checker.exception.DuplicateFunctionDefinition;
import org.cwi.waebric.checker.exception.NonExistingModuleException;
import org.cwi.waebric.checker.exception.SemanticException;
import org.cwi.waebric.checker.exception.UndefinedFunctionException;
import org.cwi.waebric.checker.exception.UndefinedVariableException;
import org.cwi.waebric.parser.ast.AbstractSyntaxTree;
import org.cwi.waebric.parser.ast.DefaultNodeVisitor;
import org.cwi.waebric.parser.ast.basic.IdCon;
import org.cwi.waebric.parser.ast.expression.Expression.VarExpression;
import org.cwi.waebric.parser.ast.markup.Markup;
import org.cwi.waebric.parser.ast.module.Module;
import org.cwi.waebric.parser.ast.module.ModuleId;
import org.cwi.waebric.parser.ast.module.function.Formals;
import org.cwi.waebric.parser.ast.module.function.FunctionDef;
import org.cwi.waebric.parser.ast.module.site.Mapping;
import org.cwi.waebric.parser.ast.statement.Assignment;
import org.cwi.waebric.parser.ast.statement.Statement;
import org.cwi.waebric.parser.ast.statement.Assignment.FuncBind;
import org.cwi.waebric.parser.ast.statement.Assignment.VarBind;
import org.cwi.waebric.parser.ast.statement.Statement.Each;
import org.cwi.waebric.parser.ast.statement.Statement.Let;
import org.cwi.waebric.util.Environment;
import org.cwi.waebric.util.ModuleRegister;

public class WaebricChecker extends DefaultNodeVisitor {

	/**
	 * Current environment
	 */
	private Environment environment;
	
	/**
	 * Exceptions
	 */
	private final List<SemanticException> exceptions = new ArrayList<SemanticException>();

	/**
	 * Check ast for semantic exceptions.
	 * @param ast Abstract Syntax Tree
	 * @return List of semantic exceptions
	 */
	public List<SemanticException> checkAST(AbstractSyntaxTree ast) {
		this.exceptions.clear();
		this.environment = new Environment();
		
		// Retrieve all function definitions
		List<Module> dependancies = ModuleRegister.getInstance().loadDependencies(ast.getRoot());
		for(Module component: dependancies) {
			for(FunctionDef function: component.getFunctionDefinitions()) {
				if(environment.isDefinedFunction(function.getIdentifier().getName())) {
					// Function is already defined, store exception
					exceptions.add(new DuplicateFunctionDefinition(function));
				} else { environment.defineFunction(function); }
			}
		}
		
		// Check module and dependencies
		for(Module component: dependancies) {
			this.visit(component);
		}
		
		return exceptions;
	}
	
	@Override
	public void visit(ModuleId identifier) {
		String path = ModuleRegister.getPath(identifier);
		File file = new File(path);
		if(! file.isFile()) {
			exceptions.add(new NonExistingModuleException(identifier));
		}
	}
	
	@Override
	public void visit(Mapping mapping) {
		mapping.getMarkup().accept(this); // Skip path
	}
	
	@Override
	public void visit(FunctionDef function) {
		// Store formals in local environment
		environment = new Environment(environment);
		for(IdCon identifier: function.getFormals().getIdentifiers()) {
			environment.defineVariable(identifier.getName(), null);
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
		environment.defineVariable(statement.getVar().getName(), statement.getExpression());
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
		environment.defineFunction(definition); // Store definition
	}
	
	@Override
	public void visit(VarBind bind) {
		bind.getExpression().accept(this); // Check expression
		environment.defineVariable(bind.getIdentifier().getName(), bind.getExpression());
	}
	
	@Override
	public void visit(VarExpression expression) {
		if(! environment.isDefinedVariable(expression.getId().getName())) {
			exceptions.add(new UndefinedVariableException(expression.getId()));
		}
	}
	
	@Override
	public void visit(Markup.Tag tag) {
		if(environment.isDefinedFunction(tag.getDesignator().getIdentifier().getName())) {
			new Markup.Call(tag.getDesignator()).accept(this);
		}
	}
	
	@Override
	public void visit(Markup.Call call) {
		String name = call.getDesignator().getIdentifier().getName();
		
		// Check if call is made to a defined function
		if(environment.isDefinedFunction(name)) {
			FunctionDef definition = environment.getFunction(name);
			
			// Determine expected arguments by counting function formals
			int expectedArguments = definition.getFormals().getIdentifiers().size();
			
			// Compare expected to actual argument count in call
			if(call.getArguments().size() != expectedArguments) {
				exceptions.add(new ArityMismatchException(call));
			}
		} else {
			// When called function is not defined and is not a XHTML tag, report undefined function
			if(! XHTMLTag.isXHTMLTag(call.getDesignator().getIdentifier().getName())) {
				exceptions.add(new UndefinedFunctionException(call));
			}
		}
	}
	
}