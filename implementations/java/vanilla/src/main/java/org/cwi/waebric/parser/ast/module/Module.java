package org.cwi.waebric.parser.ast.module;

import java.util.List;

import org.cwi.waebric.parser.ast.AbstractSyntaxNode;
import org.cwi.waebric.parser.ast.AbstractSyntaxNodeList;
import org.cwi.waebric.parser.ast.StringLiteral;
import org.cwi.waebric.parser.ast.module.function.FunctionDef;
import org.cwi.waebric.parser.ast.module.site.Site;

/**
 * "module" ModuleId ModuleElement* -> Module
 * 
 * @author Jeroen van Schagen
 * @date 20-05-2009
 */
public class Module extends AbstractSyntaxNode {

	private ModuleId identifier;
	private AbstractSyntaxNodeList<Import> imports;
	private AbstractSyntaxNodeList<Site> sites;
	private AbstractSyntaxNodeList<FunctionDef> defs;
	
	/**
	 * Construct module
	 */
	public Module() {
		imports = new AbstractSyntaxNodeList<Import>();
		sites = new AbstractSyntaxNodeList<Site>();
		defs = new AbstractSyntaxNodeList<FunctionDef>();
	}
	
	/**
	 * Retrieve module identifier
	 * @see ModuleId
	 * @return Module identifier
	 */
	public ModuleId getIdentifier() {
		return identifier;
	}
	
	/**
	 * Modify module identifier
	 * @see ModuleId
	 * @param identifier Module identifier
	 */
	public void setIdentifier(ModuleId identifier) {
		this.identifier = identifier;
	}
	
	public boolean addImport(Import imprt) {
		return imports.add(imprt);
	}
	
	public List<Import> getImports() {
		return imports;
	}
	
	public boolean addSite(Site site) {
		return sites.add(site);
	}
	
	public List<Site> getSites() {
		return sites;
	}
	
	public boolean addFunctionDef(FunctionDef def) {
		return defs.add(def);
	}
	
	public List<FunctionDef> getFunctionDefinitions() {
		return defs;
	}
	
	@Override
	public AbstractSyntaxNode[] getChildren() {
		return new AbstractSyntaxNode[] {
			new StringLiteral("module"),
			identifier,
			imports,
			sites,
			defs
		};
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof Module) {
			Module module = (Module) obj;
			return this.identifier.equals(module.getIdentifier());
		}
		
		return false;
	}

}