var JSON;
JSON || (JSON = {}), function() {
    "use strict";
    function f(e) {
        return e < 10 ? "0" + e : e
    }
    function quote(e) {
        return escapable.lastIndex = 0, escapable.test(e) ? '"' + e.replace(escapable, function(e) {
            var t = meta[e];
            return typeof t == "string" ? t : "\\u" + ("0000" + e.charCodeAt(0).toString(16)).slice(-4)
        }) + '"' : '"' + e + '"'
    }
    function str(e, t) {
        var n, r, i, s, o = gap, u, a = t[e];
        a && typeof a == "object" && typeof a.toJSON == "function" && (a = a.toJSON(e)), typeof rep == "function" && (a = rep.call(t, e, a));
        switch (typeof a) {
            case "string":
                return quote(a);
            case "number":
                return isFinite(a) ? String(a) : "null";
            case "boolean":
            case "null":
                return String(a);
            case "object":
                if (!a)
                    return "null";
                gap += indent, u = [];
                if (Object.prototype.toString.apply(a) === "[object Array]") {
                    s = a.length;
                    for (n = 0; n < s; n += 1)
                        u[n] = str(n, a) || "null";
                    return i = u.length === 0 ? "[]" : gap ? "[\n" + gap + u.join(",\n" + gap) + "\n" + o + "]" : "[" + u.join(",") + "]", gap = o, i
                }
                if (rep && typeof rep == "object") {
                    s = rep.length;
                    for (n = 0; n < s; n += 1)
                        typeof rep[n] == "string" && (r = rep[n], i = str(r, a), i && u.push(quote(r) + (gap ? ": " : ":") + i))
                } else
                    for (r in a)
                        Object.prototype.hasOwnProperty.call(a, r) && (i = str(r, a), i && u.push(quote(r) + (gap ? ": " : ":") + i));
                return i = u.length === 0 ? "{}" : gap ? "{\n" + gap + u.join(",\n" + gap) + "\n" + o + "}" : "{" + u.join(",") + "}", gap = o, i
        }
    }
    typeof Date.prototype.toJSON != "function" && (Date.prototype.toJSON = function(e) {
        return isFinite(this.valueOf()) ? this.getUTCFullYear() + "-" + f(this.getUTCMonth() + 1) + "-" + f(this.getUTCDate()) + "T" + f(this.getUTCHours()) + ":" + f(this.getUTCMinutes()) + ":" + f(this.getUTCSeconds()) + "Z" : null
    }, String.prototype.toJSON = Number.prototype.toJSON = Boolean.prototype.toJSON = function(e) {
        return this.valueOf()
    });
    var cx = /[\u0000\u00ad\u0600-\u0604\u070f\u17b4\u17b5\u200c-\u200f\u2028-\u202f\u2060-\u206f\ufeff\ufff0-\uffff]/g, escapable = /[\\\"\x00-\x1f\x7f-\x9f\u00ad\u0600-\u0604\u070f\u17b4\u17b5\u200c-\u200f\u2028-\u202f\u2060-\u206f\ufeff\ufff0-\uffff]/g, gap, indent, meta = {"\b": "\\b","	": "\\t","\n": "\\n","\f": "\\f","\r": "\\r",'"': '\\"',"\\": "\\\\"}, rep;
    typeof JSON.stringify != "function" && (JSON.stringify = function(e, t, n) {
        var r;
        gap = "", indent = "";
        if (typeof n == "number")
            for (r = 0; r < n; r += 1)
                indent += " ";
        else
            typeof n == "string" && (indent = n);
        rep = t;
        if (!t || typeof t == "function" || typeof t == "object" && typeof t.length == "number")
            return str("", {"": e});
        throw new Error("JSON.stringify")
    }), typeof JSON.parse != "function" && (JSON.parse = function(text, reviver) {
        function walk(e, t) {
            var n, r, i = e[t];
            if (i && typeof i == "object")
                for (n in i)
                    Object.prototype.hasOwnProperty.call(i, n) && (r = walk(i, n), r !== undefined ? i[n] = r : delete i[n]);
            return reviver.call(e, t, i)
        }
        var j;
        text = String(text), cx.lastIndex = 0, cx.test(text) && (text = text.replace(cx, function(e) {
            return "\\u" + ("0000" + e.charCodeAt(0).toString(16)).slice(-4)
        }));
        if (/^[\],:{}\s]*$/.test(text.replace(/\\(?:["\\\/bfnrt]|u[0-9a-fA-F]{4})/g, "@").replace(/"[^"\\\n\r]*"|true|false|null|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?/g, "]").replace(/(?:^|:|,)(?:\s*\[)+/g, "")))
            return j = eval("(" + text + ")"), typeof reviver == "function" ? walk({"": j}, "") : j;
        throw new SyntaxError("JSON.parse")
    })
}();
var qload = qload || function() {
    var e = document, t = window, n = {}, r = {}, i = function(e) {
        return e.constructor === Array
    }, s = {mods: {}}, o = e.getElementsByTagName("script"), u, a = e.createElement("script").readyState ? function(e, t) {
        e.onreadystatechange = function() {
            var n = e.readyState;
            if (n === "loaded" || n === "complete")
                e.onreadystatechange = null, t.apply(this)
        }
    } : function(e, t) {
        e.addEventListener("load", t, !1)
    }, f = function(t, i, s, u, l) {
        var c = o[0];
        if (!t)
            return;
        if (n[t]) {
            r[t] = !1, u && u(t, l);
            return
        }
        if (r[t]) {
            setTimeout(function() {
                f(t, i, s, u, l)
            }, 1);
            return
        }
        r[t] = !0;
        var h, p = i || t.toLowerCase().substring(t.lastIndexOf(".") + 1);
        p === "js" ? (h = e.createElement("script"), h.setAttribute("type", "text/javascript"), h.setAttribute("src", t), h.setAttribute("async", !0)) : p === "css" && (h = e.createElement("link"), h.setAttribute("type", "text/css"), h.setAttribute("rel", "stylesheet"), h.setAttribute("href", t), n[t] = !0), s && (h.charset = s);
        if (p === "css") {
            c.parentNode.insertBefore(h, c), u && u(t, l);
            return
        }
        a(h, function() {
            n[t] = !0, u && u(t, l)
        }), c.parentNode.insertBefore(h, c)
    }, l = function(e) {
        if (!e || !i(e))
            return;
        var t = 0, n, r = [], o = s.mods, u = [], a = {}, f = function(e) {
            var t = 0, n, r;
            if (a[e])
                return u;
            a[e] = !0;
            if (o[e].requires) {
                r = o[e].requires;
                for (; typeof (n = r[t++]) != "undefined"; )
                    o[n] ? (f(n), u.push(n)) : u.push(n);
                return u
            }
            return u
        };
        for (; typeof (n = e[t++]) != "undefined"; )
            o[n] && o[n].requires && o[n].requires[0] && (u = [], a = {}, r = r.concat(f(n))), r.push(n);
        return r
    }, c = function(e) {
        if (!e || !i(e))
            return;
        this.queue = e, this.current = null
    };
    return c.prototype = {_interval: 10,start: function() {
            var e = this;
            this.current = this.next();
            if (!this.current) {
                this.end = !0;
                return
            }
            this.run()
        },run: function() {
            var e = this, t, n = this.current;
            if (typeof n == "function") {
                n(), this.start();
                return
            }
            typeof n == "string" && (s.mods[n] ? (t = s.mods[n], f(t.path, t.type, t.charset, function(t) {
                e.start()
            }, e)) : /\.js|\.css/i.test(n) ? f(n, "", "", function(e, t) {
                t.start()
            }, e) : this.start())
        },next: function() {
            return this.queue.shift()
        }}, u = function() {
        var e = [].slice.call(arguments), t;
        t = new c(l(e)), t.start()
    }, u.add = function(e, t) {
        if (!e || !t || !t.path)
            return;
        s.mods[e] = t
    }, u.delay = function() {
        var e = [].slice.call(arguments), n = e.shift();
        t.setTimeout(function() {
            u.apply(this, e)
        }, n)
    }, u.css = function(t, n) {
        n = n || "qtool-inline-css";
        var r = e.getElementById(n);
        r || (r = e.createElement("style"), r.type = "text/css", r.id = n, e.getElementsByTagName("head")[0].appendChild(r)), r.styleSheet ? r.styleSheet.cssText = r.styleSheet.cssText + t : r.appendChild(e.createTextNode(t))
    }, u
}(), wanLogin = wanLogin || {};
wanLogin.pop = function() {
    qtool.wbox("", {style: {width: "606px"},tpl_top: "",tpl_content: wanLogin.loginTemplate,tpl_bottom: ""}).showMessage(), $(".wan_360layer .tab_login").click(function() {
        $(".wan_360layer .wrap_tt span").removeClass("cur"), $(this).addClass("cur"), $(".wan_360layer .iframe_reg").hide(), $(".wan_360layer .iframe_login").show()
    }), $(".wan_360layer .tab_reg").click(function() {
        $(".wan_360layer .wrap_tt span").removeClass("cur"), $(this).addClass("cur"), $(".wan_360layer .iframe_login").hide(), $(".wan_360layer .iframe_reg").show()
    }), qtool.xdomain.listen({LOGIN_SUCC: function(e) {
            e == "LOGIN_SUCC" && (typeof wanLogin.option.loginSuccCallback == "string" ? location.href = wanLogin.option.loginSuccCallback : typeof wanLogin.option.loginSuccCallback == "function" ? (wanLogin.option.loginSuccCallback(), QT.WM.close()) : location.reload())
        }}), qtool.xdomain.listen({REG_SUCC: function(e) {
            e == "REG_SUCC" && (location.href = "http://wan.360.cn/reg_success.php?from=reg&time=1346728039&sign=c5c50ce769e33a70fb984d5f2cf9ecc1&wansrc=" + (wanLogin.option.src || "wan") + "&refer=" + (wanLogin.option.refer || "unknown") + "&dest_url=" + encodeURIComponent(wanLogin.option.regSuccUrl || location.href))
        }}), wanLogin.loginTemplate = ""
}, wanLogin.option = wanLogin.option || {}, wanLogin.commonLoginAndReg = function(e) {
    wanLogin.option = {};
    if (typeof e == "object")
        for (prop in e)
            wanLogin.option[prop] = e[prop];
    if (!wanLogin.option.defaultMethod || wanLogin.option.defaultMethod !== "login" && wanLogin.option.defaultMethod !== "reg")
        wanLogin.option.defaultMethod = "login";
    wanLogin.option.location = encodeURIComponent(location.href), wanLogin.option.refer = location.host;
    var t = JSON.stringify(wanLogin.option), n = '<div class="wan_360layer">';
    wanLogin.option.iframe && (n += '<iframe scrolling="no" frameborder="0" style="z-index:-1;position: absolute;width:594px;height:437px;left:6px;top:6px;background:#FFFFFF"></iframe>'), n += '<div class="wrap_top"><div class="wrap_top_r"></div></div>        <div class="wrap_conn">        <div class="wrap_conn_bor">        <a title="\u5173\u95ed" class="wan_360layer_close cancel" href="javascript:void(0);"><em></em></a>        <div class="wrap_tt"><span class="tab_login' + (wanLogin.option.defaultMethod === "login" ? " cur" : "") + '"><em>\u767b  \u5f55</em></span><span class="tab_reg' + (wanLogin.option.defaultMethod === "reg" ? " cur" : "") + '"><em>\u514d\u8d39\u6ce8\u518c</em></span></div>        <div class="wrap_conn_bg">        <iframe class="iframe_login" ' + (wanLogin.option.defaultMethod === "reg" ? 'style="display:none;"' : "") + ' width="100%" scrolling="no" frameborder="0" height="383" src="http://wan.360.cn/login_pop.html?params=' + encodeURIComponent(t) + '"></iframe>        <iframe class="iframe_reg" ' + (wanLogin.option.defaultMethod === "login" ? 'style="display:none;"' : "") + ' width="100%" scrolling="no" frameborder="0" height="383" src="http://wan.360.cn/reg_pop.html?params=' + encodeURIComponent(t) + '"></iframe>        </div>        </div>        </div>        <div class="wrap_bot"><div class="wrap_bot_r"></div></div>        </div>', wanLogin.loginTemplate = n, typeof qtool != "undefined" && wanLogin.pop()
}, typeof jQuery == "undefined" ? qload.add("jq", {path: "http://s0.qhimg.com/lib/jquery/172.js",type: "js"}) : qload.add("jq", {path: "",type: "js"}), qload.add("qtool", {path: "http://s0.qhimg.com/static/75789f97260d81d9.js",type: "js",requires: ["jq"]}), qload.add("pop", {path: "http://s1.qhimg.com/static/a8783d70caf1a9bd.css",type: "css",requires: ["qtool"]}), qload("pop", function() {
    wanLogin.loginTemplate && wanLogin.pop()
});
