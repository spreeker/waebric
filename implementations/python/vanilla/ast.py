"""
Abstract Syntax Node Defenitions
"""
from error import trace
import logging

def flatten(seq):
    l = []
    for elt in seq:
        t = type(elt)
        if t is tuple or t is list:
            for elt2 in flatten(elt):
                l.append(elt2)
        else:
            l.append(elt)
    return l

def flatten_nodes(seq):
    return [n for n in flatten(seq) if isinstance(n, Node)]

nodes = {}

class Node(object):
    """Abstract base class for ast nodes."""

    def __iter__(self):
        for n in self.getChildNodes():
            yield n

    def getChildren(self):
        pass # implemented by subclasses

    def getChildNodes(self):
        return () 

    def __repr__(self):
        pass # implemented by subclasses


#Module Nodes.
class Module(Node):

    def __init__(self, id, lineo=None):
        self.lineno = lineo
        self.id = id
        self.functions = []
        self.sites = []
        self.imports = []

    def addFunction(self, function):
        self.functions.append(function)

    def addSite(self, site):
        self.sites.append(site)

    def addImport(self, import_):
        self.imports.append(import_)

    @trace
    def getChildren(self):
        #return getChildNodes(self)
        children = []
        children.append(flatten_nodes(self.imports))
        children.append(flatten_nodes(self.sites))
        children.append(flatten_nodes(self.functions))
        return tuple(children)

    @trace
    def getChildNodes(self):
        nodelist = []
        nodelist.extend(flatten_nodes(self.imports))
        nodelist.extend(flatten_nodes(self.sites))
        nodelist.extend(flatten_nodes(self.functions))
        return tuple(nodelist)

    def __repr__(self):

        functions = "\n".join( [repr(function) for function in self.functions])
        sites = "\n".join( [repr(site) for site in self.sites])
        imports = "\n".join( [repr(_import) for _import in self.imports])

        output = """MODULE %s ,
        SITES %s
        IMPORTS %s
        FUNCTIONS %s """ % ( self.id, sites, imports, functions)

        return output

class Function(Node):

    def __init__(self, name, arguments=[], lineo=None):
        self.lineo = lineo
        self.name = name
        self.arguments = arguments
        self.statements = []

    def addStatement(self, statement):
        self.statements.append(statement)

    def getChildren(self):
        children = []
        children.append(self.name)
        children.append(self.arguments)
        children.append(self.statements)
        return tuple(children)

    def getChildNodes(self):
        nodelist = []
        nodelist.extend(flatten_nodes(self.statements))

        return tuple(nodelist)

    def __repr__(self):
        return "FUNCTION %s (%s) { %s } \n" % (self.name,
            ",".join([repr(arg) for arg in self.arguments]),
            ",".join([repr(stm) for stm in self.statements]))

class Import(Node):
    def __init__(self, moduleId, lineo=None):
        self.lineo = lineo
        self.moduleId = moduleId

    def getChildren(self):
        return self.moduleId

    def __repr__(self):
        return  "IMPORT %s" % self.moduleId

class Path(Node):
    def __init__(self, dir, fileName, lineo=None):
        self.lineo = lineo
        self.dir = dir
        self.fileName = fileName

    def getChildren(self):
        return self.dir, self.fileName

    def __repr__(self):
        path = "%s/%s" % (self.dir,self.fileName) if self.dir else self.fileName
        return "PATH(%s)" % path

class Mapping(Node):
    def __init__(self, path, markup, lineo=None):
        self.lineo = lineo
        self.path = path
        self.markup = markup

    def getChildren(self):
        return self.path, self.markup

    def __repr__(self):

        return "MAPPING(%s, %s)" % (repr(self.path), repr(self.markup))

#ID
class Name(Node):
    def __init__(self, name, lineo=None):
        self.lineo = lineo
        self.name = name

    def getChildren(self):
        return self.name,

    def __repr__(self):
        return "NAME(%s)" % (repr(self.name),)

#EXPRESSION NODES
class Text(Node):
    def __init__(self, text, lineo=None):
        self.lineo = lineo
        self.text = text

    def getChildren(self):
        return self.text

    def __repr__(self):
        return "STRING(%s)" % self.text


class Number(Node):
    def __init__(self, number, lineo=None):
        self.lineo = lineo
        self.number = int(number)

    def getChildren(self):
        return self.number

    def __repr__(self):
        return "NATNUM(%d)" % self.number


class List(Node):
    def __init__(self, lineo=None):
        self.lineo = lineo
        self.expressionList = []

    def addExpression(self,expression):
        self.expressionList.append(expression)

    def getChildren(self):
        return tuple(flatten(self.expressionList))

    def getChildNodes(self):
        nodelist = []
        nodelist.extend(flatten_nodes(self.expressionList))
        return nodelist

    def __repr__(self):
        items = [repr(expr) for expr in self.expressionList]
        return "LIST(%s)" % (",".join(items))


