package org.cwi.waebric.parser;

import java.util.List;

import org.cwi.waebric.WaebricSymbol;
import org.cwi.waebric.lexer.token.Token;
import org.cwi.waebric.lexer.token.TokenIterator;
import org.cwi.waebric.lexer.token.WaebricTokenSort;
import org.cwi.waebric.parser.ast.AbstractSyntaxNodeList;
import org.cwi.waebric.parser.ast.expression.Expression;
import org.cwi.waebric.parser.ast.markup.Markup;
import org.cwi.waebric.parser.ast.statement.embedding.Embed;
import org.cwi.waebric.parser.ast.statement.embedding.Embedding;
import org.cwi.waebric.parser.ast.statement.embedding.MidText;
import org.cwi.waebric.parser.ast.statement.embedding.PostText;
import org.cwi.waebric.parser.ast.statement.embedding.PreText;
import org.cwi.waebric.parser.ast.statement.embedding.TextTail;

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

	/**
	 * Construct embedding parser.
	 * @param tokens
	 * @param exceptions
	 */
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
		// Decompose embedding token when needed
		if(tokens.peek(1).getSort() == WaebricTokenSort.EMBEDDING) {
			Token.EmbeddingToken embedding = (Token.EmbeddingToken) tokens.next();
			tokens.remove(); // Remove embedding token
			tokens.addAll(embedding.getLexeme()); // Attach sub-tokens
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
		AbstractSyntaxNodeList<Markup> markups = new AbstractSyntaxNodeList<Markup>();
		Expression expression = null;
		while(tokens.hasNext() && ! tokens.peek(1).getLexeme().equals(WaebricSymbol.GREATER_THAN)) {
			int index = tokens.index();
			try { // Backtracking
				markups.add(markupParser.parseMarkup());
			} catch(SyntaxException e) {
				tokens.seek(index);
				expression = expressionParser.parseExpression();
				next(WaebricSymbol.GREATER_THAN, "Expression embedding closure", "markup+ expression >");
				Embed.ExpressionEmbed embed = new Embed.ExpressionEmbed(markups);
				embed.setExpression(expression);
				return embed;
			}
		}
		
		// Lonely mark-up can be interpreted as expression
		if(markups.size() == 1) {
			Markup loner = markups.get(0);
			if(loner.getDesignator().getAttributes().size() == 0 && !( loner instanceof Markup.Call ) ) {
				Expression.VarExpression var = new Expression.VarExpression(loner.getDesignator().getIdentifier());
				markups.clear();
				Embed.ExpressionEmbed embed = new Embed.ExpressionEmbed(markups);
				embed.setExpression(var);
				return embed;
			}
		}
		
		return new Embed.MarkupEmbed(markups);
	}
	
	/**
	 * @see TextTail
	 * @return TextTail
	 * @throws SyntaxException 
	 */
	public TextTail parseTextTail() throws SyntaxException {
		next(WaebricSymbol.GREATER_THAN, "Embedding tail symbol \">\"", "Embed > TextChars*");
		String text = parseTextChars();
		
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
	public String parseTextChars() {
		String data = "";
		
		while(tokens.hasNext() && tokens.peek(1).getSort() == WaebricTokenSort.TEXT) {
			String peek = tokens.peek(1).getLexeme().toString();
			data += peek; // Build string
			tokens.next(); // Iterate to next token
		}
		
		return data;
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
	
}