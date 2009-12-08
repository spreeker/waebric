"""
Parser Module.

Waebric Parser Module.

-uses tokenize generator to tokenize source.
-recursive decent parser for waebric source.
-parser class keeps state of currentToken,peekedTokens.
-Builds ast tree of waebric source.

In error.py there are variables:
DEBUG,SHOWTOKENS,SHOWPARSER magic which enable detailed
logging in parser.log

-trace decorator enables logging function calls
with the @trace decorator in parser.log.

convention:

For each parser function the first token should be ready.
Therefore each parser function should read ahead the first
token for the next parser function.
"""

import logging
import tokenize
import re

from error import SyntaxError
from error import trace,logToken
from error import DEBUG, SHOWTOKENS, SHOWPARSER
from keywords import keywords
from token import *


class Parser(object):

    ast = []

    tokens = []
    peekedTokens = []
    currentToken = []

    def __init__(self, tokens=None):
       if DEBUG and SHOWPARSER:
           logging.debug(type(self))

    def setTokens(self, newtokens):
        # needed test for test code.
        self.tokens = newtokens
        self.peekedTokens = []
        self.currentToken = []

    def matchLexeme(self, lexeme):
        """ return boolean if current token matches lexeme """
        if self.currentToken[1]:
            return self.currentToken[1] == lexeme
        return False

    def matchTokensort(self, tokensort):
        """ return boolean if current token matches tokensort """
        return self.currentToken[0] == tokensort

    def check(self, expected="", tokensort=None, lexeme=None):
        """ check if current token is tokensort of lexeme """
        if lexeme:
           if not self.currentToken[1] == lexeme:
                raise SyntaxError(self.currentToken, expected=expected)
        if tokensort:
            if not self.currentToken[0] == tokensort:
                raise SyntaxError(self.currentToken, expected=expected)

    def peek(self, x=1, tokensort="", lexeme=""):
        """lookahead x tokens in advance, returns true
           if tokensort and or lexeme matches peekedtoken.
        """
        d = x - len(self.peekedTokens)
        if d > -1:
            try:
                for i in range(d+1):
                    self.peekedTokens.append(self.tokens.next())
            except StopIteration:
                return False

        peekToken = self.peekedTokens[x-1]
        if tokensort:
            if not peekToken[0] == tokensort:
                return False
        if lexeme:
            if not peekToken[1] == lexeme:
                return False
        return True

    def hasnext(self):
        return self.peek()

    def next(self, expected="", tokensort="", lexeme=""):
        """
        Get the next token, if tokensort or lexeme is defined
        checks if next token matches tokensort or lexeme.
        """

        if self.peekedTokens:
            self.currentToken = self.peekedTokens.pop(0)
        else:
            try:
                self.currentToken = self.tokens.next()
            except StopIteration:
                logging.debug(" no next token available ")
                raise StopIteration

        if tokensort or lexeme:
            expected = "'%s%s%s'" % (expected, tokensort, lexeme)
            self.check(expected, tokensort, lexeme)

        if DEBUG and SHOWTOKENS:
            logToken(*self.currentToken)

        return self.currentToken

    def __repr__(self):
        return "%s at '%s'" % ( str(type(self)), self.currentToken[1])

@trace
def parseWaebrick(parser):
    logging.debug("--------------------")
    logging.debug('start parsing tokens')
    logging.debug("--------------------")
    parser.next()
    parseModule(parser)

@trace
def parseModule(parser):
    """parse module statements"""
    parser.check('Module ModuleID', lexeme=keywords['MODULE'] )

    #create a new ast module.
    #parse the module identifier.
    parser.next()
    module = Module(parseModuleId(parser))

    # while there are more tokens
    # parse for Site , Function and Import statements
    while(parser.hasnext()):
        if parser.matchLexeme(keywords['IMPORT']):
            modules.addImport(parseImport(parser))
        elif parser.matchLexeme(keywords['DEF']):
            modules.addFunction(parseFunction(parser))
        elif parser.matchLexeme(keywords['SITE']):
            modules.addSite(parseSite(parser))
        elif parser.matchTokensort(ENDMARKER):
            return
        else:
           raise SyntaxError(parser.currentToken,
        expected="import, def, site, newline" )


@trace
def parseModuleId(parser):
    """Parse the module identifier """
    # AST new module id.
    moduleID = []
    parser.check("Module Identifier", tokensort=NAME)
    moduleID.append(parser.currentToken)

    while(parser.peek(lexeme='.')):
        parser.next() #skip .
        parser.next("Module Identifier", tokensort=NAME)
        moduleID.append(currentToken)

    parser.next()
    return moduleID

@trace
def parseImport(parser):
    parser.check(lexeme=keywords['IMPORT'])
    parser.next()
    return parseModuleId(parser)

