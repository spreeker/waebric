"""
Parser Module.

Waebric Parser Module.

-uses tokenize generator to tokenize source.
-recursive decent parser for waebric source.
-parser class keeps state of currentToken,peekedTokens.
-Builds ast tree of waebric source.

In error.py there are magic variables:
DEBUG,SHOWTOKENS which enable detailed
logging in debug.log

@trace decorator enables logging function calls
in debug.log.

convention:

For each parser function the first token should be ready.
Therefore each parser function should read ahead the first
token for the next parser function.

parser.currenToken object contains the current token of the
tokenize generator and is a list containing:
[tokentype, token, (startrow, startcolumn),(endrow,endcolumn),line]

"""

import logging
import tokenize
import re

import error
from error import SyntaxError, TokenError
from error import trace, logToken, indentedLog
from error import DEBUG, SHOWTOKENS

from keywords import keywords
from token import *

from ast import Name
from ast import Module, Function, Path, Import, Mapping
from ast import Text, Number, List, Record, Cat, Field
from ast import Designator, Attribute, Markup, EmbedMarkup
from ast import Not, And, Or, Is_a
from ast import Yield, Let, If, Assignment, Each, Block
from ast import Embedding, Comment, Cdata, Echo

class Parser(object):

    seenEndMarker = False
    tokens = [] #token generator.
    peekedTokens = []
    currentToken = []
    embed = False
    errors = []

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
        """
        Check if current token is tokensort of lexeme,
        Raises SyntaxError when there is no match.
         """
        if lexeme:
           if not self.currentToken[1] == lexeme:
                raise SyntaxError(self.currentToken, expected=lexeme)
        if tokensort:
            if not self.currentToken[0] == tokensort:
                expected = "%s for %s GOT %s %s" % (
                    tok_name[tokensort], expected,
                    tok_name[self.currentToken[0]],
                    self.currentToken[1])
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
            except StopIteration, err:
                # sys.exit maybe?
                return False
            except error.TokenError:
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
                if self.matchTokensort(ENDMARKER) and not self.seenEndMarker:
                    self.seenEndMarker = True
                elif self.seenEndMarker:
                    raise StopIteration

        if self.currentToken[0] == COMMENT:
            return self.next()

        if tokensort or lexeme:
            expected = "'%s%s%s'" % (expected, tokensort, lexeme)
            self.check(expected, tokensort, lexeme)

        if DEBUG and SHOWTOKENS:
            logToken(*self.currentToken)

        return self.currentToken

    def __repr__(self):
        token = self.currentToken
        if self.currentToken:
            token = self.currentToken[1]
        return "parsing: '%s'" % (token)

#@trace
def parseWaebrick(parser):
    logging.debug("--------------------")
    logging.debug('start parsing tokens')
    logging.debug("--------------------")
    parser.next()
    ast = parseModule(parser)
    return ast

#@trace
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
            module.addImport(parseImport(parser))
        elif parser.matchLexeme(keywords['DEF']):
            module.addFunction(parseFunction(parser))
        elif parser.matchLexeme(keywords['SITE']):
            module.addSite(parseSite(parser))
        elif parser.matchTokensort(ENDMARKER):
            return
        else:
           raise SyntaxError(parser.currentToken,
        expected="import, def, site, newline" )

    return module

#@trace
def parseModuleId(parser):
    """Parse the module identifier """
    # AST new module id.
    moduleID = []
    parser.check("Module Identifier", tokensort=NAME)
    moduleID.append(parser.currentToken[1])

    while(parser.peek(lexeme='.')):
        parser.next() #skip .
        parser.next("Module Identifier", tokensort=NAME)
        moduleID.append(parser.currentToken[1])

    parser.next()
    moduleID = "/".join(moduleID)
    return moduleID

#@trace
def parseImport(parser):
    parser.check(lexeme=keywords['IMPORT'])
    lineo = parser.currentToken[2]
    parser.next()
    return Import(parseModuleId(parser), lineo=lineo)

#@trace
def parseSite(parser):
    # AST, mappings.
    parser.check("Site Defenition", lexeme=keywords['SITE'])
    parser.next()
    # check mapping
    mappings = []
    while parser.hasnext():
        mappings.append(parseMapping(parser))
        if parser.matchLexeme(keywords['END']):
            parser.next()
            return mappings

    raise SyntaxError(parser.currentToken, expected="missing site ending END")

