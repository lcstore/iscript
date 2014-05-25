var ilog = function(msg) {
	if (msg) {
		java.lang.System.out.println(msg);
	} else {
		java.lang.System.err.println("null");
	}
};
var cookieUtils = {
get: function(e) {
	try {
		var t, n = new RegExp("(^| )" + e + "=([^;]*)(;|$)");
		return (t = document.cookie.match(n)) ? unescape(t[2]) : "";
	} catch (r) {
		return "";
	}
},set: function(e, t, n) {
	n = n || {};
	var r = n.expires;
	typeof r == "number" && (r = new Date, r.setTime(r.getTime() + n.expires));
	try {
		document.cookie = e + "=" + escape(t) + (r ? ";expires=" + r.toGMTString() : "") + (n.path ? ";path=" + n.path : "") + (n.domain ? "; domain=" + n.domain : "");
	} catch (i) {
	}
}};

window = this;
window["document"] = windowObject.getDocument();
window["navigator"] = windowObject.getNavigator();
window["location"] = windowObject.getLocation();
window["screen"] = windowObject.getScreen();
window["history"] = windowObject.getHistory();
window.self = this;
window.window = this;
window.opener = this;
window.parent = null;
window.top = null;
window.content= this;
window.frames = new Array();
window.closed = false;
window.defaultStatus = "";
window.name = "";
window.outerWidth = 0;
window.outerHeight = 0;
window.pageXOffset = 0;
window.pageYOffset = 0;
window.status = "";
window.innerWidth = 0;
window.innerHeight = 0;
window.screenX = 0;
window.screenY = 0;
window.screenLeft = 0;
window.screenTop = 0;
window.length = 0;
window.scrollX=0;
window.scrollY=0;
window.scrollMaxX=0;
window.scrollMaxY=0;
window.fullScreen="";
window.frameElement="";
window.sessionStorage="";

window.alert = function(arg){};
window.blur = function(){};
window.clearInterval = function(arg){};
window.clearTimeout = function(arg){};
window.close = function(){};
window.confirm = function(arg){return false;};
window.focus = function(){};
window.getComputedStyle = function(arg1,arg2){return new Object();};
window.moveTo = function(arg1,arg2){};
window.moveBy = function(arg1,arg2){};
window.open = function(optionalArg1, optionalArg2, optionalArg3, optionalArg4){return null;};
window.print = function(){};
/**
 * function prompt(arg1, arg2)
 * 
 * @param {String}
 *            arg1
 * @param {String}
 *            arg2
 * @memberOf Window
 * @returns {String}
 */
window.prompt = function(){return "";};
/**
 * function resizeTo(arg1, arg2)
 * 
 * @param {Number}
 *            arg1
 * @param {Number}
 *            arg2
 * @memberOf Window
 */
window.resizeTo=function(arg1,arg2){};
/**
 * function resizeBy(arg1, arg2)
 * 
 * @param {Number}
 *            arg1
 * @param {Number}
 *            arg2
 * @memberOf Window
 */
window.resizeBy=function(arg1,arg2){};
/**
 * function scrollTo(arg1, arg2)
 * 
 * @param {Number}
 *            arg1
 * @param {Number}
 *            arg2
 * @memberOf Window
 */
window.scrollTo=function(arg1,arg2){};
/**
 * function scrollBy(arg1, arg2)
 * 
 * @param {Number}
 *            arg1
 * @param {Number}
 *            arg2
 * @memberOf Window
 */
window.scrollBy=function(arg1,arg2){};
/**
 * function setInterval(arg1, arg2)
 * 
 * @param {Object}
 *            arg1
 * @param {Number}
 *            arg2
 * @memberOf Window
 * @returns {Number}
 */
window.setInterval=function(arg1, arg2){return 0;};
/**
 * function setTimeout(arg1, arg2)
 * 
 * @param {Object}
 *            arg1
 * @param {Number}
 *            arg2
 * @memberOf Window
 * @returns {Number}
 */
window.setTimeout=function(arg1, arg2){ return 0;};
/**
 * function atob(arg)
 * 
 * @param {String}
 *            arg
 * @memberOf Window
 * @returns {String}
 */
window.atob=function(arg){return "";};
/**
 * function btoa(arg)
 * 
 * @param {String}
 *            arg
 * @memberOf Window
 * @returns {String}
 */
window.btoa=function(arg){return "";};
/**
 * function setResizable(arg)
 * 
 * @param {Boolean}
 *            arg
 * @memberOf Window
 */
window.setResizable=function(arg){};

window.captureEvents=function(arg1){};
window.releaseEvents=function(arg1){};
window.routeEvent=function(arg1){};
window.enableExternalCapture=function(){};
window.disableExternalCapture=function(){};
window.find=function(){};
window.back=function(){};
window.forward=function(){};
window.home=function(){};
window.stop=function(){};
window.scroll=function(arg1,arg2){};

// event
window.dispatchEvent=function(event){
	return document.dispatchEvent(event);
};
window.removeEventListener=function(type, listener, useCapture){
	document.addEventListener(type, listener, useCapture);
};
window.removeEventListener=function(type, listener, useCapture){
	document.removeEventListener(type, listener, useCapture);
};
