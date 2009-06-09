package org.cwi.waebric.parser.ast.statement;

import java.util.List;

import org.cwi.waebric.WaebricSymbol;
import org.cwi.waebric.parser.ast.AbstractSyntaxNodeList;
import org.cwi.waebric.parser.ast.CharacterLiteral;
import org.cwi.waebric.parser.ast.AbstractSyntaxNode;
import org.cwi.waebric.parser.ast.AbstractSyntaxNodeList.AbstractSeparatedSyntaxNodeList;
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
	public static class Regular extends Formals {
		
		private AbstractSeparatedSyntaxNodeList<IdCon> identifiers;
		
		/**
		 * Construct regular formal
		 */
		public Regular() {
			this.identifiers = new AbstractSeparatedSyntaxNodeList<IdCon>(WaebricSymbol.COMMA);
		}
		
		/**
		 * Construct regular formal and attach identifier element
		 * @param identifier
		 */
		public Regular(IdCon identifier) {
			this.identifiers = new AbstractSeparatedSyntaxNodeList<IdCon>(WaebricSymbol.COMMA);
			this.identifiers.add(identifier);
		}
		
		/**
		 * Construct regular formal and attach identifier collection
		 * @param identifiers
		 */
		public Regular(List<IdCon> identifiers) {
			this.identifiers = new AbstractSeparatedSyntaxNodeList<IdCon>(WaebricSymbol.COMMA);
			this.identifiers.addAll(identifiers);
		}
		
		public boolean addIdentifier(IdCon identifier) {
			return identifiers.add(identifier);
		}
		
		public IdCon getIdentifier(int index) {
			return identifiers.get(index);
		}
		
		public int getIdentifierCount() {
			return identifiers.size();
		}
		
		@Override
		public List<IdCon> getIdentifiers() {
			return identifiers;
		}
		
		public AbstractSyntaxNode[] getChildren() {
			return new AbstractSyntaxNode[] {
				new CharacterLiteral(WaebricSymbol.LPARANTHESIS),
				identifiers,
				new CharacterLiteral(WaebricSymbol.RPARANTHESIS)
			};
		}

	}
	
	/**
	 *  -> Formals
	 * @author Jeroen van Schagen
	 * @date 09-06-2009
	 */
	public static class Empty extends Formals {
		
		public AbstractSyntaxNode[] getChildren() {
			return new AbstractSyntaxNode[]{};
		}

		@Override
		public List<IdCon> getIdentifiers() {
			return new AbstractSyntaxNodeList<IdCon>();
		}
		
	}

}