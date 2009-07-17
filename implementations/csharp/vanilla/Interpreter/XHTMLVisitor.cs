using System;
using System.Collections.Generic;
using System.Linq;
using System.Text;
using System.Web.UI;
using Parser.Ast;
using System.IO;
using Parser.Ast.Functions;
using Parser.Ast.Markup;
using Common;
using Parser.Ast.Statements;
using Parser.Ast.Expressions;

namespace Interpreter
{
    /// <summary>
    /// Class which visits syntax nodes and creates an XHTML document
    /// </summary>
    public class XHTMLVisitor : SyntaxNodeVisitor
    {
        #region Private Members

        private StreamWriter Writer;                                            //Writer to write xhtml to
        private SymbolTable SymbolTable;                                        //Table which holds functions and variables
        private XHTMLStreamWriter XHTMLWriter;                                  //Writer wich writes xhtml
        private bool StartTagWrited = false;                                    //Used to determine if start of tag has been writed
        private Dictionary<FunctionDefinition, SymbolTable> FunctionSymbolTable;//Stores symboltables per function
        private String TextValue = "";                                          //Buffer used for buffering values

        #endregion

        #region Public Methods

        /// <summary>
        /// Constructor which creates an XHTMLVisitor with specified outputstream and prefilled symboltable
        /// </summary>
        /// <param name="outputStream">Stream to write xhtml to</param>
        /// <param name="symbolTable">SymbolTable to use</param>
        public XHTMLVisitor(StreamWriter outputStream, SymbolTable symbolTable)
        {
            //Prepare members to start visiting ISyntaxNodes
            Writer = outputStream;
            SymbolTable = symbolTable;
            XHTMLWriter = new XHTMLStreamWriter(Writer, XHTMLStreamWriter.DocType.TRANSITIONAL);
            FunctionSymbolTable = new Dictionary<FunctionDefinition, SymbolTable>();
        }

        /// <summary>
        /// Interpret an functionDefinition
        /// </summary>
        /// <param name="functionDefinition">FunctionDefinition to interpret</param>
        public override void Visit(FunctionDefinition functionDefinition)
        {
            //Create XHTML tag when this function is root function
            bool root = false;
		    if(functionDefinition.GetStatements().Count > 0 && ! StartTagWrited)
            {
			    WriteBeginXHTMLTag();
                root = true;
		    }

            //Interpret statements
            foreach(Statement statement in functionDefinition.GetStatements())
            {
                statement.AcceptVisitor(this);
            }

            if (root)
            {   //Write html end teg, because root function has been interpreted
                XHTMLWriter.WriteCloseTag("html");
            }
        }

        /// <summary>
        /// Interpret an Markup
        /// </summary>
        /// <param name="markup">Markup to interpret</param>
        public override void Visit(Markup markup)
        {
            if (IsCall(markup))
            {   //We are calling a function                
                FunctionDefinition functionDefinition = SymbolTable.GetFunctionDefinition(markup.GetDesignator().GetIdentifier());

                //Create SymbolTable for specific function
                SymbolTable tempSymbolTable = SymbolTable;
                SymbolTable = new SymbolTable(GetSymbolTableOfFunction(functionDefinition));

                //Store arguments in SymbolTable as variables
                int parameternr = 0;
                foreach (Argument argument in markup.GetArguments())
                {
                    //Store parameters in symboltable
                    if (argument.GetType() == typeof(ExpressionArgument))
                    {
                        Formal parameter = (Formal) functionDefinition.GetFormals().Get(parameternr);
                        SymbolTable.AddVariableDefinition(parameter.GetIdentifier(), ((ExpressionArgument)argument).GetExpression());
                        parameternr++;
                    }
                }

                //Visit functiondefinition
                functionDefinition.AcceptVisitor(this);

                //Go back to parent SymbolTable
                SymbolTable = tempSymbolTable;
            }
            else
            {   //Dealing with an Tag
                String tag = markup.GetDesignator().GetIdentifier();
                
                //Visit the attributes
                foreach (Parser.Ast.Markup.Attribute attribute in markup.GetDesignator().GetAttributes())
                {
                    attribute.AcceptVisitor(this);
                }

                //Interpret arguments also as attributes
                foreach (Argument argument in markup.GetArguments())
                {
                    if (argument.GetType() == typeof(AttrArgument))
                    {
                        ((AttrArgument)argument).GetExpression().AcceptVisitor(this);
                        

                    }
                    else if (argument.GetType() == typeof(ExpressionArgument))
                    {
                        ((ExpressionArgument)argument).GetExpression().AcceptVisitor(this);
                    }
                }

                //Write tag with retrieved attributes
                XHTMLWriter.WriteTag(tag);
            }
        }

