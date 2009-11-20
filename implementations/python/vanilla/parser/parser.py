"""
Parser Module.

convention:

For each parser function the first token should be ready and waiting.
so each parser function should read ahead for the next parser function.
"""

import logging
import tokenize
import re
from decorator import decorator
from keywords import keywords

from token import *

DEBUG = True
SHOWTOKENS = True
SHOWPARSER =  True

if DEBUG:
    LOG_FILENAME = 'parser.log'
    logging.basicConfig(filename=LOG_FILENAME,level=logging.DEBUG,)


## debug tools.
def strToken(type, token, (srow, scol), (erow, ecol), line):
    return "%d,%d-%d,%d:\t%s\t%s" % \
        (srow, scol, erow, ecol, tok_name[type], repr(token))

def logToken(*token):
    logging.debug(strToken(*token))

def _trace(f, *args, **kw):
    logging.info("calling %s with args %s, %s" % \
            (f.__name__, args, kw ))
    result = f(*args, **kw)
    logging.info("exit %s" % f.__name__)
    return result

def trace(f):
    return decorator(_trace, f)


class UnexpectedToken(Exception):

    def __init__(self, token, expected=""):
        self.token = token
        self.expected = expected
        logging.debug("raised exception on:")
        logToken(*token)
        #tokenize.printtoken(*token)
        logging.debug("expected: %s" % expected)

    def __str__(self):
        return "%s expected: %s" % (tokenize.tokentostring(*self.token),
            self.expected)

#global variables to keep state
tokens = []
peekedTokens = []
currentToken = []

