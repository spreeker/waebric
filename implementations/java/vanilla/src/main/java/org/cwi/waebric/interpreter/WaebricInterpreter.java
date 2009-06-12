package org.cwi.waebric.interpreter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.cwi.waebric.ModuleRegister;
import org.cwi.waebric.parser.ast.AbstractSyntaxTree;
import org.cwi.waebric.parser.ast.module.Module;
import org.cwi.waebric.parser.ast.module.ModuleId;
import org.cwi.waebric.parser.ast.module.Modules;
import org.cwi.waebric.parser.ast.module.function.FunctionDef;

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
	 * Interpret AST and generate HTML file.
	 * @param tree
	 */
	public void interpretProgram(AbstractSyntaxTree ast) {
		// Retrieve collection of modules (including transitive dependencies)
		Modules modules = ModuleRegister.getInstance().loadDependancies(ast).getRoot();
		for(Module module: modules) {
			if(hasMain(module)) {
				try {
					// Interpret each module that contains a main function
					Document document = interpretModule(module, modules);

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

		// Brand-mark document
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Comment comment = new Comment("Compiled on: " + format.format(new Date()));
		document.addContent(comment);
	
		JDOMVisitor visitor = new JDOMVisitor(modules); // Construct visitor
		FunctionDef main = module.getFunctionDefinition("main"); // Retrieve main function
		
		// Fill document using the JDOM visitor
		visitor.visit(main, new Object[] { 
				document, // Document reference, used to create sub-elements
				new Object[]{} // Main function takes no call arguments
			});
		
		return document;
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