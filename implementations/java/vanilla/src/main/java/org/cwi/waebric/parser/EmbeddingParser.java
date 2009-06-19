package org.cwi.waebric.parser;

import java.io.StringReader;
import java.util.List;

import org.cwi.waebric.WaebricSymbol;
import org.cwi.waebric.parser.ast.NodeList;
import org.cwi.waebric.parser.ast.markup.Markup;
import org.cwi.waebric.parser.ast.statement.embedding.Embed;
import org.cwi.waebric.parser.ast.statement.embedding.Embedding;
import org.cwi.waebric.parser.ast.statement.embedding.MidText;
import org.cwi.waebric.parser.ast.statement.embedding.PostText;
import org.cwi.waebric.parser.ast.statement.embedding.PreText;
import org.cwi.waebric.parser.ast.statement.embedding.TextTail;
import org.cwi.waebric.parser.ast.token.StringLiteral;
import org.cwi.waebric.scanner.WaebricScanner;
import org.cwi.waebric.scanner.token.Token;
import org.cwi.waebric.scanner.token.TokenIterator;
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

	public EmbeddingParser(TokenIterator tokens, List<SyntaxException> exceptions) {
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
	public Embedding parseEmbedding() throws SyntaxException {
		Token peek = tokens.peek(1);
		if(peek.getSort() == WaebricTokenSort.QUOTE) {
			// Decompose stream when first token is quote
			tokenizeEmbedding();
		}
		
		Embedding embedding = new Embedding();
		embedding.setPre(parsePreText());
		embedding.setEmbed(parseEmbed());
		embedding.setTail(parseTextTail());
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
		PreText pre = new PreText(parseTextChars());

		next(WaebricSymbol.LESS_THAN, "Embedding pre-text symbol <", "TextChars* < Embed");
		return pre;
	}
	
	/**
	 * @see Embed
	 * @return Embed
	 * @throws SyntaxException 
	 */
	public Embed parseEmbed() throws SyntaxException {
		// Parse mark-up tokens
		NodeList<Markup> markups = new NodeList<Markup>();
		while(tokens.hasNext(2) && ! tokens.peek(2).getLexeme().equals(WaebricSymbol.GREATER_THAN)) {
			markups.add(markupParser.parseMarkup());
		}
		
		// Determine type based on look-ahead information
		if(isMarkup(1)) { // Markup* Markup -> Markup
			Embed.MarkupEmbed embed = new Embed.MarkupEmbed(markups);
			
			try {
				embed.setMarkup(markupParser.parseMarkup());
			} catch(SyntaxException e) {
				reportUnexpectedToken(tokens.current(), "Markup embedding", "Markup+ Markup");
			}
			
			return embed;
		} else { // Markup* Expression -> Markup
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
	public TextTail parseTextTail() throws SyntaxException {
		next(WaebricSymbol.GREATER_THAN, "Embedding tail symbol \">\"", "Embed > TextChars*");
		
		// Parse text characters
		StringLiteral text = parseTextChars();
		
		// Determine tail type based on look-ahead information
		if(tokens.hasNext()) {
			if(tokens.peek(1).getLexeme().equals(WaebricSymbol.DQUOTE)) {
				// PostText is always closed with a double quote
				PostText post = new PostText(text);
				TextTail.PostTail tail = new TextTail.PostTail(post);

				tokens.next(); // Accept " symbol and jump to next token
				return tail;
			} else {
				// Only remaining alternative is MidText Embed TextTail -> TextTail
				next(WaebricSymbol.LESS_THAN, "Embedding mid-text end symbol <", "> TextChars* <");
			
				MidText mid = new MidText(text);
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
	 * @see PostText
	 * @return PostText
	 * @throws SyntaxException 
	 */
	public PostText parsePostText() throws SyntaxException {
		next(WaebricSymbol.GREATER_THAN, "Embedding post-text symbol >", "Embed > TextChars*");
		PostText post = new PostText(parseTextChars());
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
		MidText mid = new MidText(parseTextChars());
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
	 * Convert next token(s) to sub-tokens. For example:<br>
	 * "<123>" is converted to [ ", <, 123, >, " ]
	 * @throws SyntaxException 
	 */
	public void tokenizeEmbedding() throws SyntaxException {
		Token start = tokens.next(); // Retrieve quote token
		String data = start.getLexeme().toString(); // Embedded token stream
		int length = 1; // Number of tokens processed, required for replacing current stream
		
		// When an embedding quote does not follow the specified regular expression, it likely 
		// contains content with double quotes. Further decompose token stream to retrieve all
		// embedding related data.
		if(tokens.hasNext() && ! start.getLexeme().toString().matches("\\w*<\\w*>\\w*")) {
			do {
				data += "\"";
				data += tokens.next().getLexeme().toString();
				length++;
			} while(tokens.hasNext() && ! tokens.current().getLexeme().toString().startsWith(">"));
			current(WaebricTokenSort.QUOTE, "Embedding back", "> Text \"");
		}
		
		// Convert data to new token stream
		StringReader reader = new StringReader(data);
		WaebricScanner scanner = new WaebricScanner(reader);
		scanner.tokenizeStream(); 
		
		// Retrieve tokens embedded in quote
		List<Token> elements = scanner.getTokens(); 
		
		// Convert token location from relative to absolute
		for(Token token : elements) {
			token.setCharacter(token.getCharacter() + start.getCharacter());
			token.setLine(token.getLine() + start.getLine());
		}
		
		// Attach double quote symbol tokens at begin and end of stream
		elements.add(0, new Token(WaebricSymbol.DQUOTE, WaebricTokenSort.CHARACTER, start.getLine(), start.getCharacter()));
		elements.add(new Token(WaebricSymbol.DQUOTE, WaebricTokenSort.CHARACTER, tokens.current().getLine(), tokens.current().getCharacter()));
		
		// Swap quote token with extended token collection
		for(int i = 0; i < length; i++) { tokens.remove(); } 
		tokens.addAll(elements);
	}
	
	/**
	 * Check if next token is mark-up.
	 * @param token
	 * @return
	 */
	public boolean isMarkup(int k) {
		if(tokens.hasNext() && tokens.peek(k).getSort() == WaebricTokenSort.IDCON) {
			if(tokens.hasNext(k+2) // Parentheses can be used to force an identifier as mark-up
					&& tokens.peek(k+1).getLexeme().equals(WaebricSymbol.LPARANTHESIS)
					&& tokens.peek(k+2).getLexeme().equals(WaebricSymbol.RPARANTHESIS)) {
				return true;
			} else if(tokens.hasNext(k+1) && tokens.peek(k+1).getLexeme().equals(WaebricSymbol.SEMICOLON)) {
				// Final identifier of string is variable by default
				return false;
			} else {
				// All identifiers not at tail are seen as mark-up
				return true;
			}
		}
		
		return false;
	}
	
	/**
	 * Determine if specified token is embedding
	 * @param token Token
	 * @return Embedding?
	 */
	public static boolean isEmbedding(Token token) {
		return token.getSort() == WaebricTokenSort.QUOTE 
			&& token.getLexeme().toString().indexOf('<') != -1;
	}
	
}