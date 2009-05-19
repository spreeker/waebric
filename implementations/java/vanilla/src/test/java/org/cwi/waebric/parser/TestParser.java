package org.cwi.waebric.parser;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.cwi.waebric.scanner.WaebricScanner;
import org.junit.Test;

public class TestParser {

	private final String PROGRAM_PATH = "src/test/waebric/helloworld.waebric";

	@Test
	public void testScanner() {
		try {
			FileReader reader = new FileReader(PROGRAM_PATH);
			WaebricScanner scanner = new WaebricScanner(reader);
			scanner.tokenizeStream();

			WaebricParser parser = new WaebricParser(scanner);
			parser.parseTokens();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
