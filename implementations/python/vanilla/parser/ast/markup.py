"""
Abstract Syntax Tree Markup Nodes
"""

from ast import Node

#markup.

class Designator(Node):
    def __init__(self, name, attributes=[]):
        self.name = name
        self.attributes = attributes

    def __repr__(self):
        attrs = ",".join([repr(attr) for attr in self.attributes])
        return "Designator(%s%s)" % (repr(self.name),attrs)

class Attribute(Node):
    def __init__(self, symbol, value):
        self.symbol = symbol
        self.value = value

    def __repr__(self):
        return "(%s, %s)" % (repr(self.symbol), repr(self.value))

class Attributes(Node):
    def __init__(self):
        attributes = []

    def add(self, attribute):
        attributes.append(attribute)

    def __repr__(self):
        attrs = ",".join([ repr(attr) for attr in self.attributes ])
        return "ATTRBS( %s )" % attrs

class Markup(Node):
    def __init__(self, designator):
        self.childs = []
        self.designator = designator
        self.arguments = []
        self.embedding = ""
        self.variable = ""
        self.expression = ""

    def __repr__(self):

        extra = ""
        if self.arguments:
            extra = "%s" %  ((repr(self.arguments)))
        elif self.embedding:
            extra = "%s%s" % ( extra, repr(self.embedding))
        elif self.variable:
            extra = "%s%s" % ( extra, repr(self.variable))
        elif self.expression:
            extra = "%s%s" % ( extra, repr(self.expression))

        if self.childs:
            child = self.childs[0]
            child.childs = self.childs[1:]
            output = "MARKUP(%s%s%s)" % (
                repr(self.designator), extra,
                repr(child))
        else:
            output =  "MARKUP(%s%s)" % (repr(self.designator), extra)
        return output
