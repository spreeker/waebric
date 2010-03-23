"""
Document module.

using elementtree a python xml library we construct the output
document. The interpter module and visitor functions use this modile.

"""

from xml.etree import cElementTree as ET
from xml.etree.cElementTree import Element,SubElement,dump

from error import trace
import error
import logging

class Document(object):
    """building output waebric xhtml document """

    def __init__(self, output, verbose=False):
        self.lastElement = Element('html')
        self.tree = ET.ElementTree(self.lastElement)
        self.trees = [self.tree]
        self.output = output
        self.verbose = verbose

    #@trace
    def addElement(self, name):
        self.lastElement = SubElement(self.lastElement, name)
        return self.lastElement

    #@trace
    def addText(self, string):
        if not len(self.lastElement):
            e = self.lastElement
            txt = "%s%s" % (e.text, string) if e.text else string
            e.text = txt
        else:
            e = self.lastElement[-1]
            txt = "%s%s" % (e.tail, string) if e.tail else string
            e.tail = txt

    def addAttribute(self,name,value):
        if self.lastElement.get(name):
            value = "%s %s" % (self.lastElement.get(name),value)
        self.lastElement.set(name, value)

    def addComment(self, string):
        self.lastElement.append(ET.Comment(string))

    def getFile(self, filename):
        if self.output:
            filename = "%s/%s" % (self.output, filename)
        try:
            _file = open(filename,'w')
        except IOError:
            print "file name %s cannot be opened, no output written" % filename
            return
        return _file

    def setGoodRootElement(self):
        """make sure we have the correct root ellement according to the
           wae standard. It could be needed to remove the top html
           element because i ad it to work correctly with elementtree Libary.
        """
        r = self.tree.getroot()
        if len(r) == 1 and not r.text and not r[-1].tail:
            child = r.getchildren()[0]
            if isinstance(child.tag, str):#check needed for comment element.
                self.tree._setroot(r[0])

    def writeOutput(self, filename):

        self.setGoodRootElement()
        _file = self.getFile(filename)

        DTD = """<?xml version="1.0" encoding="UTF-8"?>\n"""
        _file.write(DTD)
        for tree in self.trees:
            if isinstance(tree, ET.ElementTree):
                self.tree.write(_file)
            else: #could be data string.
                _file.write(tree)
        _file.write('\n')
        _file.close()

        if self.verbose:
            output = open(_file.name)
            print output.read()

    def writeEmptyFile(self, filename):
        _file = self.getFile(filename)
        _file.write('')
        _file.close()

