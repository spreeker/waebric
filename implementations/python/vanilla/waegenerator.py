from document import Document
from parser.parser import parse
from visitor import walk
from xhtmltag import XHTMLTag
from parser.error import trace

class WaeGenerator:
    """
    Defines htm code generator for waebric.
    """
    mainModule = True

    def __init__(self, source):
        tree = parse(source)
        self.doc = Document()
        walk(tree, self, verbose=1)
        self.errors = []
        self.functions = {}
        self.mappings = []

    # Module nodes.
    @trace
    def visitModule(self,node):

        for f in node.functions:
            if self.functions.has_key(f.name):
                self.errors.append("%s function %s already defined" % (f.lineo, f.name) )
            else:
                self.functions[f.name] = f

        #visit imports.
        for _import in node.imports:
            self.visit(_import)

        #visit mappings.
        for mapping in node.sites():
            self.visit(mapping)

        if not mainModule:
            return

        mainModule = False

        #visit main and site defenitions.
        for mapping in self.mapping():
            self.visit(mapping)

        if self.functions.has_key('main'):
            self.visit(self.functions['main'])

    @trace
    def visitFunction(self, node):
        #do a check?
        #arguments of markup parent are supposed to be set.
        self.functions[node.name] = node

        for statement in node.statements:
            self.visit(statement)

    @trace
    def visitPath(self, node):
        print dir(node)

    @trace
    def visitImport(self, node):
        try:
            _import = open("%s.wae" % node.moduleId)
        except:
            self.errors.append("%s could not open module %s" % (node.lineo, node.moduleId))

        source = _import.readline
        importTree = parse(source)
        self.visit(tree)

    def visitMapping(self, node):
        # new document
        self.doc = Document()
        # visit markup call
        self.visit(node.markup)
        # craete new document
        path = '%s/%s' % (node.path.dir, node.path.fileName)
        self.doc.writeOutput(path)

    # variables.
    def visitName(self, node):
        print dir(node)

    #expressions
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

