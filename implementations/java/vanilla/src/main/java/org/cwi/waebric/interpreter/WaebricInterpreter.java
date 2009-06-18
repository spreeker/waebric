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
import org.cwi.waebric.parser.ast.AbstractSyntaxNode;
import org.cwi.waebric.parser.ast.AbstractSyntaxTree;
import org.cwi.waebric.parser.ast.module.Module;
import org.cwi.waebric.parser.ast.module.ModuleId;
import org.cwi.waebric.parser.ast.module.Modules;
import org.cwi.waebric.parser.ast.module.function.FunctionDef;
import org.cwi.waebric.parser.ast.module.site.Directory;
import org.cwi.waebric.parser.ast.module.site.Mapping;
import org.cwi.waebric.parser.ast.module.site.Path;
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
	public final OutputStream os;
	
	/**
	 * Construct regular interpreter, all interpreted modules will be 
	 * stored in a file relative to their module identifier.
	 */
	public WaebricInterpreter() {
		os = null;
	}
	
	/**
	 * Construct interpreter based on output stream, all interpreted
	 * modules will be written to this output stream.
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
		for(Module module: ast.getRoot()) {
			interpretModule(module);
		}
	}
	
	/**
	 * Write module contents to XHTML file.
	 * @param module
	 */
	public void interpretModule(Module module) {
		// Retrieve function definitions
		Collection<FunctionDef> functions = new ArrayList<FunctionDef>();
		Modules dependancies = ModuleRegister.getInstance().loadDependancies(module).getRoot();
		for(Module dependancy: dependancies) {
			functions.addAll(dependancy.getFunctionDefinitions());
		}
		
		// Interpret "main" function
		if(containsMain(module)) {
			Document document = new Document();
			
			// Start interpreting main function
			JDOMVisitor visitor = new JDOMVisitor(document, functions);
			visitor.getFunction("main").accept(visitor);

			// Output document
			outputDocument(document, getPath(module.getIdentifier()));
		}
		
		// Interpret sites
		for(Site site: module.getSites()) {
			for(Mapping mapping: site.getMappings()) {
				Document document = new Document();
				
				// Start interpreting mark-up
				JDOMVisitor visitor = new JDOMVisitor(document, functions);
				mapping.getMarkup().accept(visitor);
				
				// Output document
				outputDocument(document, getPath(mapping.getPath()));
			}
		}
	}
	
	public static String getPath(Path path) {
		String result = "";
		
		if(path instanceof Path.PathWithDir) {
			Directory dir = ((Path.PathWithDir) path).getDirName().getDirectory();
			for(AbstractSyntaxNode node: dir.getChildren()) {
				result += node.toString();
			}
		}
		
		result += "/";
		result += path.getFileName().getName().toString();
		result += ".";
		result += path.getFileName().getExt().toString();
		
		return OUTPUT_DIR + result;
	}
	
	public static String getPath(ModuleId identifier) {
		String name = identifier.get(identifier.size()-1).getToken().getLexeme().toString();
		return OUTPUT_DIR + name + ".html";
	}
	
	/**
	 * Convert requested output file path into output stream. In case
	 * the interpreted was construct with an output stream, this stream
	 * will be returned instead.
	 * @param module
	 * @return
	 * @throws IOException
	 */
	private OutputStream getOutputStream(String path) throws IOException {
		if(this.os != null) { return os; }
		else {
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
	}
	
	/**
	 * Write document to output stream.
	 * @param document Document
	 * @param path Requested file path
	 */
	private void outputDocument(Document document, String path) {
		try {
			// Brand-mark document with some awesome comment
			SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Comment comment = new Comment("Compiled by JWHIZZLE on: " + format.format(new Date()));
			document.addContent(0, comment);
			
			// Output document
			OutputStream os = getOutputStream(path);
			XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());
			out.output(document, os);
		} catch (IOException e) {
			e.printStackTrace();
		}
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