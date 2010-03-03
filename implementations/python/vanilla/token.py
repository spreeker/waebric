
ENDMARKER = 0
NAME = 1
NUMBER = 2
STRING = 3
PRESTRING = 4
EMBSTRING = 5
POSTSTRING = 6
NEWLINE = 7
NL = 8
LPAR = 9
RPAR = 10
LSQB = 11
RSQB = 12
COLON = 13
COMMA = 14
SEMI = 15
COMMENT = 16
KEYWORD = 17

OP = 40
ERRORTOKEN = 50
N_TOKENS = 18

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

