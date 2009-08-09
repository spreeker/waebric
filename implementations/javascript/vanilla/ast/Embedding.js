/**************************************************************************** 
 * Specifies an Embedding used in Statements
 * 
 * @author Nickolas Heirbaut 
 ****************************************************************************/

/**
 * Embedding Pre Class
 * 
 * PreText Embed TextTail -> Embedding ("pre")
 * 
 * @param {object} head
 * @param {object} embed
 * @param {object} tail
 */
function Embedding(head, embed, tail){
	this.head = head;
	this.embed = embed;
	this.tail = tail;
	
	this.toString = function(){
		return '[embedding: ' + this.head.toString() + ', ' + this.embed.toString() + ', ' + tail.toString() +']';
	}
}
Embedding.prototype = new Node(); //Inheritance base class
