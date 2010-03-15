
ENDMARKER = 0
NAME = 1
NUMBER = 2
STRING = 3
PRESTRING = 4
EMBSTRT = 5
EMBEND = 6
POSTSTRING = 7
NEWLINE = 7
NL = 9
LPAR = 10
RPAR = 11
LSQB = 12
RSQB = 13
COLON = 14
COMMA = 15
SEMI = 16
COMMENT = 17
KEYWORD = 18

OP = 40
ERRORTOKEN = 50
N_TOKENS = 19

tok_name = {}
for _name, _value in globals().items():
    if type(_value) is type(0):
        tok_name[_value] = _name

NT_OFFSET = 13

def ISTERMINAL(x):
    return x < NT_OFFSET

def ISNONTERMINAL(x):
    return x >= NT_OFFSET

def ISEOF(x):
    return x == ENDMARKER

