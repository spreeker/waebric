package org.cwi.waebric.parser.ast.module.function;

import java.util.Collection;
import java.util.List;

import org.cwi.waebric.parser.ast.AbstractSyntaxNode;
import org.cwi.waebric.parser.ast.INodeVisitor;
import org.cwi.waebric.parser.ast.AbstractSyntaxNodeList;
import org.cwi.waebric.parser.ast.basic.IdCon;

/**
 * @see RegularFormals
 * @see EmptyFormals
 * 
 * @author Jeroen van Schagen
 * @date 09-06-2009
 */
public abstract class Formals extends AbstractSyntaxNode {
	
	public abstract List<IdCon> getIdentifiers();

	/**
	 * "(" { IdCon "," }* ")" -> Formals
	 * @author Jeroen van Schagen
	 * @date 26-05-2009
	 */
	public static class RegularFormal extends Formals {
		
		private AbstractSyntaxNodeList<IdCon> identifiers;
		
		public RegularFormal() {
			this.identifiers = new AbstractSyntaxNodeList<IdCon>();
		}
		
		public RegularFormal(Collection<IdCon> args) {
			this.identifiers = new AbstractSyntaxNodeList<IdCon>();
			this.identifiers.addAll(args);
		}

		public boolean addIdentifier(IdCon identifier) {
			return identifiers.add(identifier);
		}
		
		public AbstractSyntaxNodeList<IdCon> getIdentifiers() {
			return identifiers.clone();
		}
		
		public AbstractSyntaxNode[] getChildren() {
			return new AbstractSyntaxNode[] { getIdentifiers() };
		}
		
		@Override
		public void accept(INodeVisitor visitor) {
			visitor.visit(this);
		}

	}
	
	/**
	 *  -> Formals
	 * @author Jeroen van Schagen
	 * @date 09-06-2009
	 */
	public static class EmptyFormal extends Formals {

		@Override
		public List<IdCon> getIdentifiers() {
			return new AbstractSyntaxNodeList<IdCon>();
		}
		
		@Override
		public void accept(INodeVisitor visitor) {
			visitor.visit(this);
		}
		
	}

}