@trace
def parseSite(parser):
    # AST, mappings.
    parser.check("Site Defenition", lexeme=keywords['SITE'])
    # check mapping
    mappings = Mappings()
    while parser.hasnext():
        mappings.addMapping(parseMapping(parser))
        if parser.matchLexeme(keywords['END']):
            parser.next()
            return mappings
    raise SyntaxError(currentToken, expected="missing site ending END")

@trace
def parseMapping(parser):
    """ parse content in between site and end."""
    # AST new mapping
    path = parsePath(parser)
    parser.check( lexeme=":" )
    parser.next()
    markup = parseMarkup(parser)
    parser.next(';')
    return Mapping(path, markup)

@trace
def parsePath(parser):
    path = ""
    if parser.peek(lexeme="/") or parser.peek(x=2,lexeme="/"):
        dir = parseDirectory(parser)
    fileName = parseFileName(parser)
    #logging.debug(dir+path)
    return Path(dir, fileName)

@trace
def parseDirectory(parser):
    directory = ""
    if isPathElement(parser):
        directory += parser.currentToken[1]

    while parser.next():
        if parser.matchLexeme("/"):
            if directory:
                directory += "/"
            parser.next()

        if parser.peek(lexeme="."):
            return directory #return , we're in a filename

        if isPathElement(parser):
            directory += parser.currentToken[1]
        else:
            raise SyntaxError(parser.currentToken, expected="path element")
    raise SyntaxError(parser.currentToken, expected="directory path")

@trace
def parseFileName(parser):
    parser.check(tokensort=NAME, expected="File Name")
    name = parser.currentToken[1]
    parser.next(lexeme = ".")
    parser.next(tokensort=NAME, expected="File extension") # extension
    name = "%s.%s" % (name, parser.currentToken[1])
    parser.next()
    return name

@trace
def isPathElement(parser):
    regex = re.compile(r"(.* .*)|(.*\t.*)|(.*\n.*)|(.*\r.*)|(.*/.*)|(.*\\..*)|(.*\\\\.*)")
    if regex.match(parser.currentToken[1]):
        return False
    return True

@trace
def parseFunction(parser):
    parser.check(expected="function defenition, def", lexeme=keywords['DEF'])
    ## parse identifier
    parser.next(tokensort=NAME)
    name = parser.currentToken[1]
    parser.next()
    ## parse formals
    arguments = parseArguments(parser)
    function = Function(name,arguments)
    ## parser statements.

    while(parser.hasnext()):
        function.addStatement(parseStatement(parser))

        if parser.matchLexeme(keywords['END']):
            parser.next()
            return function

    raise SyntaxError(parser.currentToken,
            expected="""END, Missing function ending END""")


"""
Symbol expression,
Text,
Natural number,
Variable,
List,
Record,
expression + expression
"""
@trace
def parseExpression(parser):
    expression = None
    if parser.matchLexeme("'"): #symbol.
        parser.next("symbol name", tokensort=NAME)
        expression = TextExpression(parser.currentToken[1])
    elif parser.matchTokensort(STRING): #data string
        expression = TextExpression(parser.currentToken[1])
    elif parser.matchTokensort(NUMBER): #number stuff
        expression = NumberExpression(parser.currentToken[1])
    elif parser.matchLexeme("["):
        expression = parseList(parser)
    elif parser.matchLexeme("{"):
        expression = parseRecord(parser)
    elif parser.matchTokensort(NAME):
        #Variable
        expression = Name(parser.currentToken[1])
        #Check for Variable DOT Field expression
        if parser.peek(lexeme=".") and parser.peek(x=2, tokensort=NAME):
            expression = Field(expression, parser.currentToken[1])
            while parser.peek(lexeme=".") and parser.peek(x=2, tokensort=NAME):
                parser.next(lexeme=".") #skip.
                parser.next(expected="NAME", tokensort=NAME)
                expression =  Field(expression,parser.currentToken[1])
        #Check for expression PLUS expression
        if parser.peek(lexeme="+") and expression:
            #parse a + expression left and right.
            left = expression
            parser.next() # skip +
            right = parseExpression(parser)
            expression = CatExpression(left,right)

    logging.debug(expression)

    if not expression:
        raise SyntaxError(parser.currentToken,
            expected="Expression: symbol, string, number, list, record, name.field")

    parser.next()
    return expression

def parseList(parser):
    parser.check("List opening '[' ", lexeme="[")
    listExpression = ListExpression()

    while parser.next():
        if parser.matchLexeme(']'):
            return listExpression

        listExpression.addExpression(parseExpression(parser))

        if parser.matchLexeme(']'):
            return listExpression

        parser.check(expected="comma ", lexeme=",")
        parser.next()

