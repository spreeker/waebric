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

    def __init__(self, *args):
        logging.debug("raised exception")
        logToken(*args) 

class AbstractParser():

    exceptions = []
    currentToken = ""
    peekedTokens = [] 

    def __init__(self,tokenIterator):
        self.tokens = tokenIterator

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
           logToken(*self.currentToken)
           if not self.currentToken[1] == lexeme:
                raise UnexpectedToken(*self.currentToken)
        elif tokensort:
            if not self.currentToken[0] == tokensort:
                raise UnexpectedToken(*self.currentToken)

    def peek(self, x=1):

        for i in range(x):
            peekedTokens.append(self.tokens.next())

        return peekedTokens[x-1]

    def next(self, name="", syntax="", tokensort=None, lexeme=None):
        """
        Get the next token, if tokensort or lexeme is defined
        checks if next token matches tokensort of lexeme.
        """
        if self.peekedTokens:
            currentToken = self.peekedTokens.pop(0)
        else:
            currentToken = self.tokens.next()

        if tokensort or lexeme:
            self.check(name, syntax, tokensort, lexeme)

        if DEBUG:
            tokenize.printtoken(*self.currentToken)

        if self.matchTokensort(NEWLINE):
            return self.next(name,syntax,tokensort,lexeme)

        return self.currentToken

    def current(self, name, syntax, tokensort=None, lexeme=None):
        """ Get current token.  """
        self.check(name, syntax, tokensort, lexeme)
        return self.currentToken

    def __repr__(self):
        """ print parsed ast """
        pass


class WaebricParser(AbstractParser):
    """ Parse a waberic file. Start the root to the AST."""

    def parse(self):
        module = ModuleParser(self)
        module.parseModule()


class ModuleParser(AbstractParser):
    """ Parse module stmt defined in module """

    def __init__(self, Parser):
        self.tokens = Parser.tokens
        self.currentToken = Parser.currentToken
        self.siteParser = SiteParser(tokens)
        self.functionParser = FunctionParser(tokens)

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
                site = SiteParser(self.tokens)
                logging.debug("parsing IMPORT")
            elif self.matchLexeme(keywords['DEF']):
                function = FunctionParser(self.tokens)
                logging.debug("parsing Function")
            elif self.matchLexeme(keywords['SITE']):
                site = SiteParser(self.tokens)
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
            except UnexpectedToken :
                break

        return moduleID


class SiteParser(AbstractParser):
    """ Parse Site function defined in module."""

    def parseSite(self):
        """ parse site """
        #
        tokenize.printtoken(*self.currentToken)
        self.check("Site Defenition", "Site begin, Mapping, end", lexeme=keywords['SITE'])
        # check mapping
        self.parseMapping()
        self.check("Site END", "site begin, mapping, end", lexeme=keywords['END'])

    def parseMapping(self):
        """ parse content in between site and end."""

        while(self.next()):
            if self.current[1].equals(keywords['END']):
                return

        raise UnexpectedToken(self.currentToken)

class FunctionParser(AbstractParser):
    pass


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
    parser = WaebricParser(tokens)
    parser.parse()

if __name__ == '__main__':                     # testing
    import sys
    if len(sys.argv) > 1: parse(open(sys.argv[1]).readline)
    else : parse(sys.stdin.readline)

