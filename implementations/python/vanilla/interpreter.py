"""
Waebric interpreter.

Generates output of Astract Syntac Tree of waebrick parse Tree.
Visit each node in module and create XHTML output.

This waebric interpreter uses a variant on the
visitor pattern that takes advantage of Python’s
introspection features to eliminate the need for much of the visitor’s
infrastructure.

for each AST node the should be an
visitNodeClass function.
"""

from parser import parser
from xml.etree import cElementTree as ET

from document import Document


def interprete(module):
    #load modules/imports.
    #interpretate AST of each module
    #load sites
    #execute site main functions or site mapping functions.
    #write output.
    pass

def getDependencies(env, module):
    #proces imports
    pass

def getSiteMappigns(env, module):
    #find site mappings
    pass

def getEnvMainFunction(env,ast):
    #execute main function if there is one and create output.
    pass

def interpretateSites(env,ast)
    #execute site mapping functions and craete different htm documents.
    pass


class Environment(object):
    """ keeps track of environment functions and variables """

    def __init__(self):
        self.variables = {}
        self.functions = {}
        self.sites = []
        self.modules = [] # AST module nodes.

        self.doc = Document()

    def __repr__(self):
        vars = "".join(["%s:%s" % (var, value) for var in self.variables.items()])
        functions = "".join(self.functions.keys())

        return "Variables %s, \n Functions %s \n " % (vars,functions)


class UndefinedFunction(exception):
    pass

class UndefinedVariable(exception):
    pass

class UndefinedModule(exception):
    pass

class ArityMismatch(exception):
    """ function is called with wrong arguments."""
    pass