#@trace
def parseMapping(parser):
    """ parse content in between site and end."""
    # AST new mapping
    path = parsePath(parser)
    parser.check( lexeme=":" )
    parser.next()
    markup = parseMarkup(parser) #markupcall. not a statement
    #parser.check(lexeme=";")# XXX no smicolon!
    #parser.next()
    return Mapping(path, markup)

#@trace
def parsePath(parser):
    path = ""
    _dir = ""
    if parser.peek(lexeme="/") or parser.peek(x=2,lexeme="/"):
        _dir = parseDirectory(parser)
    fileName = parseFileName(parser)
    return Path(_dir, fileName)

#@trace
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

#@trace
def parseFileName(parser):
    parser.check(tokensort=NAME, expected="File Name")
    name = parser.currentToken[1]
    parser.next(lexeme = ".")
    parser.next(tokensort=NAME, expected="File extension") # extension
    name = "%s.%s" % (name, parser.currentToken[1])
    parser.next()
    return name

#@trace
def isPathElement(parser):
    regex = re.compile(r"(.* .*)|(.*\t.*)|(.*\n.*)|(.*\r.*)|(.*/.*)|(.*\\..*)|(.*\\\\.*)")
    if regex.match(parser.currentToken[1]):
        return False
    return True

#@trace
def parseFunction(parser):
    parser.check(expected="function defenition, def", lexeme=keywords['DEF'])
    lineo = parser.currentToken[2]
    ## parse identifier
    parser.next(tokensort=NAME)
    name = parser.currentToken[1]
    parser.next()
    ## parse formals
    if parser.matchLexeme('('):
        arguments = parseArguments(parser)
        function = Function(name,arguments, lineo=lineo)
    else:
        function = Function(name, lineo=lineo)

    ## parser statements.
    while(parser.hasnext()):
        if parser.matchLexeme(keywords['END']):
            parser.next()
            return function

        function.addStatement(parseStatement(parser))

    if parser.matchLexeme(keywords['END']): #empty function!
        return function

    raise SyntaxError(parser.currentToken,
            expected="END, Missing function ending END")

"""
Symbol expression,
Text,
Natural number,
Variable,
List,
Record,
expression + expression
"""
#@trace
def parseExpression(parser):
    expression = None
    if parser.matchTokensort(STRING): #data string
        expression = Text(parser.currentToken[1], lineo=parser.currentToken[2])
        parser.next()
    elif parser.matchTokensort(NUMBER): #number stuff
        expression = Number(parser.currentToken[1], lineo=parser.currentToken[2])
        parser.next()
    elif parser.matchLexeme("["):
        expression = parseList(parser)
    elif parser.matchLexeme("{"):
        expression = parseRecord(parser)
    elif parser.matchTokensort(NAME):
        #Variable
        expression = Name(parser.currentToken[1])
        #Check for Variable DOT Field expression
        fields = []
        while parser.peek(lexeme=".") and parser.peek(x=2, tokensort=NAME):
            parser.next(lexeme=".") #skip.
            parser.next(expected="NAME", tokensort=NAME)
            fields.append(parser.currentToken[1])
        if fields:
            expression = Field(expression, fields, lineo=parser.currentToken[2])
        parser.next()
    #Check for expression PLUS expression
    if parser.matchLexeme("+") and expression:
        left = expression
        parser.next()# skip +
        right = parseExpression(parser)
        expression = Cat(left, right, lineo=parser.currentToken[2])

    #indentedLog(expression)

    if not expression:
        raise SyntaxError(parser.currentToken,
            expected="Expression: symbol, string, number, list, record, name.field")

    return expression

#@trace
def parseList(parser):
    parser.check("List opening '[' ", lexeme="[")
    listExpression = List(lineo=parser.currentToken[2])
    parser.next()

    while parser.hasnext():
        if parser.matchLexeme(']'):
            parser.next()
            return listExpression

        listExpression.addExpression(parseExpression(parser))

        if parser.matchLexeme(']'):
            parser.next()
            return listExpression

        parser.check(expected="comma ", lexeme=",")
        parser.next()

