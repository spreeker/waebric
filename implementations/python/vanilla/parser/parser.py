import logging
import tokenize

from token import *

DEBUG = True
SHOWTOKENS = False
SHOWPARSER = True

if DEBUG:
    LOG_FILENAME = 'parser logging.out'
    logging.basicConfig(filename=LOG_FILENAME,level=logging.DEBUG,)


keywords = {'IF':'if', 'ELSE':'else',
            'EACH':'each', 'LET':'let', 'IN':'in', 'COMMENT':'comment',
            'ECHO':'echo', 'CDATA':'cdata', 'YIELD':'yield',

            'MODULE':'module', 'IMPORT':'import', 'DEF':'def', 'END':'end',
            'SITE':'site',

            'LIST':'list', 'RECORD':'record', 'STRING':'string',
            }


def logToken(type, token, (srow, scol), (erow, ecol), line):
    logging.debug("%d,%d-%d,%d:\t%s\t%s" % \
        (srow, scol, erow, ecol, tok_name[type], repr(token)))


class UnexpectedToken(Exception):

    def __init__(self, token, expected=""):
        self.token = token
        self.expected = expected
        logging.debug("raised exception %s")
        logToken(*token)
        tokenize.printtoken(*token)
        logging.debug("expected: %s" % expected)

    def __str__(self):
        return tokenize.tokentostring(*self.token)


class Parser(object):

    tokens = []
    peekedTokens = []
    currentToken = []

    def __init__(self, Parser=None):
        if Parser:
            self.tokens = Parser.tokens
            self.peekedTokens = Parser.peekedTokens
            self.currentToken = Parser.currentToken

            if DEBUG and SHOWPARSER:
                logging.debug(type(self))

    def matchLexeme(self, lexeme):
        """ return boolean if current token matches lexeme """
        if self.currentToken[1]:
            return self.currentToken[1] == lexeme
        return False

    def matchTokensort(self, tokensort):
        """ return boolen if current token matches tokensort """
        return self.currentToken[0] == tokensort

    def check(self, name, syntax, tokensort=None, lexeme=None):
        """ check if current token is tokensort of lexeme """
        if lexeme:
           if not self.currentToken[1] == lexeme:
                raise UnexpectedToken(self.currentToken)
        if tokensort:
            if not self.currentToken[0] == tokensort:
                raise UnexpectedToken(self.currentToken)

    def peek(self, x=1, tokensort=None, lexeme=None):
        """ lookahead x tokens in advance, returns true if tokensort of lexeme
            matches
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

    def next(self, name="", syntax="", tokensort=None, lexeme=None):
        """
        Get the next token, if tokensort or lexeme is defined
        checks if next token matches tokensort of lexeme.
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
            self.check(name, syntax, tokensort, lexeme)

        if DEBUG and SHOWTOKENS:
            logToken(*self.currentToken)

        if self.matchTokensort(NEWLINE) or self.matchTokensort(NL):
            return self.next(name,syntax,tokensort,lexeme)

        return self.currentToken

    def __repr__(self):
        """ print parsed ast """
        pass


class WaebricParser(Parser):
    """ start the parsing!"""
    def parse(self):
        logging.debug("--------------------")
        logging.debug('start parsing tokens')
        logging.debug("--------------------")
        module = ModuleParser(self)
        module.parseModule()


class ModuleParser(Parser):
    """ Parse module stmt defined in module """

    def parseModule(self):
        """parse module statements """
        self.next('module', 'Module ModuleID', lexeme=keywords['MODULE'] )

        #create a new ast module.
        #parse the module identifier.
        moduleId = self.parseModuleId()

        # while there are more tokens
        # parse for Site , Function and Import statements
        while(self.next()):
            if self.matchLexeme(keywords['IMPORT']):
                imports = ImportParser(self.tokens)
                logging.debug("parsing IMPORT")
            elif self.matchLexeme(keywords['DEF']):
                function = FunctionParser(self)
                logging.debug("parsing Function")
                function.parseFunction()
            elif self.matchLexeme(keywords['SITE']):
                site = SiteParser(self)
                logging.debug("parsing SITE")
                site.parseSite()
            elif self.matchTokensort(ENDMARKER):
                return
            else:
               raise UnexpectedToken(self.currentToken,
            expected="import, def, site, newline" )

    def parseModuleId(self):
        """Parse the module identifier """
        # AST new module id.
        moduleID = []
        self.next("Module Identifier", "head:[azAZ] body:[azAZ09]", tokensort=NAME)
        moduleID.append(self.currentToken)
        while(self.next()):
            if self.peek(x=1, lexeme="."):
                self.next("Module Identifier", "head:[azAZ] body:[azAZ09]", tokensort=NAME)
                moduleID.append(self.currentToken)
            else:
                return

class SiteParser(Parser):
    """ Parse Site function defined in module."""

    def parseSite(self):
        # AST
        self.check("Site Defenition", "Site begin, Mapping, end", lexeme=keywords['SITE'])
        # check mapping
        self.parseMapping()
        self.check("Site END", "site begin, mapping, end", lexeme=keywords['END'])

    def parseMapping(self):
        """ parse content in between site and end."""

        while(self.next()):
            if self.matchLexeme(keywords['END']):
                return

        # if site is not ending with an end we might end up here.
        raise UnexpectedToken(self.currentToken, )


class FunctionParser(Parser):

    def parseFunction(self):

        ## parse identifier
        ## parse formals
        ## parser statements.
        while(self.next()):
            statement = StatementParser(self)
            statement.parseStatement()

        raise UnexpectedToken(self.currentToken, expected="""end""")


class ExpressionParser(Parser):
    pass


class PredicateParser(Parser):
    pass


class StatementParser(Parser):

    def parseStatement(self):
        while self.next():
            if self.matchLexeme(keywords['LET']):
                self.parseLetStatement()
                return
            if self.matchLexeme(keywords['IF']):
                return
            elif self.matchLexeme(keywords['EACH']):
                return
            elif self.matchLexeme(keywords['ECHO']):
                return
            elif self.matchLexeme(keywords['CDATA']):
                return
            elif self.matchLexeme('}'): #?
                return
            elif self.matchLexeme('{'): #?
                return
            elif self.matchLexeme(keywords['COMMENT']):
                return
            elif self.matchLexeme(keywords['YIELD']):
                return
            elif self.matchTokensort(NAME):
                ##markup
                ##markup can be an expresion, functioncall.
                return
            else:
                break

        raise UnexpectedToken(self.currentToken,
            expected="""statement, "if", "each", "let", "{", "comment",
                "echo", "cdata", "yield" or Markup""" )

    def parseLetStatement(self):
        logging.debug("parse LET .. IN .. END block")
        if self.matchLexeme(keywords['LET']):
            while self.next():
                if self.matchLexeme(keywords['IN']):
                    while self.next():
                        if self.matchLexeme(keywords['END']):
                            logging.debug("end LET block")
                            return

            raise UnexpectedToken(self.currentToken,
                expected = """ LET .. IN .. END """)

    def fakeStatement(self):
        logging.debug("FAKE statement")
        while self.next():
                if self.matchLexeme(';'):
                        return

class EmbeddingParser(Parser):
    pass


class MarkupParser(Parser):
    """
    this is one of the more ambigious parts.
    """
    pass


def parse(source):
    tokens = tokenize.generate_tokens(source)
    parser = WaebricParser()
    parser.tokens = tokens
    parser.parse()

if __name__ == '__main__':                     # testing
    import sys
    if len(sys.argv) > 1: parse(open(sys.argv[1]).readline)
    else : parse(sys.stdin.readline)

