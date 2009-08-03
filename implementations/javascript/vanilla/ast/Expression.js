/**************************************************************************** 
 * Specifies an Expression used in Statements, Assignments, Embeds, 
 * Predicates, Argument, KeyValuePair
 * 
 * @author Nickolas Heirbaut 
 ****************************************************************************/

/**
 * Field Expression Class
 * 
 * Expression "." IdCon -> Expression ("field")
 * 
 * @param {object} expression
 * @param {object} field
 */
function FieldExpression (expression, field){
	//Fields
	this.expression = expression;
	this.field = field;
	
	//Methods
	this.toString = function(){
		return "[" + this.expression.toString() + "." + this.field.toString() + "]";
	}
}
FieldExpression.prototype = new Node(); //Inheritance base class

/**
 * Cat Expression Class
 * 
 * Expression "+" Expression -> Expression ("cat")
 * 
 * @param {Object} expressionLeft
 * @param {Object} expressionRight
 */
function CatExpression (expressionLeft, expressionRight){
	//Fields
	this.expressionLeft = expressionLeft;
	this.expressionRight = expressionRight;
	
	//Methods
	this.toString = function(){
		 return this.expressionLeft + "+" + expressionRight;
	}
}
CatExpression.prototype = new Node(); //Inheritance base class

/**
 * Text Expression Class
 * 
 * Text -> Expression ("text")
 * 
 * @param {Object} text
 */
function TextExpression(text){
	//Fields
	this.text = text;
	
	//Methods
	this.toString = function(){
		return this.text.toString();
	}
}
TextExpression.prototype = new Node(); //Inheritance base class

/**
 * Variable Expression Class
 * 
 * IdCon -> Expression ("var")
 * 
 * @param {Object} variable
 */
function VarExpression(variable){
	//Fields
	this.variable = variable;
	
	//Methods
	this.toString = function(){
		if(this.variable.value != null){
			return this.variable.value;
		}
		return this.variable		
	}
}
VarExpression.prototype = new Node(); //Inheritance base class

/**
 * Natural Expression Class
 * 
 * NatCon -> Expression ("num")
 * 
 * @param {Object} natural
 */
function NatExpression(natural){
	//Fields
	this.natural = natural;
	
	//Methods
	this.toString = function(){
		return this.natural;
	}
}
NatExpression.prototype = new Node(); //Inheritance base class

/**
 * Symbol Expression Class
 * 
 * SymbolCon -> Expression ("sym")
 * 
 * @param {Object} symbol
 */
function SymbolExpression(symbol){
	//Fields
	this.symbol = symbol;
	
	//Methods
	this.toString = function(){
		//Remove the single quote
		return this.symbol.substr(1, this.symbol.length-1);
	}
}
SymbolExpression.prototype = new Node(); //Inheritance base class

/**
 * List Expression Class
 * 
 * "[" (Expression ',')* "]" -> Expression ("list")
 * 
 * @param {Object} list
 */
function ListExpression(list){
	//Fields
	this.list = list;
	
	//Methods
	this.toString = function(){
		return "[" + this.list.toString() + "]";
	}
}
ListExpression.prototype = new Node(); //Inheritance base class

/**
 * Record Expression Class
 * 
 * CB (KeyValuePair ',')* CB -> Expression ("record")
 * 
 * @param {Object} record
 */
function RecordExpression(records){
	//Fields
	this.records = records;
	
	//Methods	
	this.getValue = function(key){
		for(var i = 0; i < records.length; i++){
			var record = records[i];
			if (key == record.key) {
				return record.value;
			}
		}
		return null;
	}	
	
	this.toString = function(){
		return "{" + this.records.toString() + "}";
	}
}
RecordExpression.prototype = new Node(); //Inheritance base class