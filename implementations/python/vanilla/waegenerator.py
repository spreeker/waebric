from document import Document
from parser import parse
from visitor import walk
from xhtmltag import XHTMLTag
from error import trace, DEBUG, SHOWTOKENS
from error import SyntaxError
from ast import Node, Markup, Name, Text, Number
from ast import Assignment, List, Record, Field
from ast import Predicate, Cat, EmbedMarkup
import error

import logging
import sys, getopt

class WaeGenerator:
    """
    Defines htm code generator for waebric.
    """
    mainModule = True

    def __init__(self, source, output="", verbose=False):
        #set source file path, to find imports
        self.path = "/".join(source.name.split('/')[:-1]) 
        self.fileName = self.getFileName(source)
        self.output = output if output else self.path
        self.doc = Document(output, verbose=verbose)
        self.errors = []
        self.functions = {}
        self.sites =[]
        self.imports = ()
        self.names = {}
        self.yieldQueue = []

        tree = self.parseFile(source)

        if not self.errors:
            walk(tree, self)

        if self.errors: 
            print "Errors in %s" % source.name
            for error in self.errors:
                print error

    def getFileName(self, source):
        fileName = source.name.split('/')[-1]
        if fileName.endswith('.wae'):
            fileName = '%s%s' % (fileName[:-3], 'htm')
            return fileName
        else:
            errors.append('incorrect extension found %s'% fileName)

    def parseFile(self, source):
        """
        load and parse file, handle errors, make sure there is 
        always output. return parse Tree.
        """
        try:
            tree = parse(source.readline)
            return tree
        except SyntaxError, err:
            print "Parse Error in %s" % source.name
            self.doc.writeEmptyFile(self.getFileName(source))
            self.errors.append(err)


    # Module nodes.
    #@trace
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
            self.visitMapping(mapping)

        if 'main' in self.functions:
            self.visit(self.functions['main'])
            defaultOutput = "%s.htm" % node.id
            if not defaultOutput == self.fileName:
                msg = 'module id, does not match filename %s %s' % (defaultOutput, self.fileName)
                self.errors.append(msg)
            self.doc.writeOutput(defaultOutput)

    #@trace
    def visitFunction(self, node):
        for arg in node.arguments:
            if not arg.name in self.names:
                self.names[arg.name] = 'undef'

        for statement in node.statements:
            self.visit(statement)

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

        importTree = self.parseFile(_import)
        importTree._import = True
        self.visit(importTree)

    #@trace
    def visitMapping(self, node):
        backupDoc = self.doc
        # new document
        self.doc = Document(self.output)
        # visit markup call
        self.visit(node.markup)
        # create new document
        if node.path.dir:
            path = "/".join((node.path.dir, node.path.fileName))
        else:
            path = node.path.fileName
        self.doc.writeOutput(path)
        self.doc = backupDoc

    # variables.
    #@trace
    def visitName(self, node):
        data = self.names.get(node.name,'undef')
        if isinstance(data, Node):
            self.visit(data)
        else:
            self.doc.addText(data)

    #expressions
    #@trace
    def visitText(self, node):
        self.doc.addText(node.text)

    def visitNumber(self, node):
        self.doc.addText(str(node.number))

    #@trace
    def visitList(self, node):
        self.doc.addText('[')
        for exp in node.expressionList:
            self.visit(exp)
        self.doc.addText(']')

    #@trace
    def visitRecord(self, node):
        self.doc.addText('[')
        for k, exp in node.keyExpressions:
            self.doc.addText(k)
            self.doc.addText(':')
            self.visit(exp)
        self.doc.addText(']')

    #@trace
    def visitCat(self, node):
        self.visit(node.left)
        self.visit(node.right)

    #@trace
    def visitField(self, node):
        record = self.names.get(node.name.name)
        for f in node.fields:
            record = record.keyExpressions[f]
        exp = record
        self.visit(exp)


    #Markup nodes
    #@trace
    def visitDesignator(self, node):
        #handle attributes.
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
                self.doc.addAttribute('height',value)
            elif symbol == "%":
                self.doc.addAttribute('width',value)

    def visitAttribute(self, node):
        pass

    #@trace
    def getValue(self, statement):
        if isinstance(statement, Name ):
            return self.getValue(self.names.get(statement.name,'undef'))
        elif isinstance(statement, Text):
            return statement.text
        elif isinstance(statement, Number):
            return str(statement.number)
        elif isinstance(statement, Predicate):
            return self.visit(statement)
        elif isinstance(statement, Field):
            record = self.names.get(statement.name.name, 'undef')
            for f in statement.fields:
               if isinstance(record, Record):
                    record = record.keyExpressions.get(f,'undef')
               else:
                    self.errors.append('%s no record found'% str(statement.lineo))
                    return False
            return self.getValue(record)
        elif isinstance(statement, List):
            return statement
        elif isinstance(statement, Record):
            return statement
        elif isinstance(statement, Cat):
            return self.getValue(statement.left) + self.getValue(statement.right)
        return False

    def checkArguments(self, functionArguments, nodeArguments):
        if not len(functionArguments) == len(nodeArguments):
            diff = len(functionArguments) - len(nodeArguments)
            for i in range(diff):
                name = functionArguments[i]
                self.names[name.name] = 'undef'
            return True
        return False

    #@trace
    def updateNames(self, functionArguments, nodeArguments):
        for name,exp in zip(functionArguments, nodeArguments):
            if isinstance(exp, Name):
                exp = self.names[exp.name]
            if isinstance(exp, Assignment):
                exp = exp.statement
            self.names[name.name] = exp

    #@trace
    def doFunctionCall(self, node):
        f = self.functions[node.designator.name]
        #if there is a yield in F.
        #than the child nodes in this node should be visited first
        env = [node, self.names.copy(), self.functions.copy()]
        self.yieldQueue.append(env)
        if hasattr(f,'arguments'):
            if self.checkArguments(f.arguments, node.arguments):
                self.errors.append("%s arity mismatch %s" % (node.lineo,node.designator))
            self.updateNames(f.arguments, node.arguments)
        self.visit(f)
        if self.yieldQueue:
            if self.yieldQueue[-1] == env:
                self.yieldQueue.pop()

    #@trace
    def addElement(self, node):
        self.doc.addElement(node.designator.name)
        self.visit(node.designator)
        # check if markup is a valid xhtml tag.
        if not node.designator.name.upper() in XHTMLTag:
            self.errors.append("%s invalid tag/function used/called! %s" % (
                node.lineo,
                node.designator))
        #check for extra arguments to add to element as attributes.
        for arg in node.arguments:
            if isinstance(arg,Assignment):
                self.doc.addAttribute(arg.name, self.getValue(arg.statement))
            else:
                self.doc.addAttribute('value', self.getValue(arg))

    #@trace
    def elementOrFunction(self, node):
        #markups in let assignment should have other environment.
        if hasattr(node,'env') and node.env:
            backupNames = self.names.copy()
            backupFunc  = self.functions.copy()
            #set correct env for statement
            self.names, self.functions = node.env
            #self.functions = node.env[1]

        if node.designator.name in self.functions:
            self.doFunctionCall(node)
        else:
            self.addElement(node)

        #restore correct env after assignment statement.
        if hasattr(node,'env') and node.env:
            self.names = backupNames
            self.functions= backupFunc

    #@trace
    def visitMarkup(self, node):
        lastElement = self.doc.lastElement

        self.elementOrFunction(node)

        if not node.visitedByYield:
            for child in node.getChildNodes():
                self.visit(child)

        self.doc.lastElement = lastElement

    #@trace
    def visitEmbedMarkup(self, node):
        lastElement = self.doc.lastElement

        if node.getChildNodes():
            self.elementOrFunction(node)
            for child in node.getChildNodes()[:-1]:
                self.visit(child)
            lastChild = node.getChildNodes()[-1]
            if isinstance(lastChild, Markup):
                self.lastEmbChild(node.getChildNodes()[-1])
            else:
                self.visit(lastChild)
        else:
            self.lastEmbChild(node)

        self.doc.lastElement = lastElement

    #@trace
    def lastEmbChild(self,emb):
        if hasattr(emb,'call') and emb.call:
            self.elementOrFunction(emb)
        else:
            exp = self.names[emb.designator.name]
            if isinstance(exp, Node):
                self.visit(exp)
            else:
                self.doc.addText(exp)

    #Statements nodes
    #@trace
    def visitIf(self, node):

        value = self.getValue(node.predicate)
        if value == 'undef' or value == False or value == None:
            if isinstance(node.elseStatement, Node):
                self.visit(node.elseStatement)
        else:
            self.visit(node.ifStatement)

    #@trace
    def visitEach(self, node):
        currentNames = self.names.copy()
        listexp = node.expression
        if isinstance(listexp, Name):
            listexp = self.names[listexp.name]
        if isinstance(listexp, Field):
            field = listexp
            record = self.names[field.name.name]
            for f in field.fields:
               assert(isinstance(record, Record)), 'no record found'
               record = record.keyExpressions.get(f,'undef')
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

        for ass in node.assignments:
            self.visitAssignment(ass)

        for child in node.body:
            self.visit(child)

        self.names = currentNames
        self.functions = currentFunctions

    #@trace
    def visitBlock(self, node):
        lastElement = self.doc.lastElement
        for child in node.getChildNodes():
            self.visit(child)
        self.doc.lastElement = lastElement

    #@trace
    def visitComment(self, node):
        self.doc.addComment(node.comment)

    #@trace
    def visitEcho(self, node):
        # write echo statement to document
        if node.expression :
            self.visit(node.expression)

    def visitCdata(self, node):
        # wrote cdata to document
        pass

    #@trace
    def visitEmbedding(self, node):
        self.doc.addText(node.pretext[0])
        index = 1
        for emb in node.embed:
            self.visit(emb)

            if index < len(node.pretext):
                self.doc.addText(node.pretext[index])
            index += 1

        self.doc.addText(node.tailtext)

    #@trace
    def visitAssignment(self, node):
        node.statement.env = (self.names.copy(), self.functions.copy())
        if node.function: # function assignment.
            self.functions[node.name] = node.statement
            node.statement.arguments = node.variables
        else:
            self.names[node.name] = node.statement

    #@trace
    def visitYield(self, node):
        backupNames = self.names.copy()
        backupFunc = self.functions.copy()

        previousNode, names, functions = self.yieldQueue.pop()
        self.names = names
        self.functions = functions
        #prevents double visiting by previous node.
        previousNode.visitedByYield = True
        children = previousNode.getChildNodes()
        if children:
            child = children[0]
            if hasattr(child, 'childs'):
                child.childs.extend(children[1:])
                child.visitedByYield = True
            self.visit(child)
        #restore names
        self.names = backupNames
        self.functions = backupFunc

    #Predicate nodes
    #@trace
    def visitNot(self, node):
        value = self.getValue(node.predicate)
        value = False if value == 'undef' else value
        if not value:
            return True
        return False

    #@trace
    def visitAnd(self, node):
        left = self.getValue(node.left)
        right= self.getValue(node.right)

        left = False if left == 'undef' else left
        right = False if right == 'undef' else right

        if left:
            if right:
                return True
        return False

    #@trace
    def visitOr(self, node):
        left = self.getValue(node.left)
        right= self.getValue(node.right)
        left = False if left == 'undef' else left
        right = False if right == 'undef' else right
        if left:
            return True
        if right:
            return True
        return False

    #@trace
    def visitIs_a(self, node):
        data = node.expression
        if isinstance(data, Name) or isinstance(Field):
            data = self.getValue(data)
        _type = Record if node.type == 'RECORD' else List

        if isinstance(data, _type):
            return True
        return False


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
    verbose = False

    for opt, arg in opts:
        if opt in ("-h", "--help"):
            usage()
            sys.exit()
        elif opt in ('-d', "--debug"):
            verbose = True
        elif opt in ("-o", "--output"):
            output = arg

    if args:
        source = args[0]
        sourceFile = open(source)
        if output:
            waebric = WaeGenerator(sourceFile, output, verbose=verbose)
        else:
            waebric = WaeGenerator(sourceFile, verbose=verbose)
        if verbose:
            sourceFile = open(source)
            print sourceFile.read()

    else:
        usage()

if __name__ == "__main__":
    main(sys.argv[1:])
