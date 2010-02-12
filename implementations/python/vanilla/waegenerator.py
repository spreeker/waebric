from document import Document
from parser.parser import parse
from visitor import walk

from parser.error import trace

class WaeGenerator:
    """
    Defines htm code generator for waebric.
    """
    def __init__(self, source):
        tree = parse(source)
        self.doc = Document()
        walk(tree, self, verbose=1)

        self.functions = {}

    # Module nodes.
    @trace
    def visitModule(self,node):

        #visit dependencies and imports.
        for _import in self.imports:
            self.visit(_import)

        #visit function defenitions.
        for function in self.functions():
            self.visit(child)

        # do something with the sites?
        # and/or main function.

    @trace
    def visitFunction(self, node):
        #do a check?
        self.functions[node.name] = node

        for statement in node.statements:
            self.visit(statement)

    @trace
    def visitPath(self, node):
        print dir(node)

    @trace
    def visitImport(self, node):
        # try to load up module in this environment?
        print dir(node)

    def visitMapping(self, node):
        print dir(node)

    # variables.
    def visitName(self, node):
        print dir(node)

    def visitNumber(self, node):
        print dir(node)

    def visitList(self, node):
        print dir(node)

    def visitRecord(self, node):
        print dir(node)

    def visitCat(self, node):
        pass

    def visitField(self, node):
        pass

    #Markup nodes
    def visitDesignator(self, node):
        pass

    def visitAttribute(self, node):
        pass

    def visitMarkup(self, node):
        dir(node)

    #Statements nodes
    def visitIf(self, node):
        pass

    def visitEach(self, node):
        pass

    def visitLet(self, node):
        pass

    def visitBlock(self, node):
        pass

    def visitComment(self, node):
        pass

    def visitEcho(self, node):
        pass

    def visitCdata(self, node):
        pass

    def visitEmbedding(self, node):
        pass

    def visitAssignment(self, node):
        pass

    def visitYield(self, node):
        pass

    #Predicate nodes
    def visitNot(self, node):
        pass

    def visitAnd(self, node):
        pass

    def visitOr(self, node):
        pass

    def visitIs_a(self, node):
        pass


if __name__ == '__main__':                     # testing
    import sys
    if len(sys.argv) > 1:
        sourceFile = open(sys.argv[1])
        source = sourceFile.readline
        waebrick = WaeGenerator(source)

