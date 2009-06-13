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

        MarkupParser markupParser;

        #endregion

        public SiteParser(TokenIterator tokenStream, List<Exception> exceptionList) : base(tokenStream, exceptionList)
        {
            //Create parsers
            MarkupParser markupParser = new MarkupParser(tokenStream, exceptionList);
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
            while (TokenStream.HasNext()) 
            {   // Get all path elements
                path.SetDirectoryName(ParseDirectoryName());
                path.SetFilename(ParseFileName());

                //TODO: ADD BREAKING CONDITION HERE!!!!
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
            return null;
        }

        /// <summary>
        /// Parse Directory
        /// </summary>
        /// <returns>Parsed Directory</returns>
        public Directory ParseDirectory()
        {
            Directory directory = new Directory();
            //Parse all path elements here!!!


            return directory;
        }
        
        

    }
}
