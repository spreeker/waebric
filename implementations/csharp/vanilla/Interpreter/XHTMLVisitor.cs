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
using System.Collections;
using Parser.Ast.Embedding;

namespace Interpreter
{
    /// <summary>
    /// Class which visits syntax nodes and creates an XHTML document
    /// </summary>
    public class XHTMLVisitor : SyntaxNodeVisitor
    {
        #region Private Members

        private SymbolTable SymbolTable;                                        //Table which holds functions and variables
        private XHTMLElement Root;                                              //Root element of XHTMLTree
        private XHTMLElement Current;                                           //Pointer to current element in XHTMLTree
        private Dictionary<FunctionDefinition, SymbolTable> FunctionSymbolTable;//Stores symboltables per function
        private String TextValue = "";                                          //Buffer used for buffering values
        private Stack<ISyntaxNode> YieldStack;                                  //Stack containing nodes which are referred by a yield

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
            SymbolTable = symbolTable;
            FunctionSymbolTable = new Dictionary<FunctionDefinition, SymbolTable>();
            YieldStack = new Stack<ISyntaxNode>();
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
                    if (argument is ExpressionArgument)
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
                AddElement(new XHTMLElement(tag, Current));
                
                //Visit the attributes
                foreach (Parser.Ast.Markup.Attribute attribute in markup.GetDesignator().GetAttributes())
                {
                    attribute.AcceptVisitor(this);
                }

                //Interpret arguments also as attributes
                String attributeValue = "";
                foreach (Argument argument in markup.GetArguments())
                {
                    if (argument is AttrArgument)
                    {
                        //Interpret expression to retrieve value
                        ((AttrArgument)argument).GetExpression().AcceptVisitor(this);
                        
                        //Store attribute
                        String attr = ((AttrArgument)argument).GetIdentifier();
                        Current.AddAttribute(attr, TextValue);
                    }
                    else if (argument is ExpressionArgument)
                    {
                        //Interpret expression
                        ((ExpressionArgument)argument).GetExpression().AcceptVisitor(this);
                        attributeValue += TextValue;
                        if (TextValue == "undef")
                        {
                            TextValue = "UNDEFINED";
                        }
                        //Store value
                        if (attributeValue != "" && TextValue != "")
                        {
                            attributeValue += " ";
                        }
                        attributeValue += TextValue;
                    }
                }

