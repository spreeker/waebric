package org.cwi.waebric.interpreter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import org.cwi.waebric.parser.ast.AbstractSyntaxTree;
import org.cwi.waebric.parser.ast.module.Module;
import org.cwi.waebric.parser.ast.module.ModuleId;
import org.cwi.waebric.parser.ast.module.Modules;

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
	 * Interpret AST and generate HTML file.
	 * @param tree
	 */
	public void interpretProgram(AbstractSyntaxTree ast) {
		for(Module module: ast.getRoot()) {
			try {
				// Interpret each module that contains a main function
				Document document = interpretModule(module, ast.getRoot());

				File file = new File(getOutputPath(module.getIdentifier()));
				file.createNewFile(); // Create file, in case it doesn't exist yet
				OutputStream os = new FileOutputStream(file);
				
				// Transform document in file
				XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());
				out.output(document, os);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Convert module in JDOM document.
	 * @see JDOMVisitor
	 * @param module
	 * @param modules
	 * @return
	 */
	public Document interpretModule(Module module, Modules modules) {
		Document document = new Document();
		new JDOMVisitor(document).visit(module, new Object[]{});
		return document;
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