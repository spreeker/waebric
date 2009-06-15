package org.cwi.waebric.parser.ast.module.function;

import java.util.Collection;
import java.util.List;

import org.cwi.waebric.WaebricSymbol;
import org.cwi.waebric.parser.ast.AbstractSyntaxNode;
import org.cwi.waebric.parser.ast.INodeVisitor;
import org.cwi.waebric.parser.ast.NodeList;
import org.cwi.waebric.parser.ast.SeparatedNodeList;
import org.cwi.waebric.parser.ast.basic.IdCon;
import org.cwi.waebric.parser.ast.token.CharacterLiteral;

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
		
		private SeparatedNodeList<IdCon> identifiers;
		
		public RegularFormal() {
			this.identifiers = new SeparatedNodeList<IdCon>(WaebricSymbol.COMMA);
		}
		
		public RegularFormal(Collection<IdCon> args) {
			this.identifiers = new SeparatedNodeList<IdCon>(WaebricSymbol.COMMA);
			this.identifiers.addAll(args);
		}

		public boolean addIdentifier(IdCon identifier) {
			return identifiers.add(identifier);
		}
		
		public List<IdCon> getIdentifiers() {
			return identifiers.clone();
		}
		
		public AbstractSyntaxNode[] getChildren() {
			return new AbstractSyntaxNode[] {
				new CharacterLiteral(WaebricSymbol.LPARANTHESIS),
				identifiers,
				new CharacterLiteral(WaebricSymbol.RPARANTHESIS)
			};
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
		
		public AbstractSyntaxNode[] getChildren() {
			return new AbstractSyntaxNode[]{};
		}

		@Override
		public List<IdCon> getIdentifiers() {
			return new NodeList<IdCon>();
		}
		
		@Override
		public void accept(INodeVisitor visitor) {
			visitor.visit(this);
		}
		
	}

}