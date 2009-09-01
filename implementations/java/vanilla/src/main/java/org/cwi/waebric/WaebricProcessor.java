package org.cwi.waebric;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.Reader;
import java.util.List;

import org.cwi.waebric.checker.SemanticException;
import org.cwi.waebric.checker.WaebricChecker;
import org.cwi.waebric.interpreter.WaebricInterpreter;
import org.cwi.waebric.lexer.LexicalException;
import org.cwi.waebric.lexer.WaebricScanner;
import org.cwi.waebric.parser.SyntaxException;
import org.cwi.waebric.parser.WaebricParser;

public class WaebricProcessor {
	
	/**
	 * 
	 * @param args
	 * @throws FileNotFoundException 
	 */
	public static void main(String[] args) throws FileNotFoundException {
		Reader is = null; // Input stream
		OutputStream os = System.out; // Output stream
		
		if(args.length == 0) {
			is = new InputStreamReader(System.in);
		} else if(args.length > 0) {
			is = new FileReader(args[0]); // Retrieve path from arguments
			System.out.println("Processing... " + args[0]);
			if(args.length > 1) {
				try {
					os = getOutputStream(args[1]);
					System.out.println("Storing... " + args[1]);
				} catch(IOException e) {
					e.printStackTrace();
					System.out.println("Could not write to " + args[1] + " writing to console instead.");
				}
			}
		} else {
			System.err.println("Usage:\tparser || parser -path");
			System.exit(1);
		}

		try {
			WaebricScanner scanner = new WaebricScanner(is);
	
			long curr = System.currentTimeMillis();
			List<LexicalException> le = scanner.tokenizeStream();
			long scan_time = System.currentTimeMillis() - curr;
			System.out.println("\nScanned in " + scan_time + "ms, with " + le.size() + " lexical exceptions.");
			
			if(le.size() == 0) {
				System.out.println(scanner.getTokens().toString());
			} else {
				for(LexicalException exception : le) {
					exception.printStackTrace();
				}
				
				return; // Quit application
			}

			WaebricParser parser = new WaebricParser(scanner.iterator());
			
			curr = System.currentTimeMillis();
			List<SyntaxException> se = parser.parseTokens();
			long parse_time = System.currentTimeMillis() - curr;
			System.out.println("\nParsed in " + parse_time + "ms, with " + se.size() + " syntax exceptions.");

			if(se.size() == 0) {
				System.out.println(parser.getAbstractSyntaxTree().toString());
			} else {
				for(SyntaxException exception : se) {
					exception.printStackTrace();
				}
				
				return; // Quit application
			}
			
			WaebricChecker checker = new WaebricChecker();
			
			curr = System.currentTimeMillis();
			List<SemanticException> seme = checker.checkAST(parser.getAbstractSyntaxTree());
			long check_time = System.currentTimeMillis() - curr;
			System.out.println("\nChecked in " + check_time + "ms, with " + seme.size() + " semantic exceptions.");

			if(seme.size() == 0) {
				System.out.println("Program is conform to semantic restrictions.");
			} else {
				for(SemanticException exception : seme) {
					exception.printStackTrace();
				}
			}
			
			WaebricInterpreter interpreter = new WaebricInterpreter(os);

			System.out.println("\nInterpreting program and writing to output stream:\n");
			curr = System.currentTimeMillis();
			interpreter.interpretProgram(parser.getAbstractSyntaxTree());
			long interpret_time = System.currentTimeMillis() - curr;
			System.out.println("Interpreted in " + interpret_time + "ms.");
		} catch(Exception e) {
			e.printStackTrace();
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
	public static OutputStream getOutputStream(String path) throws IOException {
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