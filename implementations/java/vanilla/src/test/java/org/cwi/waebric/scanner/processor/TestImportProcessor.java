package org.cwi.waebric.scanner.processor;

import static org.junit.Assert.assertEquals;

import java.io.FileNotFoundException;
import java.util.ArrayList;

import org.cwi.waebric.WaebricKeyword;
import org.cwi.waebric.WaebricSymbol;
import org.cwi.waebric.scanner.processor.ImportProcessor.InvalidModuleException;
import org.cwi.waebric.scanner.token.WaebricToken;
import org.cwi.waebric.scanner.token.WaebricTokenSort;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class TestImportProcessor {
	
	private ArrayList<String> cachedModules;
	
	@Before
	public void setUp() {
		cachedModules = new ArrayList<String>();
		cachedModules.add("cached");
	}
	
	@After
	public void tearDown() {
		cachedModules.clear();
		cachedModules = null;
	}

	@Test
	public void testImport() throws FileNotFoundException {
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
		ImportProcessor processor = new ImportProcessor(cachedModules);
		processor.process(tokens, exceptions);
		
		// Module is loaded after token stream (10 elements long)
		assertEquals(WaebricKeyword.MODULE, tokens.get(10).getLexeme());
	}
	
	@Test
	public void testCachedImport() throws FileNotFoundException {
		ArrayList<WaebricToken> tokens = new ArrayList<WaebricToken>();	
		tokens.add(new WaebricToken(WaebricKeyword.IMPORT, WaebricTokenSort.KEYWORD, 0, 0));
		tokens.add(new WaebricToken("cached", WaebricTokenSort.IDCON, 0, 0));
		
		ArrayList<LexicalException> exceptions = new ArrayList<LexicalException>();
		ImportProcessor processor = new ImportProcessor(cachedModules);
		processor.process(tokens, exceptions);
		
		assertEquals(2, tokens.size()); // Only "import" cached is parsed
		assertEquals(1, exceptions.size()); // Cached.wae does not exist
		assertEquals(InvalidModuleException.class, exceptions.get(0).getClass());
	}
	
}