@trace
def parseRecord(parser):
    parser.check("Record opening", lexeme="{")
    record = RecordExpression()
    while parser.next():
        if parser.matchLexeme('}'):
            return record

        parser.matchTokensort(NAME)
        key = parser.currentToken[1]
        parser.next(lexeme=":")
        parser.next()
        expression = parseExpression(parser)
        record.addRecord(key,expression)

        if parser.matchLexeme('}'):
            return record

        parser.check("comma", lexeme=",")

@trace
def parsePredicate(parser):
    predicate = None
    if parser.matchLexeme('!'):
        parser.next() #skip !.
        predicate = '!' + parser.parsePredicate()
    else:
        #normal ast predicate
        pass

    predicate = parseExpression(parser)

    if parser.matchLexeme("."): # var.type?
        parser.next("list?, record?, string?", tokensort=KEYWORD)
        if parser.matchLexeme(keywords['LIST']):
            ptype = "LIST"
        if parser.matchLexeme(keywords['RECORD']):
            ptype = "RECORD"
        if parser.matchLexeme(keywords['STRING']):
            ptype = "STRING"
        parser.next()
        parser.check('type?', lexeme='?')
        predicate = "%s.%s?" % (predicate,ptype)
        parser.next()
    elif parser.matchLexeme('&') and parser.peek(lexeme='&'):
        parser.next()
        parser.next()
        right = parsePredicate(parser)
        #and
        predicate = "%s && %s" % (predicate, right)
    elif parser.matchLexeme('|') and parser.peek(lexeme='|'):
        parser.next()
        parser.next()
        right =  parsePredicate(parser)
        predicate = "%s || %s" % (predicate, right)
        parser.next()

    if not predicate:
        raise SyntaxError(currentToken,
                    "pasing predicate failed && || type? variable")

    logging.debug(predicate)
    return predicate

@trace
def parseStatement(parser):
    statement = ""
    if parser.matchLexeme(keywords['LET']):
        return parseLetStatement(parser)
    if parser.matchLexeme(keywords['IF']):
        return parseIfStatement(parser)
    elif parser.matchLexeme(keywords['EACH']):
        return parseEachStatement(parser)
    elif parser.matchLexeme(keywords['ECHO']):
        statement = 'echo'
        if parser.peek(tokensort=PRESTRING):
            emb = parseEmbedding(parser)
            return "%s %s" % (statement, emb)
        parser.next( tokensort=STRING )
        statement = '%s %s' % (statement, parser.currentToken[1])
        return statement
    elif parser.matchLexeme(keywords['CDATA']):
        statement = 'cdata'
        parser.next()
        exp = parseExpression(parser)
        statement = '%s %s' % (statement, exp)
        return statement
    elif parser.matchLexeme('{'): #? starts a new staments block
        return  matchStatementBlock(parser)
    elif parser.matchLexeme(keywords['COMMENT']):
        statement = 'comment'
        parser.next( tokensort=STRING )
        statement = '%s %s' % (statement, parser.currentToken[1])
        return statement
    elif parser.matchLexeme(keywords['YIELD']):
        parser.next(lexeme=";")
        return "YIELD"
    elif parser.matchTokensort(NAME):
        parseMarkupStatements(parser)
        return "NAME"
    elif parser.matchTokensort( ENDMARKER ): #needed?
        return
    raise SyntaxError(parser.currentToken,
        expected="""statement, "if", "each", "let", "{", "comment",
            "echo", "cdata", "yield" or Markup""" )
@trace
def parseLetStatement(parser):
    if parser.matchLexeme(keywords['LET']):
        while parser.next():
            parseAssignment(parser)
            if parser.matchLexeme(keywords['IN']):
                while parser.next():
                    parseStatement(parser)
                    if parser.matchLexeme(keywords['END']):
                        logging.debug("end LET block")
                        return
                raise SyntaxError(parser.currentToken,
                    expected="missing END of LET .. IN .. END block")
        raise SyntaxError(parser.currentToken,
            expected = """LET .. IN .. END, missing IN """)

@trace
def parseAssignment(parser):
    if parser.peek( lexeme = '(' ):
        return parseFunctionAssignment(parser)
    else:
        return parseVariableAssignment(parser)

@trace
def parseFunctionAssignment(parser):
    return "dont know syntax"

@trace
def parseVariableAssignment(parser):
    """ var = expression """
    parser.check( tokensort=NAME )
    var = parser.currentToken[1]
    parser.next( lexeme = "=" )
    parser.next()
    exp = parseExpression(parser)
    if parser.matchLexeme( ',' ):
        return "%s = %s" % ( var, parseVariableAssignment(parser))
    return exp

