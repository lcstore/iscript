window = this;
//window["String"] = String;
//window["document"] = document;
//window["encodeURIComponent"] = encodeURIComponent;
//window["Array"] = Array;
//window["Math"] = Math;
//window["Date"] = Date;
//window["encodeURI"] = encodeURI;
//window["encodeURI"] = parseInt;
//window["RegExp"] = RegExp;
//window["Boolean"] = Boolean;
//window["Object"] = Object;
//window["parseInt"] = parseInt;
var setTimeout = function(expr, millis) {
	window.setTimeout(expr, millis);
};
var ilog = function(msg) {
	if (msg) {
		java.lang.System.out.println(msg);
	} else {
		java.lang.System.err.println("null");
	}
};
//将回调包装在另一个匿名函数中，来保持原始的上下文
var proxy = function(func, context) {
    return (function() {
        return func.apply(context, arguments);
    });
};
var javaClass = windowObject.getClass();
while (javaClass) {
	var methodArray = javaClass.getMethods();
	for ( var index in methodArray) {
		var name = methodArray[index].getName();
		if (!window[name]) {
			window[name] = function() {
				return proxy(windowObject[name], this);
			}
		}
	}
	javaClass = javaClass.getSuperclass();
}
