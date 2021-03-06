package org.cwi.waebric.interpreter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;

import org.cwi.waebric.parser.ast.AbstractSyntaxTree;
import org.cwi.waebric.parser.ast.markup.Markup;
import org.cwi.waebric.parser.ast.module.Module;
import org.cwi.waebric.parser.ast.module.Modules;
import org.cwi.waebric.parser.ast.module.function.FunctionDef;
import org.cwi.waebric.parser.ast.module.site.Mapping;
import org.cwi.waebric.parser.ast.module.site.Site;
import org.cwi.waebric.util.Environment;
import org.cwi.waebric.util.ModuleRegister;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;

/**
 * The interpreter converts WAEBRIC programs into XHTML.
 * @author Jeroen van Schagen
 * @date 10-06-2009
 */
public class WaebricInterpreter {
	
	/**
	 * Output stream in which "main" will be written
	 */
	public final OutputStream os;
	
	/**
	 * Construct interpreter without output stream, all sites will
	 * be stored in files. In case this constructor is written main
	 * will not be interpreted unless it is called from sites.
	 */
	public WaebricInterpreter() {
		os = null;
	}
	
	/**
	 * Construct interpreter based on output stream, this output stream
	 * will be used to write the main function. All sites will be stored
	 * in files.
	 * @param os
	 */
	public WaebricInterpreter(OutputStream os) {
		this.os = os;
	}
	
	/**
	 * Interpret all modules in AST and generate XHTML files.
	 * @param tree
	 */
	public void interpretProgram(AbstractSyntaxTree ast) {
		Modules modules = ModuleRegister.getInstance().loadDependencies(ast.getRoot());
		interpretModules(modules);
	}
	
	/**
	 * Write module contents to XHTML file.
	 * @param module
	 */
	public void interpretModules(Modules modules) {
		// Retrieve function definitions
		Collection<FunctionDef> functions = new ArrayList<FunctionDef>();
		for(Module module: modules) {
			functions.addAll(module.getFunctionDefinitions());
		}
		
		// Create environment
		Environment environment = new Environment();
		environment.defineFunctions(functions);
		
		// Interpret "main" function and write to output stream
		if(environment.isDefinedFunction("main")) {
			Document document = new Document();
			
			// Visit function
			WaebricEvaluator visitor = new WaebricEvaluator(document, environment);
			environment.getFunction("main").accept(visitor);

			try {
				// Output document
				if(os != null) {
					if(visitor.getCurrent() == null) { visitor.setCurrent(new Element("html")); }
					outputDocument(document, os); 
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		// Interpret sites of all used modules and store on file system
		for(Module module: modules) {
			for(Site site: module.getSites()) {
				for(Mapping mapping: site.getMappings()) {
					Document document = new Document();
					
					Markup markup = mapping.getMarkup();
					if(markup instanceof Markup.Tag) {
						markup = new Markup.Call(markup.getDesignator());
					}
					
					// Visit mapping
					WaebricEvaluator visitor = new WaebricEvaluator(document, environment);
					markup.accept(visitor);

					// Retrieve relative file path
					String path = mapping.getPath().getValue().toString();
					
					try {
						OutputStream os = getOutputStream(path);
						if(visitor.getCurrent() == null) { visitor.setCurrent(new Element("html")); }
						outputDocument(document, os); 
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	/**
	 * Convert requested output file path into output stream. In case
	 * the interpreted was construct with an output stream, this stream
	 * will be returned instead.
	 * 
	 * When a directory or file in the specified path does not exist
	 * they will be created, this might result in an IOException.
	 * 
	 * @param module
	 * @return
	 * @throws IOException
	 */
	private OutputStream getOutputStream(String path) throws IOException {
		int dirLength = path.lastIndexOf("/");
		
		// Create directories
		if(dirLength != -1) {
			File directory = new File(path.substring(0, dirLength));
			directory.mkdirs();
		}
		
		// Create file
		File file = new File(path);
		file.createNewFile(); // Create new file
		
		return new FileOutputStream(path);
	}
	
	/**
	 * Write document to output stream.
	 * @param document Document
	 * @param path Requested file path
	 * @throws IOException 
	 */
	private void outputDocument(Document document, OutputStream os) throws IOException {
		XMLOutputter out = new XMLOutputter(Format.getRawFormat());
		out.output(document, os);
	}
	
}