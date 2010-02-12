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


class waeChecker:
    """ check ast for errors """

    def __init__(self, source):
        tree = parse(source)
        functions = {}
        scopes = {}
        names = {}

    def visitFunction(self, node):
        """check function defenition. """

        if self.functions.has_key(node.name):
            # ERROR !!

        self.functions[node.name] = node.arguments

        for child in node.childNodes():
            self.visit(child)

    def visitName(self, node):
        """ check variable name """

        self.names[node.name] = ""


    def visitImport(self, node):
        try:
            open("%s.wae" % node.moduleId)

        except:
            #raise error module does not excist.
            #pass error import is skipped.
            pass

    def visitMarkup(self, node):
        # figure out if it is a function call.
        # check if function excists and arity.
        for child in node.childNodes():
            self.visit(child)
