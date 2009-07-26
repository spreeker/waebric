importPackage(java.io)

load("../tokenizer/WaebricTokenizer.js");
load("../tokenizer/WaebricCharacter.js");

load("../tokenizer/tokens/WaebricToken.js");
load("../tokenizer/tokens/WaebricTokenIdentifier.js");
load("../tokenizer/tokens/WaebricTokenComment.js");
load("../tokenizer/tokens/WaebricTokenKeyword.js");
load("../tokenizer/tokens/WaebricTokenNatural.js");
load("../tokenizer/tokens/WaebricTokenSymbol.js");
load("../tokenizer/tokens/WaebricTokenText.js");
load("../tokenizer/tokens/WaebricTokenWhitespace.js");

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


function action(){
	var tokenizer = new WaebricTokenizer();
	tokenizer.tokenizeAll(loadProgram());

	var text = ""
	for(tokenIndex in tokenizer.lexemes){
		token = (tokenizer.lexemes[tokenIndex])	
		text += (token.type + ' : ' + token.value + '\n');
	}
	
	var fw = new FileWriter('output_scanner.txt');
	var bf = new BufferedWriter(fw);
	bf.write(text);
	bf.close();
}

action();