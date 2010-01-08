"""
Abstract Syntax Tree Statement Nodes
"""

from ast import Node

class Statement(Node):
    pass

class Embedding(Statement):
    def __init__(self):
        pretext = []
        midtext = []
        tailtext= []

    def __repr__(self):
        output = ""
        for p,emb in zip(self.pretext,self.midtext):
            output = "%s%s%s" % (output,self.pretext,repr(self.midtext))
        output = "%s%s" % (output,self.tailtext)

        return output

class Assignment(Statement):
    """ Name "(" {Name ","}* ")"  "=" Statement -> Assignment """
    def __init__(self, name, statement):
        self.variables = []
        self.name = name
        self.statement = statement

    def addVariable(self, variable):
        self.variables.append(variable)

    def __repr__(self):
        if self.variables:
            assvar = "%s(%s)" % (self.name, ",".join(repr(self.variables)))
        else:
            assvar = self.name

        return "ASSIGNMENT(%s,%s)" % (assvar, repr(self.statement))


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
        pass

    def getChildNodes(self):
        pass

    def __repr__(self):
        ass = ",".join([repr(assignment) for assignment in assignments])
        return "LET(%s, %s)" % (ass, repr(self.body))


class If(Statement):
    def __init__(self, predicate, ifstmnt, elsestmnt = None,):
        self.predicate = predicate
        self.elsestmnt = elsestmnt
        self.ifstmnt = ifstmnt

    def getChildren(self):
        children = []
        return tuple(children)

    def getChildNodes(self):
        if self.elsestmnt is not None:
            nodelist.append(self.elsestmnt)
        return tuple(nodelist)

    def __repr__(self):
        return "If(%s, %s)" % (repr(self.tests), repr(self.else_))


class Echo(Statement):
    def __init__(self,embedding="",expression=""):
        expression = embedding
        embedding = expression

    def __repr__(self):
        return "ECHO( %s%s )" (repr(self.expression),repr(self.embedding))


class Cdata(Statement):
    def __init__(self, expression):
        self.expression = expression

    def __repr__(self):
        return "Cdata( %s )" % repr(self.expression)


class Comment(Statement):
    def __init__(self, comment):
        self.comment = comment

    def __repr__(self):
        return "Comment( %s )" % self.comment


class Each(Statement):
    def __init__(self, name, expression, statement):
        self.name = name
        self.expression = expression
        self.statement = statement

    def __repr__(self):
        return "Each( %s in %s do %s)" % (
                name,  repr(statement), repr(expression))