#@trace
def parseRecord(parser):
    parser.check("Record opening", lexeme="{")
    record = Record(lineo=parser.currentToken[2])
    while parser.next():
        if parser.matchLexeme('}'):
            parser.next()
            return record

        parser.matchTokensort(NAME)
        key = parser.currentToken[1]
        parser.next(lexeme=":")
        parser.next()
        expression = parseExpression(parser)
        record.addRecord(key,expression)

        if parser.matchLexeme('}'):
            parser.next()
            return record

        parser.check("comma", lexeme=",")

#@trace
def parsePredicate(parser):
    predicate = None
    if parser.matchLexeme('!'):
        parser.next() #skip !.
        return Not(parsePredicate(parser))

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
        predicate = Is_a(predicate, ptype, lineo=parser.currentToken[2])
        parser.next()
    if parser.matchLexeme('&') and parser.peek(lexeme='&'):
        parser.next() #skip &&
        parser.next()
        right = parsePredicate(parser)
        #and
        predicate = And(predicate, right, lineo=parser.currentToken[2])
    if parser.matchLexeme('|') and parser.peek(lexeme='|'):
        parser.next() #skip ||
        parser.next()
        right =  parsePredicate(parser)
        predicate = Or(predicate, right, lineo=parser.currentToken[2])
        #parser.next()

    if not predicate:
        raise SyntaxError(currentToken,
                    "pasing predicate failed && || type? variable")

    return predicate

# TODO refactor all parser.next(lexeme=';') out of parse functions.
#@trace
def parseStatement(parser):
    if parser.matchLexeme(keywords['LET']):
        return parseLetStatement(parser)
    elif parser.matchLexeme(keywords['IF']):
        return parseIfStatement(parser)
    elif parser.matchLexeme(keywords['EACH']):
        return parseEachStatement(parser)
    elif parser.matchLexeme(keywords['ECHO']):
        parser.next()
        if parser.matchTokensort(PRESTRING):
            exp = parseEmbedding(parser)
        else :
            exp = parseExpression(parser)
        parser.check(lexeme = ";")
        parser.next() # skip ;
        return Echo(expression=exp, lineo=parser.currentToken[2])

    elif parser.matchLexeme(keywords['CDATA']):
        parser.next()
        expression = parseExpression(parser)
        return Cdata(expression)
    elif parser.matchLexeme('{'): #? starts a new staments block
        return  parseStatementBlock(parser)
    elif parser.matchLexeme(keywords['COMMENT']):
        parser.next( tokensort=STRING )
        comment =  Comment(parser.currentToken[1], lineo=parser.currentToken[2])
        parser.next()
        parser.next()
        return comment
    elif parser.matchLexeme(keywords['YIELD']):
        parser.next(lexeme=";")
        parser.next()
        return Yield()
    elif parser.matchTokensort(NAME):
        markup = parseMarkupStatement(parser)
        return markup
    elif parser.matchTokensort( ENDMARKER ): #needed?
        return
    raise SyntaxError(parser.currentToken,
        expected="""statement, "if", "each", "let", "{", "comment",
            "echo", "cdata", "yield" or Markup""" )
#@trace
def parseLetStatement(parser):
    parser.check(lexeme=keywords['LET'])
    assignments = []
    parser.next()
    lineo = parser.currentToken[2]
    while parser.hasnext():
        assignments.append(parseAssignment(parser))
        if parser.matchLexeme(keywords['IN']):
            parser.next()
            body = []
            while parser.hasnext():
                body.append(parseStatement(parser))
                if parser.matchLexeme(keywords['END']):
                    parser.next() #skip end.
                    #indentedLog('****** LET *******')
                    #parser.next() #read ahead
                    return Let(assignments,body, lineo=lineo)
            raise SyntaxError(parser.currentToken,
                expected="missing END of LET statement")
    raise SyntaxError(parser.currentToken,
        expected = """LET .. IN .. END, missing IN """)

#@trace
def parseAssignment(parser):
    if parser.peek(lexeme = '('):
        return parseFunctionAssignment(parser)
    else:
        return parseVariableAssignment(parser)

#@trace
def parseFunctionAssignment(parser):
    #parse name
    parser.check( tokensort=NAME )
    name = parser.currentToken[1]
    lineo = parser.currentToken[2]
    parser.next()
    #Parse "(" { Name "," }* ")" "="
    arguments = parseArguments(parser)
    parser.check( lexeme = "=" )
    parser.next()
    expression = parseStatement(parser)
    assignment = Assignment(name, expression, lineo=lineo)
    assignment.variables = arguments
    assignment.function = True
    return assignment

