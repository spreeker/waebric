"""
Abstract Syntax Tree Module Nodes
"""

from ast import Node

class Module(Node):

    def __init__(self, id):
        self.id = id
        self.functions = []
        self.sites = []
        self.imports = []

    def addFunction(self, function):
        self.functions.append(function)

    def addSite(self, site):
        self.sites.append(site)

    def addImport(self, import_):
        self.imports.append(import_)


class Function(Node):
    def __init__(self, name, arguments):
        self.name = name
        self.arguments = arguments
        self.statements = []

    def addStatement(self, statement):
        self.statements.append(statement)

class Import(Node):
    def __init__(self, moduleId):
        self.moduleId = moduleId

class Path(Node):
    def __init__(self, dir, fileName):
        self.dir = dir
        self.fileName = fileName

class Mapping(Node):
    def __init__(self, path,markup):
        self.path = path
        self.markup = markup


