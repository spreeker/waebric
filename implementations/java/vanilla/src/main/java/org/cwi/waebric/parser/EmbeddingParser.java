package org.cwi.waebric.parser;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import org.cwi.waebric.WaebricSymbol;
import org.cwi.waebric.parser.ast.StringLiteral;
import org.cwi.waebric.parser.ast.embedding.Embed;
import org.cwi.waebric.parser.ast.embedding.Embedding;
import org.cwi.waebric.parser.ast.embedding.MidText;
import org.cwi.waebric.parser.ast.embedding.PostText;
import org.cwi.waebric.parser.ast.embedding.PreText;
import org.cwi.waebric.parser.ast.embedding.TextTail;
import org.cwi.waebric.parser.ast.expressions.Expression;
import org.cwi.waebric.parser.ast.markup.Markup;
import org.cwi.waebric.parser.exception.ParserException;
import org.cwi.waebric.scanner.WaebricScanner;
import org.cwi.waebric.scanner.token.WaebricToken;
import org.cwi.waebric.scanner.token.WaebricTokenIterator;
import org.cwi.waebric.scanner.token.WaebricTokenSort;

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

	public EmbeddingParser(WaebricTokenIterator tokens, List<ParserException> exceptions) {
		super(tokens, exceptions);

		// Initiate sub-parsers
		markupParser = new MarkupParser(tokens, exceptions);
		expressionParser = new ExpressionParser(tokens, exceptions);
	}
	
	/**
	 * Convert embedded "quote" token to multiple sub-tokens.
	 * 
	 * @param lexeme
	 */
	private void tokenizeEmbedding(String lexeme) {
		try {			
			StringReader input = new StringReader(lexeme);
			WaebricScanner scanner = new WaebricScanner(input);
			tokens.remove();
			tokens.addAll(scanner.getTokens());
		} catch(IOException exception) {
			System.out.println("Error sub-tokenizing embedding");
			exception.printStackTrace();
		}
	}
	
	public Embedding parseEmbedding() {
		// Further tokenize stream when needed
		WaebricToken peek = tokens.peek(1);
		if(peek.getSort() == WaebricTokenSort.QUOTE) {
			tokenizeEmbedding(peek.getLexeme().toString());
		}
		
		Embedding embedding = new Embedding();
		
		// Pre-text
		PreText pre = parsePreText();
		embedding.setPre(pre);
		
		// Embed
		Embed embed = parseEmbed();
		embedding.setEmbed(embed);
		
		// Text tail
		TextTail tail = parseTextTail();
		embedding.setTail(tail);
		
		return embedding;
	}
	
	public Embed parseEmbed() {
		return null;
	}
	
	public TextTail parseTextTail() {
		return null;
	}
	
	public PreText parsePreText() {
		next("Embedding opening quote \"", "\" TextChars* <", WaebricSymbol.DQUOTE);
		
		PreText pre = new PreText();
		StringLiteral text = parseTextChars();
		pre.setText(text);
		
		next("Embedding pre-text symbol <", "TextChars* < Embed", WaebricSymbol.LESS_THAN);
		
		return pre;
	}
	
	public PostText parsePostText() {
		next("Embedding post-text symbol >", "Embed > TextChars*", WaebricSymbol.GREATER_THAN);
		
		PostText post = new PostText();
		StringLiteral text = parseTextChars();
		post.setText(text);
		
		next("Embedding closure quote \"", "> TextChars* \"", WaebricSymbol.DQUOTE);
		
		return post;
	}
	
	public MidText parseMidText() {
		next("Embedding mid-text start symbol >", "> TextChars* <", WaebricSymbol.GREATER_THAN);
		
		MidText mid = new MidText();
		StringLiteral text = parseTextChars();
		mid.setText(text);
		
		next("Embedding mid-text end symbol <", "> TextChars* <", WaebricSymbol.LESS_THAN);
		
		return mid;
	}
	
	public StringLiteral parseTextChars() {
		String data = "";
		
		while(tokens.hasNext()) {
			String peek = tokens.peek(1).getLexeme().toString();
			if(! WaebricScanner.isTextChars(peek)) {
				break; // End of parse
			} else {
				data += peek; // Build string
				current = tokens.next(); // Iterate to next token
			}
		}
		
		return new StringLiteral(data);
	}

	public Expression parseExpression(String name, String syntax) {
		return expressionParser.parseExpression(name, syntax);
	}
	
	public Markup parseMarkup() {
		return markupParser.parseMarkup();
	}
	
}