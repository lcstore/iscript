// common function
var Jsoup = org.jsoup.Jsoup;

var debug = {};
debug.log = function(msg) {
	if (msg) {
		java.lang.System.out.println(msg);
	} else {
		java.lang.System.out.println("NULL OR Undefine.");
	}
};
// make property of object to global
function toGlobal(oParam) {
	if (!oParam) {
		return;
	}
	for ( var key in oParam) {
		org.mozilla.javascript.ScriptableObject.putProperty(this, key,
				oParam[key]);
	}
};
// iQuery
var iQuery = function(selector, context) {
	return new iQuery.fn.init(selector, context);
};
iQuery.fn = iQuery.prototype = {
	constructor : iQuery,
	init : function(selector, context) {
		if (!selector) {
			return;
		}
		context = context || $document;
		this.selector = selector;
		this.set(context.select(selector));
	},
	set : function(elements) {
		this.elements = elements;
		this.length = elements == null ? 0 : this.elements.size();
	},
	ajax : function(oParam) {
		iQuery.ajax(oParam);
	},
	remove : function() {
		var size = this.elements.size();
		for (var i = 0; i < size; i++) {
			this.elements.get(i).remove();
		}

	},
	parent : function() {
		var pElements = new org.jsoup.select.Elements();
		var size = this.elements.size();
		for (var i = 0; i < size; i++) {
			var ele = this.elements.get(i).parent();
			if (ele) {
				pElements.add(ele);
			}
		}
		var newQuery = iQuery();
		newQuery.set(pElements);
		return newQuery;
	},
	show : function() {
		var size = this.elements.size();
		for (var i = 0; i < size; i++) {
			var ele = this.elements.get(i);
			debug.log('@show:' + ele.tag() + "#" + ele.id());
		}
	},
	hide : function() {
		var size = this.elements.size();
		for (var i = 0; i < size; i++) {
			var ele = this.elements.get(i);
			debug.log('@hide:' + ele.tag() + "#" + ele.id());
		}
	},
	html : function(html) {
		if (this.elements.isEmpty()) {
			return;
		}
		if (!html) {
			return this.elements.get(0).html();
		} else {
			return this.elements.get(0).html(html);
		}
	},
	prepend : function(html) {
		var size = this.elements.size();
		for (var i = 0; i < size; i++) {
			this.elements.get(i).prepend(html);
		}
	},
	append : function(html) {
		var size = this.elements.size();
		for (var i = 0; i < size; i++) {
			this.elements.get(i).append(html);
		}
	},
	before : function(html) {
		var size = this.elements.size();
		for (var i = 0; i < size; i++) {
			this.elements.get(i).before(html);
		}
	},
	after : function(html) {
		var size = this.elements.size();
		for (var i = 0; i < size; i++) {
			this.elements.get(i).after(html);
		}
	},
	each : function(objArray, funName) {
		iQuery.each(objArray, funName);
	},
	size : function() {
		return this.elements.size();
	},
	get : function(index) {
		return this.elements.get(index);
	},
	eq : function(index) {
		return this.elements.eq(index);
	},
	add : function(element) {
		return this.elements.add(element);
	},
	attr : function(key) {
		return this.elements.attr(key);
	},
	attr : function(key,value) {
		return this.elements.attr(key,value);
	}
};
iQuery.ajax = function(oParam) {
	var sUrl;
	for ( var key in oParam.data) {
		if (sUrl) {
			sUrl += '&' + key + '=' + oParam.data[key];
		} else {
			sUrl = key + '=' + oParam.data[key];
		}
	}
	if (oParam.url.indexOf('?') < 0) {
		sUrl = oParam.url + '?' + sUrl;
	} else {
		sUrl = oParam.url + sUrl;
	}
	debug.log(sUrl);
	try {
		var html = http.get(sUrl);
		if (oParam.success && typeof oParam.success == 'Function') {
			oParam.success(html);
		} else {
			eval('' + html);
		}
	} catch (ex) {
		debug.log('#error:[' + sUrl + "]:" + ex);
	}

};
iQuery.each = function(objArray, funName) {
	for (var i = 0; i < objArray.length; i++) {
		funName(i, objArray[i]);
	}
};
iQuery.fn.init.prototype = iQuery.fn;
var $ = iQuery;
// ======iQuery end==============