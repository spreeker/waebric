"""
Abstract Syntax Tree Module Nodes
"""

from ast import Node

class Module(Node):

    def __init__(self, id):
        self.id = id
        self.functions = []
        self.sites = []

    def addFunction(self, function):
        self.functions.append(function)

    def addSite(self, site)
        self.sites.append(site)

    def addImport(self, import):
        self.imports.append(import)


class Function(Node):
    def __init__(name,arguments):
        self.name = name
        self.arguments = arguments
        self.statements = []

    def addStatement(self, statement):
        self.statements.append(statement)


class Site(Node):
    pass

class Path(Node):
    def __init__(self, dir, fileName):
        self.dir = dir
        self.fileName = fileName

class Import(Node):
    pass

class Mapping(Node):
    def __init__(self, path,markup):
        self.path = path
        self.markup = markup

class Mappings(Node):
    def __init__(self):
        self.mappings = []
    def addMapping(self,mapping):
        self.mappings.append(mapping)

