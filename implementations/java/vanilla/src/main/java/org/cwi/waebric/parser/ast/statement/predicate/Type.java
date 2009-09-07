package org.cwi.waebric.parser.ast.statement.predicate;

import org.cwi.waebric.parser.ast.SyntaxNode;
import org.cwi.waebric.parser.ast.INodeVisitor;

/**
 * Type represents a type definition.
 * @author Jeroen van Schagen
 * @date 27-05-2009
 */
public abstract class Type extends SyntaxNode {
	
	/**
	 * Grammar:<br>
	 * <code>
	 * 	"string" -> Type<br>
	 * </code>
	 */
	public static class StringType extends Type {
		
		public SyntaxNode[] getChildren() {
			return new SyntaxNode[] { /* No children */ };
		}
		
			@Override
	public <T> T accept(INodeVisitor<T> visitor) {
		return visitor.visit(this);
	}
		
	}
	
	/**
	 * Grammar:<br>
	 * <code>
	 * 	"list" -> Type<br>
	 * </code>
	 */
	public static class ListType extends Type {
		
		public SyntaxNode[] getChildren() {
			return new SyntaxNode[] { /* No children */ };
		}
		
			@Override
	public <T> T accept(INodeVisitor<T> visitor) {
		return visitor.visit(this);
	}
		
	}
	
	/**
	 * Grammar:<br>
	 * <code>
	 * 	"record" -> Type<br>
	 * </code>
	 */
	public static class RecordType extends Type {
		
		public SyntaxNode[] getChildren() {
			return new SyntaxNode[] { /* No children */ };
		}
		
			@Override
	public <T> T accept(INodeVisitor<T> visitor) {
		return visitor.visit(this);
	}
		
	}

}