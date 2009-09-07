package org.cwi.waebric.interpreter;

import org.cwi.waebric.parser.ast.NullVisitor;
import org.cwi.waebric.parser.ast.expression.Expression;
import org.cwi.waebric.parser.ast.expression.KeyValuePair;
import org.cwi.waebric.parser.ast.expression.Expression.CatExpression;
import org.cwi.waebric.parser.ast.expression.Expression.Field;
import org.cwi.waebric.parser.ast.expression.Expression.ListExpression;
import org.cwi.waebric.parser.ast.expression.Expression.NatExpression;
import org.cwi.waebric.parser.ast.expression.Expression.RecordExpression;
import org.cwi.waebric.parser.ast.expression.Expression.SymbolExpression;
import org.cwi.waebric.parser.ast.expression.Expression.TextExpression;
import org.cwi.waebric.parser.ast.expression.Expression.VarExpression;
import org.cwi.waebric.util.Environment;

public class ExpressionEvaluator extends NullVisitor<String> {

	private WaebricEvaluator evaluator;
	
	public ExpressionEvaluator(WaebricEvaluator evaluator) {
		this.evaluator = evaluator;
	}
	
	/**
	 * Delegate visit to variable value.
	 */
	public String visit(VarExpression expression) {
		String name = expression.getId().getName();
		
		// If reference to self , return parent definition
		Expression reference = evaluator.getEnvironment().getVariable(name);
		if(reference == expression && evaluator.getEnvironment().getParent() != null) {
			reference = evaluator.getEnvironment().getParent().getVariable(name);
		}
		
		if(reference != null) {
			return reference.accept(this);
		} else {
			return "undef";
		}
	}
	

	/**
	 * Execute left and right expression after each other.
	 */
	public String visit(CatExpression expression) {
		String result = expression.getLeft().accept(this);
		result += expression.getRight().accept(this);
		return result;
	}

	/**
	 * Retrieve expression element from record expression, when
	 * undefined or other expression type return "undef".
	 */
	public String visit(Field field) {
		Expression expr = getFieldExpression(field, evaluator.getEnvironment());
		if(expr == null) { return "undef"; }
		return expr.accept(this);
	}

	/**
	 * Convert list in [element1,element2,...] text value
	 */
	public String visit(ListExpression expression) {
		String result = "[";
		
		for(Expression sub: expression.getExpressions()) {
			// Attach a comma separator in front of each element, except first in list
			if(expression.getExpressions().indexOf(sub) != 0) { 
				result += ",";
			}
			
			// Surround symbol and text expressions between double quotes
			if(sub instanceof SymbolExpression || sub instanceof TextExpression) { 
				result += "\"";
			}
			
			 // Fill text field with expression value
			result += sub.accept(this);
			
			// Surround symbol and text expressions between double quotes
			if(sub instanceof SymbolExpression || sub instanceof TextExpression) { 
				result += "\""; 
			}
		}
		
		result += "]";
		return result;
	}

	/**
	 * Convert record in [id1:expr1,id2:expr2,...] text value
	 */
	public String visit(RecordExpression expression) {
		String result = "[";
		
		for(KeyValuePair pair: expression.getPairs()) {
			// Attach a comma separator in front of each element, except first in list
			if(expression.getPairs().indexOf(pair) != 0) { 
				result += ","; 
			}
			
			result += pair.getIdentifier().getName() + ":";
			
			// Surround symbol and text expressions between double quotes
			if(pair.getExpression() instanceof SymbolExpression || pair.getExpression() instanceof TextExpression) { 
				result += "\"";
			}
			
			 // Fill text field with expression value
			result += pair.getExpression().accept(this); 
			
			// Surround symbol and text expressions between double quotes
			if(pair.getExpression() instanceof SymbolExpression || pair.getExpression() instanceof TextExpression) { 
				result += "\"";
			}
		}
		
		result += "]";
		return result;
	}
	
	/**
	 * Store number in text field.
	 */
	public String visit(NatExpression expression) {
		return "" + expression.getNatural().getValue();
	}

	/**
	 * Store symbol name in text field.
	 */
	public String visit(SymbolExpression expression) {
		return expression.getSymbol().getName().toString();
	}

	/**
	 * Store text expression literal in text field.
	 */
	public String visit(TextExpression expression) {
		return expression.getText().getLiteral().toString();
	}
	
	/**
	 * Retrieve defined value from record expression.
	 * @param field
	 * @return
	 */
	public static Expression getFieldExpression(Field field, Environment environment) {
		Expression expression = field.getExpression();
		
		// Browse over variable expressions until a raw type is detected
		while(expression instanceof Expression.VarExpression) {
			Expression.VarExpression var = (Expression.VarExpression) expression;
			expression = environment.getVariable(var.getId().getName());
		}
		
		if(expression instanceof Expression.RecordExpression) {
			Expression.RecordExpression record = (Expression.RecordExpression) expression;
			Expression result = record.getExpression(field.getIdentifier());
			if(result != null) { return result; }
		}

		return null; // Undefined value
	}
	
	public static Expression getReference(VarExpression expression, Environment environment) {
		String name = expression.getId().getName();
		
		Expression reference = environment.getVariable(name);
		if(reference == expression && environment.getParent() != null) {
			reference = environment.getParent().getVariable(name);
		}
		
		return reference;
	}
	
}