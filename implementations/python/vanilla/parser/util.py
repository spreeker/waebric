
import parser

## convenience functions around parser functions 
def waebric_parsefile(filename, lineno=False): 
    """parse <filename> using parser module and return nested tuples 
    """ 
    waebrickf = file(filename) 
    source = waebrickf.read() 
    waebrickf.close() 
    return python_parse(source, 'exec', lineno) 
 
def python_parse(source, mode='exec', lineno=False): 
    """parse waebric source using parser module and return 
    nested tuples 
    """ 
    if mode == 'eval': 
        tp = parser.expr(source)
    else: 
        tp = parser.suite(source) 
    #return parser.ast2tuple(tp, line_info=lineno)
