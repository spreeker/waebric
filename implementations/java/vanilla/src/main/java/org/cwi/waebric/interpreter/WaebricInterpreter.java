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

	/**
	 * Output directory
	 */
	public static final String OUTPUT_DIR = "output/";
	
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

			try {
				// Output document
				if(os != null) { outputDocument(document, os); }
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		
		// Interpret sites
		for(Site site: module.getSites()) {
			for(Mapping mapping: site.getMappings()) {
				Document document = new Document();
				
				// Start interpreting mark-up
				JDOMVisitor visitor = new JDOMVisitor(document, functions);
				mapping.getMarkup().accept(visitor);

				// Retrieve relative file path
				String path = getPath(mapping.getPath());
				
				try {
					// Output document
					OutputStream os = getOutputStream(path);
					outputDocument(document, os);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	
	/**
	 * Convert path in relative file path.
	 * @param path Path
	 * @return Relative file path
	 */
	public static String getPath(Path path) {
		String result = "";
		
		if(path instanceof Path.PathWithDir) {
			Directory dir = ((Path.PathWithDir) path).getDirName().getDirectory();
			for(AbstractSyntaxNode element: dir.getChildren()) {
				result += element.toString();
			}
		}
		
		result += "/";
		result += path.getFileName().getName().toString();
		result += ".";
		result += path.getFileName().getExt().toString();
		
		return OUTPUT_DIR + result;
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
		// Brand-mark document
		SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Comment comment = new Comment("Generated on " + format.format(new Date()));
		document.addContent(0, comment);
		
		// Output document
		XMLOutputter out = new XMLOutputter(Format.getPrettyFormat());
		out.output(document, os);
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