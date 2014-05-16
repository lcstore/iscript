window["String"] = String;
window["document"] = document;
window["encodeURIComponent"] = encodeURIComponent;
window["Array"] = Array;
window["Math"] = Math;
window["Date"] = Date;
window["encodeURI"] = encodeURI;
window["encodeURI"] = parseInt;
window["RegExp"] = RegExp;
window["Boolean"] = Boolean;
window["Object"] = Object;
window["parseInt"] = parseInt;
var setTimeout = function(expr, millis) {
	window.setTimeout(expr, millis);
};
var log = function(msg) {
	if (msg) {
		java.lang.System.out.println(msg);
	} else {
		java.lang.System.err.println("null");
	}
};