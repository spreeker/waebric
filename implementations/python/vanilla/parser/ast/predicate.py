"""
Abstract Syntax Tree Predicate Nodes
"""

from ast import Node

class Not(Node):
    def __init__(self, predicate):
        self.predicate = predicate

    def __repr__(self):
        return "NOT(%s)" % self.predicate

class And(Node):
    def __init__(self, leftPredicate, rightPredicate):
        self.left = leftPredicate
        self.right = rightPredicate

    def __repr__(self):
        return "AND(%s, %s)" % (repr(self.left), repr(self.right))

class Or(Node):
    def __init__(self, leftPredicate, rightPredicate):
        self.left = leftPredicate
        self.right = rightPredicate

    def __repr__(self):
        return "OR(%s, %s)" % (repr(self.left), repr(self.right))


class Is_a(Node):
    def __init__(self, expression, type):
        self.expression = expression
        self.type = type

    def __repr__(self):
        return "IS_A(%s, %s)" % ( self.expression, self.type)
