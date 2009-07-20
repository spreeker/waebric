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
using Parser.Ast.Predicates;

namespace Interpreter
{
    /// <summary>
    /// Class which visits syntax nodes and creates an XHTML document
    /// </summary>
    public class XHTMLVisitor : SyntaxNodeVisitor
    {
        #region Private Members

        //private StreamWriter Writer;                                            //Writer to write xhtml to
        private SymbolTable SymbolTable;                                        //Table which holds functions and variables
        private XHTMLElement Root;                                              //Root element of XHTMLTree
        private XHTMLElement Current;                                           //Pointer to current element in XHTMLTree
        private Dictionary<FunctionDefinition, SymbolTable> FunctionSymbolTable;//Stores symboltables per function
        private String TextValue = "";                                          //Buffer used for buffering values

        #endregion

        #region Public Methods

        /// <summary>
        /// Constructor which creates an XHTMLVisitor with specified outputstream and prefilled symboltable
        /// </summary>
        /// <param name="symbolTable">SymbolTable to use</param>
        /// 
        public XHTMLVisitor(SymbolTable symbolTable)
        {
            //Prepare members to start visiting ISyntaxNodes
            //Writer = outputStream;
            SymbolTable = symbolTable;
            //XHTMLWriter = new XHTMLStreamWriter(Writer, XHTMLStreamWriter.DocType.TRANSITIONAL);
            FunctionSymbolTable = new Dictionary<FunctionDefinition, SymbolTable>();
        }

        /// <summary>
        /// Interpret an functionDefinition
        /// </summary>
        /// <param name="functionDefinition">FunctionDefinition to interpret</param>
        public override void Visit(FunctionDefinition functionDefinition)
        {
            //Interpret statements
            XHTMLElement temp = Current;
            foreach(Statement statement in functionDefinition.GetStatements())
            {
                Current = temp;
                statement.AcceptVisitor(this);
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

                //Write tag with retrieved attributes
                AddTag(new XHTMLElement(tag, Current));
                
                //Visit the attributes
                foreach (Parser.Ast.Markup.Attribute attribute in markup.GetDesignator().GetAttributes())
                {
                    attribute.AcceptVisitor(this);
                }

                //Interpret arguments also as attributes
                String attributeValue = "";
                foreach (Argument argument in markup.GetArguments())
                {
                    if (argument.GetType() == typeof(AttrArgument))
                    {
                        //Interpret expression to retrieve value
                        ((AttrArgument)argument).GetExpression().AcceptVisitor(this);
                        
                        //Store attribute
                        String attr = ((AttrArgument)argument).GetIdentifier();
                        Current.AddAttribute(attr, TextValue);
                    }
                    else if (argument.GetType() == typeof(ExpressionArgument))
                    {
                        //Interpret expression
                        ((ExpressionArgument)argument).GetExpression().AcceptVisitor(this);
                        attributeValue += TextValue;
                        if (attributeValue == "undef")
                        {
                            attributeValue = "UNDEFINED";
                        }
                    }
                }
            }
        }

        /// <summary>
        /// Interpret ClassAttribute
        /// </summary>
        /// <param name="attribute">ClassAttribute to interpret</param>
        public override void Visit(ClassAttribute attribute)
        {   //Add class attribute
            Current.AddAttribute("class", attribute.GetClass());
        }

        /// <summary>
        /// Interpret WidthAttribute
        /// </summary>
        /// <param name="attribute">WidthAttribute to interpret</param>
        public override void Visit(WidthAttribute attribute)
        {   //Add height attribute
            Current.AddAttribute("width", attribute.GetWidth().ToString());
        }

        /// <summary>
        /// Interpret IdAttribute
        /// </summary>
        /// <param name="attribute">IdAttribute to interpret</param>
        public override void Visit(IdAttribute attribute)
        {   //Add id attribute
            Current.AddAttribute("id", attribute.GetId());
        }

        /// <summary>
        /// Interpret NameAttribute
        /// </summary>
        /// <param name="attribute">NameAttribute to interpret</param>
        public override void Visit(NameAttribute attribute)
        {   //Add name attribute
            Current.AddAttribute("name", attribute.GetName());
        }

        /// <summary>
        /// Interpret TypeAttribute
        /// </summary>
        /// <param name="attribute">TypeAttribute to interpret</param>
        public override void Visit(TypeAttribute attribute)
        {   //Add type attribute
            Current.AddAttribute("type", attribute.GetType());
        }
        
