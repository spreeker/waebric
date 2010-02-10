"""
Test code for Parser module.

This module contains:
-basic parser function tests
-basic parser AST tests. Output of the printed ast
tree is compared with previous results.

"""

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

        self.assertRaises(SyntaxError,
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

    def initialize_parser(self, source):
        p = Parser()
        p.setTokens(generate_tokens(gen_line(source)))
        p.next()
        return p

    def test_parseFunction(self):
        source = """def test(argument)
          p "bla";
          p p p();
        end"""
        p = Parser()
        p.setTokens(generate_tokens(gen_line(source)))
        p.next()
        ast = parseFunction(p)
        self.assertEqual(str(ast),"""FUNCTION test (NAME('argument')) { MARKUP(Designator('p')STRING("bla")),MARKUP(Designator('p')MARKUP(Designator('p')MARKUP(Designator('p')))) } \n""")

    def test_module(self):
        source = "module menus"
        p = self.initialize_parser(source)
        ast = parseModule(p)
        self.assertEqual(str(ast),"""MODULE ['menus'] ,
        SITES 
        IMPORTS 
        FUNCTIONS  """)

        badsource = "mod fout"
        p = self.initialize_parser(badsource)
        self.assertRaises(SyntaxError, parseModule, p)


    def test_site(self):
        source = "site dir/dir2/file.ext: startfunction(); end"
        p = self.initialize_parser(source)
        ast = parseSite(p)
        self.assertEqual(str(ast), "[MAPPING(PATH(sitedir/dir2//file.ext), MARKUP(Designator('startfunction')))]")

        badsource = "site nameBLEH"
        p = self.initialize_parser(badsource)
        self.assertRaises(SyntaxError, parseSite, p)


class TestPredicateParser(unittest.TestCase):

    def _parserPredicate(self, source):
        p = Parser()
        p.setTokens(generate_tokens(gen_line(source)))
        p.next()
        ast = parsePredicate(p)
        return ast

    def test_predicate_type(self):
        source = "variable.list? "
        ast = self._parserPredicate(source)
        self.assertEqual(str(ast),"IS_A(NAME('variable'), LIST)")
        source = "variable.record? "
        ast = self._parserPredicate(source)
        self.assertEqual(str(ast),"IS_A(NAME('variable'), RECORD)")

    def test_predicate_or(self):
        source = "10 || 20 "
        ast = self._parserPredicate(source)
        self.assertEqual(str(ast), "OR(NATNUM(10), NATNUM(20))")

    def test_predicate_and(self):
        source = "var &&  var"
        ast = self._parserPredicate(source)
        self.assertEqual(str(ast),"AND(NAME('var'), NAME('var'))")


class TestMarkup(unittest.TestCase):

    def _parse_markup(self,source):
        p = Parser()
        p.setTokens(generate_tokens(gen_line(source)))
        p.next()
        return parseStatement(p)

    def test_markup(self):
        source = "markup ;"
        ast = self._parse_markup(source)
        self.assertEqual(str(ast),"MARKUP(Designator('markup'))")
        source = "markup variable ;"
        ast = self._parse_markup(source)
        self.assertEqual(str(ast),"MARKUP(Designator('markup')NAME('variable'))")
        source = "markup markup() ;"
        ast = self._parse_markup(source)
        self.assertEqual(str(ast),
                "MARKUP(Designator('markup')MARKUP(Designator('markup')))")
        source = 'markup function( a ) ;'
        ast = self._parse_markup(source)
        self.assertEqual(str(ast),
        "MARKUP(Designator('markup')MARKUP(Designator('function')[NAME('a')]))")
        source = 'markup function( a = "b" ) ;'
        ast = self._parse_markup(source)
        self.assertEqual(str(ast),
        """MARKUP(Designator('markup')MARKUP(Designator('function')[ASSIGNMENT('a',STRING("b"))]))""")
        source = 'markup markup function("beee") ;'
        ast = self._parse_markup(source)
        self.assertEqual(str(ast),
        """MARKUP(Designator('markup')MARKUP(Designator('markup')MARKUP(Designator('function')[STRING("beee")])))""")


class TestStatementParser(unittest.TestCase):

    def _parse_statement(self, source):
        p = Parser()
        p.setTokens(generate_tokens(gen_line(source)))
        p.next()
        return parseStatement(p)

    def test_if(self):
        source = "if ( bla )  h1 name;"
        ast = self._parse_statement(source)
        self.assertEqual(str(ast),"""If(NAME('bla'),
 MARKUP(Designator('h1')NAME('name')) )""")

        source = """if ( "b" ) { h2 markup "data"; markup; }
        else { markup "other stuff"; }
        """
        ast = self._parse_statement(source)
        rast = """If(STRING("b"),
 Block(MARKUP(Designator('h2')STRING("data")MARKUP(Designator('markup')))
MARKUP(Designator('markup'))) 
ELSE Block(MARKUP(Designator('markup')STRING("other stuff"))))"""

        self.assertEqual(str(ast),rast)

    def test_each(self):
        source = "each ( var : expression ) m var;"
        ast = self._parse_statement(source)
        self.assertEqual(str(ast),"Each(var in MARKUP(Designator('m')NAME('var')) do NAME('expression'))")

    def test_cdata(self):
        source = "cdata expression;"
        ast = self._parse_statement(source)
        self.assertEqual(str(ast),"Cdata(NAME('expression'))")

    def test_yield(self):
        source = "yield;"
        ast = self._parse_statement(source)
        self.assertEqual(str(ast),"Yield")

    def test_comment(self):
        source = 'comment "commentString";'
        ast = self._parse_statement(source)
        self.assertEqual(str(ast),'Comment("commentString")')

    def test_let(self):
        source = "let x = markup.class bla.bla.bla; in statement x; end"
        ast = self._parse_statement(source)
        self.assertEqual(str(ast),"LET(ASSIGNMENT('x',MARKUP(Designator('markup'('.', 'class'))FIELD 'bla' in FIELD 'bla' in NAME('bla'))), [MARKUP(Designator('statement')NAME('x'))])")

    def test_let_regression(self):
        source = """let
       td(img, alt) = td img(width="160",height="160", alt=alt,src=img);
     in
      tr {
          td("images/lavakaft_13-34.jpg", "lava 13-34");
          td("images/lavakaft_14-1.jpg", "lava 14-1");
          td("images/lavakaft_14-2.jpg", "lava 14-2");
      }
     end"""
        ast = self._parse_statement(source)
        astrslt = """LET(ASSIGNMENT("td([NAME(\'img\'), NAME(\'alt\')])",MARKUP(Designator(\'td\')MARKUP(Designator(\'img\')[ASSIGNMENT(\'width\',STRING("160")), ASSIGNMENT(\'height\',STRING("160")), ASSIGNMENT(\'alt\',NAME(\'alt\')), ASSIGNMENT(\'src\',NAME(\'img\'))]))), [MARKUP(Designator(\'tr\')Block(MARKUP(Designator(\'td\')[STRING("images/lavakaft_13-34.jpg"), STRING("lava 13-34")])\nMARKUP(Designator(\'td\')[STRING("images/lavakaft_14-1.jpg"), STRING("lava 14-1")])\nMARKUP(Designator(\'td\')[STRING("images/lavakaft_14-2.jpg"), STRING("lava 14-2")])))])"""
        self.assertEqual(str(ast), astrslt)


class TestExpression(unittest.TestCase):

    def _parse_expression(self,source):
        p = Parser()
        p.setTokens(generate_tokens(gen_line(source)))
        p.next()
        return parseExpression(p)

    def test_symbol(self):
        sourceSymbol = "'symbol"
        ast = self._parse_expression(sourceSymbol)
        self.assertEqual(str(ast),"STRING(symbol)")

    def test_list(self):
        sourceList = '[ "i1", "i2" ]'
        ast = self._parse_expression(sourceList)
        self.assertEqual(str(ast),"""LIST(STRING("i1"),STRING("i2"))""")

    def test_record(self):
        sourceRecord = '{ key : "value" , key2 : "value2" } '
        ast = self._parse_expression(sourceRecord)
        self.assertEqual(str(ast),"""RECORD('key2':STRING("value2"),'key':STRING("value"))""")

        sourceRecord = '{ key : "value" , key2 :  } '
        self.assertRaises(SyntaxError, self._parse_expression, sourceRecord)

    def test_number(self):
        sourceNumber = "66"
        ast = self._parse_expression(sourceNumber)
        self.assertEqual(str(ast),"NATNUM(66)")

    def test_string(self):
        sourceString = '"Hallo, dit is een string expressie!"'
        ast = self._parse_expression(sourceString)
        self.assertEqual(str(ast),'STRING("Hallo, dit is een string expressie!")')

    def test_dotnames(self):
        sourceDotName = 'een.tweee.drie'
        ast = self._parse_expression(sourceDotName)
        self.assertEqual(str(ast),"FIELD 'drie' in FIELD 'tweee' in NAME('een')")

    def test_plus(self):
        sourcePlus = '"a" + "b"'
        ast = self._parse_expression(sourcePlus)
        self.assertEqual(str(ast),'ADD(STRING("a"), STRING("b"))')

        sourcePlus = ' + "b"'
        self.assertRaises(SyntaxError, self._parse_expression, sourcePlus)

class RegressionTests(unittest.TestCase):

    def test_let_in_block(self):
        source = open('tests/blocks.wae').readline
        ast = parse(source)
        expectedAST = """MODULE ['block'] ,
        SITES [MAPPING(PATH(sitesite//block.html), MARKUP(Designator('function')))]
        IMPORTS 
        FUNCTIONS FUNCTION function () { MARKUP(Designator('layout')MARKUP(Designator('single-column')Block(MARKUP(Designator('table')Block(LET(ASSIGNMENT("td([NAME('img')])",MARKUP(Designator('td')MARKUP(Designator('img')[ASSIGNMENT('src',NAME('img'))]))), [MARKUP(Designator('tr')Block(MARKUP(Designator('td')[STRING("34.jpg")])))])))))) } 
 """
        self.assertEqual(expectedAST,repr(ast))



if __name__ == '__main__':
    unittest.main()

