namespace Lexer.Tokenizer
{
    /// <summary>
    /// Types of tokens for Lexer Tokens
    /// </summary>
    public enum TokenType : int
    {
        /// <summary>
        /// KEYWORD:
        /// Keywords are language identifiers like module, site, def, end
        /// </summary>
        KEYWORD,

        /// <summary>
        /// IDENTIFIER:
        /// Anything which identifies something
        /// </summary>
        IDENTIFIER,

        /// <summary>
        /// NUMBER:
        /// All numeric values
        /// </summary>
        NUMBER,

        /// <summary>
        /// TEXT:
        /// Anything that is between quotes that contains letters, numeric values or symbols
        /// </summary>
        TEXT,

        /// <summary>
        /// SYMBOL:
        /// Characters like @#$%^, etc
        /// </summary>
        SYMBOL        
    };
}
