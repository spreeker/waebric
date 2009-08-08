/**
 * XHTML Class
 * 
 * Holds all valid XHTML tags (copied from www.w3c.com)
 */
function XHTML(){
	
}

XHTML.tags = [	
	'a',
	'abbr',
	'acronym',
	'address',
	'applet',
	'area',
	'b',
	'base',
	'basefont',
	'bdo',
	'big',
	'blockquote',
	'body',
	'br',
	'button',
	'caption',
	'center',
	'cite',
	'code',
	'col',
	'colgroup',
	'dd',
	'del',
	'dir',
	'div',
	'dfn',
	'dl',
	'dt',
	'em',
	'fieldset',
	'font',
	'form',
	'frame',
	'frameset',
	'h1', 'h2', 'h3', 'h4', 'h5', 'h6',
	'head',
	'hr',
	'html',
	'i',
	'iframe',
	'img',
	'input',
	'ins',
	'isindex',
	'kbd',
	'label',
	'legend',
	'li',
	'link',
	'map',
	'menu',
	'meta',
	'noframes',
	'object',
	'ol',
	'optgroup',
	'option',
	'p',
	'param',
	'pre',
	'q',
	's',
	'samp',
	'script',
	'select',
	'small',
	'span',
	'strike',
	'strong',
	'style',
	'sub',
	'sup',
	'table',
	'tbody',
	'td',
	'textarea',
	'tfoot',
	'th',
	'thead',
	'title',
	'tr',
	'tt',
	'u',
	'ul',
	'var',
	'xmp'



];

/**
 * Checks whether the input tag is a valid XHTML tag.
 * 
 * @param {String} input
 */
XHTML.isXHTMLTag = function(input){
	for (var i = 0; i < XHTML.tags.length; i++) {
		if (input.toString().toLowerCase() == XHTML.tags[i]) {
			return true;
		}
	}
	return false;
}


