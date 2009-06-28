using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using Lexer.Tokenizer;
using Parser.Ast.Site;

namespace Parser
{
    /// <summary>
    /// Parser which parses a site
    /// </summary>
    public class SiteParser : AbstractParser
    {
        #region Private Members

        private MarkupParser markupParser;

        #endregion

        public SiteParser(TokenIterator tokenStream) : base(tokenStream)
        {
            //Create parsers
            markupParser = new MarkupParser(tokenStream);
        }

        /// <summary>
        /// Parse a site
        /// </summary>
        /// <returns></returns>
        public Site ParseSite()
        {
            //Create new site
            Site site = new Site();

            //Parse one or more mappings
            site.AddMappings(ParseMappings());

            //Skip END tag
            NextToken("end", "site end", "end");

            return site;
        }

        /// <summary>
        /// Parse mappings
        /// </summary>
        /// <returns>A list of parsed mappings</returns>
        public List<Mapping> ParseMappings()
        {
            List<Mapping> MappingList = new List<Mapping>();

            while (TokenStream.HasNext()) //Parse mappings
            {
                MappingList.Add(ParseMapping());

                if (TokenStream.HasNext()) //Check if another mapping comes
                {
                    if (MatchValue(TokenStream.Peek(1).GetValue().ToString(), Waebric.WaebricKeyword.END.ToString()))
                    {
                        break; //END found so stop searching
                    }
                    else
                    {
                        NextToken(";", "Mapping ; Mapping", ';'); //Match separator
                    }
                }                
            }

            return MappingList;
        }

        /// <summary>
        /// Parse one mapping
        /// </summary>
        /// <returns>Parsed mapping</returns>
        public Mapping ParseMapping()
        {
            Mapping mapping = new Mapping();
            
            // Parse Path
            mapping.SetPath(ParsePath());
            
            // Skip : symbol
            NextToken(":", "path : mapping", ':');

            // Parse Markup
            mapping.SetMarkup(markupParser.ParseMarkup());
            
            return mapping;
        }

        /// <summary>
        /// Parse a path
        /// </summary>
        /// <returns>Parsed path</returns>
        public Path ParsePath()
        {
            Path path = new Path();
           
            //Determine if we have directories in path or just a filename
            if (TokenStream.Peek(2).GetValue().ToString() == ".")
            {   //Just a single filename to parse
                path.SetFilename(ParseFileName());
            }
            else
            {   //Directory and filename
                path.SetDirectoryName(ParseDirectoryName());
                path.SetFilename(ParseFileName());
            }

            return path;
        }

        /// <summary>
        /// Parse a directoryname
        /// </summary>
        /// <returns>Parsed DirName</returns>
        public DirName ParseDirectoryName()
        {
            DirName directoryName = new DirName();
            
            directoryName.SetDirectory(ParseDirectory());
            
            return directoryName;
        }

        /// <summary>
        /// Parse FileName
        /// </summary>
        /// <returns>Parsed FileName</returns>
        public FileName ParseFileName()
        {
            FileName filename = new FileName();

            //Filename
            NextToken("filename", "filename.ext");
            filename.SetName(new PathElement(CurrentToken.GetValue().ToString()));
            
            //Period (between filename and extension)
            NextToken(".", "filename.ext", '.');
            
            //Extension
            NextToken("extension", "filename.ext");
            filename.SetFileExtension(new FileExt(CurrentToken.GetValue().ToString()));

            return filename;
        }

        /// <summary>
        /// Parse Directory
        /// </summary>
        /// <returns>Parsed Directory</returns>
        public Directory ParseDirectory()
        {
            Directory directory = new Directory();
            
            //Parse path elements
            while (TokenStream.HasNext())
            {
                if(TokenStream.Peek(2).GetValue().ToString() == ".")
                { //End of directory, filename starts here
                    break;
                }
                PathElement element = new PathElement();
                NextToken("directory", "directory/filename.ext", TokenType.IDENTIFIER);
                element.SetPathElement(CurrentToken.GetValue().ToString());
                directory.AddDirectoryElement(element);

                //Skip / or \
                NextToken("/ or \\", "directory/FileName.ext", TokenType.SYMBOL);
            }
            return directory;
        }
    }
}
