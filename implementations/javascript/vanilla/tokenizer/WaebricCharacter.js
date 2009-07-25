WaebricCharacter = function(input, position){	
	this.value = input[position];
	
	this.hasNextChar = function(){
		return position < input.length;
	}
	
	this.nextChar = function(){
		if (position < input.length){
			return new WaebricCharacter(input, position + 1);
		}else{
			return null
		}
	}
	
	this.previousChar = function(){
		if (position > 0){
			return new WaebricCharacter(input, position - 1);
		}else{
			return null;
		}
	}
	
	this.toString = function(){
		return this.value;
	}
	
	this.equals = function(input){
		return this.value == input;
	}
	
	this.match = function(regExpr){
		if (this.value != null) {
			return this.value.match(regExpr);
		}else{
			return false;
		}
	}
}

String.prototype.equals = function(input){
	return this.toUpperCase() == input.toUpperCase();
}
