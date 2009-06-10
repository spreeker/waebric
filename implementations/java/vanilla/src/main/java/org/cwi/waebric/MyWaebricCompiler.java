package org.cwi.waebric;

import java.io.FileReader;
import java.util.List;

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
		if(args.length != 1) {
			System.err.println("Usage:\tparser -path");
		} else {
			try {
				System.out.println("Compiling " + args[0] + "...");

				FileReader reader = new FileReader(args[0]);
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
				}
			} catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
}
