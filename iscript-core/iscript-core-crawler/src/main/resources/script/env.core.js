window = this;
window["document"] = windowObject.getDocument();
window["navigator"] = windowObject.getNavigator();
window["location"] = windowObject.getLocation();
window["screen"] = windowObject.getScreen();
window["history"] = windowObject.getHistory();
// window["String"] = String;
// window["encodeURIComponent"] = encodeURIComponent;
// window["Array"] = Array;
// window["Math"] = Math;
// window["Date"] = Date;
// window["encodeURI"] = encodeURI;
// window["encodeURI"] = parseInt;
// window["RegExp"] = RegExp;
// window["Boolean"] = Boolean;
// window["Object"] = Object;
// window["parseInt"] = parseInt;
// var setTimeout = function(expr, millis) {
// window.setTimeout(expr, millis);
// };
var ilog = function(msg) {
	if (msg) {
		java.lang.System.out.println(msg);
	} else {
		java.lang.System.err.println("null");
	}
};
(function() {
	// 将回调包装在另一个匿名函数中，来保持原始的上下文
	var proxy = function(func, context) {
		return func.apply(context, arguments);
	};
	var javaObject = windowObject;
	var javaClass = javaObject.getClass();
	while (javaClass) {
		var methodArray = javaClass.getMethods();
		for ( var index in methodArray) {
			(function() {
				var name = methodArray[index].getName();
				window[name] = function() {
					var args = Array.prototype.slice.call(arguments);
					if (args.length < 1) {
						return javaObject[name]();
					} else if (args.length == 1) {
						return javaObject[name](args[0]);
					} else if (args.length == 2) {
						return javaObject[name](args[0], args[1]);
					} else if (args.length == 3) {
						return javaObject[name](args[0], args[1], args[2]);
					}
				}
			}());
		}
		javaClass = javaClass.getSuperclass();
	}
}());

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
}}
