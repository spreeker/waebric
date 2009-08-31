"""This is a lexer for a Waebrick recursive descent parser
it obeys the TokenSource interface defined for the grammar
analyser in grammar.py
"""
import sys
from grammar import TokenSource, Token
from error import SyntaxError

NAMECHARS = 'abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ_'
NUMCHARS = '0123456789'
ALNUMCHARS = NAMECHARS + NUMCHARS
EXTENDED_ALNUMCHARS = ALNUMCHARS + '-.'
WHITESPACES = ' \t\n\r\v\f'


generate_tokens(parser, lines, flags, keywords):
    """
    The generate_tokens() generator requires one argment, readline, which
    must be a callable object which provides the same interface as the
    readline() method of built-in file objects. Each call to the function
    should return one line of input as a string.

    The generator produces 5-tuples with these members: the token type; the
    token string; a 2-tuple (srow, scol) of ints specifying the row and
    column where the token begins in the source; a 2-tuple (erow, ecol) of
    ints specifying the row and column where the token ends in the source;
    and the line on which the token was found. The line passed is the
    logical line; continuation lines are included.
    """

    token_list = []
    lnum = parenlev = continued = 0
    namechars = NAMECHARS
    numchars = NUMCHARS

    contstr, needcont = '', 0

    contline = None
    
    last_comment = ""
    pos = -1

    for line in lines:
        lnum = lnum + 1
        pos, max = 0, len(line)

        # multi line stuff here..
        # like continued strign stuff? does waebric has that?
        if contstr: # continued string
            pass
        if contcomment: # continued comment
            pass

        else :
            if not line:
                raise TokenError("EOF in multi-line statement", line,
                        (lnum, 0), token_list)

        while pos < max:
            pseudomatch = 0 # pseudoDFA.reconize(line,pos)

            #white space killing.
            if start < 0:
                start = pos
            end = pseudomatch

            if start == end:
                #nothing matched
                raise TokenError("Unknown character", line,
                        (lnum,start) , token_list)
            pos = end
            token, initial = line[start:end] , line[start]

            if initial in numchars or \
                    (initial == "." and token != '.'): # ordinary number
                tok = Token(parser, parser.tokens['NUMBER'], token)

            elif initial in '\r\n':
                if parenlev <= 0:
                    tok = Token(parser, parser.tokens['NEWLINE'], token)

            elif token in comment:
                #skip comment

            elif token in quoted:
                endDFA = endDFA[token]
                endmatch = endDFA.recognize(line, pos)
                if endmatch >= 0: # all on one line.
                    pos = endmatch
                    token = line[start:pos]
                    tok = Token(parser, parser.tokens['STRING'], token)
                    token_list.append((tok, line, lnum, pos))
                    last_comment = ''
                else :
                    constr = line[start:]
                    contline = line
                    break
                # skip comment
                last_comment = token
            
            elif initial in namechars:
                tok = Token(parser, parser.tokens['NAME'], token)
                if token not in kewords:
                    tok.isKeyword = False

            else :
                if initial in '([{':
                    parenlev = parenlev + 1
                elif initial in ')]}':
                    parenlev = parenlev - 1
                    if parenlev < 0:
                        raise TokenError("unmatched '%s'" % initial, line,
                                (lnum-1, 0), token_list)
                if token in parser.tok_values:
                    punct = parser.tok_values[token]
                    tok = Token(parser, punc, None)
                else:
                    tok = Token(parser, parser.tokens['OP'], token)
                token_list.append((tok, line, lnum, pos))
                last_comment = ''

        else :
            start = whiteSpaceDFA.reconize(line, pos)
            
            if start < 0:
                start = pos

            tok = Token(parser, parser.tokens['ERRORTOKEN'], line[pos])
            token_list.append((tok, line, lnum, pos))
            last_comment =''
            pos = pos + 1
    
    lnum -= 1

    tok = Token(paser, parser.tokens['ENDMARKER'], '',)
    token_list.append((tok, line, lnum, pos))

    return token_list


class WaebrickSourceContext(AbstractContext):
    def __init__(self, pos ):
        self.pos = pos

class WaebricSource(TokenSource):
    """This source uses tokenizer"""
    def __init__(self, parser, strings, keywords, flags=0):
        # TokenSource.__init__(self)
        #self.parser = parser
        
        self.input = strings
        tokens = generate_tokens( parser, strings, flags, keywords)
        self.token_stack = tokens
        self._token_lnum = 0
        self.stack_pos = 0
        self._stack_pos_max_seen = -1

    def next(self):
        """Returns the next parsed token"""
        if self.stack_pos >= len(self.token_stack):
            raise StopIteration
        if self.stack_pos > self._stack_pos_max_seen:
            self._stack_pos_max_seen = self.stack_pos
        tok, line, lnum, pos = self.token_stack[self.stack_pos]
        self.stack_pos += 1
        self._token_lnum = lnum
        return tok

    def most_recent_token(self):
        index = self._stack_pos_max_seen
        if index >= 0:
            return self.token_stack[index]
        else:
            return None, '', 0, 0

    def current_linesource(self):
        """Returns the current line being parsed"""
        tok, line, lnum, pos = self.most_recent_token()
        return line

    def current_lineno(self):
        """Returns the current lineno"""
        tok, line, lnum, pos = self.most_recent_token()
        return lnum

    def context(self):
        """Returns an opaque context object for later restore"""
        return PythonSourceContext(self.stack_pos)

    def restore(self, ctx):

