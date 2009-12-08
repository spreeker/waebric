"""
Abstract Syntax Tree Expression Nodes
"""

from ast import Node

class TextExpression(Node):
     def __init__(self, text):
        self.text = text
class NumberExpression(Node):
     def __init__(self, number):
        self.number = int(number)

class ListExpression(Node):
    def __init__(self):
        self.expressionList = []

    def addExpression(self,expression)
        self.expressionList.append(expression)

class RecordExpression(Node):
    def __init__(self):
        self.dict = {}
    def addRecord(self, key, expression):
        self.dict[key] = expression

class FieldExpression(Node):
    pass

class CatExpression(Node):
    def __init__(self, left, right):
        self.left = left
        self.right = right

class KeyValuePair(Node):
    pass


class Field(Node):
    def __init__(self, expresion, field):
        self.expression = expression
        self.field = field

