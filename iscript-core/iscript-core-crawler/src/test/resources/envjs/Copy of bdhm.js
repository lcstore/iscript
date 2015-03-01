cookieUtils = {
get: function(e) {
	try {
		var t, n = new RegExp("(^| )" + e + "=([^;]*)(;|$)");
		return (t = u.cookie.match(n)) ? unescape(t[2]) : ""
	} catch (r) {
		return ""
	}
},set: function(e, t, n) {
	n = n || {};
	var r = n.expires;
	typeof r == "number" && (r = new Date, r.setTime(r.getTime() + n.expires));
	try {
		u.cookie = e + "=" + escape(t) + (r ? ";expires=" + r.toGMTString() : "") + (n.path ? ";path=" + n.path : "") + (n.domain ? "; domain=" + n.domain : "")
	} catch (i) {
	}
}}