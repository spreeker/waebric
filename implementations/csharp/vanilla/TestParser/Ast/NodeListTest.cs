using Parser.Ast;
using Microsoft.VisualStudio.TestTools.UnitTesting;
using Parser.Ast.Module;
namespace TestParser.Ast
{
    
    
    /// <summary>
    ///This is a test class for NodeListTest and is intended
    ///to contain all NodeListTest Unit Tests
    ///</summary>
    [TestClass()]
    public class NodeListTest
    {
        private NodeList nodes;

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

        /// <summary>
        /// Initialize test items
        /// </summary>
        [TestInitialize]
        public void SetUp()
        {
            nodes = new NodeList();
        }
   
        /// <summary>
        /// Cleanup items after test run
        /// </summary>
        [TestCleanup()]
        public void TearDown()
        {
            nodes = null;
        }
        
        #endregion

        /// <summary>
        ///A test for GetSize
        ///</summary>
        [TestMethod()]
        public void GetSizeTest()
        {
            //First check if currently there are 0 items
            Assert.AreEqual(0, nodes.Count);

            //Add twoitems and check size again
            nodes.Add(new NodeList());
            nodes.Add(new NodeList());

            Assert.AreEqual(2, nodes.Count);
        }

        /// <summary>
        ///A test for GetElements
        ///</summary>
        [TestMethod()]
        public void GetElementsTest()
        {
            //Add elements to list and retrieve them and check if they are correct
            Module node1 = new Module();
            ModuleId node2 = new ModuleId();

            nodes.Add(node1);
            nodes.Add(node2);

            ISyntaxNode[] list = nodes.GetElements();
            
            Assert.IsTrue(list.Length == 2);
            Assert.AreSame(node1, list[0]);
            Assert.AreSame(node2, list[1]);
        }

        /// <summary>
        ///A test for Get
        ///</summary>
        [TestMethod()]
        public void GetTest()
        {
            //Add elements to list and retrieve them and check if they are correct
            Module node1 = new Module();
            ModuleId node2 = new ModuleId();

            nodes.Add(node1);
            nodes.Add(node2);

            ISyntaxNode[] list = nodes.GetElements();

            Assert.IsTrue(list.Length == 2);
            //Test if get with index works
            Assert.AreSame(nodes.Get(1), node2);
        }

        /// <summary>
        ///A test for Clear
        ///</summary>
        [TestMethod()]
        public void ClearTest()
        {
            //Fill list with one node
            Module node1 = new Module();
            nodes.Add(node1);

            //Check if size is bigger than zero
            Assert.AreEqual(1, nodes.Count);

            //Clear list and check if zero elements in it
            nodes.Clear();
            Assert.AreEqual(0, nodes.Count);
        }

        /// <summary>
        ///A test for Add
        ///</summary>
        [TestMethod()]
        public void AddTest()
        {
            NodeList target = new NodeList(); // TODO: Initialize to an appropriate value
            ISyntaxNode element = null; // TODO: Initialize to an appropriate value
            target.Add(element);
            Assert.Inconclusive("A method that does not return a value cannot be verified.");
        }

        /// <summary>
        ///A test for NodeList Constructor
        ///</summary>
        [TestMethod()]
        public void NodeListConstructorTest()
        {
            NodeList target = new NodeList();
            Assert.Inconclusive("TODO: Implement code to verify target");
        }
    }
}
