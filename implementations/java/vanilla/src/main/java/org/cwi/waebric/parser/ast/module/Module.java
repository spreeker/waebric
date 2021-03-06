package org.cwi.waebric.parser.ast.module;

import java.util.List;

import org.cwi.waebric.parser.ast.SyntaxNode;
import org.cwi.waebric.parser.ast.INodeVisitor;
import org.cwi.waebric.parser.ast.SyntaxNodeList;
import org.cwi.waebric.parser.ast.module.function.FunctionDef;
import org.cwi.waebric.parser.ast.module.site.Site;

/**
 * "module" ModuleId ModuleElement* -> Module
 * 
 * @author Jeroen van Schagen
 * @date 20-05-2009
 */
public class Module extends SyntaxNode {

	private ModuleId identifier;
	private SyntaxNodeList<Import> imports;
	private SyntaxNodeList<Site> sites;
	private SyntaxNodeList<FunctionDef> defs;
	
	/**
	 * Construct module
	 */
	public Module() {
		imports = new SyntaxNodeList<Import>();
		sites = new SyntaxNodeList<Site>();
		defs = new SyntaxNodeList<FunctionDef>();
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
	
	/**
	 * 
	 * @param imprt
	 * @return
	 */
	public boolean addImport(Import imprt) {
		return imports.add(imprt);
	}
	
	/**
	 * 
	 * @return
	 */
	public List<Import> getImports() {
		return imports.clone();
	}
	
	/**
	 * 
	 * @param site
	 * @return
	 */
	public boolean addSite(Site site) {
		return sites.add(site);
	}
	
	/**
	 * 
	 * @return
	 */
	public List<Site> getSites() {
		return sites.clone();
	}
	
	/**
	 * 
	 * @param def
	 * @return
	 */
	public boolean addFunctionDef(FunctionDef def) {
		return defs.add(def);
	}
	
	/**
	 * 
	 * @return
	 */
	public List<FunctionDef> getFunctionDefinitions() {
		return defs.clone();
	}
	
	@Override
	public SyntaxNode[] getChildren() {
		return new SyntaxNode[] {
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
	
	@Override
	public <T> T accept(INodeVisitor<T> visitor) {
		return visitor.visit(this);
	}

}