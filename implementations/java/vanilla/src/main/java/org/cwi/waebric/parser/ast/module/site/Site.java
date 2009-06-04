package org.cwi.waebric.parser.ast.module.site;

import org.cwi.waebric.WaebricKeyword;
import org.cwi.waebric.parser.ast.AbstractSyntaxNode;
import org.cwi.waebric.parser.ast.StringLiteral;
import org.cwi.waebric.parser.ast.module.ModuleElement;

/**
 * Site<br><br>
 * 
 * Grammar:<br>
 * <code>
 * 	"site" Mappings "end" -> Site
 * </code>
 * 
 * @author Jeroen van Schagen
 * @date 20-05-2009
 */
public class Site extends ModuleElement {

	private Mappings mappings;

	public Mappings getMappings() {
		return mappings;
	}

	public void setMappings(Mappings mappings) {
		this.mappings = mappings;
	}

	public AbstractSyntaxNode[] getChildren() {
		return new AbstractSyntaxNode[] { 
			new StringLiteral(WaebricKeyword.getLiteral(WaebricKeyword.SITE)),
			mappings,
			new StringLiteral(WaebricKeyword.getLiteral(WaebricKeyword.END))
		};
	}

}