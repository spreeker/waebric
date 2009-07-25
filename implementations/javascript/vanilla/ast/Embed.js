/**************************************************************************** 
 * Specifies an Embed used in TextTail and Embedding
 * 
 * @author Nickolas Heirbaut 
 ****************************************************************************/

/** 
 * Expression Embedding Class
 * 
 * Markup* Expression -> Embed ("exp-embedding")
 * 
 * @param {Array} Array of markup elements
 * @param {object} expression
 */
function ExpressionEmbedding(markups, expression){
	this.markups = markups;
	this.expression = expression;
}
ExpressionEmbedding.prototype = new Node(); //Inheritance base class

/**
 * Markup Embedding Class
 * 
 * Markup* Markup -> Embed ("markup-embedding")
 * 
 * @param {Array} Array of markup elements
 */
function MarkupEmbedding(markups){
	this.markups = markups;
}
MarkupEmbedding.prototype = new Node(); //Inheritance base class