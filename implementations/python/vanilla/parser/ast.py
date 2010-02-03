"""
Abstract Syntax Tree Expression Nodes
"""

class Node:
    """Abstract base class for ast nodes."""
    def __iter__(self):
        for n in self.getChildren():
            yield n

    def getChildren(self):
        pass # implemented by subclasses

    def getChildNodes(self):
        pass # implemented by subclasses

    def accept(self,visitor):
        visitor.visit(self)

    def __repr__(self):
        pass # implemented by subclasses

#Module Nodes.
class Module(Node):

    def __init__(self, id):
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
    def __init__(self, name, arguments):
        self.name = name
        self.arguments = arguments
        self.statements = []

    def addStatement(self, statement):
        self.statements.append(statement)

    def __repr__(self):
        return "FUNCTION %s (%s) { %s } \n" % (self.name,
            ",".join([repr(arg) for arg in self.arguments]),
            ",".join([repr(stm) for stm in self.statements]))

class Import(Node):
    def __init__(self, moduleId):
        self.moduleId = moduleId

    def __repr__(self):
        return  "IMPORT %s" % self.moduleId

class Path(Node):
    def __init__(self, dir, fileName):
        self.dir = dir
        self.fileName = fileName

    def __repr__(self):
        return "PATH(%s/%s)" % (self.dir, self.fileName)

class Mapping(Node):
    def __init__(self, path,markup):
        self.path = path
        self.markup = markup

    def __repr__(self):

        return "MAPPING(%s, %s)" % (repr(self.path), repr(self.markup))

#ID 
class Name(Node):
    def __init__(self, name):
        self.name = name

    def getChildren(self):
        return self.name,

    def getChildNodes(self):
        return ()

    def __repr__(self):
        return "NAME(%s)" % (repr(self.name),)

#EXPRESSION NODES

class Text(Node):
    def __init__(self, text):
        self.text = text

    def __repr__(self):
        return "STRING(%s)" % self.text


class Number(Node):
    def __init__(self, number):
        self.number = int(number)

    def __repr__(self):
        return "NATNUM(%d)" % self.number


class List(Node):
    def __init__(self):
        self.expressionList = []

    def addExpression(self,expression):
        self.expressionList.append(expression)

    def __repr__(self):
        items = [repr(expr) for expr in self.expressionList]
        return "LIST(%s)" % (",".join(items))


class Record(Node):
    def __init__(self):
        self.dict = {}
    def addRecord(self, key, expression):
        self.dict[key] = expression

    def __repr__(self):
        items = [":".join([repr(key),repr(value)]) for key,value in self.dict.items()]
        return "RECORD(%s)" % (",".join(items))


class Cat(Node):
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

#MARKUP Nodes.

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

class Markup(Node):
    def __init__(self, designator):
        self.childs = []
        self.designator = designator
        self.arguments = []
        self.embedding = ""
        self.expression = ""

    def __repr__(self):

        extra = ""
        if self.arguments:
            extra = "%s" %  ((repr(self.arguments)))
        elif self.embedding:
            extra = "%s%s" % (extra, repr(self.embedding))
        elif self.expression:
            extra = "%s%s" % (extra, repr(self.expression))

        if self.childs:
            child = self.childs[0]
            child.childs = self.childs[1:]
            output = "MARKUP(%s%s%s)" % (
                repr(self.designator), extra,
                repr(child))
        else:
            output =  "MARKUP(%s%s)" % (repr(self.designator), extra)
        return output

#PREDICATE Nodes.
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

#STATEMENT nodes.
class Statement(Node):
    pass

class Embedding(Statement):
    def __init__(self):
        self.pretext = []
        self.midtext = []
        self.tailtext= []

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
            var = ",".join([repr(var) for var in self.variables])
            assvar = "%s(%s)" % (self.name, var)
        else:
            assvar = self.name

        return "ASSIGNMENT(%s,%s)" % (repr(assvar), repr(self.statement))


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
        ass = ",".join([repr(assignment) for assignment in self.assignments])
        return "LET(%s, %s)" % (ass, repr(self.body))


class If(Statement):
    def __init__(self, predicate, ifstmnt, elsestmnt="",):
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
        extra = ""
        if self.elsestmnt:
            extra = "\nELSE %s" % repr(self.elsestmnt)
        return "If(%s,\n %s %s)" % (repr(self.predicate),
            repr(self.ifstmnt), extra)

class Echo(Statement):
    def __init__(self,embedding="",expression=""):
        self.expression = embedding
        self.embedding = expression

    def __repr__(self):
        return "ECHO(%s%s)" % (str(self.expression),str(self.embedding))


class Cdata(Statement):
    def __init__(self, expression):
        self.expression = expression

    def __repr__(self):
        return "Cdata(%s)" % repr(self.expression)


class Comment(Statement):
    def __init__(self, comment):
        self.comment = comment

    def __repr__(self):
        return "Comment(%s)" % self.comment


class Each(Statement):
    def __init__(self, name, expression, statement):
        self.name = name
        self.expression = expression
        self.statement = statement

    def __repr__(self):
        return "Each(%s in %s do %s)" % (
                self.name,  repr(self.statement), repr(self.expression))


class Block(Statement):
    def __init__(self):
       self.statements = []

    def __repr__(self):
        statements = "\n".join([repr(stm) for stm in self.statements])
        return "Block(%s)" % (statements)


