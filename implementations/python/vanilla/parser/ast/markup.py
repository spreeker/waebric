"""
Abstract Syntax Tree Markup Nodes
"""

from ast import Node

#markup.

class Designator(Node):
    def __init(self, name, attributes=[]):
        self.name = name
        self.attributes = attributes

class Attribute(Node):
    def __init__(self, symbol, value):
        self.symbol = symbol
        self.value = value

class Attributes(Node):
    def __init__(self):
        attributes = []

    def add(self, attribute)
        attributeis.append(attribute)

class Argument(Node):
    pass
class Arguments(Node):
    pass
class Markup(Node):
    pass

