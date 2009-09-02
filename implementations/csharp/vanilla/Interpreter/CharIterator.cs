using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;

namespace Interpreter
{
    /// <summary>
    /// Class which contains some utils to make handling of strings better
    /// </summary>
    public class CharIterator
    {
        #region Private Members

        private char[] TextArray;
        private int Index = -1;

        #endregion

        #region Public Methods

        /// <summary>
        /// This method parses a string and filters out explicit spelled layout chars
        /// Accepted layout chars are \n, \t, \r and 
        /// </summary>
        /// <param name="text">Text to parse</param>
        /// <returns>Parsed text per line</returns>
        public String ParseText(String text)
        {
            //Reset index
            Index = -1;

            //Put text in buffer
            TextArray = text.ToCharArray();

            //Some buffers for escape detection
            String newString = "";
            char Previous = '\0';
            char Current = '\0';
            while (HasNext())
            {
                //Get current
                Current = NextChar();
                
                //Detect layout chars
                if (Current == '\\' && HasNext() && Peek(1) == '\\')
                {
                    MoveIndex(1);
                    newString += "\\";
                }
                else if (Current == '\\' && HasNext() && Peek(1) == 'n')
                {
                    MoveIndex(1);
                    newString += '\n';
                }
                else if (Current == '\\' && HasNext() && Peek(1) == 't')
                {
                    MoveIndex(1);
                    newString += '\t';
                }
                else if (Current == '\\' && HasNext() && Peek(1) == 'r')
                {
                    MoveIndex(1);
                    newString += '\r';
                }
                else if (Current == '\\' && HasNext() && Peek(1) == '\"')
                {
                    MoveIndex(1);
                    newString += "\"";
                }
                else if (Current == '&')
                {
                    newString += "&amp;"; //ampersand
                }
                else if (Current == '>')
                {
                    newString += "&gt;"; //greater than
                }
                else if(Current == '<')
                {
                    newString += "&lt;"; //less than
                }
                else if (Current == '\'')
                {
                    newString += "&apos;"; //apostrophe
                }
                else
                {
                    newString += Current;
                }


                Previous = Current;
            }

            return newString;
        }


        #endregion

        #region Private Methods

        private void MoveIndex(int offset)
        {
            if(HasNext(offset))
            {
                Index += offset;
            }
            else
            {
                throw new IndexOutOfRangeException("Index out of bound during MoveIndex!");
            }
        }

        private char NextChar()
        {
            Index++;
            if (Index > TextArray.Length)
            {
                throw new IndexOutOfRangeException("Index out of bound during retrieving NextChar!");
            }
            else
            {
                return TextArray[Index];
            }
        }

        private bool HasNext(int offset)
        {
            return ((this.Index + offset) < TextArray.Length);
        }

        private bool HasNext()
        {
            return HasNext(1);
        }

        private char Peek(int offset)
        {
            if (HasNext(offset))
            {
                return TextArray[(this.Index + offset)];
            }
            else
            {
                throw new IndexOutOfRangeException("Index of peek out of bound during text parsing!");
            }
        }

        #endregion
    }
}
