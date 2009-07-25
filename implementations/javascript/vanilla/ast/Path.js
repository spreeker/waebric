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