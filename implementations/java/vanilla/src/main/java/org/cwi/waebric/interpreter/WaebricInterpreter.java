package org.cwi.waebric.interpreter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.cwi.waebric.ModuleRegister;
import org.cwi.waebric.parser.ast.AbstractSyntaxTree;
import org.cwi.waebric.parser.ast.module.Module;
import org.cwi.waebric.parser.ast.module.ModuleId;
import org.cwi.waebric.parser.ast.module.Modules;

import org.jdom.Document;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 * Interpreter converts the Abstract Syntax Tree of a Waebric
 * program into XHTML code.
 * 
 * @author Jeroen van Schagen
 * @date 10-06-2009
 */
public class WaebricInterpreter {
	
	public static final String OUTPUT_DIR = "output/";

	/**
	 * Interpret AST and generate HTML file.
	 * @param tree
	 */
	public void interpretProgram(AbstractSyntaxTree ast) {
		ModuleRegister.getInstance().loadDependancies(ast); // Retrieve all dependent modules
		
		Modules modules = ast.getRoot();
		for(Module module: modules) {
			if(hasMain(module)) {
				try {
					// Construct document
					Document document = new Document();
					new JDOMVisitor(modules).visit(module, new Object[] { document });

					File file = new File(getOutputPath(module.getIdentifier()));
					file.createNewFile(); // Create file
					OutputStream os = new FileOutputStream(file);
					
					// Transform document in file
					XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());
					out.output(document, os);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Check if module has a main function.
	 * @param module
	 * @return
	 */
	public static boolean hasMain(Module module) {
		return module.getFunctionDefinition("main") != null;
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

}