class Record(Node):
    def __init__(self, lineo=None):
        self.lineo = lineo
        self.keyExpressions = {}

    def addRecord(self, key, expression):
        self.keyExpressions[key] = expression

    def getChildren(self):
        return tuple(flatten(self.keyExpressions))

    def getChildNodes(self):
        nodelist = []
        nodelist.extend(flatten_nodes(self.keyExpressions))
        return tuple(nodelist)

    def __repr__(self):
        items = [":".join([repr(key),repr(value)]) for key,value in self.keyExpressions.items()]
        return "RECORD(%s)" % (",".join(items))


class Cat(Node):
    def __init__(self, left, right, lineo=None):
        self.lineo = lineo
        self.left = left
        self.right = right

    def getChildren(self):
        return self.left, self.right

    def getChildNodes(self):
        return self.left, self.right

    def __repr__(self):
        return "ADD(%s, %s)" % (repr(self.left), repr(self.right))


class Field(Node):
    def __init__(self, name, fields, lineo=None):
        self.lineo = lineo
        self.name = name
        self.fields = fields

    def getChildren(self):
        return ()

    def __repr__(self):
       fields = ".".join(self.fields)
       return "FIELD %s in %s" % (fields, repr(self.name))

#MARKUP Nodes.
class Designator(Node):

    def __init__(self, name, attributes=[], lineo=None):
        self.lineo = lineo
        self.name = name
        self.attributes = attributes

    def getChildren(self):
        children = []
        children.append(self.name)
        children.extend(flatten(self.attributes))
        return tuple(children)

    def getChildNodes(self):
        nodelist = []
        nodelist.extend(flatten_nodes(self.attributes))
        return tuple(nodelist)

    def __repr__(self):
        attrs = ",".join([repr(attr) for attr in self.attributes])
        return "Designator(%s%s)" % (repr(self.name),attrs)


class Attribute(Node):
    def __init__(self, symbol, value, lineo=None):
        self.lineo = lineo
        self.symbol = symbol
        self.value = value

    def getChildren(self):
        return self.symbol, self.value

    def __repr__(self):
        return "(%s, %s)" % (repr(self.symbol), repr(self.value))


class Markup(Node):

    def __init__(self, designator, lineo=None):
        self.lineo = lineo
        self.childs = []
        self.designator = designator
        self.arguments = []
        self.call = False # set if there are () parsed.
        self.embedding = ""
        self.expression = "" #variable
        self.visitedByYield = False
        self.embed = False

    def getChildren(self):
        children = []
        children.append(self.designator)
        children.extend(flatten(self.arguments))
        if self.childs:
            children.extend(flatten(self.childs))
        elif self.embedding:
            children.append(self.embedding)
        elif self.expression:
            children.append(self.expression)

    def getChildNodes(self):
        nodelist = []
        nodelist.extend(flatten_nodes(self.childs))
        if self.embedding is not "":
            nodelist.append(self.embedding)
        if self.expression is not "":
            nodelist.append(self.expression)
        return tuple(nodelist)

    def __repr__(self):

        extra = ""
        if self.arguments:
            extra = "ARGS %s" %  ((repr(self.arguments)))
        elif self.embedding:
            extra = "EMB %s%s" % (extra, repr(self.embedding))
        elif self.expression:
            extra = "EXP %s%s" % (extra, repr(self.expression))

        childOutput = "".join([repr(child) for child in self.childs])

        output = "MARKUP(%s%s%s)" % (repr(self.designator), extra, childOutput)

        return output

class EmbedMarkup(Markup):

    def __repr__(self):
        output = super(EmbedMarkup, self).__repr__()
        return "EMBED %s" % output

#PREDICATE Nodes.
class Predicate(Node):
    pass

class Not(Predicate):
    def __init__(self, predicate, lineo=None):
        self.lineo = lineo
        self.predicate = predicate

    def getChildren(self):
        return self.predicate

    def __repr__(self):
        return "NOT(%s)" % self.predicate

class And(Predicate):
    def __init__(self, leftPredicate, rightPredicate, lineo=None):
        self.lineo = lineo
        self.left = leftPredicate
        self.right = rightPredicate

    def getChildren(self):
        return self.left, self.right

    def getChildNodes(self):
        return self.left, self.right


    def __repr__(self):
        return "AND(%s, %s)" % (repr(self.left), repr(self.right))

class Or(Predicate):
    def __init__(self, leftPredicate, rightPredicate, lineo=None):
        self.lineo = lineo
        self.left = leftPredicate
        self.right = rightPredicate

    def getChildren(self):
        return self.left, self.right

    def getChildNodes(self):
        return self.left, self.right

    def __repr__(self):
        return "OR(%s, %s)" % (repr(self.left), repr(self.right))


class Is_a(Predicate):
    def __init__(self, expression, type, lineo=None):
        self.lineo = lineo
        self.expression = expression
        self.type = type

    def getChildren(self):
        return self.expression, self.type

    def __repr__(self):
        return "IS_A(%s, %s)" % ( self.expression, self.type)

