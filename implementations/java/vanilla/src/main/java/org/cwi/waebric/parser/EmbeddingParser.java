package org.cwi.waebric.parser;

import java.util.List;

import org.cwi.waebric.parser.ast.embedding.Embed;
import org.cwi.waebric.parser.ast.embedding.Embedding;
import org.cwi.waebric.parser.ast.embedding.MidText;
import org.cwi.waebric.parser.ast.embedding.PostText;
import org.cwi.waebric.parser.ast.embedding.PreText;
import org.cwi.waebric.parser.ast.embedding.TextTail;
import org.cwi.waebric.parser.ast.expressions.Expression;
import org.cwi.waebric.parser.ast.markup.Markup;
import org.cwi.waebric.parser.exception.ParserException;
import org.cwi.waebric.scanner.token.TokenIterator;

/**
 * Embedding
 * 
 * module languages/waebric/syntax/Embedding
 * 
 * @author Jeroen van Schagen
 * @date 29-05-2009
 */
class EmbeddingParser extends AbstractParser {
	
	private final MarkupParser markupParser;
	private final ExpressionParser expressionParser;

	public EmbeddingParser(TokenIterator tokens, List<ParserException> exceptions) {
		super(tokens, exceptions);
		
		// Initiate sub-parsers
		markupParser = new MarkupParser(tokens, exceptions);
		expressionParser = new ExpressionParser(tokens, exceptions);
	}
	
	public Embedding parseEmbedding() {
		return null;
	}
	
	public Embed parseEmbed() {
		return null;
	}
	
	public TextTail parseTextTail() {
		return null;
	}
	
	public PreText parsePreText() {
		return null;
	}
	
	public PostText parsePostText() {
		return null;
	}
	
	public MidText parseMidText() {
		return null;
	}

	public Expression parseExpression(String name, String syntax) {
		return expressionParser.parseExpression(name, syntax);
	}
	
	public Markup parseMarkup() {
		return markupParser.parseMarkup();
	}
	
}