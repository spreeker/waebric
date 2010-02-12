"""
Checks AST for errors.

In the tokenizer and parser many errors are already catched reported.
Errors in the AST or semantic errors are checked for here.

This visitor checks for:
    Undefined Functions.
    Undefined Variables
    Non-existing modules
    Duplicate definitions
    Arity Mismatches
"""


class waeChecker:
    """ check ast for errors """

    
