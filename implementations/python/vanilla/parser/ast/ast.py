
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

    def __repr__(self):
        pass # implemented by subclasses


class Name(Node):
    def __init__(self, name):
        self.name = name

    def getChildren(self):
        return self.name,

    def getChildNodes(self):
        return ()

    def __repr__(self):
        return "NAME(%s)" % (repr(self.name),)


for name, obj in globals().items():
    if isinstance(obj, type) and issubclass(obj, Node):
        nodes[name.lower()] = obj

