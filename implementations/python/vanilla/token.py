
ENDMARKER = 0
NAME = 1
NUMBER = 2
STRING = 3
PRESTRING = 4
MIDSTRING = 5
EMBSTRT = 6
EMBEND = 7
POSTSTRING = 8
NEWLINE = 8
NL = 10
LPAR = 11
RPAR = 12
LSQB = 13
RSQB = 14
COLON = 15
COMMA = 16
SEMI = 17
COMMENT = 18
KEYWORD = 19

OP = 40
ERRORTOKEN = 50
N_TOKENS = 20

tok_name = {}
for _name, _value in globals().items():
    if type(_value) is type(0):
        tok_name[_value] = _name
