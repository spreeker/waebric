package org.cwi.waebric.parser.ast.basic;

import org.cwi.waebric.parser.ast.SyntaxNode;
import org.cwi.waebric.parser.ast.INodeVisitor;

/**
 * 
 * @author Jeroen van Schagen
 *
 */
public class NatCon extends SyntaxNode {

	/**
	 * Integer literal.
	 */
	private int value;
	
	/**
	 * Construct empty natural.
	 */
	public NatCon() { 
		this(0);
	}
	
	/**
	 * Construct natural based on integer.
	 * @param identifier
	 */
	public NatCon(int value) {
		this.value = value;
	}
	
	/**
	 * Construct natural based on string. Its value needs to a valid 
	 * integer value, else the default value '0' will be set.
	 * @param identifier
	 */
	public NatCon(String value) {
		try {
			this.value = Integer.parseInt(value);
		} catch(NumberFormatException e) {
			this.value = 0;
		}
	}
	
	/**
	 * Retrieve literal
	 * @return
	 */
	public int getValue() {
		return value;
	}

	@Override
	public boolean equals(Object obj) {
		try {
			return value == ((NatCon) obj).getValue();
		} catch(Exception e) {
			return false;
		}
	}

	@Override
	public <T> T accept(INodeVisitor<T> visitor) {
		return visitor.visit(this);
	}

}