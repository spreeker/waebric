package org.cwi.waebric;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;

import org.cwi.waebric.checker.SemanticException;
import org.cwi.waebric.checker.WaebricChecker;
import org.cwi.waebric.parser.WaebricParser;
import org.cwi.waebric.parser.exception.SyntaxException;
import org.cwi.waebric.scanner.LexicalException;
import org.cwi.waebric.scanner.WaebricScanner;

public class MyWaebricCompiler {
	
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		String path = "";
		if(args.length == 0) {
			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			System.out.println("Enter file path: ");
			try {
				path = br.readLine();
			} catch (IOException e) {
				e.printStackTrace();
				System.exit(1);
			}
		} else if(args.length == 1) {
			path = args[0]; // Retrieve path from arguments
		} else {
			System.err.println("Usage:\tparser || parser -path");
			System.exit(1);
		}

		try {
			System.out.println("Compiling " + path + "...");

			FileReader reader = new FileReader(path);
			WaebricScanner scanner = new WaebricScanner(reader);
			
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
				
				return; // Quit application
			}
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
}
