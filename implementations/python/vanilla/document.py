"""
Document module.

using elementtree a python xml library we construct the output
document. The interpter module and visitor functions use this modile.

"""

from xml.etree import ElementTree as ET
from xml.etree.ElementTree import Element,SubElement,dump

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

    @trace
    def addText(self, string):
        txt = "%s%s" % (self.lastElement.text, string) if self.lastElement.text else string
        self.lastElement.text = txt

    @trace
    def tailText(self, string):
        #if not len(self.lastElement):
        #    self.addText(string)
        #    return
        lastChild = self.lastElement[-1]
        txt = "%s%s" % (lastChild.tail, string) if lastChild.tail else string
        lastChild.tail = txt

    #@trace
    def addAttribute(self,name,value):
        if self.lastElement.get(name):
            value = "%s %s" % (self.lastElement.get(name),value)
        self.lastElement.set(name, value)

    def addComment(self, string):
        self.lastElement.append(ET.Comment(string))

    def writeOutput(self, filename):
        r = self.tree.getroot()
        #check if there is a correct root element.
        #If not keep the default html.
        if len(r) == 1 and not r.text:
            child = r.getchildren()[0]
            if isinstance(child.tag, str):#check needed for comment element.
                self.tree._setroot(r[0])

        if self.output:
            filename = "%s/%s" % (self.output, filename)
        try:
            _file = open(filename,'w')
        except IOError:
            print "file name %s cannot be opened, no output written" % filename
            return

        #DTD = """<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">"""
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
            output = open(filename)
            print output.read()
