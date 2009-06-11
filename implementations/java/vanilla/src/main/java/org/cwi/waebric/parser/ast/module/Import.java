package org.cwi.waebric.parser.ast.module;

import org.cwi.waebric.WaebricKeyword;
import org.cwi.waebric.parser.ast.AbstractSyntaxNode;
import org.cwi.waebric.parser.ast.INodeVisitor;
import org.cwi.waebric.parser.ast.StringLiteral;

/**
 * "import" ModuleId -> Import
 * 
 * @author Jeroen van Schagen
 * @date 20-05-2009
 */
public class Import extends AbstractSyntaxNode {
	
	private static final String IMPORT_KEYWORD = WaebricKeyword.IMPORT.name().toLowerCase();
	
	private ModuleId identifier;

	public ModuleId getIdentifier() {
		return identifier;
	}

	public void setIdentifier(ModuleId identifier) {
		this.identifier = identifier;
	}

	public AbstractSyntaxNode[] getChildren() {
		return new AbstractSyntaxNode[] { 
			new StringLiteral(IMPORT_KEYWORD), 
			identifier
		};
	}
	
	@Override
	public void accept(INodeVisitor visitor, Object[] args) {
		visitor.visit(this, args);
	}

}