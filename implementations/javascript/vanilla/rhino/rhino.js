load('../vanilla/rhino/rhino-imports.js')

//Retrieve arguments from rhino
var waebricFilePath = arguments[0];
var htmlOutputDirectory = arguments[1];

//Convert waebric program to xHTML website
var waebricConverter = new WaebricConverter();
waebricConverter.convertToHTML(waebricFilePath, htmlOutputDirectory)