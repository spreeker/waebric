"""
Abstract Syntax Tree Predicate Nodes
"""

from ast import Node

class Not(Node):
    def __init__(self, predicate):
        self.predicate = predicate

class And(Node):
    def __init__(self, leftPredicate, rightPredicate):
        self.left = leftPredicate
        self.right = rightPredicate


class Or(Node):
    def __init__(self, leftPredicate, rightPredicate):
        self.left = leftPredicate
        self.right = rightPredicate

class Is_a(Node):
    def __init__(self, expression, type):
        self.expression = expression
        self.type = type
