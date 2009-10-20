import logging
import tokenize

from token import *

DEBUG = True
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

    def __init__(self, token):
        logging.debug("raised exception")
        logToken(*token)


class AbstractParser():

    tokens = []
    peekedTokens = []
    currentToken = []

    def __init__(self, Parser=None):
        if Parser:
            self.tokens = Parser.tokens
            self.peekedTokens = Parser.peekedTokens
            self.currentToken = Parser.currentToken

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

    def peek(self, name, syntax, x=1, tokensort=None, lexeme=None):
        """ lookahead x tokens in advance, returns true if tokensort of lexeme
            matches
        """
        x = x - len(self.peekedTokens)

        if x > 0:
            for i in range(x):
                self.peekedTokens.append(self.tokens.next())

        peekToken = self.peekedTokens[x-1]
        if tokensort:
            if not peektoken[0] == tokensort:
                return False
        if lexeme:
            if not peektoken[1] == lexeme:
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
            self.currentToken = self.tokens.next()

        if tokensort or lexeme:
            self.check(name, syntax, tokensort, lexeme)

        if DEBUG:
            logToken(*self.currentToken)

        if self.matchTokensort(NEWLINE):
            return self.next(name,syntax,tokensort,lexeme)

        return self.currentToken

    def __repr__(self):
        """ print parsed ast """
        pass


class WaebricParser(AbstractParser):
    """ start the parsing!"""
    def parse(self):
        logging.debug('start parsing tokens')
        module = ModuleParser(self)
        module.parseModule()


class ModuleParser(AbstractParser):
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
                function.parseFunction()
                logging.debug("parsing Function")
            elif self.matchLexeme(keywords['SITE']):
                site = SiteParser(self)
                logging.debug("parsing SITE")
                site.parseSite()
            elif self.matchTokensort(NL):
                pass
            elif self.matchTokensort(NEWLINE):
                pass
            else:
               raise UnexpectedToken(self.currentToken)

    def parseModuleId(self):
        """Parse the module identifier """
        # AST new module id.
        moduleID = []
        self.next("Module Identifier", "head:[azAZ] body:[azAZ09]", tokensort=NAME)
        moduleID.append(self.currentToken)
        while(self.next()):
            try:
                self.check("DOT", ".", lexeme=".")
                self.next("Module Identifier", "head:[azAZ] body:[azAZ09]", tokensort=NAME)
                moduleID.append(self.currentToken)
            except UnexpectedToken :
                break


class SiteParser(AbstractParser):
    """ Parse Site function defined in module."""

    def parseSite(self):
        # AST
        logging.debug('parse site')
        logToken(*self.currentToken)
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
        raise UnexpectedToken(self.currentToken)


class FunctionParser(AbstractParser):

    def parseFunction(self):
        logging.debug('parse function')

        self.matchLexeme(keywords['DEF'])

        while(self.next()):
            #TODO staments of all sorts...
            if self.matchLexeme(keywords['END']):
                return

        raise UnexpectedToken(self.currentToken)


class ExpressionParser(AbstractParser):
    pass


class PredicateParser(AbstractParser):
    pass


class StatementParser(AbstractParser):
    pass


class EmbeddingParser(AbstractParser):
    pass


class MarkupParser(AbstractParser):
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

