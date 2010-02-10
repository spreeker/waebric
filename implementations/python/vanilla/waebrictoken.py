""" Waebrick token definition """

waebrick_tokens = {}
waebrick_opmap = {}


def _add_tok(name, *values):
    index = len(waebrick_tokens)
    waebrick_tokens[name] = index
    for value in values:
        waebrick_opmap[value] = index

#_add_tok('IF', 'if')
#_add_tok('ELSE', 'else', EACH, LET, IN, COMMENT, ECHO, CDATA, YIELD,
#MODULE, IMPORT, DEF, END, SITE,
#LIST, RECORD, STRING;
