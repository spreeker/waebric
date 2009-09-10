

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
        pass
