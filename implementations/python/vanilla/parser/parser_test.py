import unittest

from parser import * 
from tokenize import generate_tokens

from token import *

def gen_line(lines):
    gen = (line for line in lines.splitlines())
    return gen.next

class TestParser(unittest.TestCase):
    "test parser functions"

    def test_matchLexeme(self):
        source = "def test 10"
        p = Parser()
        p.tokens = generate_tokens(gen_line(source))
        p.next()
        self.assertEqual(p.matchLexeme("def"), True)
        self.assertEqual(p.matchLexeme("site"), False)

    def test_matchTokensort(self):
        source = "def test 10"
        p = Parser()
        p.tokens = generate_tokens(gen_line(source))
        p.next()
        self.assertEqual(p.matchTokensort(NAME), True)
        self.assertEqual(p.matchTokensort(NUMBER), False)
        p.next()
        p.next()
        self.assertEqual(p.matchTokensort(NAME), False)
        self.assertEqual(p.matchTokensort(NUMBER), True)


    def test_check(self):
        source = "def test 10"
        p = Parser()
        p.tokens = generate_tokens(gen_line(source))
        p.next()

        self.assertRaises(UnexpectedToken,
        p.check,tokensort=NUMBER, lexeme='def')

    def test_peek(self):
        source = "def test 10 site"
        p = Parser()
        p.tokens = generate_tokens(gen_line(source))
        p.next()
        self.assertEqual(p.peek(x=2, tokensort=NUMBER), True)
        self.assertEqual(p.peek(x=1, lexeme='test'), True)
        self.assertEqual(p.peek(x=2, lexeme='test'), False)
        self.assertEqual(p.peek(x=3, lexeme='site'), True)
        p.next()
        self.assertEqual(p.peek(x=2, tokensort=NUMBER), False)
        self.assertEqual(p.peek(x=2, lexeme='site'), True)

    def test_next(self):
        source = "def test 10 site"
        p = Parser()
        p.tokens = generate_tokens(gen_line(source))
        p.next()
        self.assertEqual(p.matchLexeme('def'), True)
        p.next()
        self.assertEqual(p.matchLexeme('test'), True)

class TestParserClasses(unittest.TestCase):

    def initialize_parser(self, parserClass, source):
        p = parserClass()
        p.tokens = generate_tokens(gen_line(source))
        p.next()
        return p

    def test_parseFunction(self):
        source = """def test
          p "bla";
          p p p();
        end"""
        p = FunctionParser()
        p.tokens = generate_tokens(gen_line(source))
        p.next()
        p.parseFunction()

        #source = """def test
        #  p "bla";
        #  p p p();
        #"""
        #p.tokens = generate_tokens(gen_line(source))
        #p.next()
        #self.assertRaises(UnexpectedToken, p.parseFunction)

    def test_module(self):
        source = "module menus"
        p = self.initialize_parser(ModuleParser, source)
        p.parseModule()

        badsource = "mod fout"
        p = self.initialize_parser(ModuleParser, badsource)
        self.assertRaises(UnexpectedToken, p.parseModule)


    def test_site(self):

        def _parse_site(source):
            p = SiteParser()
            p.tokens = generate_tokens(gen_line(source))
            p.next()
            p.parseSite()

        source = "site dir/dir2/file.ext: startfunction(); end"
        _parse_site(source)

        badsource = "site nameBLEH"
        self.assertRaises(UnexpectedToken, _parse_site, badsource)
        #TODO more cases

class TestMarkup(unittest.TestCase):
 
    def _parse_markup(self,source):
        p = StatementParser()
        p.tokens = generate_tokens(gen_line(source))
        p.next()
        p.parseStatement()

    def test_markup(self):
        source = "markup ;"
        self._parse_markup(source)
        source = "markup variable ;"
        self._parse_markup(source)
        source = "markup markup() ;"
        self._parse_markup(source)
        source = 'markup function( a = "b" ) ;'
        self._parse_markup(source)
        source = 'markup markup function("beee") ;'
        self._parse_markup(source)


    def test_statement(self):
        pass

    def test_let_statement(self):
        pass



class TestExpression(unittest.TestCase):

    def _parse_expression(self,source):
        p = ExpressionParser()
        p.tokens = generate_tokens(gen_line(source))
        p.currentToken = ""
        p.peekedTokens = []
        p.next()
        p.parseExpression()

    def test_symbol(self):
        sourceSymbol = "'symbol"
        self._parse_expression(sourceSymbol)
        sourceSymbol = "symbol"
        self.assertRaises(UnexpectedToken,self._parse_expression,sourceSymbol)

    def test_list(self):
        sourceList = '[ "i1", "i2" ]'
        self._parse_expression(sourceList)

    def test_record(self):
        sourceRecord = '{ key : "value" , key2 : "value2" } '
        self._parse_expression(sourceRecord)

        sourceRecord = '{ key : "value" , key2 : value2 } '
        self.assertRaises(UnexpectedToken, self._parse_expression, sourceRecord)

    def test_number(self):
        sourceNumber = "66"
        self._parse_expression(sourceNumber)

    def test_string(self):
        sourceString = '"Hallo, dit is een string expressie!"'
        self._parse_expression(sourceString)

    def test_dotnames(self):
        sourceDotName = 'een.tweee.drie'
        self._parse_expression(sourceDotName)

    def test_plus(self):
        sourcePlus = '"a" + "b"'
        self._parse_expression(sourcePlus)
        sourcePlus = 'a + "b"'
        self.assertRaises(UnexpectedToken, self._parse_expression, sourcePlus)

if __name__ == '__main__':
    unittest.main()

