"""
Abstract Syntax Tree Statement Nodes
"""

from ast import Node

class Embedding(Node):
    pass

class PreText(Node):
    pass

class MidText(Node):
    pass

class TailText(Node):
    pass


class Assignment(Node):
    """ Name "(" {Name ","}* ")"  "=" Statement -> Assignment """
     def __init__(self, name, statement):
        self.variables = []
        self.name = name
        self.statement = statement

     def addVariable(self, variable):
        self.variables.append(variable)

class Statement(Node):
    def __init__(self):
        self.markups = []

class Yield(Node):
    def __init__(self, value, lineno=None):
        self.value = value
        self.lineno = lineno

    def getChildren(self):
        return self.value,

    def getChildNodes(self):
        return self.value,

    def __repr__(self):
        return "Yield(%s)" % (repr(self.value),)

class Let(Node):
    def __init__(self, expr, vars, body):
        self.expr = expr
        self.vars = vars
        self.body = body

    def getChildren(self):
        children = []
        children.append(self.expr)
        children.append(self.vars)
        children.append(self.body)
        return tuple(children)

    def getChildNodes(self):
        nodelist = []
        nodelist.append(self.expr)
        if self.vars is not None:
            nodelist.append(self.vars)
        nodelist.append(self.body)
        return tuple(nodelist)

    def __repr__(self):
        return "With(%s, %s, %s)" % (repr(self.expr), repr(self.vars), repr(self.body))


class If(Node):
    def __init__(self, tests, else_, lineno=None):
        self.tests = tests
        self.else_ = else_
        self.lineno = lineno

    def getChildren(self):
        children = []
        children.extend(flatten(self.tests))
        children.append(self.else_)
        return tuple(children)

    def getChildNodes(self):
        nodelist = []
        nodelist.extend(flatten_nodes(self.tests))
        if self.else_ is not None:
            nodelist.append(self.else_)
        return tuple(nodelist)

    def __repr__(self):
        return "If(%s, %s)" % (repr(self.tests), repr(self.else_))

class Echo(Statement):
    pass


