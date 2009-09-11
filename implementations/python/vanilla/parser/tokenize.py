
import string, re
from token import *

COMMENT = N_TOKENS
tok_name[COMMENT] = 'COMMENT'
NL = N_TOKENS + 1
tok_name[NL] = 'NL'
N_TOKENS += 2

def group(*choices): return '(' + '|'.join(choices) + ')'
def any(*choices): return group(*choices) + '*'
def maybe(*choices): return group(*choices) + '?'

Whitespace = r'[ \f\t]*'

Name = r'[a-zA-Z_]\w*'

Decnumber = r'[1-9]\d*'
Pointfloat = group(r'\d+\.\d*', r'\.\d+')
Number = group(Pointfloat, Decnumber)

String = group(r"'[^\n'\\]*(?:\\.[^\n'\\]*)*'",
               r'"[^\n"\\]*(?:\\.[^\n"\\]*)*"')

ContStr = group(r"'[^\n'\\]*(?:\\.[^\n'\\]*)*" +
                group("'", r'\n'),
                r'"[^\n"\\]*(?:\\.[^\n"\\]*)*' +
                group('"', r'\n'))

# Tail end of ' string.
Single = r"[^'\\]*(?:\\.[^'\\]*)*'"
# Tail end of " string.
Double = r'[^"\\]*(?:\\.[^"\\]*)*"'

endprogs = {"'": re.compile(Single), '"': re.compile(Double)}

# continued comment
ContComment = r'/\*[^\n\*\\]*(?:\\.[^\n\*\\]*)*' + group("\*/", r'\n')
endcomment = re.compile(r'[^*\\]*(?:\\.[^*\\]*)*\*/')
Comment = r'//[^\r\n]*'

Operator = r"/+%&|^=?"

Bracket = r'[][(){}]'
Special = group(r'\r?\n', r'[:;.,`@]')
Funny = group( Bracket, Special, )

Ignore = Whitespace + any(r'\\\r?\n' + Whitespace) + maybe(Comment)
PlainToken = group(Number, Funny, String, Name)
Token = Ignore + PlainToken

PseudoExtras = group(r'\\\r?\n', Comment, )
PseudoToken = Whitespace + group(PseudoExtras, Number, Funny , Name, ContStr, ContComment)

pseudoprog = re.compile( PseudoToken )

single_quoted = {"'" : "'", '"' : '"'}

def printtoken(type, token, (srow, scol), (erow, ecol), line): # for testing
    print "%d,%d-%d,%d:\t%s\t%s" % \
        (srow, scol, erow, ecol, tok_name[type], repr(token))

def tokenize(readline, tokeneater=printtoken):
    """
    The tokenize() function accepts two parameters: one representing the
    input stream, and one providing an output mechanism for tokenize().

    The first parameter, readline, must be a callable object which provides
    the same interface as the readline() method of built-in file objects.
    Each call to the function should return one line of input as a string.

    The second parameter, tokeneater, must also be a callable object. It is
    called once for each token, with five arguments, corresponding to the
    tuples generated by generate_tokens().
    """
    for token_info in generate_tokens(readline):
        tokeneater(*token_info)

def generate_tokens(readline):
    """
    The generate_tokens() generator requires one argment, readline, which
    must be a callable object which provides the same interface as the
    readline() method of built-in file objects. Each call to the function
    should return one line of input as a string.  Alternately, readline
    can be a callable function terminating with StopIteration:
        readline = open(myfile).next    # Example of alternate readline

    The generator produces 5-tuples with these members: the token type; the
    token string; a 2-tuple (srow, scol) of ints specifying the row and
    column where the token begins in the source; a 2-tuple (erow, ecol) of
    ints specifying the row and column where the token ends in the source;
    and the line on which the token was found. The line passed is the
    logical line; continuation lines are included.
    """
    lnum = parenlev = 0
    namechars, numchars = string.ascii_letters + '_', '0123456789'
    contstr, contcomment =  '', ''
    contline = None

    while 1:                                   # loop over lines in stream
        try :
            line = readline()
        except StopIteration:
            line = ''

        lnum = lnum + 1
        pos, max = 0, len(line)

        if contstr:                            # continued string
            if not line:
                raise TokenError, ("EOF in multi-line string", strstart)
            endmatch = endprog.match(line)
            if endmatch:
                pos = end = endmatch.end(0)
                yield (STRING, contstr + line[:end],
                           strstart, (lnum, end), contline + line)
                contstr = ''
                contline = None
            else :
                contstr = contstr + line
                contline = contline + line
                continue
        elif contcomment :
            if not line:
                raise TokenError, ("EOF in multi-line comment", commentstart)
            endmatch = endcomment.match(line)
            if endmatch:
                pos = end = endmatch.end(0)
                yield(COMMENT, contcomment + line[:end],
                           commentstart, (lnum, end), contline + line)
                contcomment = ''
                contline = None
            else:
                contcomment = contcomment + line
                contline = contline + line
                continue
        elif parenlev == 0 :  # new statement
            if not line: break
            column = 0

            if line[pos] in '\r\n':           # skip blank lines
                yield (NL, line[pos:], (lnum, pos), (lnum, len(line)), line)
                continue

        else :                                # continued statement
            if not line:
                raise TokenError, ("EOF in multi-line statement", (lnum, 0))

        while pos < max:
            pseudomatch = pseudoprog.match(line, pos)
            if pseudomatch:                                # scan for tokens
                start, end = pseudomatch.span(1)
                spos, epos, pos = (lnum, start), (lnum, end), end
                token, initial = line[start:end], line[start]

                if initial in numchars or \
                   (initial == '.' and token != '.'):      # ordinary number
                    yield (NUMBER, token, spos, epos, line)
                elif initial in '\r\n':
                    yield (parenlev > 0 and NL or NEWLINE,
                               token, spos, epos, line)
                elif initial == '/' and token[:2] == '//': # comment
                    yield (COMMENT, token, spos, epos, line)
                    continue
                elif initial == '/' and token[:2] == '/*':
                    if token[-1] == '\n':                  # multiline comment
                        contline = line
                        commentstart = (lnum, start)
                        contcomment, needcont = line[start:], 1
                        break
                    else :                                 # ordinary comment
                        yield( COMMENT, token, spos, epos, line)
                        continue
                elif initial in single_quoted:
                    if token[-1] == '\n':                  # continued string
                        strstart = (lnum, start)
                        endprog = endprogs[initial]
                        contstr, needcont = line[start:], 1
                        contline = line
                        break
                    else:                                  # ordinary string
                        yield (STRING, token, spos, epos, line)
                elif initial in namechars:                 # ordinary name
                    yield (NAME, token, spos, epos, line)
                else:
                    if initial in '([{': parenlev = parenlev + 1
                    elif initial in ')]}': parenlev = parenlev - 1
                    yield (OP, token, spos, epos, line)
            else:
                yield (ERRORTOKEN, line[pos],
                           (lnum, pos), (lnum, pos+1), line)
                pos = pos + 1

    yield (ENDMARKER, '', (lnum, 0), (lnum, 0), '')


if __name__ == '__main__':                     # testing
    import sys
    if len(sys.argv) > 1: tokenize(open(sys.argv[1]).readline)
    else : tokenize(sys.stdin.readline)
