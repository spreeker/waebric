package org.cwi.waebric.parser.ast.site;

import org.cwi.waebric.WaebricKeyword;
import org.cwi.waebric.parser.ast.ISyntaxNode;
import org.cwi.waebric.parser.ast.StringLiteral;
import org.cwi.waebric.parser.ast.module.IModuleElement;

public class Site implements IModuleElement {
	
	// Keyword literals
	private static final String SITE_LITERAL = WaebricKeyword.SITE.name().toLowerCase();
	private static final String END_LITERAL = WaebricKeyword.END.name().toLowerCase();
	
	private Mappings mappings;
	
	public Site() {
		mappings = new Mappings();
	}

	public Mappings getMappings() {
		return mappings;
	}

	public boolean addMapping(Mapping mapping) {
		return mappings.add(mapping);
	}

	public ISyntaxNode[] getChildren() {
		return new ISyntaxNode[] { 
			new StringLiteral(SITE_LITERAL),
			mappings,
			new StringLiteral(END_LITERAL)
		};
	}

}