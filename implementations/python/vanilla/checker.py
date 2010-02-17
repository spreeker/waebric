"""
Checks AST for errors.

In the tokenizer and parser many errors are already catched reported.
Errors in the AST or semantic errors are checked for here.

This visitor checks for:
    Undefined Functions.
    Undefined Variables
    Non-existing modules
    Duplicate definitions
    Arity Mismatches
"""
from visitor import walk
from parser import parse
from error import trace
from xhtmltag import XHTMLTag

class WaeChecker:
    """ check ast for errors """

    def __init__(self, source):
        tree = parse(source)
        self.functions = {}
        self.scopes = {}
        self.names = {}

        self.errors = []

        walk(tree, self, verbose=1)

        for error in self.errors:
            print error
    @trace
    def visitFunction(self, node):
        """check function defenition. """

        if self.functions.has_key(node.name):
            self.errors.append("function %s already defined" % node.name )

        self.functions[node.name] = len(node.arguments)

        for child in node.getChildNodes():
            self.visit(child)

    @trace
    def visitAssignment(self, node):
        """ check variable name """

        self.names[node.name] = node.statement

        for child in node.getChildNodes():
            self.visit(child)

    @trace
    def visitImport(self, node):
        try:
            open("%s.wae" % node.moduleId)
        except:
            #raise error module does not excist.
            #pass error import is skipped.
            self.errors.append("could not open module %s" % node.moduleId)

    @trace
    def visitMarkup(self, node):

        if self.functions.has_key(node.designator):
            #it is a function.
            if not function[node.designator] == len(node.arguments):
                #arity is not ok
                self.errors.append("arity mismatch %s" % node.designator)
        else:
            # check if it is a valid xhtml tag.
            if not node.designator.name.upper() in XHTMLTag:
                self.errors.append("invalid tag used! %s"  % node.designator)

        for child in node.getChildNodes():
            self.visit(child)


if __name__ == '__main__':
    import sys
    if len(sys.argv) > 1:
        sourceFile = open(sys.argv[1])
        source = sourceFile.readline
        check = WaeChecker(source)

