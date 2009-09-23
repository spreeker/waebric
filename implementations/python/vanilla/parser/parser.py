
import tokenizer

class AbstracParser():

    exceptions = []

    def __init__(self,tokens):
        self.tokens = tokeniterator

    def current(self):
        return self.current

    def next(self.next):
        return self.next

class WaebricParser(AbstracParser):

    def parse(self)
        module = ModuleParser(self.tokens)

        while( 
class ModuleParser(AbstracParser):
    pass
    def parseModule(self):


class SiteParser(AbstracParser):
    pass

class FunctionParser(AbstracParser):
    pass


class ExpressionParser(AbstracParser):
    pass


class PredicateParser(AbstracParser):
    pass


class StatementParser(AbstracParser):
    pass


class EmbeddingParser(AbstracParser):
    pass

class MarkupParser(AbstracParser):
    pass


def parse(source):
    tokens = tokenizer.generate_tokens(source)

    parser = WaebricParser(tokens)

    print parser

if __name__ == '__main__':                     # testing
    import sys
    if len(sys.argv) > 1: parse(open(sys.argv[1]).readline)
    else : parse(sys.stdin.readline)
