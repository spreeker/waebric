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

NOTE

Varible checking could be more thorow. Now scoping is
not correctly handled.

"""

from visitor import walk
from parser import parse
from error import trace
from xhtmltag import XHTMLTag
import logging

class WaeChecker:
    """ check ast for errors """

    def __init__(self, source):
        tree = parse(source)
        self.functions = {}
        self.scopes = {}
        self.names = {}

        self.errors = []

        walk(tree, self)

        for error in self.errors:
            print error

    @trace
    def visitModule(self,node):
        for f in node.functions:
            if self.functions.has_key(f.name):
                self.errors.append("%s function %s already defined" % (f.lineo, f.name) )
            else:
                self.functions[f.name] = f#len(f.arguments)

        #note when executing visiting is different!
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
            #parse it and merge it.
        except:
            self.errors.append("%s could not open module %s" % (node.lineo, node.moduleId))

    @trace
    def visitMarkup(self, node):
        if self.functions.has_key(node.designator.name):
            #print "valid function call"
            f = self.functions[node.designator.name]
            if not len(f.arguments) == len(node.arguments):
                self.errors.append("%s arity mismatch %s" % (node.lineo,node.designator))
            else:
                for name,exp in zip(f.arguments, node.arguments):
                    self.names[name.name] = exp

        elif node.arguments: #it has arguments. so it must be an function call
                             #but there is no function defenition.
            print "invalid function call"
            self.errors.append("arguments given to not defined function %s" % (
                node.lineo,
                node.designator.name))
            self.errors.append("args = %s "% node.arguments)

        else:
            # check if markup is a valid xhtml tag.
            if not node.designator.name.upper() in XHTMLTag:
                self.errors.append("%s invalid tag/function used/called! %s" % (
                    node.lineo,
                    node.designator))

        #print node.getChildNodes()
        logging.debug("CHILDNODES: %s" % repr(node.getChildNodes()))
        for child in node.getChildNodes():
            self.visit(child)

    @trace
    def visitName(self, node):
        if not node.name in self.names:
            self.errors.append("variable not found!! %s" % node.name)


if __name__ == '__main__':
    import sys
    if len(sys.argv) > 1:
        sourceFile = open(sys.argv[1])
        source = sourceFile.readline
        check = WaeChecker(source)

