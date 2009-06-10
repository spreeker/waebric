package org.cwi.waebric.checker;

import java.util.ArrayList;
import java.util.List;

import org.cwi.waebric.parser.ast.AbstractSyntaxNode;
import org.cwi.waebric.parser.ast.basic.IdCon;
import org.cwi.waebric.parser.ast.expression.Expression;
import org.cwi.waebric.parser.ast.module.Module;
import org.cwi.waebric.parser.ast.module.Modules;
import org.cwi.waebric.parser.ast.module.function.Formals;
import org.cwi.waebric.parser.ast.module.function.FunctionDef;
import org.cwi.waebric.parser.ast.statement.Assignment;
import org.cwi.waebric.parser.ast.statement.Statement;

/**
 * Check variables for semantic violations.
 * @see UndefinedVariableException
 * @author Jeroen van Schagen
 * @date 09-06-2009
 */
class VarCheck implements IWaebricCheck {
	
	public void checkAST(Modules modules, List<SemanticException> exceptions) {
		for(Module module: modules) {
			for(FunctionDef function: module.getFunctionDefinitions()) {
				checkVar(function, new ArrayList<IdCon>(), exceptions);
			}
		}
	}
	
	/**
	 * Walk threw AST and determine if each variable expression uses a
	 * variables defined in its parents function definition or let-statement.
	 * @param node Node being checked
	 * @param vars Collection of defined variables
	 * @param exceptions Exceptions currently occurred
	 */
	public void checkVar(AbstractSyntaxNode node, List<IdCon> vars, List<SemanticException> exceptions) {
		if(node instanceof Expression.VarExpression) {
			// Check if expressed variable is defined
			Expression.VarExpression expr = (Expression.VarExpression) node;
			if(! vars.contains(expr.getVar())) {
				exceptions.add(new UndefinedVariableException(expr.getVar()));
			}
		} else if(node instanceof FunctionDef) {
			FunctionDef def = (FunctionDef) node;
			if(def.getFormals() instanceof Formals.RegularFormal) {
				// Attach variables defined in function formals
				Formals.RegularFormal formals = (Formals.RegularFormal) def.getFormals();
				vars.addAll(formals.getIdentifiers());
			}
		} else if(node instanceof Statement.Let) {
			Statement.Let stm = (Statement.Let) node;
			for(Assignment assignment: stm.getAssignments()) {
				if(assignment instanceof Assignment.VarBind) {
					// Attach variables bound in let assignments
					Assignment.VarBind bind = (Assignment.VarBind) assignment;
					vars.add(bind.getIdentifier());
				}
			}
		}
		
		// Recursively check children of node
		for(AbstractSyntaxNode child: node.getChildren()) {
			checkVar(child, new ArrayList<IdCon>(vars), exceptions);
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
			super(var.toString() + " is not defined.");
		}
		
	}

}