        /// <summary>
        /// Interpret ClassAttribute
        /// </summary>
        /// <param name="attribute">ClassAttribute to interpret</param>
        public override void Visit(ClassAttribute attribute)
        {   //Add class attribute
            XHTMLWriter.AddAttribute("class", attribute.GetClass());
        }

        /// <summary>
        /// Interpret WidthAttribute
        /// </summary>
        /// <param name="attribute">WidthAttribute to interpret</param>
        public override void Visit(WidthAttribute attribute)
        {   //Add height attribute
            XHTMLWriter.AddAttribute("width", attribute.GetWidth().ToString());
        }

        /// <summary>
        /// Interpret IdAttribute
        /// </summary>
        /// <param name="attribute">IdAttribute to interpret</param>
        public override void Visit(IdAttribute attribute)
        {   //Add id attribute
            XHTMLWriter.AddAttribute("id", attribute.GetId());
        }

        /// <summary>
        /// Interpret NameAttribute
        /// </summary>
        /// <param name="attribute">NameAttribute to interpret</param>
        public override void Visit(NameAttribute attribute)
        {   //Add name attribute
            XHTMLWriter.AddAttribute("name", attribute.GetName());
        }

        /// <summary>
        /// Interpret TypeAttribute
        /// </summary>
        /// <param name="attribute">TypeAttribute to interpret</param>
        public override void Visit(TypeAttribute attribute)
        {   //Add type attribute
            XHTMLWriter.AddAttribute("type", attribute.GetType());
        }
        
        /// <summary>
        /// Interpret Width_HeightAttribute
        /// </summary>
        /// <param name="attribute">Width_HeightAttribute to interpret</param>
        public override void Visit(Width_HeightAttribute attribute)
        {   //Add width and height attribute
            XHTMLWriter.AddAttribute("width", attribute.GetWidth().ToString());
            XHTMLWriter.AddAttribute("height", attribute.GetHeight().ToString());
        }

        /// <summary>
        /// Interpret SymExpression
        /// </summary>
        /// <param name="expression">SymExpression to interpret</param>
        public override void Visit(SymExpression expression)
        {
            TextValue = expression.GetSym();
        }

        /// <summary>
        /// Interpret TextExpression
        /// </summary>
        /// <param name="expression">TextExpression to interpret</param>
        public override void Visit(TextExpression expression)
        {
            TextValue = expression.GetText();
        }

        /// <summary>
        /// Interpret NumExpression
        /// </summary>
        /// <param name="expression">NumExpression to interpret</param>
        public override void Visit(NumExpression expression)
        {
            TextValue = expression.GetNum().ToString();
        }

        /// <summary>
        /// Interpret CatExpression
        /// </summary>
        /// <param name="expression">CatExpression to interpret</param>
        public override void Visit(CatExpression expression)
        {
            //Concatenate two expressions
            String tempExpressionText = "";
            
            //Visit left 
            expression.GetLeftExpression().AcceptVisitor(this);
            tempExpressionText += TextValue;

            //Visit right
            expression.GetRightExpression().AcceptVisitor(this);
            tempExpressionText += TextValue;

            TextValue = tempExpressionText;
        }

        /// <summary>
        /// Interpret VarExpression
        /// </summary>
        /// <param name="expression">VarExpression to interpret</param>
        public override void Visit(VarExpression expression)
        {
            //Check if variable does exist
            String varName = expression.GetVariableIdentifier();
            Expression check = SymbolTable.GetVariableDefinition(varName);
            //Maybe an additional check should be placed here!!!! TO IMPLEMENT


            if (check != null)
            {   //Visit variable to retrieve current value
                check.AcceptVisitor(this);
            }
            else
            {
                TextValue = "undef";
            }

        }

