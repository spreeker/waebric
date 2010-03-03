"""
Document module.

using elementtree a python xml library we construct the output
document. The interpter module and visitor functions use this modile.

"""

from xml.etree import ElementTree as ET
from xml.etree.ElementTree import Element,SubElement,dump

#ET.Element
#ET.SubElement

class Document(object):
    """building output waebric xhtml document """

    def __init__(self):
        self.lastElement = None
        self.trees = []
        self.yieldlist = []
        #self.lastValue = ""
        self.pretext = ""

    def addElement(self, name):
        if self.lastElement is None:
            self.lastElement = Element(name)
            self.tree = ET.ElementTree(self.lastElement)
            self.trees.append(self.tree)
        else:
            self.lastElement = SubElement(self.lastElement, name)

    def addText(self, string):
        if self.lastElement is None:
            self.trees.append(string)
        else:
            self.lastElement.text = string

    def writeOutput(self, filename):
        _file = open(filename, 'w')
        DTD = """<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">"""
        _file.write(DTD)
        for tree in self.trees:
            if isinstance(tree, ET.ElementTree):
                self.tree.write(_file)
            else: #could be data string.
                _file.write(tree)
        _file.close()

