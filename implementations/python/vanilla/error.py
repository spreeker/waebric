import logging

from decorator import decorator
from token import *

DEBUG = False
SHOWTOKENS = False
SHOWPARSER = False

if DEBUG:
    LOG_FILENAME = 'debug.log'
    logging.basicConfig(filename=LOG_FILENAME,level=logging.DEBUG,)

def strToken(type, token, (srow, scol), (erow, ecol), line):
    global tabs
    space = " " * tabs
    return "%s%d,%d-%d,%d:\t%s\t%s" % \
        (space,srow, scol, erow, ecol, tok_name[type], repr(token))

def logToken(*token):
    logging.info(strToken(*token))

def indentedLog(data):
    global tabs
    space = " " * tabs
    logging.info("%s%s" % (space,data))

tabs = 0

def _trace(f, *args, **kw):
    global tabs
    space = tabs * " "
    #smallargs = "".join([o.__class__.__name__ for o in args])
    logging.info("%d%scalling %s with args \n%s, %s\n" % \
            (tabs,space,f.__name__, args, kw ))
    tabs += 1
    result = f(*args, **kw)
    #logging.info(str(result))
    logging.info("%d%sexit %s" % (tabs,space,f.__name__))
    tabs -= 1
    return result

def trace(f):
    return decorator(_trace, f)

class SyntaxError(Exception):
    """Base class for exceptions raised by the parser."""

    def __init__(self, token, expected=""):
        self.token = token
        self.expected = expected
        self.line = token[-1]
        self.lineno = token[3][0]
        self.offset = token[3][1]

        if DEBUG:
            logging.debug("raised exception on:")
            logToken(*token)
            logging.debug("expected: %s" % expected)

    def __str__(self):
        return "%s at pos (%d, %d) in %r \n expected: %s" % (self.__class__.__name__,
                                             self.lineno,
                                             self.offset,
                                             self.line,
                                             self.expected)


class ASTError(Exception):
    def __init__(self, msg, ast_node ):
        self.msg = msg
        self.ast_node = ast_node


class TokenError(Exception):
    def __init__(self, msg, tokens):
        self.msg = msg
        self.tokens = tokens

