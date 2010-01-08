"""
Abstract Syntax Tree Expression Nodes
"""

from ast import Node

class TextExpression(Node):
    def __init__(self, text):
        self.text = text

    def __repr__(self):
        return "STRING(%s)" % self.text


class NumberExpression(Node):
    def __init__(self, number):
        self.number = int(number)

    def __repr__(self):
        return "NATNUM(%d)" % self.number


class ListExpression(Node):
    def __init__(self):
        self.expressionList = []

    def addExpression(self,expression):
        self.expressionList.append(expression)

    def __repr__(self):
        items = [repr(expr) for expr in self.expressionList]
        return "LIST(%s)" % (",".join(items))


class RecordExpression(Node):
    def __init__(self):
        self.dict = {}
    def addRecord(self, key, expression):
        self.dict[key] = expression

    def __repr__(self):
        items = [":".join([repr(key),repr(value)]) for key,value in self.dict.items()]
        return "RECORD(%s)" % (",".join(items))


class CatExpression(Node):
    def __init__(self, left, right):
        self.left = left
        self.right = right

    def __repr__(self):
        return "ADD(%s, %s)" % (repr(self.left), repr(self.right))


class Field(Node):
    def __init__(self, expression, field):
        self.expression = expression
        self.field = field

    def __repr__(self):
       return "FIELD %s in %s" % (repr(self.field), repr(self.expression))
