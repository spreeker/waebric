package org.cwi.waebric;

import java.io.FileReader;
import java.util.List;

import org.cwi.waebric.parser.WaebricParser;
import org.cwi.waebric.parser.exception.SyntaxException;
import org.cwi.waebric.scanner.WaebricScanner;

public class MyWaebricCompiler {
	
	/**
	 * 
	 * @param args
	 */
	public static void main(String[] args) {
		if(args.length != 1) {
			System.err.println("Usage:\tparser -path");
		}

		try {
			System.out.println("Compiling " + args[0] + "...");
			
			long curr = System.currentTimeMillis();
			
			FileReader reader = new FileReader(args[0]);
			WaebricScanner scanner = new WaebricScanner(reader);
			scanner.tokenizeStream();
			
			WaebricParser parser = new WaebricParser(scanner);
			List<SyntaxException> se = parser.parseTokens();
			
			curr = System.currentTimeMillis() - curr;
			
			System.out.println("Executed in " + curr + "ms, with " + se.size() + " exceptions.");
			
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
