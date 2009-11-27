
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

class Node:
    """Abstract base class for ast nodes."""
    def __iter__(self):
        for n in self.getChildren():
            yield n

    def getChildren(self):
        pass # implemented by subclasses

    def getChildNodes(self):
        pass # implemented by subclasses

class EmptyNode(Node):
    pass

#simple terminal nodes.
class Name(Node):
    def __init__(self,name):
        self.name = name

    def getChildren(self):
        pass # implemented by subclasses

    def getChildNodes(self):
        pass # implemented by subclasses


    def __repr__(self):
        return "NAME(%s)" % self.name


class NatNum(Node):
    """ Natural Number"""
    def __init__(self, number):
        self.number = int(number)

    def getChildren(self):
        pass # implemented by subclasses

    def getChildNodes(self):
        pass # implemented by subclasses


    def __repr__(self):
        return "NATNUM(%d)" % self.number

class Text(Node):
    def __init__(self, string):
        self.string = string

    def getChildren(self):
        pass # implemented by subclasses

    def getChildNodes(self):
        pass # implemented by subclasses

    def __repr__(self):
        return "STRING(%s)" % self.string


class Formals(Node):

    def getChildren(self):
        pass # implemented by subclasses

    def getChildNodes(self):
        pass # implemented by subclasses


class CallFunc(Node):
    def __init__(self, node, args, star_args = None, dstar_args = None, lineno=None):
        self.node = node
        self.args = args
        self.star_args = star_args
        self.dstar_args = dstar_args
        self.lineno = lineno

    def getChildren(self):
        children = []
        children.append(self.node)
        children.extend(flatten(self.args))
        children.append(self.star_args)
        children.append(self.dstar_args)
        return tuple(children)

    def getChildNodes(self):
        nodelist = []
        nodelist.append(self.node)
        nodelist.extend(flatten_nodes(self.args))
        if self.star_args is not None:
            nodelist.append(self.star_args)
        if self.dstar_args is not None:
            nodelist.append(self.dstar_args)
        return tuple(nodelist)

    def __repr__(self):
        return "CallFunc(%s, %s, %s, %s)" % (repr(self.node), repr(self.args), repr(self.star_args), repr(self.dstar_args))


class For(Node):
    def __init__(self, assign, list, body, else_, lineno=None):
        self.assign = assign
        self.list = list
        self.body = body
        self.else_ = else_
        self.lineno = lineno

    def getChildren(self):
        children = []
        children.append(self.assign)
        children.append(self.list)
        children.append(self.body)
        children.append(self.else_)
        return tuple(children)

    def getChildNodes(self):
        nodelist = []
        nodelist.append(self.assign)
        nodelist.append(self.list)
        nodelist.append(self.body)
        if self.else_ is not None:
            nodelist.append(self.else_)
        return tuple(nodelist)

    def __repr__(self):
        return "For(%s, %s, %s, %s)" % (repr(self.assign), repr(self.list), repr(self.body), repr(self.else_))

class If(Node):
    def __init__(self, tests, else_, lineno=None):
        self.tests = tests
        self.else_ = else_
        self.lineno = lineno

    def getChildren(self):
        children = []
        children.extend(flatten(self.tests))
        children.append(self.else_)
        return tuple(children)

    def getChildNodes(self):
        nodelist = []
        nodelist.extend(flatten_nodes(self.tests))
        if self.else_ is not None:
            nodelist.append(self.else_)
        return tuple(nodelist)

    def __repr__(self):
        return "If(%s, %s)" % (repr(self.tests), repr(self.else_))

class Name(Node):
    def __init__(self, name):
        self.name = name
        self.lineno = lineno

    def getChildren(self):
        return self.name,

    def getChildNodes(self):
        return ()

    def __repr__(self):
        return "Name(%s)" % (repr(self.name),)

for name, obj in globals().items():
    if isinstance(obj, type) and issubclass(obj, Node):
        nodes[name.lower()] = obj