        /// <summary>
        /// Interpret Width_HeightAttribute
        /// </summary>
        /// <param name="attribute">Width_HeightAttribute to interpret</param>
        public override void Visit(Width_HeightAttribute attribute)
        {   //Add width and height attribute
            Current.AddAttribute("width", attribute.GetWidth().ToString());
            Current.AddAttribute("height", attribute.GetHeight().ToString());
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

        /// <summary>
        /// Interpret IfStatement
        /// </summary>
        /// <param name="statement">IfStatement to interpret</param>
        public override void Visit(IfStatement statement)
        {
            if (EvaluatePredicate(statement.GetPredicate()))
            {
                statement.GetTrueStatement().AcceptVisitor(this);
            }
        }

        /// <summary>
        /// Interpret IfElseStatement
        /// </summary>
        /// <param name="statement">IfElseStatement to interpret</param>
        public override void Visit(IfElseStatement statement)
        {
            if (EvaluatePredicate(statement.GetPredicate()))
            {
                statement.GetTrueStatement().AcceptVisitor(this);
            }
            else
            {
                statement.GetFalseStatement().AcceptVisitor(this);
            }
        }

        /// <summary>
        /// Interpret BlockStatement
        /// </summary>
        /// <param name="statement">BlockStatement to interpret</param>
        public override void Visit(BlockStatement statement)
        {
            XHTMLElement temp = Current;
            foreach(Statement currentStatement in statement.GetStatements())
            {
                Current = temp;
                currentStatement.AcceptVisitor(this);
            }
        }

        /// <summary>
        /// Interpret CommentStatement
        /// </summary>
        /// <param name="statement"></param>
        public override void Visit(CommentStatement statement)
        {
            //XHTMLWriter.WriteComment(statement.GetCommentString());
        }

        public XHTMLElement GetTree()
        {
            return Root;
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
            AddTag(new XHTMLElement("html", null));
            Current.AddAttribute("xmlns", "http://www.w3.org/1999/xhtml");
            Current.AddAttribute("lang", "en");
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

        /// <summary>
        /// Method which evaluates an predicate and returns true or false
        /// </summary>
        /// <param name="predicate">Predicate to evaluate</param>
        /// <returns>True if predicate is true, otherwise false</returns>
        private bool EvaluatePredicate(Predicate predicate)
        {
            if (predicate.GetType() == typeof(IsPredicate))
            {   //Evaluate Expression Predicate
                IsPredicate isPredicate = (IsPredicate)predicate;
                Expression expression = isPredicate.GetExpression();
                
                //If right type return true, otherwise false
                if (isPredicate.GetType().GetType() == typeof(StringType))
                {
                    return isPredicate.GetExpression().GetType() == typeof(TextExpression);
                }
                else if (isPredicate.GetType().GetType() == typeof(ListType))
                {
                    return isPredicate.GetExpression().GetType() == typeof(ListExpression);
                }
                else if (isPredicate.GetType().GetType() == typeof(RecordType))
                {
                    return isPredicate.GetExpression().GetType() == typeof(RecordExpression);
                }
                else
                {   //No match between types which could be checked, so false
                    return false;
                }
            }
            else if (predicate.GetType() == typeof(ExpressionPredicate))
            {   //Evaluate Expression Predicate
                ExpressionPredicate expressionPredicate = (ExpressionPredicate)predicate;
                Expression expression = expressionPredicate.GetExpression();

                if (expression.GetType() == typeof(FieldExpression))
                {   //Check if specific field exists in record (not null)
                    Expression expr = GetExpression((FieldExpression)expression);
                    return expr != null;
                }
                else if (expression.GetType() == typeof(VarExpression))
                {   //Check if specific variable is defined
                    VarExpression varExpr = (VarExpression)expression;
                    return SymbolTable.ContainsVariable(varExpr.GetVariableIdentifier());
                }
                else
                {   //Other expressions are always true, because they doesn't refer to something
                    return true;
                }
            }
            else if (predicate.GetType() == typeof(AndPredicate))
            {   //Evaluate And Predicate
                AndPredicate andPredicate = (AndPredicate)predicate;
                return EvaluatePredicate(andPredicate.GetLeftPredicate()) && EvaluatePredicate(andPredicate.GetLeftPredicate());
            }
            else if (predicate.GetType() == typeof(OrPredicate))
            {   //Evaluate Or Predicate
                OrPredicate orPredicate = (OrPredicate) predicate;
                return EvaluatePredicate(orPredicate.GetLeftPredicate()) || EvaluatePredicate(orPredicate.GetRightPredicate());
            }
            else if (predicate.GetType() == typeof(NotPredicate))
            {   //Evaluate Not Predicate
                NotPredicate notPredicate = (NotPredicate) predicate;
                return !EvaluatePredicate(notPredicate);
            }
            return false;
        }

        private void AddTag(XHTMLElement element)
        {
            //If no root element, new element is root
            if (Root == null)
            {
                Root = element;
                Current = Root;
                return;
            }
            
            //Add element as child of current element
            Current.AddChild(element);
            //Set current to last added element
            Current = element;
        }

        #endregion

    }
}
