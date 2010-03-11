from document import Document
from parser import parse
from visitor import walk
from xhtmltag import XHTMLTag
from error import trace
from ast import Node, Markup, Name, Text, Number
from ast import Assignment, List, Record, Field
import error

import logging
import sys, getopt

class WaeGenerator:
    """
    Defines htm code generator for waebric.
    """
    mainModule = True

    def __init__(self, source, output=""):
        self.path = "/".join(source.name.split('/')[:-1]) #source file path, to find imports
        tree = parse(source.readline)
        self.doc = Document(output)
        self.errors = []
        self.functions = {}
        self.sites =[]
        self.imports = ()
        self.names = {}
        self.output = output
        walk(tree, self)

        for error in self.errors:
            print error

    # Module nodes.
    @trace
    def visitModule(self, node):

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

        if hasattr(node, '_import'): #do not execute imported module.
            return

        #visit main and site defenitions.
        for mapping in self.sites:
            self.visit(mapping)

        if 'main' in self.functions:
            self.doc.addElement('html')
            self.visit(self.functions['main'])
            defaultOutput = "%s.htm" % node.id
            self.doc.writeOutput(defaultOutput)

    @trace
    def visitFunction(self, node):
        for arg in node.arguments:
            if not arg.name in self.names:
                self.names[arg.name] = 'undef'

        for statement in node.statements:
            self.visit(statement)

    #@trace
    def visitPath(self, node):
        print dir(node)

    #@trace
    def visitImport(self, node):
        if node.moduleId in self.imports:
            return #already parsed!
        try:
            waefile = '%s/%s' % (self.path,node.moduleId) if self.path else "%s" % node.moduleId
            _import = open("%s.wae" % waefile)
        except:
            self.errors.append("%s could not open module %s" % (node.lineo, node.moduleId))
            return

        self.imports = self.imports + (node.moduleId,)
        source = _import.readline
        importTree = parse(source)
        importTree._import = True
        self.visit(importTree)

    #@trace
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
    @trace
    def visitName(self, node):
        data = self.names.get(node.name,'undef')
        if isinstance(data, Node):
            self.visit(data)
        else:
            self.doc.addText(data)

    #expressions
    def visitText(self, node):
        self.doc.addText(node.text)

    def visitNumber(self, node):
        self.doc.addText(str(node.number))

    def visitList(self, node):
        print dir(node)

    def visitRecord(self, node):
        print dir(node)

    def visitCat(self, node):

        self.visit(node.left)
        self.visit(node.right)

    @trace
    def visitField(self, node):
        logging.debug(node.field)
        logging.debug(node.expression)

        record = node.expression
        if isinstance(record, Name):
            record = self.names[record.name]
            print record
        if isinstance(record, Record):
            self.visit(record.keyExpressions.get(node.field,'undef'))

        elif isinstance(record, Node):
            logging.debug("now some field went wrong..")
            self.errors.append('%s error in Field expression' % str(node.lineo))
        else:
            logging.debug("now some field went wrong..")
            logging.debug(node)

    #Markup nodes
    @trace
    def visitDesignator(self, node):
        #handle attributes.
        logging.debug(node.attributes)
        for attr in node.attributes:
            symbol = attr.symbol
            value = attr.value

            if symbol == ".":
                self.doc.addAttribute('class',value)
            elif symbol == "#":
                self.doc.addAttribute('id',value)
            elif symbol == "$":
                self.doc.addAttribute('name',value)
            elif symbol == ":":
                self.doc.addAttribute('type',value)
            elif symbol == "@":
                self.doc.addAttribute('width',value)
            elif symbol == "%":
                self.doc.addAttribute('height',value)

    def visitAttribute(self, node):
        pass

    def getValue(self, statement):
        if isinstance(statement, Name ):
            return self.getValue(self.names.get(statement.name,'undef'))
        elif isinstance(statement, Text):
            return statement.text
        elif isinstance(statement, Number):
            return str(statement.number)

        return "undef"

    @trace
    def visitMarkup(self, node):
        lastElement = self.doc.lastElement

        if node.designator.name in self.functions:
            f = self.functions[node.designator.name]
            if hasattr(f,'arguments'):
                if not len(f.arguments) == len(node.arguments):
                    self.errors.append("%s arity mismatch %s" % (node.lineo,node.designator))
                for name,exp in zip(f.arguments, node.arguments):
                    if isinstance(exp, Name):
                        exp = self.names[name.name]
                    self.names[name.name] = exp
            self.visit(f)
        else:
            self.doc.addElement(node.designator.name)
            self.visit(node.designator)
            # check if markup is a valid xhtml tag.
            if not node.designator.name.upper() in XHTMLTag:
                print "%s invalid tag/function used/called! %"
                self.errors.append("%s invalid tag/function used/called! %s" % (
                    node.lineo,
                    node.designator))
            #check for extra arguments to add to element as attributes.
            for arg in node.arguments:
                if isinstance(arg,Assignment):
                    self.doc.addAttribute(arg.name,self.getValue(arg.statement))
                else:
                    self.doc.addAttribute('value', self.getValue(arg))

        for child in node.getChildNodes():
            self.visit(child)

        self.doc.lastElement = lastElement

    #Statements nodes
    def visitIf(self, node):

        value = self.getValue(node.predicate)
        if value == 'undef' or False:
            if isinstance(node.elseStatement, Node):
                self.visit(node.elseStatement)
        else:
            self.visit(node.ifStatement)

    def visitEach(self, node):
        currentNames = self.names.copy()
        listexp = node.expression
        if isinstance(listexp, Name):
            listexp = self.names[listexp.name]
        if isinstance(listexp, Field):
            fields = []
            field = listexp
            while isinstance(field, Field):
                fields.append(field.field)
                field = field.expression
            if isinstance(field, Name):
                name = field
                record = self.names[name.name]
            if isinstance(record, Record):
                fields.reverse()
                for f in fields:
                    record = record.keyExpressions.get(f,"")
                    if not isinstance(record, Record):
                        self.errors.append("%s %s wrong call in records" %(node.lineo, f))
                        logging.debug("%s %s wrong call in records" %(node.lineo, f))
                        return
                listexp = record

        if isinstance(listexp, List):
            for exp in listexp:
               if isinstance(exp, Name):
                   exp = self.names[exp.name]
               self.names[node.name] = exp
               self.visit(node.statement)
        else:
            self.errors.append("%s Each did not get list argument" % str(node.lineo))
        self.names = currentNames

    #@trace
    def visitLet(self, node):
        currentNames = self.names.copy()
        currentFunctions = self.functions.copy()

        for assignment in node.assignments:
            self.visit(assignment)

        for child in node.body:
            self.visit(child)

        self.names = currentNames
        self.functions = currentFunctions

    def visitBlock(self, node):
        lastElement = self.doc.lastElement
        for child in node.getChildNodes():
            self.visit(child)
        self.doc.lastElement = lastElement

    def visitComment(self, node):
        pass

    @trace
    def visitEcho(self, node):
        # write echo statement to document
        if node.expression is not None:
            self.visit(node.expression)
        #TODO Embedding.

    def visitCdata(self, node):
        # wrote cdata to document
        pass

    def visitEmbedding(self, node):
        pass

    #@trace
    def visitAssignment(self, node):
        if node.function: # function assignment.
            if isinstance(node.statement, Markup) and \
                node.statement.designator.name in self.functions: #function 2 function call
                node.statement = self.functions[node.statement.designator.name]

            self.functions[node.name] = node.statement
            node.statement.arguments = node.variables
            #for arg in node.variables:
            #    self.names[arg] = 'undef'
        else:
            self.names[node.name] = node.statement

    def visitYield(self, node):
        #keep current state,
        #continue at previous state
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


def usage():
    usage = """
compile waebric sourcefile.

    python waegenerator.py OPTIONS sourcefile

OPTIONS:

-h
    show help

-o ouput= [output dir]
    directory where to output compiled waebric source
    NOTE no trailing slash.

-d --debug
    show debug information, and write debug information to debug.log
    tip: tail -f that file to show realtime progress.
    """
    print usage

def main(argv):
    try:
        opts, args = getopt.getopt(argv, "ho:d", ["help", "output=", "debug"])
    except getopt.GetoptError, err:
        print str(err)
        usage()
        sys.exit(2)

    output = None
    showresult = False

    for opt, arg in opts:
        if opt in ("-h", "--help"):
            usage()
            sys.exit()
        elif opt in ('-d', "--debug"):
           error.DEBUG = True
           error.SHOWTOKENS = True
           error.SHOWPARSER = True
        elif opt in ("-o", "--output"):
            output = arg

    if args:
        source = args[0]
        sourceFile = open(source)
        if output:
            waebric = WaeGenerator(sourceFile, output)
        else:
            waebric = WaeGenerator(sourceFile)
        if error.DEBUG:
            sourceFile = open(source)
            print sourceFile.read()

    else:
        usage()

if __name__ == "__main__":
    main(sys.argv[1:])
