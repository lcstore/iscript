var println = function(msg){
	if(msg){
		java.lang.System.out.println(msg);
	}else {
		java.lang.System.err.println("null");
	}
}
location.href='http://jifen.wan.360.cn/';
document.domain='http://jifen.wan.360.cn/';
	var hm = document.createElement("script");
	hm.src = "http://s0.qhimg.com/i360/;js;pass_api_/seed,log/v3202.js";
	var s = document.getElementsByTagName("script")[0];
	s.parentNode.appendChild(hm);
	println('document.domain:'+document.domain);
(function() {
    if (typeof window.monitor != "undefined")
        return;
    var a = "V1.2.2(2012.8.7)", b = "360.cn", c = function(d, e) {
        var f = document, g = navigator, h = d.screen, j = document.domain.toLowerCase(), k = g.userAgent.toLowerCase(), l = {trim: function(a) {
                return a.replace(/^[\s\xa0\u3000]+|[\u3000\xa0\s]+$/g, "")
            }}, m = {on: function(a, b, c) {
                a.addEventListener ? a && a.addEventListener(b, c, !1) : a && a.attachEvent("on" + b, c)
            },parentNode: function(a, b, c) {
                c = c || 5, b = b.toUpperCase();
                while (a && c-- > 0) {
                    if (a.tagName === b)
                        return a;
                    a = a.parentNode
                }
                return null
            }}, n = {fix: function(a) {
                if (!("target" in a)) {
                    var b = a.srcElement || a.target;
                    b && b.nodeType == 3 && (b = b.parentNode), a.target = b
                }
                return a
            }}, o = function() {
            function a(a) {
                return a != null && a.constructor != null ? Object.prototype.toString.call(a).slice(8, -1) : ""
            }
            return {isArray: function(b) {
                    return a(b) == "Array"
                },isObject: function(a) {
                    return a !== null && typeof a == "object"
                },mix: function(a, b, c) {
                    for (i in b)
                        if (c || !(a[i] || i in a))
                            a[i] = b[i];
                    return a
                },encodeURIJson: function(a) {
                    var b = [];
                    for (var c in a) {
                        if (a[c] == null)
                            continue;
                        b.push(encodeURIComponent(c) + "=" + encodeURIComponent(a[c]))
                    }
                    return b.join("&")
                }}
        }(), p = {get: function(a) {
                var b, c = new RegExp("(^| )" + a + "=([^;]*)(;|$)");
                ilog('f.cookie:'+f.cookie);
                return (b = f.cookie.match(c)) ? unescape(b[2]) : ""
            },set: function(a, b, c) {
                c = c || {};
                var d = c.expires;
                typeof d == "number" && (d = new Date, d.setTime(d.getTime() + c.expires)), f.cookie = a + "=" + escape(b) + (d ? ";expires=" + d.toGMTString() : "") + (c.path ? ";path=" + c.path : "") + (c.domain ? "; domain=" + c.domain : "")
            }}, q = {getProject: function() {
                return ""
            },getReferrer: function() {
                return f.referrer
            },getBrowser: function() {
                var a = {"360se-ua": "360se",TT: "tencenttraveler",Maxthon: "maxthon",GreenBrowser: "greenbrowser",Sogou: "se 1.x / se 2.x",TheWorld: "theworld"};
                for (i in a)
                    if (k.indexOf(a[i]) > -1)
                        return i;
                var b = !1;
                try {
                    +external.twGetVersion(external.twGetSecurityID(d)).replace(/\./g, "") > 1013 && (b = !0)
                } catch (c) {
                }
                if (b)
                    return "360se-noua";
                var e = k.match(/(msie|chrome|safari|firefox|opera)/);
                return e = e ? e[0] : "", e == "msie" && (e = k.match(/msie[^;]+/)), e
            },getLocation: function() {
                var a = "";
                try {
                    a = location.href
                } catch (b) {
                    a = f.createElement("a"), a.href = "", a = a.href
                }
                return a = a.replace(/[?#].*$/, ""), a = /\.(s?htm|php)/.test(a) ? a : a.replace(/\/$/, "") + "/", a
            },getGuid: function() {
                function a(a) {
                	ilog('a.len:'+a);
                    var b = 0, c = 0, d = a.length - 1;
                    for (d; d >= 0; d--) {
                        var e = parseInt(a.charCodeAt(d), 10);
                        b = (b << 6 & 268435455) + e + (e << 14), (c = b & 266338304) != 0 && (b ^= c >> 21)
                    }
                    return b
                }
                function c() {
                	ilog('b.len:'+b);
                	ilog('d.history:'+d.history);
                    var b = [g.appName, g.version, g.language || g.browserLanguage, g.platform, k, h.width, "x", h.height, h.colorDepth, f.referrer].join(""), c = b.length, e = d.history.length;
                    while (e)
                        b += e-- ^ c++;
                    return (Math.round(Math.random() * 2147483647) ^ a(b)) * 2147483647
                }
                var e = "__guid", i = p.get(e);
                println(e);
                if (!i) {
                    i = [a(f.domain), c(), +(new Date) + Math.random() + Math.random()].join(".");
                    var l = {expires: 2592e7,path: "/"};
                    if (b) {
                        var m = "." + b;
                        if (j.indexOf(m) > 0 && j.lastIndexOf(m) == j.length - m.length || j == m)
                            l.domain = m
                    }
                    p.set(e, i, l)
                    println('document.cookie:'+document.cookie);
                }
                return function() {
                    return i
                }
            }(),getCount: function() {
                var a = "count", b = p.get(a);
                return b = (parseInt(b) || 0) + 1, p.set(a, b, {expires: 864e5,path: "/"}), function() {
                    return b
                }
            }(),getFlashVer: function() {
                var a = -1;
                if (g.plugins && g.mimeTypes.length) {
                    var b = g.plugins["Shockwave Flash"];
                    b && b.description && (a = b.description.replace(/([a-zA-Z]|\s)+/, "").replace(/(\s)+r/, ".") + ".0")
                } else if (d.ActiveXObject && !d.opera)
                    for (var c = 16; c >= 2; c--)
                        try {
                            var e = new ActiveXObject("ShockwaveFlash.ShockwaveFlash." + c);
                            if (e) {
                                var f = e.GetVariable("$version");
                                a = f.replace(/WIN/g, "").replace(/,/g, ".")
                            }
                        } catch (h) {
                        }
                return a = parseInt(a, 10), function() {
                    return a
                }
            }(),getContainerId: function(a) {
                var b = s.areaIds;
                if (b) {
                    var c, d = new RegExp("^(" + b.join("|") + ")$", "ig");
                    while (a) {
                        if (a.id && d.test(a.id))
                            return (a.getAttribute("data-desc") || a.id).substr(0, 100);
                        a = a.parentNode
                    }
                }
                return ""
            },getText: function(a) {
                return l.trim((a.getAttribute("text") || a.innerText || a.textContent || a.title || "").substr(0, 100))
            }}, r = {getBase: function() {
                return {p: q.getProject(),u: q.getLocation(),id: q.getGuid(),guid: q.getGuid()}
            },getTrack: function() {
                return {b: q.getBrowser(),c: q.getCount(),r: q.getReferrer(),fl: q.getFlashVer()}
            },getClick: function(a) {
                a = n.fix(a || event);
                var b = a.target, c = b.tagName, d = q.getContainerId(b);
                if (b.type != "submit") {
                    if (c == "AREA")
                        return {f: b.href,c: "area:" + b.parentNode.name,cId: d};
                    var j, k;
                    return c == "IMG" && (j = b), b = m.parentNode(b, "A"), b ? (k = q.getText(b), {f: b.href,c: k ? k : j.src.match(/[^\/]+$/),cId: d}) : !1
                }
                var e = m.parentNode(b, "FORM");
                if (e) {
                    var f = e.id || "", g = b.id, h = {f: e.action,c: "form:" + (e.name || f),cId: d};
                    if ((f == "search-form" || f == "searchForm") && (g == "searchBtn" || g == "search-btn")) {
                        var i = t("kw") || t("search-kw") || t("kw1");
                        h.w = i ? i.value : ""
                    }
                    return h
                }
            },getKeydown: function(a) {
                a = n.fix(a || event);
                if (a.keyCode != 13)
                    return !1;
                var b = a.target, c = b.tagName, d = q.getContainerId(b);
                if (c == "INPUT") {
                    var e = m.parentNode(b, "FORM");
                    if (e) {
                        var f = e.id || "", g = b.id, h = {f: e.action,c: "form:" + (e.name || f),cId: d};
                        if (g == "kw" || g == "search-kw" || g == "kw1")
                            h.w = b.value;
                        return h
                    }
                }
                return !1
            }}, s = {trackUrl: null,clickUrl: null,areaIds: null}, t = function(a) {
            return document.getElementById(a)
        };
        return {version: a,util: q,data: r,config: s,sendLog: function() {
                return d.__monitor_imgs = {}, function(a) {
                    var b = "log_" + +(new Date), c = d.__monitor_imgs[b] = new Image;
                    c.onload = c.onerror = function() {
                        d.__monitor_imgs[b] = null, delete d.__monitor_imgs[b]
                    }, c.src = a
                }
            }(),buildLog: function() {
                var a = "";
                return function(b, c) {
                    if (b === !1)
                        return;
                    b = b || {};
                    var d = r.getBase();
                    b = o.mix(d, b, !0);
                    var e = o.encodeURIJson(b);
                    if (e == a)
                        return;
                    a = e, setTimeout(function() {
                        a = ""
                    }, 500), e += "&t=" + +(new Date), c = c.indexOf("?") > -1 ? c + "&" + e : c + "?" + e, this.sendLog(c)
                }
            }(),log: function(a, b) {
                b = b || "click";
                var c = s[b + "Url"];
                c || alert("Error : the " + b + "url does not exist!"), this.buildLog(a, c)
            },setConf: function(a, b) {
                var c = {};
                return o.isObject(a) ? c = a : c[a] = b, this.config = o.mix(this.config, c, !0), this
            },setUrl: function(a) {
                return a && (this.util.getLocation = function() {
                    return a
                }), this
            },setProject: function(a) {
                return a && (this.util.getProject = function() {
                    return a
                }), this
            },setId: function() {
                var a = [], b = 0, c;
                while (c = arguments[b++])
                    o.isArray(c) ? a = a.concat(c) : a.push(c);
                return this.setConf("areaIds", a), this
            },getTrack: function() {
                var a = this.data.getTrack();
                return this.log(a, "track"), this
            },getClickAndKeydown: function() {
                var a = this;
                return m.on(f, "click", function(b) {
                    var c = a.data.getClick(b);
                    a.log(c, "click")
                }), m.on(f, "keydown", function(b) {
                    var c = a.data.getKeydown(b);
                    a.log(c, "click")
                }), c.getClickAndKeydown = function() {
                    return a
                }, this
            }}
    }(window);
    c.setConf({trackUrl: "http://s.360.cn/w360/s.htm",clickUrl: "http://s.360.cn/w360/c.htm",wpoUrl: "http://s.360.cn/w360/p.htm"}), window.monitor = c
})();
