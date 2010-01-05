"""
Abstract Syntax Tree Statement Nodes
"""

from ast import Node

class Embedding(Node):
    def __init__(self):
        pretext = []
        midtext = []
        tailtext= []


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

class Yield(Statement):

    def getChildren(self):
        return []

    def getChildNodes(self):
        return []

    def __repr__(self):
        return "Yield"

class Let(Statement):
    def __init__(self, assignments, body):
        self.assignments = assignments
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


class If(Statement):
    def __init__(self, tests, else_ = None,):
        self.tests = tests
        self.else_ = else_

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
    def __init__(self,embedding=None,expression=None)
        expression = None
        embedding = None


class Cdata(Statement):
    def __init__(self, expression):
        self.expression = expression

class Comment(Statement):
    def __init__(self, comment):
        self.comment = comment



