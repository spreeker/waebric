package org.cwi.waebric.parser.ast;

import java.util.ArrayList;
import java.util.List;

import org.cwi.waebric.parser.ast.token.CharacterLiteral;

/**
 * Generic syntax node list implementation for syntax that
 * represent a list structure, including a separator literal
 * between each element.
 * 
 * @param <E>
 * 
 * @author Jeroen van Schagen
 * @date 20-05-2009
 */
public class SeparatedNodeList<E extends AbstractSyntaxNode> extends NodeList<E> {
	
	/**
	 * Separation character
	 */
	public final char separator;
	
	/**
	 * Construct separated list.
	 * @param separator Separation character
	 */
	public SeparatedNodeList(char separator) {
		this.separator = separator;
	}
	
	/**
	 * Construct separated list.
	 * @param list List containing all elements.
	 * @param separator Separation character
	 */
	public SeparatedNodeList(List<E> list, char separator) {
		super(list);
		this.separator = separator;
	}

	@Override
	public SeparatedNodeList<E> clone() {
		return new SeparatedNodeList<E>(new NodeList<E>(new ArrayList<E>(list)), separator);
	}

	@Override
	public AbstractSyntaxNode[] getChildren() {
		AbstractSyntaxNode[] elements = super.getChildren();
		
		int length = elements.length > 0 ? (elements.length * 2) - 1 : 0;
		AbstractSyntaxNode[] children = new AbstractSyntaxNode[length];
		
		for(int i = 0; i < children.length; i++) {
			if(i % 2 == 0) { children[i] = elements[i/2]; }
			else { children[i] = new CharacterLiteral(separator); }
		}

		return children;
	}
	
	@Override
	public boolean equals(Object arg) {
		if(arg instanceof SeparatedNodeList) {
			SeparatedNodeList<?> list = (SeparatedNodeList<?>) arg;
			return list.separator == this.separator && super.equals(arg);
		}
		
		return false;
	}

}