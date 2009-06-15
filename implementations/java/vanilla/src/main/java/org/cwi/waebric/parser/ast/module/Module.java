package org.cwi.waebric.parser.ast.module;

import java.util.List;

import org.cwi.waebric.parser.ast.AbstractSyntaxNode;
import org.cwi.waebric.parser.ast.INodeVisitor;
import org.cwi.waebric.parser.ast.NodeList;
import org.cwi.waebric.parser.ast.module.function.FunctionDef;
import org.cwi.waebric.parser.ast.module.site.Site;
import org.cwi.waebric.parser.ast.token.StringLiteral;

/**
 * "module" ModuleId ModuleElement* -> Module
 * 
 * @author Jeroen van Schagen
 * @date 20-05-2009
 */
public class Module extends AbstractSyntaxNode {

	private ModuleId identifier;
	private NodeList<Import> imports;
	private NodeList<Site> sites;
	private NodeList<FunctionDef> defs;
	
	/**
	 * Construct module
	 */
	public Module() {
		imports = new NodeList<Import>();
		sites = new NodeList<Site>();
		defs = new NodeList<FunctionDef>();
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
	
	/**
	 * 
	 * @param designator
	 * @return
	 */
	public FunctionDef getFunctionDefinition(String designator) {
		for(FunctionDef def: defs) {
			if(def.getIdentifier().getToken().getLexeme().equals(designator)) {
				return def;
			}
		}
		
		return null;
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
	
	@Override
	public void accept(INodeVisitor visitor, Object[] args) {
		visitor.visit(this, args);
	}

}