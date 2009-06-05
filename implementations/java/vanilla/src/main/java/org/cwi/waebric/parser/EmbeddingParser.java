package org.cwi.waebric.parser;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import org.cwi.waebric.WaebricSymbol;
import org.cwi.waebric.parser.ast.AbstractSyntaxNodeList;
import org.cwi.waebric.parser.ast.StringLiteral;
import org.cwi.waebric.parser.ast.expression.Expression;
import org.cwi.waebric.parser.ast.markup.Markup;
import org.cwi.waebric.parser.ast.statement.embedding.Embed;
import org.cwi.waebric.parser.ast.statement.embedding.Embedding;
import org.cwi.waebric.parser.ast.statement.embedding.MidText;
import org.cwi.waebric.parser.ast.statement.embedding.PostText;
import org.cwi.waebric.parser.ast.statement.embedding.PreText;
import org.cwi.waebric.parser.ast.statement.embedding.TextTail;
import org.cwi.waebric.parser.exception.SyntaxException;
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

	public EmbeddingParser(WaebricTokenIterator tokens, List<SyntaxException> exceptions) {
		super(tokens, exceptions);

		// Initiate sub-parsers
		markupParser = new MarkupParser(tokens, exceptions);
		expressionParser = new ExpressionParser(tokens, exceptions);
	}
	
	/**
	 * Convert embedded "quote" token to sub tokens.
	 * 
	 * @param lexeme
	 */
	private void tokenizeEmbedding() {
		try {		
			current = tokens.next();
			
			// Convert text (including first " symbol) to new token stream
			StringReader reader = new StringReader(current.getLexeme().toString());
			WaebricScanner scanner = new WaebricScanner(reader);
			scanner.tokenizeStream();
			
			// Retrieve token stream
			List<WaebricToken> elements = scanner.getTokens();
			
			// Attach " symbols to stream
			elements.add(0, new WaebricToken(
					WaebricSymbol.DQUOTE, WaebricTokenSort.CHARACTER, 
					current.getLine(), current.getCharacter()));
			elements.add(new WaebricToken(
					WaebricSymbol.DQUOTE, WaebricTokenSort.CHARACTER, 
					current.getLine(), current.getCharacter()));
			
			// Change token location to absolute instead of relative
			for(WaebricToken token : elements) {
				token.setCharacter(token.getCharacter() + current.getCharacter());
				token.setLine(token.getLine() + current.getLine());
			}
			
			// Swap quote token with extended token collection
			tokens.remove();
			tokens.addAll(elements);
		} catch(IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public Embedding parseEmbedding() {
		// Decompose stream when needed
		WaebricToken peek = tokens.peek(1);
		if(peek.getSort() == WaebricTokenSort.QUOTE) {
			tokenizeEmbedding();
		}
		
		Embedding embedding = new Embedding();
		embedding.setPre(parsePreText());
		embedding.setEmbed(parseEmbed());
		embedding.setTail(parseTextTail());
		return embedding;
	}
	
	/**
	 * 
	 * @return
	 */
	public Embed parseEmbed() {
		// Parse mark-up tokens
		AbstractSyntaxNodeList<Markup> markups = new AbstractSyntaxNodeList<Markup>();
		while(tokens.hasNext(2) && ! tokens.peek(2).getLexeme().equals(WaebricSymbol.GREATER_THAN)) {
			markups.add(parseMarkup());
		}
		
		// Determine type based on look-ahead information
		if(tokens.hasNext() && tokens.peek(1).getSort() == WaebricTokenSort.IDCON) {
			// Mark-up always start with an identifier
			Embed.MarkupEmbed embed = new Embed.MarkupEmbed(markups);
			embed.setMarkup(parseMarkup());
			return embed;
		} else {
			// Only remaining alternative is Markup* Expression -> Markup
			Embed.ExpressionEmbed embed = new Embed.ExpressionEmbed(markups);
			embed.setExpression(parseExpression("embed", "< Markup* Expression >"));
			return embed;
		}
	}
	
	/**
	 * 
	 * @return
	 */
	public TextTail parseTextTail() {
		next("Embedding tailsymbol >", "Embed > TextChars*", WaebricSymbol.GREATER_THAN);
		
		// Parse text characters
		StringLiteral text = parseTextChars();
		
		// Determine tail type based on look-ahead information
		if(tokens.hasNext()) {
			if(tokens.peek(1).getLexeme().equals(WaebricSymbol.DQUOTE)) {
				// PostText is always closed with a double quote
				PostText post = new PostText();
				post.setText(text);
				
				TextTail.PostTail tail = new TextTail.PostTail();
				tail.setPost(post);
				
				tokens.next(); // Skip "
				return tail;
			} else {
				// Only remaining alternative is MidText Embed TextTail -> TextTail
				next("Embedding mid-text end symbol <", "> TextChars* <", WaebricSymbol.LESS_THAN);
				
				MidText mid = new MidText();
				mid.setText(text);

				TextTail.MidTail tail = new TextTail.MidTail();
				tail.setMid(mid);
				tail.setEmbed(parseEmbed());
				tail.setTail(parseTextTail());
				
				return tail;
			}
		}
		
		return null;
	}
	
	/**
	 * 
	 * @return
	 */
	public PreText parsePreText() {
		next("Embedding opening quote \"", "\" TextChars* <", WaebricSymbol.DQUOTE);
		
		// Parse text characters
		PreText pre = new PreText();
		pre.setText(parseTextChars());
		
		next("Embedding pre-text symbol <", "TextChars* < Embed", WaebricSymbol.LESS_THAN);
		
		return pre;
	}
	
	/**
	 * 
	 * @return
	 */
	public PostText parsePostText() {
		next("Embedding post-text symbol >", "Embed > TextChars*", WaebricSymbol.GREATER_THAN);
		
		// Parse text characters
		PostText post = new PostText();
		post.setText(parseTextChars());
		
		next("Embedding closure quote \"", "> TextChars* \"", WaebricSymbol.DQUOTE);
		
		return post;
	}
	
	/**
	 * 
	 * @return
	 */
	public MidText parseMidText() {
		next("Embedding mid-text start symbol >", "> TextChars* <", WaebricSymbol.GREATER_THAN);
		
		// Parse text characters
		MidText mid = new MidText();
		mid.setText(parseTextChars());
		
		next("Embedding mid-text end symbol <", "> TextChars* <", WaebricSymbol.LESS_THAN);
		
		return mid;
	}
	
	/**
	 * 
	 * @return
	 */
	public StringLiteral parseTextChars() {
		String data = "";
		
		while(tokens.hasNext()) {
			String peek = tokens.peek(1).getLexeme().toString();
			if(! WaebricScanner.isTextChars(peek)) {
				break; // No more text chars found, quit parse
			} else {
				data += peek; // Build string
				current = tokens.next(); // Iterate to next token
			}
		}
		
		return new StringLiteral(data);
	}

	/**
	 * 
	 * @param name
	 * @param syntax
	 * @return
	 */
	public Expression parseExpression(String name, String syntax) {
		return expressionParser.parseExpression(name, syntax);
	}
	
	/**
	 * 
	 * @return
	 */
	public Markup parseMarkup() {
		return markupParser.parseMarkup();
	}
	
}