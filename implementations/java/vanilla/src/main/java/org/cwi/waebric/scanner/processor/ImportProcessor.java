//package org.cwi.waebric.scanner.processor;
//
//import java.io.FileNotFoundException;
//import java.io.FileReader;
//import java.io.IOException;
//import java.util.List;
//
//import org.cwi.waebric.WaebricKeyword;
//import org.cwi.waebric.WaebricSymbol;
//import org.cwi.waebric.scanner.LexicalException;
//import org.cwi.waebric.scanner.WaebricScanner;
//import org.cwi.waebric.scanner.token.WaebricToken;
//import org.cwi.waebric.scanner.token.WaebricTokenSort;
//
///**
// * Detect import tokens and attach their file contents to stream.
// * 
// * @author Jeroen van Schagen
// * @date 08-06-2009
// */
//public class ImportProcessor implements ILexicalProcessor {
//
//	private List<String> cachedModules;
//	
//	public ImportProcessor(List<String> cachedModules) {
//		this.cachedModules = cachedModules;
//	}
//	
//	@Override
//	public void process(List<WaebricToken> tokens, List<LexicalException> exceptions) {
//		for(int index = 0; index < tokens.size(); index++) {
//			if(tokens.get(index).getLexeme().equals(WaebricKeyword.IMPORT)) {
//				final String path = getPath(tokens, index);
//				if(! cachedModules.contains(path)) {
//					try {
//						// Convert imported module to token stream
//						FileReader reader = new FileReader(path);
//						WaebricScanner scanner = new WaebricScanner(reader);
//						exceptions.addAll(scanner.tokenizeStream());
//						tokens.addAll(scanner.getTokens());
//					} catch (FileNotFoundException e) {
//						exceptions.add(new InvalidModuleException(path));
//					} catch (IOException e) {
//						throw new InternalError(); // Should never occur
//					}
//				}
//			}
//		}
//	}
//	
//	/**
//	 * Convert token stream from offset into file path.
//	 * @param tokens
//	 * @param offset
//	 * @return
//	 */
//	public String getPath(List<WaebricToken> tokens, int offset) {
//		String path = "";
//		for(int index = offset+1; index < tokens.size(); index++) {
//			WaebricToken current = tokens.get(index);
//			if(current.getSort() == WaebricTokenSort.IDCON) {
//				if(! path.equals("")) { path += "/"; }
//				path += current.getLexeme().toString(); // Build path
//			} else if(! current.getLexeme().equals(WaebricSymbol.PERIOD)) {
//				break; // Invalid type, stop scanning
//			}
//		}
//		if(! path.equals("")) { path += ".wae"; }
//		return path;
//	}
//
//	/**
//	 * Exception thrown when an invalid module is being referenced.
//	 * 
//	 * @see FileNotFoundException
//	 * 
//	 * @author Jeroen van Schagen
//	 * @date 08-06-2009
//	 */
//	public class InvalidModuleException extends LexicalException {
//
//		/**
//		 * Generated serial ID
//		 */
//		private static final long serialVersionUID = 7223993886155789311L;
//
//		public InvalidModuleException(String path) {
//			super("Cannot open " + path + ", as the file was not found.");
//		}
//		
//	}
//	
//}