"""
Document module.

using elementtree a python xml library we construct the output
document. The interpter module and visitor functions use this modile.

"""

from xml.etree import ElementTree as ET
from xml.etree.ElementTree import Element,SubElement,dump

import error

class Document(object):
    """building output waebric xhtml document """

    def __init__(self, output):
        self.lastElement = Element('html')
        self.tree = ET.ElementTree(self.lastElement)
        self.trees = [self.tree]
        self.virgin = True
        self.output = output

    def addElement(self, name):
        if self.virgin:
            self.lastElement.tag = name
        else:
            self.lastElement = SubElement(self.lastElement, name)

    def addText(self, string):
        if self.virgin:
            self.virgin = False
        txt = "%s%s" % (self.lastElement.text, string) if self.lastElement.text else string
        self.lastElement.text = txt

    def addAttribute(self,name,value):
        if self.lastElement.get(name):
            value = "%s %s" % (self.lastElement.get(name),value)
        self.lastElement.set(name, value)

    def writeOutput(self, filename):
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

        if error.DEBUG:
            output = open(filename)
            print output.read()
