package org.cwi.waebric.parser;

import java.io.IOException;
import java.io.StringReader;
import java.util.List;

import org.cwi.waebric.WaebricSymbol;
import org.cwi.waebric.parser.ast.AbstractSyntaxNodeList;
import org.cwi.waebric.parser.ast.StringLiteral;
import org.cwi.waebric.parser.ast.markup.Markup;
import org.cwi.waebric.parser.ast.statement.Formals;
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
	 * @see Embedding
	 * @return Embedding
	 * @throws SyntaxException 
	 */
	public Embedding parseEmbedding(Formals formals) throws SyntaxException {
		WaebricToken peek = tokens.peek(1);
		if(peek.getSort() == WaebricTokenSort.QUOTE) {
			// Decompose stream when first token is quote
			tokenizeEmbedding();
		}
		
		Embedding embedding = new Embedding();
		embedding.setPre(parsePreText());
		embedding.setEmbed(parseEmbed(formals));
		embedding.setTail(parseTextTail(formals));
		return embedding;
	}
	
	/**
	 * @see PreText
	 * @return PreText
	 * @throws SyntaxException 
	 */
	public PreText parsePreText() throws SyntaxException {
		next(WaebricSymbol.DQUOTE, "Embedding opening quote \"", "\" TextChars* <");
		
		// Parse text characters
		PreText pre = new PreText();
		pre.setText(parseTextChars());
		
		next(WaebricSymbol.LESS_THAN, "Embedding pre-text symbol <", "TextChars* < Embed");
		return pre;
	}
	
	/**
	 * @see Embed
	 * @return Embed
	 * @throws SyntaxException 
	 */
	public Embed parseEmbed(Formals formals) throws SyntaxException {
		// Parse mark-up tokens
		AbstractSyntaxNodeList<Markup> markups = new AbstractSyntaxNodeList<Markup>();
		while(tokens.hasNext(2) && ! tokens.peek(2).getLexeme().equals(WaebricSymbol.GREATER_THAN)) {
			markups.add(markupParser.parseMarkup());
		}
		
		// Determine type based on look-ahead information
		if(tokens.hasNext() && StatementParser.isMarkup(tokens.peek(1), formals)) {
			// Markup* Markup -> Markup
			Embed.MarkupEmbed embed = new Embed.MarkupEmbed(markups);
			try {
				embed.setMarkup(markupParser.parseMarkup());
			} catch(SyntaxException e) {
				reportUnexpectedToken(tokens.current(), "Markup embedding", "Markup+ Markup");
			}
			return embed;
		} else {
			// Only remaining alternative is Markup* Expression -> Markup
			Embed.ExpressionEmbed embed = new Embed.ExpressionEmbed(markups);
			try {
				embed.setExpression(expressionParser.parseExpression());
			} catch(SyntaxException e) {
				reportUnexpectedToken(tokens.current(), "Expression embedding", "Markup+ Expression");
			}
			return embed;
		}
	}
	
	/**
	 * @see TextTail
	 * @return TextTail
	 * @throws SyntaxException 
	 */
	public TextTail parseTextTail(Formals formals) throws SyntaxException {
		next(WaebricSymbol.GREATER_THAN, "Embedding tail symbol \">\"", "Embed > TextChars*");
		
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
				
				tokens.next(); // Accept " symbol and jump to next token
				return tail;
			} else {
				// Only remaining alternative is MidText Embed TextTail -> TextTail
				next(WaebricSymbol.LESS_THAN, "Embedding mid-text end symbol <", "> TextChars* <");
				
				MidText mid = new MidText();
				mid.setText(text);

				TextTail.MidTail tail = new TextTail.MidTail();
				tail.setMid(mid);
				tail.setEmbed(parseEmbed(formals));
				tail.setTail(parseTextTail(formals));
				
				return tail;
			}
		}
		
		return null;
	}
	
	/**
	 * @see PostText
	 * @return PostText
	 * @throws SyntaxException 
	 */
	public PostText parsePostText() throws SyntaxException {
		next(WaebricSymbol.GREATER_THAN, "Embedding post-text symbol >", "Embed > TextChars*");
		
		// Parse text characters
		PostText post = new PostText();
		post.setText(parseTextChars());
		
		next(WaebricSymbol.DQUOTE, "Embedding closure quote \"", "> TextChars* \"");
		
		return post;
	}
	
	/**
	 * @see MidText
	 * @return MidText
	 * @throws SyntaxException 
	 */
	public MidText parseMidText() throws SyntaxException {
		next(WaebricSymbol.GREATER_THAN, "Embedding mid-text start symbol >", "> TextChars* <");
		
		// Parse text characters
		MidText mid = new MidText();
		mid.setText(parseTextChars());
		
		next(WaebricSymbol.LESS_THAN, "Embedding mid-text end symbol <", "> TextChars* <");
		
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
				tokens.next(); // Iterate to next token
			}
		}
		
		return new StringLiteral(data);
	}
	
	/**
	 * Convert next token to sub-tokens. For example:<br>
	 * "<123>" is converted to [ ", <, 123, >, " ]
	 */
	private void tokenizeEmbedding() {
		try {		
			tokens.next(); // Jump to next element
			
			// Convert text to new token stream
			StringReader reader = new StringReader(tokens.current().getLexeme().toString());
			WaebricScanner scanner = new WaebricScanner(reader);
			scanner.tokenizeStream();
			
			// Retrieve token stream
			List<WaebricToken> elements = scanner.getTokens();
			
			// Attach " symbols to stream
			elements.add(0, new WaebricToken(
					WaebricSymbol.DQUOTE, WaebricTokenSort.CHARACTER, 
					tokens.current().getLine(), tokens.current().getCharacter()));
			elements.add(new WaebricToken(
					WaebricSymbol.DQUOTE, WaebricTokenSort.CHARACTER, 
					tokens.current().getLine(), tokens.current().getCharacter()));
			
			// Change token location to absolute instead of relative
			for(WaebricToken token : elements) {
				token.setCharacter(token.getCharacter() + tokens.current().getCharacter());
				token.setLine(token.getLine() + tokens.current().getLine());
			}
			
			// Swap quote token with extended token collection
			tokens.remove();
			tokens.addAll(elements);
		} catch(IOException e) {
			e.printStackTrace(); // Should never occur
		}
	}
	
}