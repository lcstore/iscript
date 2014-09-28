ilogger = org.mozilla.javascript.Context.reportRuntimeError;
var ilog = function(msgObject) {
	if (!msgObject) {
		java.lang.System.err.println("null");
	} else if (msgObject.getMessage) {
		java.lang.System.out.println(msgObject.getMessage());
	} else {
		java.lang.System.out.println(msgObject);
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
var window = this;
window.parent = null;
window.self = window;
window.content= window;
window.window = window;
window.opener = null;
window.top = null;
window.frames = new Array();
window.closed = new Boolean();
window.document= document;
window.history= history;
window.navigator = navigator;
window.screen = screen;
window.location=location;
// window.event = new Event();
window.defaultStatus = "";
window.name = "";
window.status = "";
window.outerWidth = 1366;
window.outerHeight = 728 ;
window.pageXOffset = 0;
window.pageYOffset = 0;
window.innerWidth = 1366;
window.innerHeight = 147;
window.screenX = 0;
window.screenY = 0;
window.screenLeft = 0;
window.screenTop = 0;
window.length = 1;
window.scrollX=0;
window.scrollY=0;
window.scrollMaxX=0;
window.scrollMaxY=0;
window.fullScreen="";
window.frameElement="";
window.sessionStorage="";
window = window;


/**
 * function alert()
 * 
 * @param {String}
 *            arg
 * @memberOf Window
 */
window.alert = function(arg){};
/**
 * function blur()
 * 
 * @memberOf Window
 */
window.blur = function(){};
/**
 * function clearInterval(arg)
 * 
 * @param arg
 * @memberOf Window
 */
window.clearInterval = function(arg){};
/**
 * function clearTimeout(arg)
 * 
 * @param arg
 * @memberOf Window
 */
window.clearTimeout = function(arg){};
/**
 * function close()
 * 
 * @memberOf Window
 */
window.close = function(){};
/**
 * function confirm()
 * 
 * @param {String}
 *            arg
 * @memberOf Window
 * @returns {Boolean}
 */
window.confirm = function(arg){return false;};
/**
 * function focus()
 * 
 * @memberOf Window
 */
window.focus = function(){};
/**
 * function getComputedStyle(arg1, arg2)
 * 
 * @param {Element}
 *            arg1
 * @param {String}
 *            arg2
 * @memberOf Window
 * @returns {Object}
 */
window.getComputedStyle = function(arg1,arg2){return new Object();};
/**
 * function moveTo(arg1, arg2)
 * 
 * @param {Number}
 *            arg1
 * @param {Number}
 *            arg2
 * @memberOf Window
 */
window.moveTo = function(arg1,arg2){};
/**
 * function moveBy(arg1, arg2)
 * 
 * @param {Number}
 *            arg1
 * @param {Number}
 *            arg2
 * @memberOf Window
 */
window.moveBy = function(arg1,arg2){};
/**
 * function open(optionalArg1, optionalArg2, optionalArg3, optionalArg4)
 * 
 * @param {String}
 *            optionalArg1
 * @param {String}
 *            optionalArg2
 * @param {String}
 *            optionalArg3
 * @param {Boolean}
 *            optionalArg4
 * @memberOf Window
 * @returns {Window}
 */
window.open = function(optionalArg1, optionalArg2, optionalArg3, optionalArg4){return null;};
/**
 * function print()
 * 
 * @memberOf Window
 */
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
	return window.document.dispatchEvent(event);
};
window.addEventListener=function(type, listener, useCapture){
   window.document.addEventListener(type, listener, useCapture);
};
window.removeEventListener=function(type, listener, useCapture){
	window.document.removeEventListener(type, listener, useCapture);
};
window.XMLHttpRequest=function(){
	ilog('new XMLHttpRequest...');
};

// function Image(width, height){gImg=this;};
// Image.prototype = document.createElement('img');