#@trace
def parseVariableAssignment(parser):
    """ var = expression """
    parser.check( tokensort=NAME )
    name = parser.currentToken[1]
    lineo = parser.currentToken[2]
    parser.next( lexeme = "=" )
    parser.next()
    if parser.matchTokensort(NAME):
        #expression = parseStatement(parser)
        expression = parseExpression(parser)
        parser.next() #skip ;
    else:
        expression = parseExpression(parser)
        parser.next() #skip ;

    assignment = Assignment(name, expression, lineo=lineo)
    return assignment

#@trace
def parseEachStatement(parser):
    parser.check(lexeme = keywords['EACH'])
    lineo = parser.currentToken[2]
    parser.next(lexeme='(')
    parser.next("Var", tokensort=NAME)
    name = parser.currentToken[1]
    parser.next(lexeme=':')
    parser.next()
    exp = parseExpression(parser)
    parser.check(lexeme = ')' )
    parser.next()
    stm = parseStatement(parser)
    return Each(name, exp, stm, lineo=lineo)

#@trace
def parseIfStatement(parser):
    parser.check(lexeme = keywords['IF'])
    lineo = parser.currentToken[2]
    parser.next(lexeme = '(')
    parser.next()
    predicate = parsePredicate(parser)
    parser.check(lexeme = ')')
    parser.next()
    stm = parseStatement(parser)
    elsestm = ""
    if parser.matchLexeme(keywords['ELSE']):
        parser.next()
        elsestm = parseStatement(parser)

    ifstm = If(predicate, stm, elsestm, lineo=lineo)
    return ifstm

#@trace
def parseStatementBlock(parser):
    parser.check(lexeme='{')
    lineo = parser.currentToken[2]

    parser.next()
    block = Block(lineo=lineo)
    while not parser.matchLexeme('}'):
        block.statements.append(parseStatement(parser))

    parser.next() # skip }
    return block

#@trace
def checkForLastExpression(parser):
    """
    check if parser is at the last expression item in a
    markup chain.
    if it is, parsing should be done by expression parser.
    except if it is a makup call.
    This is a helper function wich returns True if it
    is an expression.
    """
    if parser.matchTokensort(STRING):
        return True
    elif parser.matchTokensort(NUMBER):
        return True
    elif parser.matchTokensort(NAME):
        if parser.peek(lexeme=';') or parser.peek(lexeme='+'):
            return True
        peek = 1;
        while(parser.peek(peek, lexeme='.')):
            #could be field of last expression.
            peek = peek + 1
            if parser.peek(peek,tokensort=NAME):
                peek = peek + 1
        if parser.peek(peek,lexeme=';'):
            return True
    elif parser.matchLexeme('['):
        parenlev = 1
        peek = 1;
        while(parser.peek(peek)):
            if parser.peek(peek, lexeme=']'):
                parenlev -= 1
                if parenlev == 0:
                    peek += 1
                    break
            elif parser.peek(peek,lexeme='['):
                parenlev += 1  
            peek = peek + 1
        if parser.peek(peek,lexeme=';'):
            return True
    elif parser.matchLexeme('{'):
        parenlev = 1
        peek = 1;
        while(parser.peek(peek)):
            if parser.peek(peek, lexeme='}'):
                parenlev -= 1 
                if parenlev == 0:
                    peek += 1
                    break
            elif parser.peek(peek, lexeme='{'):
                parenlev += 1
            peek += 1
        if parser.peek(peek,lexeme=';'):
            return True
    return False

#@trace
def parseMarkupStatement(parser):
    """
    p;      markup
    p();    call
    p p;    markup variable
    p p();  markup - markup
    p p p(); markup markup markup
    p "data"; markup expression
    p "embedding < >"; markup embedding
    p.class%100@200('bla') expression.field.subfield
            markup expression
    p { }   markup block
    """
    markupRoot = parseMarkup(parser)
    markup = markupRoot
    while parser.matchTokensort(tokensort=NAME) and not checkForLastExpression(parser):
        m = parseMarkup(parser)
        markup.childs.append(m)
        markup = m

    if checkForLastExpression(parser):
        markup.expression = parseExpression(parser)

    if not markup.expression and parser.matchTokensort(PRESTRING):
        markup.embedding = parseEmbedding(parser)

    if parser.matchLexeme('{'):
        markup.childs.append(parseStatement(parser))
        return markupRoot

    if parser.matchTokensort(KEYWORD):
        markup.childs.append(parseStatement(parser))
        return markupRoot

    if parser.matchTokensort(EMBEND):
        return markupRoot

    parser.check(lexeme = ';')
    parser.next() #read ahead. skip ;

    return markupRoot

