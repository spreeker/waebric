package org.cwi.waebric.parser.ast.module.function;

import java.util.Collection;
import java.util.List;

import org.cwi.waebric.parser.ast.SyntaxNode;
import org.cwi.waebric.parser.ast.INodeVisitor;
import org.cwi.waebric.parser.ast.SyntaxNodeList;
import org.cwi.waebric.parser.ast.basic.IdCon;

/**
 * @see RegularFormals
 * @see EmptyFormals
 * 
 * @author Jeroen van Schagen
 * @date 09-06-2009
 */
public abstract class Formals extends SyntaxNode {
	
	public abstract List<IdCon> getIdentifiers();

	/**
	 * "(" { IdCon "," }* ")" -> Formals
	 * @author Jeroen van Schagen
	 * @date 26-05-2009
	 */
	public static class RegularFormal extends Formals {
		
		private SyntaxNodeList<IdCon> identifiers;
		
		public RegularFormal() {
			this.identifiers = new SyntaxNodeList<IdCon>();
		}
		
		public RegularFormal(Collection<IdCon> args) {
			this.identifiers = new SyntaxNodeList<IdCon>();
			this.identifiers.addAll(args);
		}

		public boolean addIdentifier(IdCon identifier) {
			return identifiers.add(identifier);
		}
		
		public SyntaxNodeList<IdCon> getIdentifiers() {
			return identifiers.clone();
		}
		
		public SyntaxNode[] getChildren() {
			return new SyntaxNode[] { getIdentifiers() };
		}
		
			@Override
	public <T> T accept(INodeVisitor<T> visitor) {
		return visitor.visit(this);
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
			return new SyntaxNodeList<IdCon>();
		}
		
			@Override
	public <T> T accept(INodeVisitor<T> visitor) {
		return visitor.visit(this);
	}
		
	}

}