#STATEMENT nodes.
class Statement(Node):
    pass

class Embedding(Statement):

    def __init__(self, lineo=None):
        self.lineo = lineo
        self.pretext = []
        self.embed = []
        self.tailtext= []

    def getChildren(self):
        children = []
        #XXX zip pretext and midtext?
        children.extend(flatten(pretext))
        children.extend(flatten(embed))
        children.extend(flatten(tailtext))
        return tuple(children)

    def __repr__(self):
        output = ""
        for p,emb in zip(self.pretext, self.embed):
            output = "%s%s%s" % (output, p, repr(emb))
        output = "EMBEDDING %s%s" % (output, self.tailtext)

        return output

class Assignment(Statement):
    """ Name "(" {Name ","}* ")"  "=" Statement -> Assignment """
    def __init__(self, name, statement, lineo=None):
        self.lineo = lineo
        self.variables = []
        self.name = name
        self.statement = statement
        self.function = False

    def getChildren(self):
        children = []
        children.append(self.name)
        children.extend(flatten(self.variables))
        children.append(self.statement)
        return tuple(children)

    def getChildNodes(self):
        nodelist = []
        nodelist.extend(flatten_nodes(self.variables))
        nodelist.extend(flatten_nodes(self.statement))
        return tuple(nodelist)

    def __repr__(self):
        if self.variables:
            var = ",".join([repr(var) for var in self.variables])
            assvar = "%s(%s)" % (self.name, var)
        else:
            assvar = self.name

        return "ASSIGNMENT(%s,%s)" % (repr(assvar), repr(self.statement))


class Yield(Statement):

    def __init__(self,lineo=None):
        self.lineo = lineo

    def getChildren(self):
        return ()

    def __repr__(self):
        return "Yield"


class Let(Statement):
    def __init__(self, assignments, body, lineo=None):
        self.lineo = lineo
        self.assignments = assignments
        self.body = body

    def getChildren(self):
        children = []
        children.extend(flatten(self.assignments))
        children.extend(flatten(self.body))

    def getChildNodes(self):
        nodelist = []
        nodelist.extend(flatten_nodes(self.assignments))
        nodelist.extend(flatten_nodes(self.body))
        return tuple(nodelist)

    def __repr__(self):
        ass = ",".join([repr(assignment) for assignment in self.assignments])
        return "LET(%s, %s)" % (ass, repr(self.body))


class If(Statement):
    def __init__(self, predicate, ifstmnt, elsestmnt="", lineo=None):
        self.lineo = lineo
        self.predicate = predicate
        self.elseStatement = elsestmnt
        self.ifStatement = ifstmnt

    def getChildren(self):
        children = []
        return tuple(children)

    def getChildNodes(self):
        nodelist = []
        if self.elseStatement:
            nodelist.append(self.elseStatement)
        return tuple(nodelist)

    def __repr__(self):
        extra = ""
        if self.elseStatement:
            extra = "\nELSE %s" % repr(self.elseStatement)
        return "If(%s,\n %s %s)" % (repr(self.predicate),
            repr(self.ifStatement), extra)

class Echo(Statement):

    def __init__(self, expression="", lineo=None):
        self.lineo = lineo
        self.expression = expression

    def getChildren(self):
        if self.expression:
            return self.expression

    def getChildNodes(self):
        assert isinstance(self.expression,Node), "expression is not NODE!"

        if self.expression:
            return tuple(self.expression)

    def __repr__(self):
        return "ECHO(%s)" % (repr(self.expression))


class Cdata(Statement):
    def __init__(self, expression, lineo=None):
        self.lineo = lineo
        self.expression = expression

    def getChildren(self):
        if self.expression:
            return self.expression
        return self.embedding

    def getChildNodes(self):
        if self.expression:
            return self.expression
        return self.embedding

    def __repr__(self):
        return "Cdata(%s)" % repr(self.expression)


class Comment(Statement):
    def __init__(self, comment, lineo=None):
        self.lineo = lineo
        self.comment = comment

    def getChildren(self):
        return self.comment

    def __repr__(self):
        return "Comment(%s)" % self.comment


class Each(Statement):

    def __init__(self, name, expression, statement, lineo=None):
        self.lineo = lineo
        self.name = name
        self.expression = expression
        self.statement = statement

    def getChildren(self):
        return self.name, self.expression, self.statement

    def getChildNodes(self):
        return self.expression, self.statement

    def __repr__(self):
        return "Each(%s in %s do %s)" % (
                self.name,  repr(self.expression), repr(self.statement))


class Block(Statement):
    def __init__(self, lineo=None):
        self.lineo = lineo
        self.statements = []

    def getChildren(self):
        tuple(flatten(self.statements))

    def getChildNodes(self):
        nodelist = []
        nodelist.extend(flatten_nodes(self.statements))
        return tuple(nodelist)

    def __repr__(self):
        statements = "\n".join([repr(stm) for stm in self.statements])
        return "Block(%s)" % (statements)


