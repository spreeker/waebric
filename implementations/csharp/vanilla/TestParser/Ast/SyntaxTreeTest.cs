using Parser.Ast;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Parser.Ast.Module;
namespace TestParser.Ast
{
    
    
    /// <summary>
    ///This is a test class for SyntaxTreeTest and is intended
    ///to contain all SyntaxTreeTest Unit Tests
    ///</summary>
    [TestClass()]
    public class SyntaxTreeTest
    {


        private TestContext testContextInstance;

        /// <summary>
        ///Gets or sets the test context which provides
        ///information about and functionality for the current test run.
        ///</summary>
        public TestContext TestContext
        {
            get
            {
                return testContextInstance;
            }
            set
            {
                testContextInstance = value;
            }
        }

        #region Additional test attributes
        // 
        //You can use the following additional attributes as you write your tests:
        //
        //Use ClassInitialize to run code before running the first test in the class
        //[ClassInitialize()]
        //public static void MyClassInitialize(TestContext testContext)
        //{
        //}
        //
        //Use ClassCleanup to run code after all tests in a class have run
        //[ClassCleanup()]
        //public static void MyClassCleanup()
        //{
        //}
        //
        //Use TestInitialize to run code before running each test
        //[TestInitialize()]
        //public void MyTestInitialize()
        //{
        //}
        //
        //Use TestCleanup to run code after each test has run
        //[TestCleanup()]
        //public void MyTestCleanup()
        //{
        //}
        //
        #endregion


        /// <summary>
        ///A test for SetRoot
        ///</summary>
        [TestMethod()]
        public void SetRootTest()
        {
            SyntaxTree target = new SyntaxTree(); // TODO: Initialize to an appropriate value
            ModuleList rootNode = null; // TODO: Initialize to an appropriate value
            target.SetRoot(rootNode);
            Assert.Inconclusive("A method that does not return a value cannot be verified.");
        }

        /// <summary>
        ///A test for GetRoot
        ///</summary>
        [TestMethod()]
        public void GetRootTest()
        {
            SyntaxTree target = new SyntaxTree(); // TODO: Initialize to an appropriate value
            ISyntaxNode expected = null; // TODO: Initialize to an appropriate value
            ISyntaxNode actual;
            actual = target.GetRoot();
            Assert.AreEqual(expected, actual);
            Assert.Inconclusive("Verify the correctness of this test method.");
        }

        /// <summary>
        ///A test for SyntaxTree Constructor
        ///</summary>
        [TestMethod()]
        public void SyntaxTreeConstructorTest1()
        {
            SyntaxTree target = new SyntaxTree();
            Assert.Inconclusive("TODO: Implement code to verify target");
        }

        /// <summary>
        ///A test for SyntaxTree Constructor
        ///</summary>
        [TestMethod()]
        public void SyntaxTreeConstructorTest()
        {
            ModuleList rootNode = null; // TODO: Initialize to an appropriate value
            SyntaxTree target = new SyntaxTree(rootNode);
            Assert.Inconclusive("TODO: Implement code to verify target");
        }
    }
}