#@trace
def parseEmbedding(parser):
    parser.check('embedding " < > " ', tokensort=PRESTRING)
    emb = Embedding()
    emb.pretext.append(parser.currentToken[1])
    parser.next()
    while not parser.matchTokensort(POSTSTRING):
        if parser.matchTokensort(EMBSTRT):
            embedding = parseEmbed(parser)
            emb.embed.append(embedding)
        elif parser.matchTokensort(MIDSTRING):
            emb.pretext.append(parser.currentToken[1])
            parser.next()
        else:
            raise SyntaxError(parser.currentToken,
                expected = "Embedded string Error")

    parser.check("tail of embedded string", tokensort=POSTSTRING)
    emb.tailtext = parser.currentToken[1]
    parser.next()
    return emb

#@trace
def parseEmbed(parser):
    parser.check(' <  ' , tokensort=EMBSTRT)
    parser.next()
    parser.embed = True
    markup = parseMarkupStatement(parser)
    parser.embed = False
    parser.check(tokensort=EMBEND)
    parser.next()
    return markup

"""
p     markup
p attributes (arguments) markup
"""
#@trace
def parseMarkup(parser):
    """Differentiate between p and p()"""
    lineo = parser.currentToken[2]
    designator = parseDesignator(parser)
    if parser.embed:
        markup = EmbedMarkup(designator, lineo=lineo)
        parser.embed = False
    else:
        markup = Markup(designator, lineo=lineo)

    if parser.matchLexeme('('):
        arguments = parseArguments(parser)
        markup.arguments = arguments
        markup.call = True
    return markup

#@trace
def parseDesignator(parser):
    """ p attributes* """
    parser.check("NAME",tokensort=NAME)
    lineo = parser.currentToken[2]
    designator = Designator(parser.currentToken[1], lineo=lineo)
    parser.next()
    if parser.matchTokensort(OP) and parser.currentToken[1][0] in "#$@:%.":
            attributes = parseAttributes(parser)
            designator.attributes = attributes
    return designator

#@trace
def parseAttributes(parser):
    """ # . $ : @ % @ """
    attributes = []
    while parser.matchTokensort(OP) and parser.currentToken[1] in "#$@:%.":
        symbol = parser.currentToken[1]
        lineo = parser.currentToken[2]
        if symbol in "@%":
            parser.next(expected="Attribute NUMBER", tokensort=NUMBER)
        else:
            parser.next(expected="Attribute NAME", tokensort=NAME)
        #AST
        attributes.append(Attribute(symbol, parser.currentToken[1], lineo=lineo))
        parser.next()

    return attributes

#@trace
def parseArguments(parser):
    """ ( Arguments,* ) """
    #ast stuff.
    arguments = []
    parser.check(lexeme='(')
    parser.next()
    while parser.hasnext():
        if parser.matchLexeme(')'):
            parser.next()
            return arguments

        arguments.append(parseArgument(parser))

        if not parser.matchLexeme(lexeme=')'):
            parser.check(lexeme=',')
            parser.next()

#@trace
def parseArgument(parser):
    """ name = expression
        expression
        name
    """
    if parser.matchTokensort(tokensort=NAME) and not \
        parser.peek(lexeme='.'):
        argument = parser.currentToken[1]
        lineo = parser.currentToken[2]
        parser.next()
        if parser.matchLexeme('='):
            parser.next() # skip =
            exp = parseExpression(parser)
            return Assignment(argument, exp, lineo=lineo)
        else:
            return Name(argument, lineo=lineo)
    else: #must be expression.
        return parseExpression(parser)

def parse(source):
    parser = Parser()
    tokens = tokenize.generate_tokens(source)
    parser.setTokens(tokens)
    ast = parseWaebrick(parser)
    return ast

if __name__ == '__main__':                     # testing
    import sys
    if len(sys.argv) > 1: print parse(open(sys.argv[1]).readline)
    else : print parse(sys.stdin.readline)
