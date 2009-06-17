package org.cwi.waebric.interpreter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;

import org.cwi.waebric.ModuleRegister;
import org.cwi.waebric.parser.ast.AbstractSyntaxTree;
import org.cwi.waebric.parser.ast.module.Module;
import org.cwi.waebric.parser.ast.module.ModuleId;
import org.cwi.waebric.parser.ast.module.Modules;
import org.cwi.waebric.parser.ast.module.function.FunctionDef;
import org.cwi.waebric.parser.ast.module.site.Site;

import org.jdom.Comment;
import org.jdom.Document;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 * The interpreter converts WAEBRIC programs into XHTML.
 * @author Jeroen van Schagen
 * @date 10-06-2009
 */
public class WaebricInterpreter {

	public static final String OUTPUT_DIR = "output/";
	
	/**
	 * Interpret AST and generate HTML file(s).
	 * @param tree
	 */
	public void interpretProgram(AbstractSyntaxTree ast) {
		for(Module module: ast.getRoot()) {
			interpretModule(module);
		}
	}
	
	/**
	 * Convert module in JDOM document.
	 * @see JDOMVisitor
	 * @param module
	 * @param modules
	 */
	public void interpretModule(Module module) {
		// Retrieve function definitions
		Modules dependancies = ModuleRegister.getInstance().loadDependancies(module).getRoot();
		Collection<FunctionDef> functions = new ArrayList<FunctionDef>();
		for(Module dependancy: dependancies) {
			functions.addAll(dependancy.getFunctionDefinitions());
		}
		
		// Interpret "main" function
		if(containsMain(module)) {
			Document document = new Document();
			JDOMVisitor visitor = new JDOMVisitor(document, functions);
			
			// Start attaching content by interpreting "main" function
			FunctionDef main = visitor.getFunction("main");
			visitor.visit(main);
			
			// Convert document object to XHTML file
			writeFile(document, getOutputPath(module.getIdentifier()));
		}
		
		// Interpret sites
		for(Site site: module.getSites()) {
			// TODO: Do this when I'm not bored.
			System.out.println(site.toString()); // LOLOL
		}
	}
	
	private void writeFile(Document document, String path) {
		try {
			// Brand-mark document with some awesome comment
			SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Comment comment = new Comment("Compiled by JWHIZZLE on: " + format.format(new Date()));
			document.addContent(0, comment);
			
			File file = new File(path);
			file.createNewFile(); // Create new file
			OutputStream os = new FileOutputStream(file);
			
			// Transform document in file
			XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());
			out.output(document, os);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Convert module identifier out output path.
	 * @param identifier
	 * @return
	 */
	public static String getOutputPath(ModuleId identifier) {
		String name = identifier.get(identifier.size()-1).getToken().getLexeme().toString();
		return OUTPUT_DIR + name + ".html";
	}
	
	/**
	 * Check if module contains a main function.
	 * @param module
	 * @return
	 */
	public static boolean containsMain(Module module) {
		for(FunctionDef function: module.getFunctionDefinitions()) {
			if(function.getIdentifier().getName().equals("main")) { return true; }
		}
		
		return false;
	}

}