class Parser(object):

    def __init__(self, tokens=None):
       if DEBUG and SHOWPARSER:
           logging.debug(type(self))

    def setTokens(self, newtokens):
        global tokens
        global peekedTokens
        global currentToken
        tokens = newtokens
        peekedTokens = []
        currentToken = []

    def matchLexeme(self, lexeme):
        """ return boolean if current token matches lexeme """
        if currentToken[1]:
            return currentToken[1] == lexeme
        return False

    def matchTokensort(self, tokensort):
        """ return boolean if current token matches tokensort """
        return currentToken[0] == tokensort

    def check(self, expected="", tokensort=None, lexeme=None):
        """ check if current token is tokensort of lexeme """
        if lexeme:
           if not currentToken[1] == lexeme:
                raise UnexpectedToken(currentToken, expected=expected)
        if tokensort:
            if not currentToken[0] == tokensort:
                raise UnexpectedToken(currentToken, expected=expected)

    def peek(self, x=1, tokensort="", lexeme=""):
        """lookahead x tokens in advance, returns true
           if tokensort and or lexeme matches peekedtoken.
        """
        d = x - len(peekedTokens)
        if d > -1:
            try:
                for i in range(d+1):
                    peekedTokens.append(tokens.next())
            except StopIteration:
                return False

        peekToken = peekedTokens[x-1]
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
        checks if next token matches tokensort of lexeme.
        """
        global peekedTokens
        global currentToken

        if peekedTokens:
            currentToken = peekedTokens.pop(0)
        else:
            try:
                currentToken = tokens.next()
            except StopIteration:
                logging.debug(" no next token available ")
                raise StopIteration

        if tokensort or lexeme:
            expected = "'%s%s%s'" % (expected, tokensort, lexeme)
            self.check(expected, tokensort, lexeme)

        if DEBUG and SHOWTOKENS:
            logToken(*currentToken)

        return currentToken

    def __repr__(self):
        return "%s at '%s'" % ( str(type(self)), currentToken[1])

class WaebricParser(Parser):
    """ start the parsing!"""

    def parse(self):
        logging.debug("--------------------")
        logging.debug('start parsing tokens')
        logging.debug("--------------------")
        module = ModuleParser()
        module.next()
        module.parseModule()


class ModuleParser(Parser):
    """ Parse module statement defined in module """

    def parseModule(self):
        """parse module statements """
        self.check('Module ModuleID', lexeme=keywords['MODULE'] )

        #create a new ast module.
        #parse the module identifier.
        self.next()
        moduleId = self.parseModuleId()

        # while there are more tokens
        # parse for Site , Function and Import statements
        while(self.next()):
            if self.matchLexeme(keywords['IMPORT']):
                imports = ImportParser(self.tokens)
            elif self.matchLexeme(keywords['DEF']):
                function = FunctionParser(self)
                function.parseFunction()
            elif self.matchLexeme(keywords['SITE']):
                site = SiteParser(self)
                site.parseSite()
            elif self.matchTokensort(ENDMARKER):
                return
            else:
               raise UnexpectedToken(currentToken,
            expected="import, def, site, newline" )

    def parseModuleId(self):
        """Parse the module identifier """
        # AST new module id.
        moduleID = []
        self.check("Module Identifier", tokensort=NAME)
        moduleID.append(currentToken)

        while(self.peek(lexeme='.')):
            self.next() #skip .
            self.next("Module Identifier", tokensort=NAME)
            moduleID.append(currentToken)


class SiteParser(Parser):
    """ Parse Site function defined in module."""
    @trace
    def parseSite(self):
        # AST, mappings.
        self.check("Site Defenition", lexeme=keywords['SITE'])
        # check mapping

        while self.next():
            self.parseMapping()

            if self.matchLexeme(keywords['END']):
                return

        raise UnexpectedToken(currentToken, expected="missing site ending END")
    @trace
    def parseMapping(self):
        """ parse content in between site and end."""
        # AST new mapping
        self.parsePath()
        self.next( lexeme=":" )
        self.next( tokensort=NAME )
        #self.next()
        p = MarkupParser()
        p.parseMarkup()
        p.next()
        #p.next( lexeme=';')
        #setmarkup on mapping

    @trace
    def parsePath(self):
        path = ""
        if self.peek(lexeme="/") or self.peek(x=2,lexeme="/"):
            path = self.parseDirectory()
        path = path + self.parseFileName()
        logging.debug(path)
        #AST path stuff.

    @trace
    def parseDirectory(self):
        directory = ""
        if self.isPathElement():
            directory += currentToken[1]

        while self.next():
            if self.matchLexeme("/"):
                if directory:
                    directory += "/"
                self.next()

            if self.peek(lexeme="."):
                return directory #return , we're in a filename

            if self.isPathElement():
                directory += currentToken[1]
            else:
                raise UnexpectedToken(currentToken, expected="path element")
        raise UnexpectedToken(currentToken, expected="directory path")

    @trace
    def parseFileName(self):
        self.check(tokensort=NAME, expected="File Name")
        name = currentToken[1]
        self.next(lexeme = ".")
        self.next(tokensort=NAME, expected="File extension") # extension
        name = "%s.%s" % (name, currentToken[1])
        return name

    @trace
    def isPathElement(self):
        regex = re.compile(r"(.* .*)|(.*\t.*)|(.*\n.*)|(.*\r.*)|(.*/.*)|(.*\\..*)|(.*\\\\.*)")
        if regex.match(currentToken[1]):
            return False
        return True


class FunctionParser(Parser):
    @trace
    def parseFunction(self):
        self.check(expected="function defenition, def", lexeme=keywords['DEF'])
        self.next(tokensort=NAME)
        ## parse identifier

        ## parse formals

        ## parser statements.
        while(self.next()):
            statement = StatementParser(self)
            statement.parseStatement()

            if self.matchLexeme(keywords['END']):
                return

        raise UnexpectedToken(currentToken,
                expected="""END, Missing function ending END""")


class ExpressionParser(Parser):
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
    def parseExpression(self):
        expression = None
        if self.matchLexeme("'"):
            self.next("symbol name", tokensort=NAME)
            expression = currentToken[1]
            #symbol.
        elif self.matchTokensort(NAME):
            #Variable
            expression = currentToken[1]
        elif self.matchTokensort(STRING):
            expression = currentToken[1]
            #data string
        elif self.matchTokensort(NUMBER):
            expression = currentToken[1]
            #number stuff
        elif self.matchLexeme("["):
            expression = self.parseList()
        elif self.matchLexeme("{"):
            expression = self.parseRecord()
        elif self.peek(lexeme=".") and self.peek(x=2, tokensort=NAME):
            expression = currentToken[1]
            while self.peek(lexeme=".") and self.peek(x=2, tokensort=NAME):
                self.next(lexeme=".") #skip.
                self.next(expected="NAME", tokensort=NAME)
                expression = expression + '.' + currentToken[1]
                #ast. stuff.
        elif self.peek(lexeme="+") and expression:
            #parse a + expression left and right.
            #ast set left = currently parsed expression
            left = currentToken[1]
            self.next() # skip +
            right = self.parseExpression()
            expression = left + '+' + right
            #ast set right.
        logging.debug(expression)
        if not expression:
            raise UnexpectedToken(currentToken,
                expected="Expression: symbol, string, number, list, record, name.field")

        self.next()
        return expression

    def parseList(self):
        self.check("List opening '[' ", lexeme="[")
        #ast stuff.
        expression = "["

        while self.next():
            if self.matchLexeme(']'):
                logging.debug(expression)
                return expression + " ]"

            expression = expression + currentToken[1]
            self.parseExpression()
            #AST add expression.

            if self.matchLexeme(']'):
                logging.debug(expression)
                return expression + " ]"

            self.check(expected="comma ", lexeme=",")
            self.next()
            expression = expression + ','
    @trace
    def parseRecord(self):
        self.check("Record opening", lexeme="{")
        expression = "{"
        while self.next():
            if self.matchLexeme('}'):
                logging.debug(expression)
                return expression + "}"

            self.matchTokensort(NAME)
            expression = expression + currentToken[1]
            self.next(lexeme=":")
            self.next()
            expression = expression + ':' + self.parseExpression()

            if self.matchLexeme('}'):
                logging.debug(expression)
                return expression + "}"

            expression = expression + ','
            self.check("comma", lexeme=",")
            logging.debug("exp" + expression)

class PredicateParser(Parser):

    @trace
    def parsePredicate(self):
        predicate = None
        if self.matchLexeme('!'):
            self.next() #skip !.
            predicate = '!' + self.parsePredicate()
        else:
            #normal ast predicate
            pass

        p = ExpressionParser()
        predicate = p.parseExpression()

        if self.matchLexeme("."): # var.type?
            self.next("list?, record?, string?", tokensort=KEYWORD)
            if self.matchLexeme(keywords['LIST']):
                ptype = "LIST"
            if self.matchLexeme(keywords['RECORD']):
                ptype = "RECORD"
            if self.matchLexeme(keywords['STRING']):
                ptype = "STRING"
            self.next()
            self.matchLexeme('?')
            predicate = "%s.%s?" % (predicate,ptype)
            self.next()
        elif self.matchLexeme('&') and self.peek(lexeme='&'):
            self.next()
            self.next()
            right = self.parsePredicate()
            #and
            predicate = "%s && %s" % (predicate, right)
        elif self.matchLexeme('|') and self.peek(lexeme='|'):
            self.next()
            self.next()
            right =  self.parsePredicate()
            predicate = "%s || %s" % (predicate, right)
            self.next()

        if not predicate:
            raise UnexpectedToken(currentToken,
                        "pasing predicate failed && || type? variable")

        logging.debug(predicate)
        return predicate


class StatementParser(Parser):

    @trace
    def parseStatement(self):
        if self.matchLexeme(keywords['LET']):
            self.parseLetStatement()
            return
        if self.matchLexeme(keywords['IF']):
            return
        elif self.matchLexeme(keywords['EACH']):
            self.parseEachStatement()
        elif self.matchLexeme(keywords['ECHO']):
            return
        elif self.matchLexeme(keywords['CDATA']):
            return
        elif self.matchLexeme('}'): #? should be removed.
            return
        elif self.matchLexeme('{'): #? starts a new staments block

            return
        elif self.matchLexeme(keywords['COMMENT']):
            return
        elif self.matchLexeme(keywords['YIELD']):
            self.next(lexeme=";")
            return
        elif self.matchTokensort(NAME):
            self.parseMarkupStatements()
            return

        raise UnexpectedToken(currentToken,
            expected="""statement, "if", "each", "let", "{", "comment",
                "echo", "cdata", "yield" or Markup""" )
    @trace
    def parseLetStatement(self):
        if self.matchLexeme(keywords['LET']):
            while self.next():
                #TODO parse assignment markup.
                if self.matchLexeme(keywords['IN']):
                    while self.next():
                        if self.matchLexeme(keywords['END']):
                            logging.debug("end LET block")
                            return
                        self.parseStatement()
                    raise UnexpectedToken(currentToken,
                        expected="missing END of LET .. IN .. END block")
            raise UnexpectedToken(currentToken,
                expected = """LET .. IN .. END, missing IN """)

    @trace
    def parserEachtStatement(self):
        self.matchLexeme(keywords['EACH'])
        self.next(lexeme='(')
        self.next("Var", tokensort=NAME)
        self.next(lexeme=':')
        self.next()
        self.parseExpression()


    @trace
    def parseMarkupStatements(self):
        """
        p;      markup
        p p;    markup variable
        p p();  markup - markup
        p p p();
        p "data"
        p "embedding < >";
        """

        markup = MarkupParser(self)
        while self.hasnext():
            markup.parseMarkup()
            if self.matchTokensort(STRING):
                self.next()
            #TODO embedding.
            #if self.matchTokensort(
            if self.matchLexeme(';'):
                self.next() # skip ;
                return
        raise UnexpectedToken(currentToken,
                expected =  "Statement ;")

    def isMarkup(self):
        """
        NEEDED??
        current token is markup if:
        next = name
        next = ()
        next = atributes
        """
        pass


class EmbeddingParser(Parser):
    @trace
    def parseEmbedding(self):
        self.check('embedding " < > " ', tokensort=PRESTRING)

        while not self.peek(tokensort=POSTSTRING):
            self.next()
            if self.matchTokensort(EMBSTRING):
                #AST.
                pass
            elif self.matchTokensort(PRESTRING):
                #AST
                pass
            else:
                raise UnexpectedToken(currentToken,
                    expected = "Embedded string Error")
        self.next("tail of embedded string", tokensort=POSTSTRING)
        #AST
        self.next()

class MarkupParser(Parser):
    """
    p     markup
    p p   markup variable
    p p() markup - markup
    p p p()
    """

    @trace
    def parseMarkup(self):
        """Differentiate between p and p()"""
        self.parseDesignator()
        if self.matchLexeme('('):
            self.parseArguments()

    @trace
    def parseDesignator(self):
        """ p attributes* markup """
        self.check("NAME",tokensort=NAME)
        #AST create designator.
        #peek first character for possible attribute
        self.next()
        if currentToken[1][0] in "#$@:%.":
                self.parseAttributes()

    @trace
    def parseAttributes(self):
        """ # . $ : @ % @ """
        attributes = []

        while currentToken[0][1] in "#$@:%.":
            if self.matchLexeme('#'):
                self.next(tokensort=NAME)
                attributes.append(('#', currentToken))
            elif self.matchLexeme('.'):
                self.next(tokensort=NAME)
            elif self.matchLexeme('$'):
                self.next(tokensort=NAME)
            elif self.matchLexeme('@'):
                self.next(tokensort=NUMBER)
            elif self.matchLexeme(':'):
                self.next(tokensort=NAME)
            else:
                raise UnexpectedToken(currentToken,
                    expected=" Attribute, # . : @ % ")
            self.next()

    @trace
    def parseArguments(self):
        """ ( Arguments,* ) """
        self.matchLexeme('(')
        self.next()
        while self.hasnext():
            if self.matchLexeme(')'):
                self.next()
                return

            self.parseArgument()

            if not self.matchLexeme(lexeme=')'):
                self.next(lexeme=',')

    @trace
    def parseArgument(self):
        """ name = expression
            expression
        """
        argument = ""

        if self.peek(lexeme='='):
            self.check("Varname",tokensort=NAME)
            argument = currentToken[1]
            self.next() # skip =
            self.next()
        else:
            #normal argument
            pass

        p = ExpressionParser(self)
        argument = argument + p.parseExpression()

def parse(source):
    global tokens
    tokens = tokenize.generate_tokens(source)
    parser = WaebricParser()
    parser.parse()

if __name__ == '__main__':                     # testing
    import sys
    if len(sys.argv) > 1: parse(open(sys.argv[1]).readline)
    else : parse(sys.stdin.readline)

