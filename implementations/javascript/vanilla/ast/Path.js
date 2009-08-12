/**************************************************************************** 
 * Specifies a Path used in Mapping
 *  
 * @author Nickolas Heirbaut [nickolas.heirbaut@dejasmijn.be]
 ****************************************************************************/

/**
 * Path class
 *
 * @author Nickolas Heirbaut
 */

function Path (directory, file){
	this.file = file;
	this.directory = directory;
	
	this.toString = function(){
		return this.directory + "/" + this.file;
	}
}
Path.prototype = new Node(); //Inheritance base class