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
        return "Designator( %s %s)" % (self.name,repr(self.attibutes))

class Attribute(Node):
    def __init__(self, symbol, value):
        self.symbol = symbol
        self.value = value

    def __repr__(self):
        return "ATTR( %s, %s )" % (self.symbol, self.value)

class Attributes(Node):
    def __init__(self):
        attributes = []

    def add(self, attribute):
        attributeis.append(attribute)

    def __repr__(self):
        attrs = ",".join([ repr(attr) for attr in self.attributes ])
        return "( %s )" % attrs

class Markup(Node):
    def __init__(self, designator):
        self.designator = designator
        self.arguments = []
        self.embedding = ""
        self.variable = ""
        self.expression = ""

    def __repr__(self):

        extra = ""
        if self.arguments:
            extra = "( %s )" %  (",".join(arguments))
        if self.embedding:
            extra = "%s %s" % ( extra, repr(self.embedding))
        elif self.variable:
            extra = "%s %s" % ( extra, repr(self.variable))
        elif self.expression:
            extra = "%s %s" % ( extra, repr(self.expression))

        return "MARKUP( %s %s )" % (repr(self.designator), extra)
