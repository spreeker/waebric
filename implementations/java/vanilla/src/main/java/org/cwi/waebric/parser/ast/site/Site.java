package org.cwi.waebric.parser.ast.site;

import org.cwi.waebric.parser.ast.ISyntaxNode;
import org.cwi.waebric.parser.ast.module.IModuleElement;

public class Site implements IModuleElement {
	
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
		return mappings.toArray(new Mapping[0]);
	}

}