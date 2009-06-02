package org.cwi.waebric.parser;

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
import org.cwi.waebric.scanner.token.Token;
import org.cwi.waebric.scanner.token.TokenIterator;
import org.cwi.waebric.scanner.token.TokenSort;

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
		MidText text = new MidText();
		
		if(! next("Embed closure token >", "\">\" TextChars \"<\"", WaebricSymbol.GREATER_THAN)) {
			return null;
		}

		Token peek = tokens.peek(1); // Optional: Parse text
		if(peek.getSort() == TokenSort.IDCON) {
			text.setText(new StringLiteral(peek.getLexeme().toString()));
			tokens.next(); // Skip text
		}
		
		if(! next("Embed opening token <", "\">\" TextChars \"<\"", WaebricSymbol.LESS_THAN)) {
			return null;
		}
		
		return text;
	}

	public Expression parseExpression(String name, String syntax) {
		return expressionParser.parseExpression(name, syntax);
	}
	
	public Markup parseMarkup() {
		return markupParser.parseMarkup();
	}
	
	public StringLiteral parseTextChars() {
		String data = "";
		
		Token previous = current;
		while(tokens.hasNext()) {
			current = tokens.next();
			Object lexeme = current.getLexeme();
			
			if(lexeme.equals('<')) { 
				break;
			} else if(lexeme.equals('&') || lexeme.equals('"')) {
				if(previous.getLexeme().equals('\\')) { break; }
			}

			data += current.getLexeme().toString();
			previous = current;
		}
		
		return new StringLiteral(data);
	}
	
}