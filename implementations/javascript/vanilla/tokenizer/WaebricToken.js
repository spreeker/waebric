WaebricToken = function(value){	
	this.value = value;
	this.type = "unrecognized"
	
	this.addToken = function(token){
		this.value += token
	}
		
	this.toString = function(){
		return this.value;
	}
}