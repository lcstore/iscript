/**
 * wrap jsoup Document as HTMLDocument
 */
function HTMLDocument(document) {
	this.document = document;
};
HTMLDocument.prototype.__defineGetter__("domain", function() {
	var url = this.document.baseUri();
	var sMark = "://";
	var fromIndex = url.indexOf(sMark);
	fromIndex += sMark.length;
	var toIndex = url.indexOf("/", fromIndex);
	toIndex = toIndex < 0 ? url.length() : toIndex;
	return url.substring(fromIndex, toIndex);
});
HTMLDocument.prototype.__defineSetter__("domain", function(val) {
	this.domain = val;
});
HTMLDocument.prototype.createElement = function(tagName) {
	//wrapper to js Element
	return this.document.createElement(tagName);
};

function Location(url) {
	this.url = url;
};
Location.prototype.userAgent = "Mozilla/5.0 (Windows NT 6.3; WOW64; rv:33.0) Gecko/20100101 Firefox/33.0";
Location.prototype.__defineGetter__("userAgent", function() {
	return this._userAgent;
});
Location.prototype.__defineSetter__("userAgent", function(val) {
	this._userAgent = val;
});

function Navigator() {
};

Navigator.prototype.appCodeName = "Mozilla"
Navigator.prototype.appName = "Netscape";
Navigator.prototype.appVersion = "5.0 (Windows NT 6.1) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/35.0.1916.114 Safari/537.36";
Navigator.prototype.cookieEnabled = new Boolean();
Navigator.prototype.mimeTypes = new Array();
Navigator.prototype.platform = "Win32";
Navigator.prototype.plugins = new Array();
Navigator.prototype.userAgent = "Mozilla/5.0 (Windows NT 6.1; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/34.0.1847.116 Safari/537.36";
Navigator.prototype.javaEnabled = function() {
	return false;
};
Navigator.prototype.availHeight = 0;
Navigator.prototype.availWidth = 0;
Navigator.prototype.colorDepth = 0;
Navigator.prototype.height = 0;
Navigator.prototype.width = 0;

// document
var document = new HTMLDocument($document);
var location = new Location($document.baseUri());
var navigator = new Navigator();
var window = $window;
debug.log('domain:' + document.domain);
debug.log('userAgent:' + navigator.userAgent);
