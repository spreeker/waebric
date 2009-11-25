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
        p.setTokens(generate_tokens(gen_line(source)))
        p.next()
        self.assertEqual(p.matchLexeme("def"), True)
        self.assertEqual(p.matchLexeme("site"), False)

    def test_matchTokensort(self):
        source = "def test 10"
        p = Parser()
        p.setTokens(generate_tokens(gen_line(source)))
        p.next()
        self.assertEqual(p.matchTokensort(KEYWORD), True)
        self.assertEqual(p.matchTokensort(NUMBER), False)
        p.next()
        self.assertEqual(p.matchTokensort(NAME), True)
        p.next()
        self.assertEqual(p.matchTokensort(NAME), False)
        self.assertEqual(p.matchTokensort(NUMBER), True)


    def test_check(self):
        source = "def test 10"
        p = Parser()
        p.setTokens(generate_tokens(gen_line(source)))
        p.next()

        self.assertRaises(UnexpectedToken,
        p.check,tokensort=NUMBER, lexeme='def')

    def test_peek(self):
        source = "def test 10 site"
        p = Parser()
        p.setTokens(generate_tokens(gen_line(source)))
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
        p.setTokens(generate_tokens(gen_line(source)))
        p.next()
        self.assertEqual(p.matchLexeme('def'), True)
        p.next()
        self.assertEqual(p.matchLexeme('test'), True)

class TestParserClasses(unittest.TestCase):

    def initialize_parser(self, parserClass, source):
        p = parserClass()
        p.setTokens(generate_tokens(gen_line(source)))
        p.next()
        return p

    def test_parseFunction(self):
        source = """def test(argument)
          p "bla";
          p p p();
        end"""
        p = FunctionParser()
        p.setTokens(generate_tokens(gen_line(source)))
        p.next()
        p.parseFunction()


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
            p.setTokens(generate_tokens(gen_line(source)))
            p.next()
            p.parseSite()

        source = "site dir/dir2/file.ext: startfunction(); end"
        _parse_site(source)

        badsource = "site nameBLEH"
        self.assertRaises(UnexpectedToken, _parse_site, badsource)
        #TODO more cases

class TestPredicateParser(unittest.TestCase):

    def _parserPredicate(self, source):
        p = PredicateParser()
        p.setTokens(generate_tokens(gen_line(source)))
        p.next()
        p.parsePredicate()

    def test_predicate_type(self):
        source = "variable.list? "
        self._parserPredicate(source)
        source = "variable.record? "
        self._parserPredicate(source)

    def test_predicate_or(self):
        source = "10 || 20 "
        self._parserPredicate(source)

    def test_predicate_and(self):
        source = "var &&  var"
        self._parserPredicate(source)


class TestMarkup(unittest.TestCase):

    def _parse_markup(self,source):
        p = StatementParser()
        p.setTokens(generate_tokens(gen_line(source)))
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


class TestStatementParser(unittest.TestCase):

    def _parse_statement(self, source):
        p = StatementParser()
        p.setTokens(generate_tokens(gen_line(source)))
        p.next()
        p.parseStatement()

    def test_if_(self):
        source = "if  ( bla )  h1 name;"
        self._parse_statement(source)
        source = """
        if ( a = "b" ) { h2 markup "data"; markup; }
        else { "other stuff"; }
        """
        self._parse_statement(source)

    def test_each(self):
        source = "each ( var : expression ) m var;"
        self._parse_statement(source)


    def test_cdata(self):
        source = "cdata expression;"
        self._parse_statement(source)

    def test_yield(self):
        source = "yield;"
        self._parse_statement(source)

    def test_comment(self):
        source = 'comment "commentString";'
        self._parse_statement(source)

    def test_let_(self):
        source = "let x = bla.bla.bla in statement x; end"
        self._parse_statement(source)



class TestExpression(unittest.TestCase):

    def _parse_expression(self,source):
        p = ExpressionParser()
        p.setTokens(generate_tokens(gen_line(source)))
        p.next()
        p.parseExpression()

    def test_symbol(self):
        sourceSymbol = "'symbol"
        self._parse_expression(sourceSymbol)

    def test_list(self):
        sourceList = '[ "i1", "i2" ]'
        self._parse_expression(sourceList)

    def test_record(self):
        sourceRecord = '{ key : "value" , key2 : "value2" } '
        self._parse_expression(sourceRecord)

        sourceRecord = '{ key : "value" , key2 :  } '
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
        sourcePlus = ' + "b"'
        self.assertRaises(UnexpectedToken, self._parse_expression, sourcePlus)

if __name__ == '__main__':
    unittest.main()

