using System;
using System.IO;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Lexer.Tokenizer
{
   

    /// <summary>
    /// StreamTokenizer class tokenizes an StreamReader input
    /// </summary>
    public class StreamTokenizer
    {
        #region Constants

        //Constants for types of tokens
        public const int EOF = -1;      //End of file
        public const int LAYOUT = 1;    //Layout (like newlines, tabs, etc)
        public const int NUMBER = 2;    //Integer value (floats not supported)
        public const int WORD = 3;      //String value
        public const int CHARACTER = 4; //Character value (like symbol, etc)
        public const int COMMENT = 5;   //Comment (like //test /*test*/)

        #endregion

        #region Private Members
        
        //Common members
        private TextReader InputStream; // Inputstream to read from
        private int CurrentCharacter = '\0';  // Current character
        private int PreviousCharacter = '\0'; // Previous character
        private bool URL = false; // Is character part of URL

        //Current character value
        private int LineNumber = 1; // Linenumber of stream


        //Current token value
        private int NumValue = -1; // Numeric value of token
        private String TextValue = ""; // Text value of token    
        private char CharValue = '\0'; //Character value

        #endregion

        #region Public Methods

        /// <summary>
        /// Initialize StreamTokenizer with inputstream to tokenize
        /// </summary>
        /// <param name="inputStream">InputStream to tokenize</param>
        public StreamTokenizer(TextReader inputStream)
        {
            if (inputStream == null)
            {
                throw new NullReferenceException();
            }
            this.InputStream = inputStream;
            Read();
        }

        /// <summary>
        /// Retrieves NextToken from stream
        /// </summary>
        /// <returns>Type of token or character of symbol found</returns>
        public int NextToken()
        {
            //Reset all values
            NumValue = 0;
            TextValue = "";
            CharValue = '\0';

            //Determine token type
            if (CurrentCharacter < 0) //End of stream
            {
                return EOF;
            }
            else if (CurrentCharacter == '/') //Possible a comment
            {
                return CommentToken();
            }
            else if (IsLayout(CurrentCharacter)) //Layout token
            {
                return LayoutToken();
            }
            else if (IsNumeric(CurrentCharacter)) //Numeric token
            {
                return NumericToken();
            }
            else if (IsLetter(CurrentCharacter)) //Textual token
            {
                return LetterToken();
            }
            else //We are dealing with a character
            {
                return CharacterToken();
            }
        }


        /// <summary>
        /// Get number of scanned lines of stream
        /// </summary>
        /// <returns>Number of scanned lines</returns>
        public int GetScannedLines()
        {
            return LineNumber;
        }

        /// <summary>
        /// Set number of scanned lines
        /// </summary>
        /// <param name="line">LineNumber to set</param>
        public void SetScannedLines(int line)
        {
            LineNumber = line;
        }

        /// <summary>
        /// Get numeric value of last scanned token
        /// </summary>
        /// <returns>Numeric (double) value</returns>
        public double GetNumericValue()
        {
            return NumValue;
        }

        /// <summary>
        /// Get textual value of last scanned token
        /// </summary>
        /// <returns>String value</returns>
        public String GetTextValue()
        {
            return TextValue;
        }

        /// <summary>
        /// Get character value of last scanned token
        /// </summary>
        /// <returns>Character value</returns>
        public char GetCharacterValue()
        {
            return CharValue;
        }

        /// <summary>
        /// Peeks the next character from stream (not tokenized)
        /// </summary>
        /// <returns>Peeked character</returns>
        public char PeekCharacter()
        {
            return (char) CurrentCharacter;
        }

        /// <summary>
        /// Retrieves whatever the type the value as string
        /// </summary>
        /// <returns>String representation of tokenized value</returns>
        public override String ToString()
        {
            if (TextValue != "")
            {
                return TextValue;
            }
            else if(CharValue != '\0')
            {
                return CharValue.ToString();
            }
            else if(NumValue != -1)
            {
                return NumValue.ToString();
            }
            return null; //No value found
        }

        #endregion

        #region Private Methods

        /// <summary>
        /// Tokenize character token and return type
        /// </summary>
        /// <returns>Type</returns>
        private int CharacterToken()
        {
            //Convert values to string and character
            CharValue = (char) CurrentCharacter;
            Read(); //Buffer character ahead
            
            return CHARACTER;
        }

        /// <summary>
        /// Tokenize letter/word token and return type
        /// </summary>
        /// <returns>Type</returns>
        private int LetterToken()
        {
            //Build word until seperator found
            StringBuilder stringBuilder = new StringBuilder();
            do {
                stringBuilder.Append((char)CurrentCharacter);
                Read();
            } while (IsLetter(CurrentCharacter) || IsNumeric(CurrentCharacter));
            TextValue = stringBuilder.ToString();

            return WORD;
        }

        /// <summary>
        /// Tokenize numeric token and return type
        /// </summary>
        /// <returns>Type</returns>
        private int NumericToken()
        {
            //Calculate integer value of token
            
            int value = Int32.Parse(ConvertIntCharToString(CurrentCharacter));
            Read();
            while(IsNumeric(CurrentCharacter))
            {
                value*= 10;
                value += Int32.Parse(ConvertIntCharToString(CurrentCharacter));
                Read();
            }
            
            NumValue = value;
            
            return NUMBER;
        }

        /// <summary>
        /// Converts an integer indirectly to an string
        /// String value contains real character representation
        /// </summary>
        /// <param name="character">Character as integer</param>
        /// <returns>String</returns>
        private String ConvertIntCharToString(int character)
        {
            return ((char)character).ToString();
        }

        /// <summary>
        /// Tokenize layout token and return type
        /// </summary>
        /// <returns>Type</returns>
        private int LayoutToken()
        {
            CharValue = (char) CurrentCharacter;
            
            Read();

            return LAYOUT;
        }

        /// <summary>
        /// Tokenize possible comment token and return type
        /// </summary>
        /// <returns>Type</returns>
        private int CommentToken()
        {
            if (PreviousCharacter == ':' && InputStream.Peek() == '/' && !URL)
            {   //It is an URL and not an comment like http://
                Read(); //Skip /
                CharValue = '/';
                URL = true;

                //Return first / from URL
                return CHARACTER;
            }
            else if (URL && CurrentCharacter == '/')
            {   //Return second / of URL
                Read(); //Skip /
                CharValue = '/';
                URL = false;

                return CHARACTER;
            }
            else if (URL)
            {   //No second / found after :/
                throw new StreamTokenizerException("URL not ok", LineNumber);
            }
            Read(); //Read one ahead to determine we are dealing with comments

            if(CurrentCharacter == '/') // Single line comment
            {
                //Build string to store comment as string
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.Append('/'); //Add already scanned /
                do {
                    stringBuilder.Append((char)CurrentCharacter);
                    Read();
                } while (CurrentCharacter != '\n' && CurrentCharacter != EOF); //Read until linefeed

                TextValue = stringBuilder.ToString();

                return COMMENT;
            }
            else if (CurrentCharacter == '*') // Multiple line comment /* */
            {
                //Build string to store comment as string
                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.Append('/');
                
                do {
                    stringBuilder.Append((char) CurrentCharacter);
                    Read();
                } while (!(PreviousCharacter == '*' && CurrentCharacter == '/') && CurrentCharacter != EOF); //Read until */ foun
                //Risc: endless loop when incorrect termination of */
                
                //Buffer new character and write complete string back
                stringBuilder.Append('/');
                Read();
                TextValue = stringBuilder.ToString();

                return COMMENT;
            }
            else // We are dealing with a single / symbol
            {
                CharValue = '/';
                
                //No another read, we already read the next character
                return CHARACTER;
            }
        }

        /// <summary>
        /// Determines if the character is a Layout Character
        /// Layout is whitespace, newline, tab, carriage return
        /// </summary>
        /// <param name="c">Character to check</param>
        /// <returns>IsLayout</returns>
        private bool IsLayout(int c)
        {
            return c == ' ' || c == '\n' || c == '\t' || c == '\r';
        }

        /// <summary>
        /// Determine if the character is a Letter character
        /// Letters are [a-z][A-Z]
        /// </summary>
        /// <param name="c">Character to check</param>
        /// <returns>IsLetter</returns>
        private bool IsLetter(int c)
        {
            return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z');
        }

        /// <summary>
        /// Determine if the character is a Numeric character
        /// Numeric is [0-9]
        /// </summary>
        /// <param name="c">Character to check</param>
        /// <returns>IsNumeric</returns>
        private bool IsNumeric(int c)
        {
            return c >= '0' && c <= '9';
        }



        /// <summary>
        /// Reads next character from stream
        /// </summary>
        private void Read()
        {
            PreviousCharacter = CurrentCharacter;
            CurrentCharacter = InputStream.Read();
            //Check for newline
            if (CurrentCharacter == '\n')
            {
                LineNumber++;
            }

        }

        

        #endregion
    }
}
