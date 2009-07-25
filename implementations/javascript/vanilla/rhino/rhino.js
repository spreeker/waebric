importPackage(java.io)

load("../tokenizer/WaebricToken.js");
load("../tokenizer/WaebricTokenIdentifier.js");
load("../tokenizer/WaebricTokenComment.js");
load("../tokenizer/WaebricTokenKeyword.js");
load("../tokenizer/WaebricTokenNatural.js");
load("../tokenizer/WaebricTokenSymbol.js");
load("../tokenizer/WaebricTokenText.js");
load("../tokenizer/WaebricTokenWhitespace.js");

load("../tokenizer/WaebricCharacter.js");
load("../tokenizer/WaebricTokenizer.js");

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
		//print(token.type + ' : ' + token.value);
	}
	
	var fw = new FileWriter('output_scanner.txt');
	var bf = new BufferedWriter(fw);
	bf.write(text);
	bf.close();
}

action();