package org.cwi.waebric.checker;

import java.util.List;

import org.cwi.waebric.parser.ast.basic.IdCon;
import org.cwi.waebric.parser.ast.module.Modules;

/**
 * Check variables for semantic violations.
 * @see UndefinedVariableException
 * @author Jeroen van Schagen
 * @date 09-06-2009
 */
class VarCheck implements IWaebricCheck {
	
	public void checkAST(Modules modules, List<SemanticException> exceptions) {
		// TODO Auto-generated method stub
		
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