//src:http://s8.qhimg.com/mall/;js;qload/fcfedfbc.js
qload = function() {
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
}();



//src:http://jifen.wan.360.cn/
var _3600G = _3600G || [];		qload.add("analyse", {
			path : "http://s4.qhimg.com/mall/;js;monitor/fcfedfbc.js",
			type : "js",
			requires : [ "app" ]
		}),
		qload("analyse", function() {
			moniter.getTrack().setId(
					[ "user_info_nav", "new_goods_list",
							"recommand_goods_list", "common_qu_list",
							"online_cs", "m_focusimg_conn",
							"interest_goods_list", "hot_goods_list",
							"sign_in_link", "switch_piclist", "reward_list" ])
		}),
		_3600G.push([ "session", !1, !1, {
			WanVer : "2"
		} ]),
		function(e, t, n, r, i, s, o) {
	        ilog(e+','+t+","+i);
			e.GoogleAnalyticsObject = i, e[i] = e[i] || function() {
				(e[i].q = e[i].q || []).push(arguments)
			}, e[i].l = 1 * new Date, s = t.createElement(n), o = t
					.getElementsByTagName(n)[0], s.async = 1, s.src = r,
					o.parentNode.insertBefore(s, o)
		}(window, document, "script",
				"//www.google-analytics.com/analytics.js", "ga"),
		ga("create", "UA-45744745-1", "360.cn"),
		ga("send", "pageview"),
		function() {
			var e = document.createElement("script");
					e.type = "text/javascript",
					e.async = !0,
					e.src = ("https:" == document.location.protocol ? "https://ssl"
							: "http://www")
							+ ".google-analytics.com/ga.js";
			var t = document.getElementsByTagName("script")[0];
			t.parentNode.insertBefore(e, t)
		}();
ilog('wga:'+window.ga);