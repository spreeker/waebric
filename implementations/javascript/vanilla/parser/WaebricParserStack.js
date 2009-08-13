
function WaebricParserStack(){
	
	this.stack = new Array();	
	
	/**
	 * Adds the name of the (sub)parser to the stack
	 * 
	 * @param {String} name
	 */
	this.addParser = function(name){
		this.stack.push(name);
	}
	
	/**
	 * Replaces the last sub(parser) on the stack with a new one
	 * 
	 * @param {String} name
	 */
	this.updateParser = function(name){
		this.stack.pop();
		this.stack.push(name);
	}
	
	/**
	 * Removes the last sub(parser) from the stack
	 */
	this.removeParser = function(){
		this.stack.pop();
	}
	
	/**
	 * Returns the last parser added to the stack
	 */
	this.getLastParser = function(){
		return this.stack[this.stack.length - 1];
	}
	/**
	 * Replaces the current stack with the input
	 * 
	 * @param {Object} parserStack
	 */
	this.setStack = function(parserStack){
		this.stack = parserStack.stack;
	}
	
	/**
	 * Concats the current stack with the input
	 * 
	 * @param {Object} parserStack
	 */
	this.addStack = function(parserStack){
		this.stack = this.stack.concat(parserStack.stack);
	}
	
	this.toString = function(){
		return '> Parsing ' + this.stack.join('\n> Parsing ') + ' \n> Failed';
	}
}
