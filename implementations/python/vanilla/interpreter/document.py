"""
Document module.

using elementtree a python xml library we construct the output
document. The interpter module and visitor functions use this modile.

"""

from xml.etree import cElementTree as ET

class Document(object):
    """building output waebric xhtml document """

    def __init__(self):
        #self.modulePath = modulePath
        root = ET.Element('html')
        self.tree = ET.ElementTree()

        self.yieldlist = []
        self.lastElement = ""
        self.lastValue = ""

    def writeOutput(self, filename):
        filename = "%s.htm" % filename
        file = open(filename, 'w')
        DTD = """<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">""" 
        self.tree.write(file)
        file.close()


