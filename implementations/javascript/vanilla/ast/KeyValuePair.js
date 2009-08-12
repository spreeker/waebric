/**************************************************************************** 
 * Specifies a KeyValuePair used in Expression
 * 
 * @author Nickolas Heirbaut [nickolas.heirbaut@dejasmijn.be]
 ****************************************************************************/

/**
 * KeyValuePair Class
 * 
 * IdCon ":" Expression -> KeyValuePair ("pair")
 * 
 * @param {Object} key
 * @param {Object} value
 */
function KeyValuePair(key, value){
	this.key = key;
	this.value = value;
	
	this.toString = function(){
		return this.key + ':' + this.value;
	}
}
KeyValuePair.prototype = new Node(); //Inheritance base class
