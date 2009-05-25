package org.cwi.waebric.parser.ast.site;

import org.cwi.waebric.WaebricKeyword;
import org.cwi.waebric.parser.ast.ISyntaxNode;
import org.cwi.waebric.parser.ast.StringLiteral;
import org.cwi.waebric.parser.ast.module.IModuleElement;

public class Site implements IModuleElement {
	
	private static final String SITE_KEYWORD = WaebricKeyword.SITE.name().toLowerCase();
	private static final String END_KEYWORD = WaebricKeyword.END.name().toLowerCase();
	
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
		final ISyntaxNode[] maps = mappings.getChildren();
		
		ISyntaxNode[] children = new ISyntaxNode[maps.length+2];
		children[0] = new StringLiteral(SITE_KEYWORD);
		children[children.length-1] = new StringLiteral(END_KEYWORD);
		
		for(int i = 0; i < maps.length; i++) {
			children[i+1] = maps[i];
		}
		
		return children;
	}

}