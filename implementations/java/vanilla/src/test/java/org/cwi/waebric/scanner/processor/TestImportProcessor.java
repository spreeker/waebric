package org.cwi.waebric.scanner.processor;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import org.cwi.waebric.WaebricKeyword;
import org.cwi.waebric.WaebricSymbol;
import org.cwi.waebric.scanner.token.WaebricToken;
import org.cwi.waebric.scanner.token.WaebricTokenSort;
import org.junit.Test;

public class TestImportProcessor {

	@Test
	public void testImport() throws FileNotFoundException {
		// Create token stream: import src.test.waebric.module.sub
		ArrayList<WaebricToken> tokens = new ArrayList<WaebricToken>();
		tokens.add(new WaebricToken(WaebricKeyword.IMPORT, WaebricTokenSort.KEYWORD, 0, 0));
		tokens.add(new WaebricToken("src", WaebricTokenSort.IDCON, 0, 0));
		tokens.add(new WaebricToken(WaebricSymbol.PERIOD, WaebricTokenSort.CHARACTER, 0, 0));
		tokens.add(new WaebricToken("test", WaebricTokenSort.IDCON, 0, 0));
		tokens.add(new WaebricToken(WaebricSymbol.PERIOD, WaebricTokenSort.CHARACTER, 0, 0));
		tokens.add(new WaebricToken("waebric", WaebricTokenSort.IDCON, 0, 0));
		tokens.add(new WaebricToken(WaebricSymbol.PERIOD, WaebricTokenSort.CHARACTER, 0, 0));
		tokens.add(new WaebricToken("module", WaebricTokenSort.IDCON, 0, 0));
		tokens.add(new WaebricToken(WaebricSymbol.PERIOD, WaebricTokenSort.CHARACTER, 0, 0));
		tokens.add(new WaebricToken("sub", WaebricTokenSort.IDCON, 0, 0));
		
		ArrayList<LexicalException> exceptions = new ArrayList<LexicalException>();
		ImportProcessor processor = new ImportProcessor();
		processor.process(tokens, exceptions);
		
		assertEquals(WaebricKeyword.MODULE, tokens.get(10).getLexeme());
	}
	
}
