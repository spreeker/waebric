importPackage(java.io)
load('env.rhino.js')

load("../ast/WaebricEnvironment.js")
load("../ast/VisitorNode.js")
load("../ast/Node.js")
load("../ast/Path.js")
load("../ast/Module.js")
load("../ast/ModuleId.js")
load("../ast/Import.js")
load("../ast/Site.js")
load("../ast/Mapping.js")
load("../ast/FunctionDefinition.js")
load("../ast/Statement.js")
load("../ast/Designator.js")
load("../ast/Attribute.js")
load("../ast/Argument.js")
load("../ast/Markup.js")
load("../ast/Embed.js")
load("../ast/Embedding.js")
load("../ast/Expression.js")
load("../ast/KeyValuePair.js")
load("../ast/Predicate.js")
load("../ast/TextTail.js")
load("../ast/Type.js")
load("../ast/Variable.js")
load("../ast/Assignment.js")

load("../tokenizer/WaebricTokenizer.js");
load("../tokenizer/WaebricTokenizerResult.js");
load("../tokenizer/WaebricTokenizerException.js");
load("../tokenizer/WaebricCharacter.js");
load("../tokenizer/tokens/WaebricToken.js");
load("../tokenizer/tokens/WaebricTokenIdentifier.js");
load("../tokenizer/tokens/WaebricTokenComment.js");
load("../tokenizer/tokens/WaebricTokenKeyword.js");
load("../tokenizer/tokens/WaebricTokenNatural.js");
load("../tokenizer/tokens/WaebricTokenSymbol.js");
load("../tokenizer/tokens/WaebricTokenText.js");
load("../tokenizer/tokens/WaebricTokenWhitespace.js");

load("../parser/WaebricParser.js");
load("../parser/WaebricParserResult.js");
load("../parser/WaebricParserToken.js");
load("../parser/WaebricModuleParser.js");
load("../parser/WaebricSiteParser.js");
load("../parser/WaebricMarkupParser.js");
load("../parser/WaebricExpressionParser.js");
load("../parser/WaebricImportParser.js");
load("../parser/WaebricFunctionDefinitionParser.js");
load("../parser/WaebricStatementParser.js");
load("../parser/WaebricPredicateParser.js");
load("../parser/WaebricEmbeddingParser.js");

load("../validator/WaebricSemanticValidator.js");
load("../validator/WaebricSemanticValidatorException.js");
load("../validator/WaebricSemanticValidatorResult.js");
load("../validator/WaebricSemanticValidatorVisitor.js");
load("../validator/XHTML.js")

load('../interpreter/WaebricInterpreterResult.js')
load('../interpreter/WaebricInterpreter.js')
load("../interpreter/WaebricInterpreterVisitor.js")
load('../interpreter/DOM.js')

function loadProgram(){
    var fis = new FileInputStream('../programs/program.wae');
    var bis = new BufferedInputStream(fis);
    var dis = new DataInputStream(bis);
    
    var program = '';
    while (dis.available() != 0) {
        program += dis.readLine() + '\n';
    }
    fis.close();
    bis.close();
    dis.close();
    return program;
}

/**
 * Outputs the result of the tokenizer
 * @param {Object} tokens
 */
function writeTokenizerResult(tokens){
	var text = ""
	for(tokenIndex in tokens){		
		token = (tokens[tokenIndex])	
		text += (token.type + ' : ' + token.value + '\n');
	}
	
	var fw = new FileWriter('output_scanner.txt');
	var bf = new BufferedWriter(fw);
	bf.write(text);
	bf.close();
}

/**
 * Outputs the HTML code to a set of files
 * 
 * @param {Array} An array of XML documents
 */
function writeHTML(waebricEnvironments){
	for(var i = 0; i < waebricEnvironments.length; i++){	
		var waebricEnvironment = waebricEnvironments[i];
		var path = waebricEnvironment.path.toString();		
		
		//Create directory
		var pathElements = path.split('/');
		var directory = pathElements.slice(0, pathElements.length - 1).join('');
		
		var fDir = new File(directory);
		if (!fDir.exists()) {
			fDir.mkdir();
		}
		
		//Write file
		var fw = new FileWriter(waebricEnvironment.path);
		var bf = new BufferedWriter(fw);
		bf.write(waebricEnvironment.document);
		bf.close();
	}
}


function action(){	
	//Tokenizing
	var tokenizerResult = WaebricTokenizer.tokenizeAll(loadProgram());
	
	//Parsing
	var parserResult = WaebricParser.parse(tokenizerResult);
	print(parserResult.module.functionDefinitions)
	
	//Validating		
	var validationResult = WaebricSemanticValidator.validateAll(parserResult.module)	
		
	//Interpreting
	var interpreterResult = WaebricInterpreter.interpreteAll(parserResult.module);		
		
	//Output results
	//print(validationResult.exceptions)
	print(interpreterResult.environments[0].document)
	writeHTML(interpreterResult.environments);

}

action();