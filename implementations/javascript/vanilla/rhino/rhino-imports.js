importPackage(java.io);

load("../vanilla/ast/WaebricEnvironment.js");
load("../vanilla/ast/Node.js");
load("../vanilla/ast/Path.js");
load("../vanilla/ast/Module.js");
load("../vanilla/ast/ModuleId.js");
load("../vanilla/ast/Import.js");
load("../vanilla/ast/Site.js");
load("../vanilla/ast/Mapping.js");
load("../vanilla/ast/FunctionDefinition.js");
load("../vanilla/ast/Statement.js");
load("../vanilla/ast/Designator.js");
load("../vanilla/ast/Attribute.js");
load("../vanilla/ast/Argument.js");
load("../vanilla/ast/Markup.js");
load("../vanilla/ast/Embed.js");
load("../vanilla/ast/Embedding.js");
load("../vanilla/ast/Expression.js");
load("../vanilla/ast/KeyValuePair.js");
load("../vanilla/ast/Predicate.js");
load("../vanilla/ast/TextTail.js");
load("../vanilla/ast/Type.js");
load("../vanilla/ast/Assignment.js");

load("../vanilla/parser/lexical/WaebricTokenizer.js");
load("../vanilla/parser/lexical/WaebricTokenizerResult.js");
load("../vanilla/parser/lexical/WaebricTokenizerException.js");
load("../vanilla/parser/lexical/WaebricLexicalException.js");
load("../vanilla/parser/lexical/WaebricCharacter.js");
load("../vanilla/parser/lexical/tokens/WaebricToken.js");
load("../vanilla/parser/lexical/tokens/WaebricTokenIdentifier.js");
load("../vanilla/parser/lexical/tokens/WaebricTokenComment.js");
load("../vanilla/parser/lexical/tokens/WaebricTokenKeyword.js");
load("../vanilla/parser/lexical/tokens/WaebricTokenNatural.js");
load("../vanilla/parser/lexical/tokens/WaebricTokenSymbol.js");
load("../vanilla/parser/lexical/tokens/WaebricTokenText.js");
load("../vanilla/parser/lexical/tokens/WaebricTokenWhitespace.js");

load("../vanilla/parser/WaebricParser.js");
load("../vanilla/parser/WaebricParserResult.js");
load("../vanilla/parser/WaebricParserException.js");

load("../vanilla/parser/syntactic/WaebricBaseParser.js");
load("../vanilla/parser/syntactic/WaebricParserStack.js");
load("../vanilla/parser/syntactic/WaebricSyntaxException.js");
load("../vanilla/parser/syntactic/WaebricRootParser.js");
load("../vanilla/parser/syntactic/WaebricModuleParser.js");
load("../vanilla/parser/syntactic/WaebricSiteParser.js");
load("../vanilla/parser/syntactic/WaebricFunctionDefinitionParser.js");
load("../vanilla/parser/syntactic/WaebricMarkupParser.js");
load("../vanilla/parser/syntactic/WaebricExpressionParser.js");
load("../vanilla/parser/syntactic/WaebricParserToken.js");

load("../vanilla/parser/syntactic/WaebricStatementParser.js");
load("../vanilla/parser/syntactic/WaebricPredicateParser.js");
load("../vanilla/parser/syntactic/WaebricEmbeddingParser.js");

load("../vanilla/validator/WaebricValidator.js");
load("../vanilla/validator/WaebricSemanticException.js");
load("../vanilla/validator/WaebricValidatorException.js");
load("../vanilla/validator/WaebricValidatorResult.js");
load("../vanilla/validator/WaebricValidatorVisitor.js");
load("../vanilla/validator/XHTML.js");

load('../vanilla/interpreter/WaebricInterpreter.js');
load("../vanilla/interpreter/WaebricInterpreterException.js");
load('../vanilla/interpreter/WaebricInterpreterResult.js');
load("../vanilla/interpreter/WaebricInterpreterVisitor.js");
load('../vanilla/interpreter/DOM.js');
load('env.rhino.js')