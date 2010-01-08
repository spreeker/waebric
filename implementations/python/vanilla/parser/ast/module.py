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

    def __repr__(self):

        functions = "\n".join( [repr(function) for function in self.functions])
        sites = "\n".join( [repr(site) for site in self.sites])
        imports = "\n".join( [repr(_import) for _import in self.imports])

        output = """MODULE %s ,
        SITES %s
        IMPORTS %s
        FUNCTIONS %s
        """ % ( self.id, sites, imports, functions)

        return output

class Function(Node):
    def __init__(self, name, arguments):
        self.name = name
        self.arguments = arguments
        self.statements = []

    def addStatement(self, statement):
        self.statements.append(statement)

    def __repr__(self):
        return "FUNCTION %s ( %s ) { %s } \n" % (self.name,
            ",".join([repr(arg) for arg in self.arguments]),
            "\n".join([repr(stm) for stm in self.statement]))

class Import(Node):
    def __init__(self, moduleId):
        self.moduleId = moduleId

    def __repr__(self):
        return  "IMPORT %s" % self.moduleId

class Path(Node):
    def __init__(self, dir, fileName):
        self.dir = dir
        self.fileName = fileName

    def __repr__(self):
        return "PATH(%s/%s)" % (self.dir, self.filename)

class Mapping(Node):
    def __init__(self, path,markup):
        self.path = path
        self.markup = markup

    def __repr__(self):

        return "MAPPING(%s, %s)" % (repr(path), repr(markup))