                if (attributeValue != "")
                {   //Add value attribute
                    Current.AddAttribute("value", attributeValue);
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
            
            //If reference has been made to parent variable with same name, take parent reference
            if (check == expression && SymbolTable.GetParentSymbolTable() != null)
            {
                check = SymbolTable.GetParentSymbolTable().GetVariableDefinition(varName);
            }

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
                if (currentExpression is TextExpression || currentExpression is SymExpression)
                {
                    tempList += "\"";
                }

                //Store value in tempList
                currentExpression.AcceptVisitor(this);
                tempList += TextValue;

                if (currentExpression is TextExpression || currentExpression is SymExpression)
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
        /// Interpret EchoExpressionStatement
        /// </summary>
        /// <param name="statement">EchoExpressionStatement to interpret</param>
        public override void Visit(EchoExpressionStatement statement)
        {
            statement.GetExpression().AcceptVisitor(this);
            
            //Add just an tag to current as parent
            XHTMLElement echoElement = new XHTMLElement(TextValue, Current);
            echoElement.SetTagState(false);

            Current.AddChild(echoElement);
            
        }

        /// <summary>
        /// Interpret EchoEmbeddingStatement
        /// </summary>
        /// <param name="statement">EchoEmbeddingStatement to interpret</param>
        public override void Visit(EchoEmbeddingStatement statement)
        {
            statement.GetEmbedding().AcceptVisitor(this);
        }

        /// <summary>
        /// Interpret CdataStatement
        /// </summary>
        /// <param name="statement">CdataStatement to interpret</param>
        public override void Visit(CdataStatement statement)
        {
            statement.GetExpression().AcceptVisitor(this);
            AddElement(new XHTMLElement("cdata", Current));
            Current.AddContent(TextValue);
        }

        /// <summary>
        /// Interpret CommentStatement
        /// </summary>
        /// <param name="statement">CommentStatement to interpret</param>
        public override void Visit(CommentStatement statement)
        {
            AddElement(new XHTMLElement("comment", Current));
            Current.AddContent(statement.GetCommentString());
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
        /// Interpret LetStatement
        /// </summary>
        /// <param name="statement">LetStatement to interpret</param>
        public override void Visit(LetStatement statement)
        {
            //Create SymbolTable's for each assignment to let referencing work properly
            foreach (Assignment asgn in statement.GetAssignments())
            {
                SymbolTable = new SymbolTable(SymbolTable);
                asgn.AcceptVisitor(this);
            }

            //Interpret statements
            foreach (Statement stmt in statement.GetStatements())
            {
                stmt.AcceptVisitor(this);
            }

            //Go back to scope outside let statement
            foreach(Assignment asgn in statement.GetAssignments())
            {
                SymbolTable = SymbolTable.GetParentSymbolTable();
            }
        }

        /// <summary>
        /// Interpret EachStatement
        /// </summary>
        /// <param name="statement">EachStatement to interpret</param>
        public override void Visit(EachStatement statement)
        {
            Expression expr = statement.GetExpression();
            //Different looping with different expression types
            if(expr is ListExpression)
            {   //Iterate through list
                ListExpression listExpression = (ListExpression)expr;
                
                //Iterate through list with expression
                XHTMLElement temp = Current;
                foreach (Expression currentExpr in listExpression.GetExpressions())
                {
                    Current = temp;
                    //New scope
                    SymbolTable = new SymbolTable(SymbolTable);

                    //Define variable and interpret statement with this scope
                    SymbolTable.AddVariableDefinition(statement.GetIdentifier(), currentExpr);
                    statement.GetStatement().AcceptVisitor(this);

                    //Return to parent scope
                    SymbolTable = SymbolTable.GetParentSymbolTable();
                }
            }
            else if(expr is FieldExpression)
            {   //Get expression in referenced record
                Expression expression = GetExpression((FieldExpression)expr);

                //Visit each for expression
                EachStatement eachStatement = new EachStatement();
                eachStatement.SetExpression(expression);
                eachStatement.SetStatement(statement.GetStatement());
                eachStatement.SetIdentifier(statement.GetIdentifier());
                Visit(eachStatement);
            }
            else if(expr is VarExpression)
            {   //Get expression of variable
                Expression expression = SymbolTable.GetVariableDefinition(((VarExpression)expr).GetVariableIdentifier());

                //Visit each for expression
                EachStatement eachStatement = new EachStatement();
                eachStatement.SetExpression(expression);
                eachStatement.SetStatement(statement.GetStatement());
                eachStatement.SetIdentifier(statement.GetIdentifier());
                Visit(eachStatement);
            }
        }

        /// <summary>
        /// Interpret MarkupEmbeddingStatement
        /// </summary>
        /// <param name="statement">MarkupEmbeddingStatement to interpret</param>
        public override void Visit(MarkupEmbeddingStatement statement)
        {
            //Iterate through Markup+
            ISyntaxNode[] MarkupArray = statement.GetMarkups().ToArray();
            for (int i = 0; i <= (MarkupArray.Length - 1); i++)
            {
                if (IsMarkupCall((Markup)MarkupArray[i]))
                {
                    //Check if called function contains an yield, if so, add remaining markups/expression to yield stack
                    String functionIdentifier = ((Markup)MarkupArray[i]).GetDesignator().GetIdentifier();
                    if (NodeContainsYield(SymbolTable.GetFunctionDefinition(functionIdentifier)))
                    {
                        //Get remaining markups
                        NodeList nonInterpretedMarkups = new NodeList();
                        for (int j = i + 1; j <= (MarkupArray.Length - 1); j++)
                        {
                            nonInterpretedMarkups.Add(MarkupArray[j]);
                        }
                        //Create new MarkupExpressionStatement and push it to stack
                        MarkupEmbeddingStatement markupEmbeddingStatement = new MarkupEmbeddingStatement();
                        markupEmbeddingStatement.SetMarkups(nonInterpretedMarkups);
                        markupEmbeddingStatement.SetEmbedding(statement.GetEmbedding());
                        YieldStack.Push(markupEmbeddingStatement);
                    }
                    //Interpret markup
                    ((Markup)MarkupArray[i]).AcceptVisitor(this);
                    return;
                }
                else
                {   //Interpret Tag
                    ((Markup)MarkupArray[i]).AcceptVisitor(this);
                }
            }
            
            //Interpret Embedding
            statement.GetEmbedding().AcceptVisitor(this);
        }

        /// <summary>
        /// Interpret MarkupExpressionStatement
        /// </summary>
        /// <param name="statement">MarkupExpressionStatement to interpret</param>
        public override void Visit(MarkupExpressionStatement statement)
        {
            //Iterate through Markup+
            ISyntaxNode[] MarkupArray = statement.GetMarkups().ToArray();
            for(int i = 0; i <= (MarkupArray.Length - 1); i++)
            {
                if(IsMarkupCall((Markup)MarkupArray[i]))
                {
                    //Check if called function contains an yield, if so, add remaining markups/expression to yield stack
                    String functionIdentifier = ((Markup)MarkupArray[i]).GetDesignator().GetIdentifier();
                    if(NodeContainsYield(SymbolTable.GetFunctionDefinition(functionIdentifier)))
                    {
                        //Get remaining markups
                        NodeList nonInterpretedMarkups = new NodeList();
                        for(int j = i; j <= (MarkupArray.Length - 1); j++)
                        {
                            nonInterpretedMarkups.Add(MarkupArray[j]);
                        }
                        //Create new MarkupExpressionStatement and push it to stack
                        MarkupExpressionStatement markupExpressionStatement = new MarkupExpressionStatement();
                        markupExpressionStatement.SetMarkups(nonInterpretedMarkups);
                        markupExpressionStatement.SetExpression(statement.GetExpression());
                        YieldStack.Push(markupExpressionStatement);
                    }
                    //Interpret markup
                    ((Markup)MarkupArray[i]).AcceptVisitor(this);
                    return;
                }
                else
                {   //Interpret Tag
                    ((Markup)MarkupArray[i]).AcceptVisitor(this);
                }
            }
            
            //Interpret expression
            statement.GetExpression().AcceptVisitor(this);
            Current.AddContent(TextValue);
        }

        /// <summary>
        /// Interpret MarkupMarkupStatement
        /// </summary>
        /// <param name="statement">MarkupMarkupStatement to interpret</param>
        public override void Visit(MarkupMarkupStatement statement)
        {
            //Iterate through Markup+
            ISyntaxNode[] MarkupArray = statement.GetMarkups().ToArray();
            for (int i = 0; i <= (MarkupArray.Length - 1); i++)
            {
                if (IsMarkupCall((Markup)MarkupArray[i]))
                {
                    //Check if called function contains an yield, if so, add remaining markups/expression to yield stack
                    String functionIdentifier = ((Markup)MarkupArray[i]).GetDesignator().GetIdentifier();
                    if (NodeContainsYield(SymbolTable.GetFunctionDefinition(functionIdentifier)))
                    {
                        //Get remaining markups
                        NodeList nonInterpretedMarkups = new NodeList();
                        for (int j = i + 1; j <= (MarkupArray.Length - 1); j++)
                        {
                            nonInterpretedMarkups.Add(MarkupArray[j]);
                        }
                        //Create new MarkupExpressionStatement and push it to stack
                        MarkupMarkupStatement markupMarkupStatement = new MarkupMarkupStatement();
                        markupMarkupStatement.SetMarkups(nonInterpretedMarkups);
                        markupMarkupStatement.SetMarkup(statement.GetMarkup());
                        YieldStack.Push(markupMarkupStatement);
                    }
                    //Interpret markup
                    ((Markup)MarkupArray[i]).AcceptVisitor(this);
                    return;
                }
                else
                {   //Interpret Tag
                    ((Markup)MarkupArray[i]).AcceptVisitor(this);
                }
            }

            //Interpret markup
            statement.GetMarkup().AcceptVisitor(this);
        }

        /// <summary>
        /// Interpret MarkupStatement
        /// </summary>
        /// <param name="statement">MarkupStatement to interpret</param>
        public override void Visit(MarkupStatement statement)
        {
            //Determine if markup is a call
            if(IsCall(statement.GetMarkup()))
            {
                String functionIdentifier = statement.GetMarkup().GetDesignator().GetIdentifier();
                if (NodeContainsYield(SymbolTable.GetFunctionDefinition(functionIdentifier)))
                {   //Store null element, because there is nothing to yield in this statement
                    YieldStack.Push(null);
                }
            }

            //Interpret markup
            statement.GetMarkup().AcceptVisitor(this);
        }

        /// <summary>
        /// Interpret MarkupStatStatement
        /// </summary>
        /// <param name="statement">MarkupStatStatement to interpret</param>
        public override void Visit(MarkupStatStatement statement)
        {
            //Iterate through Markup+
            ISyntaxNode[] MarkupArray = statement.GetMarkups().ToArray();
            for (int i = 0; i <= (MarkupArray.Length - 1); i++)
            {
                if (IsMarkupCall((Markup)MarkupArray[i]))
                {
                    //Check if called function contains an yield, if so, add remaining markups/expression to yield stack
                    String functionIdentifier = ((Markup)MarkupArray[i]).GetDesignator().GetIdentifier();
                    if (NodeContainsYield(SymbolTable.GetFunctionDefinition(functionIdentifier)))
                    {
                        //Get remaining markups
                        NodeList nonInterpretedMarkups = new NodeList();
                        for (int j = i+1; j <= (MarkupArray.Length - 1); j++)
                        {
                            nonInterpretedMarkups.Add(MarkupArray[j]);
                        }
                        //Create new MarkupExpressionStatement and push it to stack
                        MarkupStatStatement markupStatStatement = new MarkupStatStatement();
                        markupStatStatement.SetMarkups(nonInterpretedMarkups);
                        markupStatStatement.SetStatement(statement.GetStatement());
                        YieldStack.Push(markupStatStatement);
                    }
                    //Interpret markup
                    ((Markup)MarkupArray[i]).AcceptVisitor(this);
                    return;
                }
                else
                {   //Interpret Tag
                    ((Markup)MarkupArray[i]).AcceptVisitor(this);
                }
            }
            
            //Interpret statement
            statement.GetStatement().AcceptVisitor(this);
        }

        /// <summary>
        /// Interpret YieldStatement
        /// </summary>
        /// <param name="statement">YieldStatement to interpret</param>
        public override void Visit(YieldStatement statement)
        {
            if (YieldStack.Count == 0)
            {
                return;
            }
            
            //Pop from YieldStack and lets interpet it
            ISyntaxNode node = YieldStack.Pop();

            if (node != null)
            {
                //Let's copy yieldstack, because there are possible yields in the yield. 
                Stack<ISyntaxNode> tempYieldStack = new Stack<ISyntaxNode>();
                List<ISyntaxNode> yieldList = YieldStack.ToList();
                foreach (ISyntaxNode yieldNode in yieldList)
                {
                    tempYieldStack.Push(yieldNode);
                }

                //Lets interpret it
                node.AcceptVisitor(this);

                //Add some content when node is an expression or embedding
                if (node is Expression || node is Embedding)
                {
                    Current.AddContent(TextValue);
                }

                //Restore YieldStack in original shape before interpreting
                YieldStack = tempYieldStack;
            }
        }

        /// <summary>
        /// Interpret FuncBindAssignment
        /// </summary>
        /// <param name="assignment">FuncBindAssignment to interpret</param>
        public override void Visit(FuncBindAssignment assignment)
        {   //Make a function of this binding and add it to SymbolTable
            FunctionDefinition functionDefinition = new FunctionDefinition();
            functionDefinition.SetIdentifier(assignment.GetIdentifier());

            //Convert identifiers to formals
            foreach (String id in assignment.GetIdentifiers())
            {
                Formal frml = new Formal();
                frml.SetIdentifier(id);
                functionDefinition.AddFormal(frml);
            }

            //Create new SymbolTable for function
            FunctionSymbolTable.Add(functionDefinition, (SymbolTable)SymbolTable.Clone());
           
            //Add function to SymbolTable
            SymbolTable.AddFunctionDefinition(functionDefinition);
        }

        /// <summary>
        /// Interpret VarBindAssignment
        /// </summary>
        /// <param name="assignment">VarBindAssignment to interpret</param>
        public override void Visit(VarBindAssignment assignment)
        {   //Add variable to SymbolTable
            SymbolTable.AddVariableDefinition(assignment.GetIdentifier(), assignment.GetExpression());
        }

        /// <summary>
        /// Interpret Embedding
        /// </summary>
        /// <param name="embedding">Embedding to interpret</param>
        public override void Visit(Embedding embedding)
        {
            //Add content of pretext
            Current.AddContent(embedding.GetPreText().GetText());
            
            //Interpret Embed and TextTail
            embedding.GetEmbed().AcceptVisitor(this);
            embedding.GetTextTail().AcceptVisitor(this);
        }

        /// <summary>
        /// Interpret MarkupEmbed
        /// </summary>
        /// <param name="embed">MarkupEmbed to interpret</param>
        public override void Visit(MarkupEmbed embed)
        {
            //Structure is same as MarkupMarkupStatement, so convert and interpret
            MarkupMarkupStatement markupMarkupStatement = new MarkupMarkupStatement();
            markupMarkupStatement.SetMarkups(embed.GetMarkups());
            markupMarkupStatement.SetMarkup(embed.GetMarkup());
            markupMarkupStatement.AcceptVisitor(this);
        }

        /// <summary>
        /// Interpret ExpressionEmbed
        /// </summary>
        /// <param name="embed">ExpressionEmbed to interpret</param>
        public override void Visit(ExpressionEmbed embed)
        {
            //Structure is same as MarkupExpressionStatement, so convert and interpret
            MarkupExpressionStatement markupExpressionStatement = new MarkupExpressionStatement();
            markupExpressionStatement.SetMarkups(embed.GetMarkups());
            markupExpressionStatement.SetExpression(embed.GetExpression());
            markupExpressionStatement.AcceptVisitor(this);
        }

        /// <summary>
        /// Interpret MidTextTail
        /// </summary>
        /// <param name="textTail">MidTextTail to Interpret</param>
        public override void Visit(MidTextTail textTail)
        {
            Current.AddContent(textTail.GetMidText().GetText());
            textTail.GetEmbed().AcceptVisitor(this);
            textTail.GetTextTail().AcceptVisitor(this);
        }

        /// <summary>
        /// Interpret PostTextTail
        /// </summary>
        /// <param name="textTail">PostTextTail to interpret</param>
        public override void Visit(PostTextTail textTail)
        {
            Current.AddContent(textTail.GetPostText().GetText());
        }

        /// <summary>
        /// Get XHTML tree representation
        /// </summary>
        /// <returns>XHTMLElement which is root of the tree</returns>
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

        /// <summary>
        /// Get scoped SymbolTable of specific function
        /// </summary>
        /// <param name="function">FunctionDefinition</param>
        /// <returns>SymbolTable of specific function</returns>
        private SymbolTable GetSymbolTableOfFunction(FunctionDefinition function)
        {
            if (FunctionSymbolTable.ContainsKey(function))
            {
                return FunctionSymbolTable[function];
            }
            return SymbolTable;
        }

        /// <summary>
        /// Retrieve concrete expression of an fieldexpression
        /// </summary>
        /// <param name="expression">FieldExpression</param>
        /// <returns>Concrete expression</returns>
        private Expression GetExpression(FieldExpression expression)
        {
            Expression expr = expression.GetExpression();
            //Get real expression, not a variable
            while (expr is VarExpression)
            {
                expr = SymbolTable.GetVariableDefinition(((VarExpression)expr).GetVariableIdentifier());
            }

            //Get specific record from recordExpression
            if (expr is RecordExpression)
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
            if (predicate is IsPredicate)
            {   //Evaluate Expression Predicate
                IsPredicate isPredicate = (IsPredicate)predicate;
                Expression expression = isPredicate.GetExpression();
                
                //If right type return true, otherwise false
                if (isPredicate.GetType() is StringType)
                {
                    return isPredicate.GetExpression() is TextExpression;
                }
                else if (isPredicate.GetType() is ListType)
                {
                    return isPredicate.GetExpression() is ListExpression;
                }
                else if (isPredicate.GetType() is RecordType)
                {
                    return isPredicate.GetExpression() is RecordExpression;
                }
                else
                {   //No match between types which could be checked, so false
                    return false;
                }
            }
            else if (predicate is ExpressionPredicate)
            {   //Evaluate Expression Predicate
                ExpressionPredicate expressionPredicate = (ExpressionPredicate)predicate;
                Expression expression = expressionPredicate.GetExpression();

                if (expression is FieldExpression)
                {   //Check if specific field exists in record (not null)
                    Expression expr = GetExpression((FieldExpression)expression);
                    return expr != null;
                }
                else if (expression is VarExpression)
                {   //Check if specific variable is defined
                    VarExpression varExpr = (VarExpression)expression;
                    return SymbolTable.ContainsVariable(varExpr.GetVariableIdentifier());
                }
                else
                {   //Other expressions are always true, because they doesn't refer to something
                    return true;
                }
            }
            else if (predicate is AndPredicate)
            {   //Evaluate And Predicate
                AndPredicate andPredicate = (AndPredicate)predicate;
                return EvaluatePredicate(andPredicate.GetLeftPredicate()) && EvaluatePredicate(andPredicate.GetLeftPredicate());
            }
            else if (predicate is OrPredicate)
            {   //Evaluate Or Predicate
                OrPredicate orPredicate = (OrPredicate) predicate;
                return EvaluatePredicate(orPredicate.GetLeftPredicate()) || EvaluatePredicate(orPredicate.GetRightPredicate());
            }
            else if (predicate is NotPredicate)
            {   //Evaluate Not Predicate
                NotPredicate notPredicate = (NotPredicate) predicate;
                return !EvaluatePredicate(notPredicate);
            }
            return false;
        }

        /// <summary>
        /// Add an XHTMLElement to XHTMLTree
        /// </summary>
        /// <param name="element">Element to add</param>
        private void AddElement(XHTMLElement element)
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

        /// <summary>
        /// Check if an markup tag is an call
        /// </summary>
        /// <param name="markup">Markup to check</param>
        /// <returns>True if call, otherwise false</returns>
        private bool IsMarkupCall(Markup markup)
        {
            return SymbolTable.ContainsFunction(markup.GetDesignator().GetIdentifier());
        }

        /// <summary>
        /// Check if an node contains an yield
        /// </summary>
        /// <param name="node">Node to check</param>
        /// <returns>True if node contains yield, otherwise false</returns>
        private bool NodeContainsYield(ISyntaxNode node)
        {
            if (node is YieldStatement)
            {   //Node itself is an yield
                return true;
            }
            else
            {   //Check subnodes
                foreach(ISyntaxNode subNode in node.GetSubNodes())
                {
                    if(NodeContainsYield(subNode))
                    {
                        return true;
                    }
                }
                //No Yields found
                return false;
            }
        }

        #endregion

    }
}
