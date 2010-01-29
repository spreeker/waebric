"""
Waebric interpreter.

Visit each node in module and create HTML output.

Generates output of Astract Syntac Tree of waebrick parse Tree.

"""
from parser import parser

import elementtree.ElementTree as ET #change to cElementree
#if possible.


class Interpreter(object):
    """interpret waebric ast """
    output = ""
    #keep track of dependencies/modules

    def interprete(module):
        #create envitonment.
        #parse source and get AST.
        #interpretate AST
        #create output.?

        #load modules.
        #load sites
        #execute site main functions.
        #write output.
        pass

    def getDependencies(env,ast):
        pass

    def getSiteMappigns(env, ast):
        pass

    def getEnvMainFunction(env,ast):
        pass


class Environment(object):
    """ keeps track of environment functions and variables """

    def __init__(self):
        self.variables = {}
        self.functions = {}

    def __repr__(self):
        vars = "".join(["%s:%s" % (var, value) for var in self.variables.items()])
        functions = "".join(self.functions.keys())

        return "Variables %s, \n Functions %s" % (vars,functions)


class UndefinedFunction(exception):
    pass

class UndefinedVariable(exception):
    pass

class UndefinedModule(exception):
    pass

class ArityMismatch(exception):
    """ function is called with wrong arguments."""
    pass


