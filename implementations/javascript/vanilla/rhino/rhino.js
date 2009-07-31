importPackage(java.io)

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
load("../parser/WaebricParserToken.js");

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

function write(tokens){
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
function action(){	
	//try {
		var tokenizerResult = WaebricTokenizer.tokenizeAll(loadProgram());
		var parserResult = WaebricParser.parseAll(tokenizerResult);
		print('\n');
		print(parserResult.moduleId.identifier);
		print(parserResult.imports);
		print(parserResult.site.mappings);
		print('\n');
	//}catch(exception){
	//	print(exception)
	//}
}

action();