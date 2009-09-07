package org.cwi.waebric.interpreter;

import org.cwi.waebric.parser.ast.NullVisitor;
import org.cwi.waebric.parser.ast.expression.Expression;
import org.cwi.waebric.parser.ast.expression.Expression.Field;
import org.cwi.waebric.parser.ast.expression.Expression.VarExpression;
import org.cwi.waebric.parser.ast.statement.predicate.Predicate;
import org.cwi.waebric.parser.ast.statement.predicate.Type;

public class PredicateEvaluator extends NullVisitor<Boolean> {

	private WaebricEvaluator evaluator;
	
	public PredicateEvaluator(WaebricEvaluator evaluator) {
		this.evaluator = evaluator;
	}
	
	public Boolean visit(Predicate.Not not) {
		return ! not.getPredicate().accept(this);
	}
	
	public Boolean visit(Predicate.And and) {
		return and.getLeft().accept(this) && and.getRight().accept(this);
	}
	
	public Boolean visit(Predicate.Or or) {
		return or.getLeft().accept(this) || or.getRight().accept(this);
	}
	
	public Boolean visit(Predicate.Is is) {
		Expression expression = is.getExpression();
		
		if(expression instanceof VarExpression) {
			VarExpression variable = (VarExpression) expression;
			expression = ExpressionEvaluator.getReference(variable, evaluator.getEnvironment());
			if(expression == null || is.getType() == null) { return false; }
		}
		
		if(is.getType() instanceof Type.StringType) {
			return expression.getClass() == Expression.TextExpression.class;
		} else if(is.getType() instanceof Type.ListType) {
			return expression.getClass() == Expression.ListExpression.class;
		} else if(is.getType() instanceof Type.RecordType) {
			return expression.getClass() == Expression.RecordExpression.class;
		} else { return false; }
	}
	
	public Boolean visit(Predicate.RegularPredicate predicate) {
		if(predicate.getExpression() instanceof Expression.Field) {
			Field field = (Field) predicate.getExpression();
			Expression value = ExpressionEvaluator.getFieldExpression(field, evaluator.getEnvironment());
			return value != null;
		} else if(predicate.getExpression() instanceof Expression.VarExpression) {
			VarExpression variable = (Expression.VarExpression) predicate.getExpression();
			return evaluator.getEnvironment().isDefinedVariable(variable.getId().getName());
		} else { return true; }
	}
	
}