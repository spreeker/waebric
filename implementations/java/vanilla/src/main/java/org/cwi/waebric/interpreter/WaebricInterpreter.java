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
import org.cwi.waebric.parser.ast.module.function.FunctionDef;
import org.jdom.Comment;
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
		
		for(Module module: ast.getRoot()) {
			if(hasMain(module)) {
				try {
					// Construct document
					Document document = interpretModule(module, ast);
					
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
	
	private Document interpretModule(Module module, AbstractSyntaxTree ast) {
		Document document = new Document();

		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Comment comment = new Comment("Compiled on: " + format.format(new Date()));
		document.addContent(comment);
	
		FunctionDef main = module.getFunctionDefinition("main");
		main.accept(new JDOMVisitor(document), new Object[]{});

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