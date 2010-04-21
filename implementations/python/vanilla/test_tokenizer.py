
import unittest
from tokenize import tokenize
from token import *

from difflib import Differ
from pprint import pprint
import sys


class Testtokenizer(unittest.TestCase):
    "test tokenize functions"

    result = ""

    def setUP(self):
        self.result = "" 

    def tokeneater(self, type, token, (srow, scol), (erow, ecol), line):
        line = "%2d,%-2d %2d,%-2d: %15s  %15s\n" % \
                        (srow, scol, erow, ecol, tok_name[type], repr(token))
        self.result = self.result + line

    def test_comment(self):
        source = open('tests/wae/commenttest.wae').readline
        tokenize(source, self.tokeneater)
        expected = r""" 1,0   1,6 :         KEYWORD         'module'
 1,7   1,18:            NAME    'commenttest'
 1,18  1,19:              OP              ';'
 3,0   3,17:         COMMENT  '/* commentline */'
 4,0   4,17:         COMMENT  '// commentslashes'
 5,0   7,2 :         COMMENT  '/* comment\n   comment\n*/'
 9,0  13,2 :         COMMENT  '/* comment    \n// commentblaaaaaaaaaa\n\n   comment\n*/'
14,0  14,0 :       ENDMARKER               ''
"""
        #d = Differ()
        #diffresult = list(d.compare(expected.splitlines(1), self.result.splitlines(1)))
        #pprint(diffresult) 
        #sys.stdout.writelines(r""+self.result)

        self.assertEqual(self.result, expected)

    def test_embedding(self):
        source = open('tests/wae/embeddingtest.wae').readline
        tokenize(source, self.tokeneater)
        expected = r""" 1,0   1,6 :         KEYWORD         'module'
 1,7   1,20:            NAME  'embeddingtest'
 3,0   3,4 :         KEYWORD           'site'
 4,2   4,6 :         KEYWORD           'site'
 4,6   4,7 :              OP              '/'
 4,7   4,16:            NAME      'embedtest'
 4,16  4,17:              OP              '.'
 4,17  4,21:            NAME           'html'
 4,21  4,22:              OP              ':'
 4,23  4,32:            NAME      'embedtext'
 4,32  4,33:              OP              '('
 4,33  4,34:              OP              ')'
 5,0   5,3 :         KEYWORD            'end'
 7,0   7,3 :         KEYWORD            'def'
 7,4   7,13:            NAME      'embedtext'
 8,4   8,5 :            NAME              'p'
 8,6   8,17:       PRESTRING      ' pretext '
 8,16  8,17:         EMBSTRT              '<'
 8,18  8,27:          STRING        'midtext'
 8,28  8,40:          EMBEND              '>'
 8,28  8,40:      POSTSTRING     ' posttext '
 8,40  8,41:              OP              ';'
 9,4   9,5 :            NAME              'p'
 9,6   9,8 :       PRESTRING               ''
 9,7   9,8 :         EMBSTRT              '<'
 9,8   9,10:            NAME             'em'
 9,11  9,19:          STRING        'UTRECHT'
 9,19  9,45:          EMBEND              '>'
 9,19  9,45:       MIDSTRING  ' - De Vlaamse schrijver '
 9,44  9,45:         EMBSTRT              '<'
 9,45  9,47:            NAME             'em'
 9,48  9,69:          STRING  'Joost Vandecasteele'
 9,69  9,87:          EMBEND              '>'
 9,70 10,65:      POSTSTRING  ' heeft afgelopen\n  zaterdag literatuurprijs De Brandende Pen gewonnen. Zijn tekst'
10,65 10,66:              OP              ';'
11,1  11,2 :            NAME              'p'
11,4  13,2 :       PRESTRING  ' pre string over \n                        multi lines\n\t\t'
13,2  13,3 :         EMBSTRT              '<'
13,3  13,5 :            NAME             'em'
13,7  14,9 :          STRING  'midtext over multi lines\n\t\t\t\t\t\t//'
14,10 14,22:          EMBEND              '>'
14,11 15,30:      POSTSTRING  ' posttext \n\t\tpost text over multi lines!'
15,30 15,31:              OP              ';'
16,0  16,3 :         KEYWORD            'end'
17,0  17,0 :       ENDMARKER               ''
"""
        #d = Differ()
        #diffresult = list(d.compare(expected.splitlines(1), self.result.splitlines(1)))
        #pprint(diffresult)
        #sys.stdout.writelines(self.result)
        self.assertEqual(self.result, expected)


if __name__ == '__main__':
        unittest.main()