        /// <summary>
        /// Interpret FieldExpression
        /// </summary>
        /// <param name="expression">FieldExpression to interpret</param>
        public override void Visit(FieldExpression expression)
        {
            //Retrieve expression of field
            Expression expr = GetExpression(expression);
            if (expr != null)
            {
                expr.AcceptVisitor(this);
            }
            else
            {
                TextValue = "undef";
            }
        }

        /// <summary>
        /// Interpret ListExpression
        /// </summary>
        /// <param name="expression">ListExpression to interpret</param>
        public override void Visit(ListExpression expression)
        {
            //Convert list to textvalue
            String tempList = "[";
            for(int i = 0; i < expression.GetExpressions().Count; i++)
            {
                Expression currentExpression = (Expression) expression.GetExpressions().Get(i);

                //Store Text and SymExpressions between " to see , as real separator
                if (currentExpression.GetType() == typeof(TextExpression) || currentExpression.GetType() == typeof(SymExpression))
                {
                    tempList += "\"";
                }

                //Store value in tempList
                currentExpression.AcceptVisitor(this);
                tempList += TextValue;

                if (currentExpression.GetType() == typeof(TextExpression) || currentExpression.GetType() == typeof(SymExpression))
                {
                    tempList += "\"";
                }

                //Add , seperator
                if (i != (expression.GetExpressions().Count - 1))
                {
                    tempList += ",";
                }
            }

            tempList += "]";
            TextValue = tempList;
        }

        /// <summary>
        /// Interpret RecordExpression
        /// </summary>
        /// <param name="expression">RecordExpression to interpret</param>
        public override void Visit(RecordExpression expression)
        {
            //convert record to textvalue
            String tempRecord = "{";
            for (int i = 0; i < expression.GetRecords().Count; i++)
            {
                KeyValuePair pair = (KeyValuePair)expression.GetRecords().Get(i);

                //Convert KeyValuePair to textual representation
                tempRecord += pair.GetKey() + ":";
                pair.GetValue().AcceptVisitor(this);
                tempRecord += TextValue;

                //Add seperator
                if (i != (expression.GetRecords().Count - 1))
                {
                    tempRecord += ",";
                }
            }

            tempRecord += "}";
            TextValue = tempRecord;
        }

        #endregion

        #region Private Methods

        /// <summary>
        /// Determine if an markup is an CallMarkup
        /// </summary>
        /// <param name="markup">Markup to check</param>
        /// <returns>IsCall</returns>
        private bool IsCall(Markup markup)
        {
            return SymbolTable.ContainsFunction(markup.GetDesignator().GetIdentifier());
        }

        private void WriteBeginXHTMLTag()
        {
            //Write html tag with xmlns and lang attribute by default
            StartTagWrited = true;
            XHTMLWriter.AddAttribute("xmlns", "http://www.w3.org/1999/xhtml");
            XHTMLWriter.AddAttribute("lang", "en");
            XHTMLWriter.WriteTag("html");
        }

        private SymbolTable GetSymbolTableOfFunction(FunctionDefinition function)
        {
            if (FunctionSymbolTable.ContainsKey(function))
            {
                return FunctionSymbolTable[function];
            }
            return SymbolTable;
        }

        private Expression GetExpression(FieldExpression expression)
        {
            Expression expr = expression.GetExpression();
            //Get real expression, not a variable
            while (expr.GetType() == typeof(VarExpression))
            {
                expr = SymbolTable.GetVariableDefinition(((VarExpression)expr).GetVariableIdentifier());
            }

            //Get specific record from recordExpression
            if (expr.GetType() == typeof(RecordExpression))
            {
                RecordExpression record = (RecordExpression)expr;
                ISyntaxNode[] recordArray = record.GetRecords().ToArray();
                foreach (KeyValuePair pair in recordArray)
                {
                    if (pair.GetKey() == expression.GetIdentifier())
                    {
                        return pair.GetValue();
                    }
                }
            }
            return null;
        }

        #endregion

    }
}
