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


class Predicate(Node):
    pass

class And(Node):
    def __init__(self, nodes, lineno=None):
        self.nodes = nodes
        self.lineno = lineno

    def getChildren(self):
        return tuple(flatten(self.nodes))

    def getChildNodes(self):
        nodelist = []
        nodelist.extend(flatten_nodes(self.nodes))
        return tuple(nodelist)

    def __repr__(self):
        return "And(%s)" % (repr(self.nodes),)


class Not(Node):
    def __init__(self, expr, lineno=None):
        self.expr = expr
        self.lineno = lineno

    def getChildren(self):
        return self.expr,

    def getChildNodes(self):
        return self.expr,

    def __repr__(self):
        return "Not(%s)" % (repr(self.expr),)

class Or(Node):
    def __init__(self, nodes, lineno=None):
        self.nodes = nodes
        self.lineno = lineno

    def getChildren(self):
        return tuple(flatten(self.nodes))

    def getChildNodes(self):
        nodelist = []
        nodelist.extend(flatten_nodes(self.nodes))
        return tuple(nodelist)

    def __repr__(self):
        return "Or(%s)" % (repr(self.nodes),)




class Type(Node):
    pass


class Assignment(Node):
    pass

class Statement(Node):
    pass

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
    def __init__(self, expr, vars, body, lineno=None):
        self.expr = expr
        self.vars = vars
        self.body = body
        self.lineno = lineno

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


