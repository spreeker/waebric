from document import Document
from parser import parse
from visitor import walk
from xhtmltag import XHTMLTag
from error import trace

import logging

class WaeGenerator:
    """
    Defines htm code generator for waebric.
    """
    mainModule = True

    def __init__(self, source):
        tree = parse(source)
        self.doc = Document()
        self.errors = []
        self.functions = {}
        self.sites =[]
        self.imports = ()
        self.names = {}
        self.mainModule = True
        walk(tree, self)

    # Module nodes.
    @trace
    def visitModule(self,node):

        for f in node.functions:
            if f.name in self.functions:
                self.errors.append("%s function %s already defined" % (f.lineo, f.name) )
            else:
                self.functions[f.name] = f

        #visit imports.
        for _import in node.imports:
            self.visit(_import)

        for site in node.sites:
            for mapping in site:
               self.sites.append(mapping)

        if not self.mainModule: #do not execute imported module.
            return

        self.mainModule = False

        #visit main and site defenitions.
        for mapping in self.sites:
            self.visit(mapping)

        if self.functions.has_key('main'):
            self.visit(self.functions['main'])

        #visit mapping takes care of printing.
        #self.document

    @trace
    def visitFunction(self, node):
        for arg in node.arguments:
            if not arg.name in self.names:
                self.names[arg.name] = 'undef'

        for statement in node.statements:
            self.visit(statement)

    @trace
    def visitPath(self, node):
        print dir(node)

    @trace
    def visitImport(self, node):
        if node.moduleId in self.imports:
            return #already parsed!
        try:
            _import = open("%s.wae" % node.moduleId)
        except:
            self.errors.append("%s could not open module %s" % (node.lineo, node.moduleId))

        source = _import.readline
        importTree = parse(source)
        self.visit(tree)
        self.imports = self.imports + (node.moduleId,)

    @trace
    def visitMapping(self, node):
        # new document
        self.doc = Document()
        # visit markup call
        self.visit(node.markup)
        # create new document
        if node.path.dir:
            path = "/".join((node.path.dir, node.path.fileName))
        else:
            path = node.path.fileName

        self.doc.writeOutput(path)

    # variables.
    def visitName(self, node):
        data = self.names[node.name]
        self.doc.addText(data)

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
        if node.designator.name in self.functions:
            f = self.functions[node.designator.name]
            if not len(f.arguments) == len(node.arguments):
                self.errors.append("%s arity mismatch %s" % (node.lineo,node.designator))
            else:
                for name,exp in zip(f.arguments, node.arguments):
                    self.names[name.name] = exp
            self.visit(f)
        else:
            # check if markup is a valid xhtml tag.
            if not node.designator.name.upper() in XHTMLTag:
                self.errors.append("%s invalid tag/function used/called! %s" % (
                    node.lineo,
                    node.designator))

            self.doc.addElement(node.designator.name)

        #print node.getChildNodes()
        #logging.debug("CHILDNODES: %s" % repr(node.getChildNodes()))
        for child in node.getChildNodes():
            self.visit(child)



    #Statements nodes
    def visitIf(self, node):
        pass

    def visitEach(self, node):
        pass

    def visitLet(self, node):
        pass

    def visitBlock(self, node):
        lastElement = self.doc.lastElement

        for child in node.getChildNodes():
            self.visit(child)

        self.doc.lastElement = lastElement

    def visitComment(self, node):
        pass

    def visitEcho(self, node):
        # write echo statement to document
        pass

    def visitCdata(self, node):
        # wrote cdata to document
        pass

    def visitEmbedding(self, node):
        pass

    def visitAssignment(self, node):
        pass

    def visitYield(self, node):
        #keep current state, continue at previous state
        #finish on return left state.
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

