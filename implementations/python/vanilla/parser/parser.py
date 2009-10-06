
import tokenizer

keywords = {'IF':'if', 'ELSE':'else',
            'EACH':'each', 'LET':'let', 'IN':'in', 'COMMENT':'comment',
            'ECHO':'echo', 'CDATA':'cdata', 'YIELD':'yield',

            'MODULE':'module', 'IMPORT':'import', 'DEF':'def', 'END':'end',
            'SITE':'site',

            'LIST':'list', 'RECORD':'record', 'STRING':'string',
            }

class AbstracParser():

    exceptions = []
    currentToken = ""
    peek = ""

    def __init__(self,tokenIterator):
        self.tokens = tokenIterator

    def next(self, name, syntax, tokensort=None, lexeme=None):
        """
        Get the next token, if tokensort or lexeme is defined 
        checks if next token matches tokensort of lexeme.
        """
        self.currentToken = self.tokens.next()

        if lexeme:
           if self.current[1].equals(lexeme):
                continue
           else:
                self.reportUnexpectedToken(self, self.current, name, syntax)
        elif tokensort:
           if self.current[0] == tokensort
                continue
           else:
               self.reportUnexpectedToken(self,self.current, name, syntax)

        return self.currentToken

    def current(self, name, syntax, tokensort=None, lexeme=None):
        """ Set current token.  """
        return self.currentToken

    def reportMissingToken(self, name, syntax):
        """
        If we expect an token and it is not there raise a missing token
        exception
        """
        print "missing token %s %s" % (name, syntax)
        pass

    def reportUnexpectedToken(self, token, name, syntax):
        """ Report unexpected token by raising an unexpected token exception.
        """
        print "unexpected token %s %s %s" % (token, name, syntax)

    def __repr__(self):
        """ print parsed ast """
        pass

class WaebricParser(AbstracParser):
    """ Parse a waberic file. Start the root to the AST."""

    def parse(self)
        module = ModuleParser(self.tokens)


class ModuleParser(AbstracParser):
    """ Parse module stmt defined in module """

    def __init__(self, tokens):
        self.siteParser = SiteParser(tokens)
        self.functionParser = FunctionParser(tokens)

    def parseModule(self):
        """parse module statements """
        self.next('module', 'Module ModuleID', lexeme=keywords[MODULE] )

        #create a new ast module.
        #parse the module identifier.
        moduleId = self.parseModuleId()

        while(self.next)


    def parseModuleId(self):
        """Parse the module identifier """
        # AST new module id.
        moduleID = []
        self.next("Module Identifier", "head:[azAZ] body:[azAZ09]", tokensort=NAME)
        moduleID.add(self.currentToken)
        moduleidentied = False
        while( not moduleidentified ):
            self.next("DOT", ".", lexeme=".")
            self.next("Module Identifier", "head:[azAZ] body:[azAZ09]", tokensort=NAME)


class SiteParser(AbstracParser):
    """ Parse Site function defined in module."""
    pass

class FunctionParser(AbstracParser):
    pass


class ExpressionParser(AbstracParser):
    pass


class PredicateParser(AbstracParser):
    pass


class StatementParser(AbstracParser):
    pass


class EmbeddingParser(AbstracParser):
    pass

class MarkupParser(AbstracParser):
    pass


def parse(source):
    tokens = tokenizer.generate_tokens(source)
    parser = WaebricParser(tokens)
    parser.parse()

    print parser

if __name__ == '__main__':                     # testing
    import sys
    if len(sys.argv) > 1: parse(open(sys.argv[1]).readline)
    else : parse(sys.stdin.readline)

