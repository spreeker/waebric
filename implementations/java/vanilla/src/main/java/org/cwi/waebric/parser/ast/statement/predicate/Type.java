package org.cwi.waebric.parser.ast.statement.predicate;

import org.cwi.waebric.parser.ast.AbstractSyntaxNode;
import org.cwi.waebric.parser.ast.INodeVisitor;

/**
 * Type represents a type definition.
 * @author Jeroen van Schagen
 * @date 27-05-2009
 */
public abstract class Type extends AbstractSyntaxNode {
	
	/**
	 * Grammar:<br>
	 * <code>
	 * 	"string" -> Type<br>
	 * </code>
	 */
	public static class StringType extends Type {
		
		public AbstractSyntaxNode[] getChildren() {
			return new AbstractSyntaxNode[] { /* No children */ };
		}
		
		@Override
		public void accept(INodeVisitor visitor) {
			visitor.visit(this);
		}
		
	}
	
	/**
	 * Grammar:<br>
	 * <code>
	 * 	"list" -> Type<br>
	 * </code>
	 */
	public static class ListType extends Type {
		
		public AbstractSyntaxNode[] getChildren() {
			return new AbstractSyntaxNode[] { /* No children */ };
		}
		
		@Override
		public void accept(INodeVisitor visitor) {
			visitor.visit(this);
		}
		
	}
	
	/**
	 * Grammar:<br>
	 * <code>
	 * 	"record" -> Type<br>
	 * </code>
	 */
	public static class RecordType extends Type {
		
		public AbstractSyntaxNode[] getChildren() {
			return new AbstractSyntaxNode[] { /* No children */ };
		}
		
		@Override
		public void accept(INodeVisitor visitor) {
			visitor.visit(this);
		}
		
	}

}