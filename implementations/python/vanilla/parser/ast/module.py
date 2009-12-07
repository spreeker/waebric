"""
Abstract Syntax Tree Module Nodes
"""

from ast import Node

class Module(Node):

    def __init__(self, nodes):
        self.nodes = nodes

    def getChildren(self):
        pass


class Function(Node):
    pass

class Site(Node):
    pass

class Path(Node):
    pass

class Import(Node):
    pass

class Mapping(Node):
    pass

class Mappings(Node):
    pass

class Function(Node):
    pass


