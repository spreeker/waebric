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
        #self.modulePath = modulePath
        #root = ET.Element('html')
        self.tree = ET.ElementTree()

        self.yieldlist = []
        self.lastElement = None
        self.lastValue = ""

    def addElement(self, name):
        if self.lastElement is None:
            self.lastElement = Element(name)
            self.tree._setroot(self.lastElement)
        else:
            newElement = Element(name)
            SubElement(self.lastElement, newElement)
            self.lastElement = newElement

    def addText(self, string):
        #self.lastElement.text = "%s%s" % (self.lastElement.text, string)
        self.lastElement.text = string

    def writeOutput(self, filename):
        #filename = "%s.htm" % filename
        #file = open(filename, 'w')
        #DTD = """<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">"""
        self.tree.write(filename)
        file.close()

