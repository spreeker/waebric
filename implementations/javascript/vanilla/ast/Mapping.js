/**************************************************************************** 
 * Specifies a Mapping used in Site
 * 
 * @author Nickolas Heirbaut [nickolas.heirbaut@dejasmijn.be]
 ****************************************************************************/

/**
 * Mapping Class
 * 
 * @param {Object} path
 * @param {Object} markup
 */
function Mapping (path, markup){
	this.path = path;
	this.markup = markup;
	
	this.toString = function(){
		return this.path + ' : ' + this.markup;
	}
}
Mapping.prototype = new Node(); //Inheritance base class