@trace
def parseEachStatement(parser):
    parser.check(lexeme = keywords['EACH'])
    parser.next(lexeme='(')
    parser.next("Var", tokensort=NAME)
    name = parser.currentToken[1]
    parser.next(lexeme=':')
    parser.next()
    exp = parseExpression(parser)
    parser.check(lexeme = ')' )
    parser.next()
    stm = parseStatement(parser)
    return "each ( %s : %s ) %s" % (name, exp,stm)

@trace
def parseIfStatement(parser):
    parser.check(lexeme = keywords['IF'])
    parser.next( lexeme = '(' )
    parser.next()
    predicate = parsePredicate(parser)
    parser.check( lexeme = ')' )
    parser.next()
    stm = parseStatement(parser)
    statement = "if %s {%s}" % (stm,predicate)
    if parser.matchLexeme(keywords['ELSE']):
        parser.next()
        stm = parseStatement(parser)
        statement = "%s else %s" % (statement, stm)

    return statement

@trace
def parseStatementBlock(parser):
    parser.check(lexeme='{')
    block = ""
    while not parser.matchLexeme('}'):
        statement = parseStatement(parser)
        block = "%s%s" % (block, statement)
    parser.next()
    return block

@trace
def parseMarkupStatements(parser):
    """
    p;      markup
    p p;    markup variable
    p p();  markup - markup
    p p p();
    p "data"
    p "embedding < >";
    """

    while parser.hasnext():
        m = parseMarkup(parser)
        if parser.matchTokensort(STRING):
            parser.next()
        if parser.matchTokensort(PRESTRING):
            emb = parseEmbedding(parser)
        if parser.matchLexeme(';'):
            parser.next() # skip ;
            return
    raise SyntaxError(parser.currentToken,
            expected =  "Statement ;")

@trace
def parseEmbedding(parser):
    parser.check('embedding " < > " ', tokensort=PRESTRING)
    emb = ""
    while not parser.peek(tokensort=POSTSTRING):
        emb = "%s %s" % ( emb, parser.currentToken[1])
        parser.next()
        if parser.matchTokensort(EMBSTRING):
            emb = "%s %s" % ( emb, parser.currentToken[1])
        elif parser.matchTokensort(PRESTRING):
            emb = "%s %s" % ( emb, parser.currentToken[1])
        else:
            raise SyntaxError(parser.currentToken,
                expected = "Embedded string Error")

    parser.next("tail of embedded string", tokensort=POSTSTRING)
    emb = "%s %s" % ( emb, parser.currentToken[1])
    #AST
    parser.next()
    return emb

"""
p     markup
p p   markup variable
p p() markup - markup
p p p()
"""
@trace
def parseMarkup(parser):
    """Differentiate between p and p()"""
    parseDesignator(parser)
    if parser.matchLexeme('('):
        parseArguments(parser)

@trace
def parseDesignator(parser):
    """ p attributes* markup """
    parser.check("NAME",tokensort=NAME)
    #AST create designator.
    #peek first character for possible attribute
    parser.next()
    if parser.currentToken[1][0] in "#$@:%.":
            parseAttributes(parser)

@trace
def parseAttributes(parser):
    """ # . $ : @ % @ """
    attributes = []

    while currentToken[0][1] in "#$@:%.":
        if parser.matchLexeme('#'):
            parser.next(tokensort=NAME)
            attributes.append(('#', currentToken))
        elif parser.matchLexeme('.'):
            parser.next(tokensort=NAME)
        elif parser.matchLexeme('$'):
            parser.next(tokensort=NAME)
        elif parser.matchLexeme('@'):
            parser.next(tokensort=NUMBER)
        elif parser.matchLexeme(':'):
            parser.next(tokensort=NAME)
        else:
            raise SyntaxError(currentToken,
                expected=" Attribute, # . : @ % ")
        parser.next()

@trace
def parseArguments(parser):
    """ ( Arguments,* ) """
    parser.matchLexeme('(')
    parser.next()
    while parser.hasnext():
        if parser.matchLexeme(')'):
            parser.next()
            return

        parseArgument(parser)

        if not parser.matchLexeme(lexeme=')'):
            parser.next(lexeme=',')

@trace
def parseArgument(parser):
    """ name = expression
        expression
    """
    argument = ""

    if parser.peek(lexeme='='):
        parser.check("Varname",tokensort=NAME)
        argument = parser.currentToken[1]
        parser.next() # skip =
        parser.next()
    else:
        #normal argument
        pass

    argument = argument + parseExpression(parser)


def parse(source):
    parser = Parser()
    tokens = tokenize.generate_tokens(source)
    parser.setTokens(tokens)
    parseWaebrick(parser)

if __name__ == '__main__':                     # testing
    import sys
    if len(sys.argv) > 1: parse(open(sys.argv[1]).readline)
    else : parse(sys.stdin.readline)

