window = this;
// window["String"] = String;
// window["document"] = document;
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
				var setName = name.replace('get', 'set');
				if (name.indexOf('get') == 0 && javaObject[setName]) {
					var fieldName = name.substring(3, 4).toLowerCase()
							+ name.substring(4);
					ilog('fieldName.' + fieldName + "," + name);
					window[fieldName] = javaObject[name]();
				}
			}());
		}
		javaClass = javaClass.getSuperclass();
	}
}());
ilog('windowObject.' + windowObject.history);
ilog('windowObject.' + window.getHistory());
ilog('windowObject.' + window.history);