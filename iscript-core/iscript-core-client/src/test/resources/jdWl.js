(function($) {
    var escapeable = /["\\\x00-\x1f\x7f-\x9f]/g, meta = {"\b": "\\b","\t": "\\t","\n": "\\n","\f": "\\f","\r": "\\r",'"': '\\"',"\\": "\\\\"};
    $.toJSON = typeof JSON === "object" && JSON.stringify ? JSON.stringify : function(o) {
        if (o === null) {
            return "null"
        }
        var type = typeof o;
        if (type === "undefined") {
            return undefined
        }
        if (type === "number" || type === "boolean") {
            return "" + o
        }
        if (type === "string") {
            return $.quoteString(o)
        }
        if (type === "object") {
            if (typeof o.toJSON === "function") {
                return $.toJSON(o.toJSON())
            }
            if (o.constructor === Date) {
                var month = o.getUTCMonth() + 1, day = o.getUTCDate(), year = o.getUTCFullYear(), hours = o.getUTCHours(), minutes = o.getUTCMinutes(), seconds = o.getUTCSeconds(), milli = o.getUTCMilliseconds();
                if (month < 10) {
                    month = "0" + month
                }
                if (day < 10) {
                    day = "0" + day
                }
                if (hours < 10) {
                    hours = "0" + hours
                }
                if (minutes < 10) {
                    minutes = "0" + minutes
                }
                if (seconds < 10) {
                    seconds = "0" + seconds
                }
                if (milli < 100) {
                    milli = "0" + milli
                }
                if (milli < 10) {
                    milli = "0" + milli
                }
                return '"' + year + "-" + month + "-" + day + "T" + hours + ":" + minutes + ":" + seconds + "." + milli + 'Z"'
            }
            if (o.constructor === Array) {
                var ret = [];
                for (var i = 0; i < o.length; i++) {
                    ret.push($.toJSON(o[i]) || "null")
                }
                return "[" + ret.join(",") + "]"
            }
            var name, val, pairs = [];
            for (var k in o) {
                type = typeof k;
                if (type === "number") {
                    name = '"' + k + '"'
                } else {
                    if (type === "string") {
                        name = $.quoteString(k)
                    } else {
                        continue
                    }
                }
                type = typeof o[k];
                if (type === "function" || type === "undefined") {
                    continue
                }
                val = $.toJSON(o[k]);
                pairs.push(name + ":" + val)
            }
            return "{" + pairs.join(",") + "}"
        }
    };
    $.evalJSON = typeof JSON === "object" && JSON.parse ? JSON.parse : function(src) {
        return eval("(" + src + ")")
    };
    $.secureEvalJSON = typeof JSON === "object" && JSON.parse ? JSON.parse : function(src) {
        var filtered = src.replace(/\\["\\\/bfnrtu]/g, "@").replace(/"[^"\\\n\r]*"|true|false|null|-?\d+(?:\.\d*)?(?:[eE][+\-]?\d+)?/g, "]").replace(/(?:^|:|,)(?:\s*\[)+/g, "");
        if (/^[\],:{}\s]*$/.test(filtered)) {
            return eval("(" + src + ")")
        } else {
            throw new SyntaxError("Error parsing JSON, source is not valid.")
        }
    };
    $.quoteString = function(string) {
        if (string.match(escapeable)) {
            return '"' + string.replace(escapeable, function(a) {
                var c = meta[a];
                if (typeof c === "string") {
                    return c
                }
                c = a.charCodeAt();
                return "\\u00" + Math.floor(c / 16).toString(16) + (c % 16).toString(16)
            }) + '"'
        }
        return '"' + string + '"'
    }
})(jQuery);
function setCookieMills(b, c, e) {
    var d = new Date();
    d.setTime(d.getTime() + e);
    var a = window.document.domain.indexOf("360buy") >= 0 ? ".360buy.com" : ".jd.com";
    document.cookie = b + "=" + escape(c) + ";expires=" + d.toGMTString() + ";path=/;domain=" + a
}
function getCookie(b) {
    var a = document.cookie.match(new RegExp("(^| )" + b + "=([^;]*)(;|$)"));
    if (a != null) {
        return unescape(a[2])
    }
    return null
}
function deleteCookie(a) {
    var b = getCookie(a);
    if (b != null) {
        setCookieMills(a, "", -1)
    }
}
function seClick(b, d, f) {
    var a = "seWids" + b;
    var c = getCookie(a);
    if (c != null) {
        var e = c.toString().indexOf(f);
        if (e < 0) {
            c = c + "," + f
        }
    } else {
        c = f
    }
    setCookieMills(a, c, 86400000);
    log(2, 2, d, f)
}
function appendJSONCookie(cookieName, key, wid, Mills) {
    var ns = eval("(" + getCookie(cookieName) + ")");
    if (ns == null || ns == "") {
        ns = new Object()
    }
    if (ns[key] == null) {
        ns[key] = ""
    }
    var pos = ns[key].indexOf(wid);
    if (pos < 0) {
        ns[key] = ns[key] + "," + wid
    }
    setCookieMills(cookieName, $.toJSON(ns), Mills)
}
function reBook(b, f, a) {
	debug.log('reBook:'+f);
    var e = "_rtbook";
    var c = f.toString().split("#")[0];
    appendJSONCookie(e, b, c, 86400000);
    log(3, b, c, a)
}
function fe(a, b, c) {
    log("f", a, b, c)
}
function reClick2012(a, e, b) {
	debug.log('reClick2012:'+e);
    var d = "reHome2012";
    var c = e.toString().split("#")[0];
    appendJSONCookie(d, a, c, 86400000);
    log(3, a, c, b)
}
function reClickCube(e, b) {
    var a = "_rdCube";
    appendJSONCookie(a, "p" + e, b, 86400000)
}
function mark(b, a) {
    log(1, a, b)
}
function isMeta(b) {
    if (b.metaKey || b.altKey || b.ctrlKey || b.shiftKey) {
        return true
    }
    var c = b.which, a = b.button;
    if (!c && a !== undefined) {
        return (!a & 1) && (!a & 2) && (a & 4)
    } else {
        if (c === 2) {
            return true
        } else {
            if (a === 2) {
                return true
            }
        }
    }
    return false
}
document.onclick = function(h) {
    h = h || event;
    if (!h.clientX && !h.clientY && !h.pageX && !h.pageY) {
        return
    }
    var u = document, b = window;
    var a = tag = h.srcElement || h.target;
    var l = $(tag).attr("clstag");
    var q = "";
    while (!l) {
        tag = tag.parentNode;
        if (!tag || (tag.nodeName == "BODY")) {
            break
        }
        l = $(tag).attr("clstag")
    }
    if (l) {
    	debug.log('(l):'+l);
        var f = l.split("|"), c = f[1], p = f[2], r = f[3];
        if (c === "keycount") {
            $(a).attr("href") ? log(p, r, "Q", $(a).attr("href")) : log(p, r, "Q");
            q = p + "|" + r;
            if ($(a).attr("href") && /http:\/\/.*?/.exec($(a).attr("href")) && $(a).attr("target") !== "_blank" && !isMeta(h)) {
                h.preventDefault ? h.preventDefault() : h.returnValue = false;
                setTimeout(function() {
                    b.location.href = $(a).attr("href")
                }, 200)
            }
        }
    }
    var i = this.location.hostname.toLowerCase();
    if (/(sale|mall|jmall|pop).(jd|360buy).com/.test(i) || b.ja_heat_map) {
        var o = 0, n = 0, g = b.screen.width >= 1210 && i == "item.jd.com" ? 1210 : 990, m = u.body.clientWidth > g ? Math.round((u.body.clientWidth - g) / 2) : 0;
        if (h.pageX || h.pageY) {
            o = h.pageX;
            n = h.pageY
        } else {
            o = h.clientX + u.body.scrollLeft - u.body.clientLeft;
            n = h.clientY + u.body.scrollTop - u.body.clientTop
        }
        log("d", "c", q || "-", o + "x" + n, u.body.scrollWidth + "x" + u.body.scrollHeight, m)
    }
};
function HashMap() {
    this.values = new Object()
}
HashMap.prototype.Set = function(a, b) {
    this.values[a] = b
};
HashMap.prototype.Get = function(a) {
    return this.values[a]
};
HashMap.prototype.Contains = function(a) {
    return this.values.hasOwnProperty(a)
};
HashMap.prototype.Remove = function(a) {
    delete this.values[a]
};
var SucInfoMethod = {Init: function() {
        this.orderDetailMap = new HashMap();
        this.rSM = new HashMap();
        debug.log('SucInfo_OrderDetail:'+SucInfo_OrderDetail);
        var b = SucInfo_OrderDetail.toString().split(",");
        for (var c = 0; c < b.length; c++) {
            var a = b[c].split(":");
            this.orderDetailMap.Set(a[0], a[1]);
            this.sku = a[0]
        }
    },GetSkuNum: function(a) {
        return this.orderDetailMap.Get(a)
    },Contains: function(a) {
        return this.orderDetailMap.Contains(a)
    },GetDefaultSku: function() {
        return this.sku
    },ARS: function(a) {
        this.rSM.Set(a, 0)
    },RSContains: function(a) {
        if (this.rSM.Contains(a)) {
            return 1
        } else {
            return 0
        }
    }};
function RecommendTrans(recName, tag, logtype) {
	 debug.log('recName:'+recName);
    var cookieNames = recName.split(",");
    for (var i = 0; i < cookieNames.length; i++) {
        var recCookies = eval("(" + getCookie(cookieNames[i]) + ")");
        for (var k in recCookies) {
            if (recCookies[k] != "") {
                if (k == "cai2012") {
                    loginfo(recCookies[k], k.toString(), "R", logtype)
                } else {
                    loginfo(recCookies[k], k.toString(), tag, logtype)
                }
            }
        }
    }
}
function simpleMold(d, a, g, f, b) {
    for (var e = 0; e < d.length; e++) {
        var c = getCookie(g + d[e]);
        if (c != null && c != "") {
            loginfo(c, d[e], a, f, b)
        }
    }
}
function complexMold(cookieArrary, tag, prefix, logtype, flag) {
    for (var i = 0; i < cookieArrary.length; i++) {
        var items = eval("(" + getCookie(prefix + cookieArrary[i]) + ")");
        if (items != null) {
            for (var k in items) {
                if (items[k] != "") {
                    loginfo(items[k], k.toString(), tag, logtype, flag)
                }
            }
        }
    }
}
function loginfo(k, j, a, e, h) {
	debug.log('loginfo:'+k);
    var g = k.split(",");
    var c = SucInfo_OrderId, f = SucInfo_OrderType, b = SucInfo_OrderDetail;
    for (var d = 0; d < g.length; d++) {
        if (g[d].length > 0) {
            var l = g[d].toString().split("#")[0];
            if (SucInfoMethod.Contains(l)) {
                if (h) {
                    log(e, a, j.concat(".o"), c, f, b, l + ":" + SucInfoMethod.GetSkuNum(l));
                    log("4", "R" + j.concat(".o"), c, f, b, l, SucInfoMethod.GetSkuNum(l))
                } else {
                    log(e, a + j, c, f, b, l, SucInfoMethod.GetSkuNum(l))
                }
            }
        }
    }
}
function isChecked() {
    SucInfo_OrderId = window.SucInfo_OrderId || JA.util.getParameter(document.location.href, "suc_orderid") || undefined;
    SucInfo_OrderType = window.SucInfo_OrderType || JA.util.getParameter(document.location.href, "suc_ordertype") || undefined;
    SucInfo_OrderDetail = window.SucInfo_OrderDetail || decodeURIComponent(JA.util.getParameter(document.location.href, "suc_sku")) || undefined;
    return SucInfo_OrderId && SucInfo_OrderDetail
}
function funLoad() {
    var a = getCookie("pin");
    if (a != null && a.length > 0) {
        setCookieMills("rpin", a, 259200000)
    }
}
function Clublog() {
    var b = this.location.pathname.toLowerCase();
    var a = this.location.hostname.toLowerCase();
    if ((b.indexOf("/cart.html", 0) >= 0) || (b.indexOf("shoppingcart", 0) >= 0)) {
        log("R2&Page", "Show")
    } else {
        if (b.indexOf("user_home", 0) >= 0) {
            log("R3&Page", "Show")
        } else {
            if ((b.indexOf("initcart.html", 0) >= 0) || (b.indexOf("addtocart.html", 0) >= 0) || (b.indexOf("initcart.aspx", 0) >= 0)) {
                log("R4R5&Page", "Show")
            } else {
                if ((b.indexOf("normal/list.action", 0) >= 0) || (b.indexOf("orderlist.aspx", 0) >= 0)) {
                    log("DDR&Page", "Show")
                } else {
                    if (a == "home.360buy.com") {
                        if (b == "/") {
                            log("R3&Page", "Show")
                        }
                    }
                }
            }
        }
    }
}
function getHistory() {
    var b = decodeURIComponent(escape(getCookie("pin")));
    var d = getCookie("_ghis");
    var c = window.document.location.host.toLowerCase().indexOf("360buy.com") >= 0 ? "360buy" : "jd";
    if (d == null && b != null) {
        var a = "http://gh." + c + ".com/BuyHistory.aspx?mid=" + encodeURIComponent(b);
        $.ajax({url: a,type: "GET",dataType: "jsonp",success: function(e) {
                var f = e.SSkus;
                var g = e.UserInsterest;
                if (f.toString().length > 0) {
                    setCookieMills("_ghis", f.toString().substring(0, 51))
                }
                if (g.toString().length > 0) {
                    setCookieMills("_ghit", g)
                }
            }})
    }
}
(function() {
    function HashMap() {
        this.values = new Object()
    }
    HashMap.prototype.Set = function(key, value) {
        this.values[key] = value
    };
    HashMap.prototype.Get = function(key) {
        return this.values[key]
    };
    HashMap.prototype.Contains = function(key) {
        return this.values.hasOwnProperty(key)
    };
    HashMap.prototype.Remove = function(key) {
        delete this.values[key]
    };
    function SortedHashMap(IComparer, IGetKey) {
        this.IComparer = IComparer;
        this.IGetKey = IGetKey;
        this.a = new Array();
        this.h = new HashMap()
    }
    SortedHashMap.prototype.Add = function(key, value) {
        if (this.ContainsKey(key)) {
            this.Remove(key)
        }
        this.a.push(value);
        this.a.sort(this.IComparer);
        for (var i = 0; i < this.a.length; i++) {
            var key = this.IGetKey(this.a[i]);
            this.h.Set(key, i)
        }
    };
    SortedHashMap.prototype.Insert = function(value, maxlength) {
        for (var i = 0, l = this.a.length; i < l; i++) {
            if (this.a[i].s === value.s) {
                this.a.splice(i, 1);
                break
            }
        }
        if (this.a.length >= maxlength) {
            this.a.splice(maxlength - 1, 1)
        }
        this.a.unshift(value)
    };
    SortedHashMap.prototype.Get = function(key) {
        return this.a[this.h.Get(key)]
    };
    SortedHashMap.prototype.Count = function() {
        return this.a.length
    };
    SortedHashMap.prototype.Remove = function(key) {
        if (!this.h.Contains(key)) {
            return
        }
        var index = this.h.Get(key);
        this.a.splice(index, 1);
        this.h.Remove(key)
    };
    SortedHashMap.prototype.ContainsKey = function(key) {
        return this.h.Contains(key)
    };
    SortedHashMap.prototype.Clear = function() {
        this.a = new Array();
        this.h = new HashMap()
    };
    SortedHashMap.prototype.GetJson = function() {
        return $.toJSON(this.a)
    };
    function ThirdType(thirdType, sku, value) {
        this.t = thirdType;
        this.v = 5;
        this.s = 0;
        if (arguments.length > 1) {
            this.s = sku
        }
        if (arguments.length > 2) {
            this.v = value
        }
    }
    ThirdType.prototype.Increase = function() {
        this.v = this.v + 2
    };
    ThirdType.prototype.Decrease = function() {
        this.v = this.v - 1
    };
    ThirdType.prototype.SetSku = function(sku) {
        this.s = sku
    };
    Ttracker = {IComparer: function(a, b) {
            return b.v - a.v
        },IGetKey: function(a) {
            return a.t
        },isbook: function(id) {
            return id > 10000000 && id < 20000000
        },trace: function() {
            var crumb = $(".breadcrumb span a");
            if (crumb.length < 2) {
                return
            }
            var thref = $(crumb[1]).attr("href");
            if (thref === "" || thref == undefined) {
                return
            }
            var sortidmaths = thref.match(/[http:\/\/\w*?.\w*?.com]*\/[products\/]*\d*-\d*-(\d*).html/i);
            var sortid = (sortidmaths && sortidmaths.length == 2) ? sortidmaths[1] : 0;
            if (!sortid) {
                return
            }
            var wid = $("#name").attr("PshowSkuid") || (pageConfig ? (pageConfig.product ? pageConfig.product.skuid : 0) : 0);
            this.view(sortid, wid);
            this.viewtypewid()
        },viewtypewid: function() {
            var maps = Ttracker.util.Vv("typewid");
            if (maps) {
                Ttracker.util.Wv("typewid", "", -63072000000)
            }
        },viewhisotry: function(t, s, cname) {
            var nview = {t: t,s: s};
            var bookmap = new SortedHashMap(this.IComparer, this.IGetKey);
            var bview = Ttracker.util.Vv(cname);
            if (bview) {
                try {
                    if (bview.indexOf(".") > 0) {
                    	debug.log('bview:'+bview);
                        var viewarray = bview.split("|");
                        for (var j = viewarray.length - 1; j >= 0; j--) {
                            var book = viewarray[j].split(".");
                            bookmap.Insert({t: Number(book[0]),s: Number(book[1])}, 8)
                        }
                    } else {
                        var bviews = eval("(" + bview + ")");
                        if (bviews.length > 0 && bviews[0].d != undefined) {
                            Ttracker.util.Wv(cname, "", -63072000000)
                        } else {
                            for (var i = bviews.length - 1; i >= 0; i--) {
                                bookmap.Insert(bviews[i], 8)
                            }
                        }
                    }
                } catch (e) {
                    Ttracker.util.Wv(cname, "", -63072000000)
                }
            }
            bookmap.Insert(nview, 8);
            var cvalue = "";
            for (var k = 0, klen = bookmap.a.length; k < klen; k++) {
                cvalue += (bookmap.a[k].t + "." + bookmap.a[k].s + (k == klen - 1 ? "" : "|"))
            }
            cvalue && Ttracker.util.Wv(cname, cvalue, 63072000000)
        },viewrate: function(t, s, cname) {
            var ntw = {t: t,s: s,v: 5};
            var sitesortmap = new SortedHashMap(this.IComparer, this.IGetKey);
            var vrate = Ttracker.util.Vv(cname);
            if (vrate) {
                try {
                    if (vrate.indexOf(".") > 0) {
                    	debug.log('vrate:'+vrate);
                        var ratearray = vrate.split("|");
                        for (var j = ratearray.length - 1; j >= 0; j--) {
                            var tw = ratearray[j].split(".");
                            var tv = Number(tw[2] || 0), tid = Number(tw[0]);
                            tv = t === tid ? tv : (tv - 1);
                            sitesortmap.Add(Number(tw[0]), {t: Number(tw[0]),s: Number(tw[1]),v: tv}, 8)
                        }
                    } else {
                        var vrates = eval("(" + vrate + ")");
                        if (vrates.length > 0 && vrates[0].d != undefined) {
                            Ttracker.util.Wv(cname, "", -63072000000)
                        } else {
                            for (var i = 0; i < vrates.length; i++) {
                                var rate = vrates[i];
                                if (rate.t != t) {
                                    rate.v -= 1
                                }
                                sitesortmap.Add(rate.t, rate)
                            }
                        }
                    }
                } catch (e) {
                    Ttracker.util.Wv(cname, "", -63072000000)
                }
            }
            if (!sitesortmap.ContainsKey(t)) {
                sitesortmap.Add(t, ntw)
            } else {
                var curtt = sitesortmap.Get(t);
                curtt.s = s ? s : curtt.s;
                curtt.v += 2
            }
            if (sitesortmap.Count() > 8) {
                var del = sitesortmap.a[sitesortmap.Count() - 1];
                sitesortmap.Remove(del.t)
            }
            var cvalue = "";
            for (var k = 0, klen = sitesortmap.a.length; k < klen; k++) {
                cvalue += (sitesortmap.a[k].t + "." + sitesortmap.a[k].s + "." + sitesortmap.a[k].v + (k == klen - 1 ? "" : "|"))
            }
            cvalue && Ttracker.util.Wv(cname, cvalue, 63072000000)
        },view: function(t, s) {
            var tid = Number(t), sku = Number(s), _this = this;
            $.ajax({url: "http://diviner.jd.com/cookie?ck=" + tid + "." + sku,dataType: "jsonp",success: function(json) {
                    if (typeof (json) == "object" && json.errCode == 0) {
                        _this.util.Wv("atw", "", -63072000000);
                        if (_this.isbook(sku)) {
                            _this.util.Wv("btw", "", -63072000000);
                            _this.util.Wv("bview", "", -63072000000)
                        }
                    }
                }});
            $.ajax({url: "http://x.jd.com/aview?ck=" + tid + "." + sku,dataType: "jsonp",success: function(res) {
                    if (typeof (res) == "object" && res.errCode == 0) {
                        _this.util.Wv("aview", "", -63072000000)
                    }
                }})
        }};
    Ttracker.util = {Wv: function(n, v, t) {
            var d = window.document.domain.indexOf("360buy") >= 0 ? ".360buy.com" : ".jd.com";
            n = n + "=" + v + "; path=/; ";
            t && (n += "expires=" + (new Date(new Date().getTime() + t)).toGMTString() + "; ");
            n += "domain=" + d + ";";
            document.cookie = n
        },Vv: function(n) {
        	debug.log('document.cookie:'+document.cookie);
            for (var b = [], c = document.cookie.split(";"), n = RegExp("^\\s*" + n + "=\\s*(.*?)\\s*$"), d = 0; d < c.length; d++) {
                var e = c[d]["match"](n);
                e && b.push(e[1])
            }
            return b[0]
        }};
    Ttracker.trace()
})();
(function() {
    var ac = window, an = document, aB = encodeURIComponent, ad = decodeURIComponent, R = void 0, N = "push", F = "join", J = "split", Q = "length", w = "indexOf", m = "prototype", H = "toLowerCase";
    var r = {};
    r.util = {join: function(l) {
            if (l instanceof Array) {
                var s = "";
                for (var p = 0, g = l.length; p < g; p++) {
                    s += l[p] + ((p == g - 1) ? "" : "|||")
                }
                return s
            }
            return l
        },getParameter: function(p, l) {
            var s = new RegExp("(?:^|&|[?]|[/])" + l + "=([^&]*)");
            var g = s.exec(p);
            return g ? aB(g[1]) : ""
        },Wv: function(s, g, p, l) {
            s = s + "=" + g + "; path=/; ";
            l && (s += "expires=" + (new Date(new Date().getTime() + l)).toGMTString() + "; ");
            p && (s += "domain=" + p + ";");
            an.cookie = s
        },Vv: function(y) {
            for (var g = [], t = an.cookie[J](";"), l = RegExp("^\\s*" + y + "=\\s*(.*?)\\s*$"), s = 0; s < t[Q]; s++) {
                var p = t[s]["match"](l);
                p && g[N](p[1])
            }
            return g
        }};
    var aH = 0;
    function ah(g) {
        return (g ? "_" : "") + aH++
    }
    var aj = ah(), ae = ah(), ag = ah(), I = ah(), d = ah(), aJ = ah(), X = ah(), ao = ah(), af = ah(), ai = ah(), ay = ah(), ax = ah(), aF = ah(), aO = ah(), Z = ah(), U = ah(), B = ah(), z = ah(), M = ah(), aA = ah(), n = ah(), A = ah(), i = ah(), a = ah(), aM = ah(), av = ah(), P = ah(), aK = ah(), f = ah(), at = ah(), c = ah(), ap = ah(), aS = ah(), b = ah(), aw = ah();
    var aN = function() {
        var s = {};
        this.set = function(y, t) {
            s[y] = t
        };
        this.get = function(t) {
            return s[t] !== R ? s[t] : null
        };
        this.m = function(C) {
            var t = this.get(C);
            var D = t == R || t === "" ? 0 : 1 * t;
            s[C] = D + 1
        };
        this.set(aj, "UA-J2011-1");
        var l = window.document.domain.indexOf("360buy") >= 0 ? "360buy.com" : "jd.com";
        this.set(I, l);
        this.set(ag, k());
        this.set(d, Math.round((new Date).getTime() / 1000));
        this.set(aJ, 15552000000);
        this.set(X, 1296000000);
        this.set(ao, 1800000);
        this.set(aO, T());
        var g = ab();
        this.set(Z, g.name);
        this.set(U, g.version);
        this.set(B, G());
        var p = aI();
        this.set(z, p.D);
        this.set(M, p.C);
        this.set(aA, p.language);
        this.set(n, p.javaEnabled);
        this.set(A, p.characterSet);
        this.set(aK, am);
        this.set(aS, new Date().getTime())
    };
    var am = "baidu:wd,baidu:word,so.com:q,so.360.cn:q,360so.com:q,360sou.com:q,baidu:q1,m.baidu:word,m.baidu:w,wap.soso:key,m.so:q,page.yicha:key,sz.roboo:q,i.easou:q,wap.sogou:keyword,google:q,soso:w,sogou:query,youdao:q,ucweb:keyword,ucweb:word,114so:kw,yahoo:p,yahoo:q,live:q,msn:q,bing:q,aol:query,aol:q,daum:q,eniro:search_word,naver:query,pchome:q,images.google:q,lycos:query,ask:q,netscape:query,cnn:query,about:terms,mamma:q,voila:rdata,virgilio:qs,alice:qs,yandex:text,najdi:q,seznam:q,search:q,wp:szukaj,onet:qt,szukacz:q,yam:k,kvasir:q,ozu:q,terra:query,rambler:query".split(","), aR = function() {
        return Math.round((new Date).getTime() / 1000)
    }, v = function() {
        return Math.round(Math.random() * 2147483647)
    }, Y = function() {
        return v() ^ al() & 2147483647
    }, k = function() {
        return V(an.domain)
    }, aI = function() {
        var l = {}, g = ac.navigator, p = ac.screen;
        l.D = p ? p.width + "x" + p.height : "-";
        l.C = p ? p.colorDepth + "-bit" : "-";
        l.language = (g && (g.language || g.browserLanguage) || "-")[H]();
        l.javaEnabled = g && g.javaEnabled() ? 1 : 0;
        l.characterSet = an.characterSet || an.charset || "-";
        return l
    }, T = function() {
        var D, C, y, t;
        y = "ShockwaveFlash";
        if ((D = (D = window.navigator) ? D.plugins : R) && D[Q] > 0) {
            for (C = 0; C < D[Q] && !t; C++) {
                y = D[C], y.name[w]("Shockwave Flash") > -1 && (t = y.description[J]("Shockwave Flash ")[1])
            }
        } else {
            y = y + "." + y;
            try {
                C = new ActiveXObject(y + ".7"), t = C.GetVariable("$version")
            } catch (s) {
            }
            if (!t) {
                try {
                    C = new ActiveXObject(y + ".6"), t = "WIN 6,0,21,0", C.AllowScriptAccess = "always", t = C.GetVariable("$version")
                } catch (p) {
                }
            }
            if (!t) {
                try {
                    C = new ActiveXObject(y), t = C.GetVariable("$version")
                } catch (l) {
                }
            }
            t && (t = t[J](" ")[1][J](","), t = t[0] + "." + t[1] + " r" + t[2])
        }
        var K = r.util.Vv("_r2");
        D = t ? (t + (K[Q] > 0 ? ("_" + K[0]) : "")) : "-";
        var g = r.util.Vv("limgs");
        D = D + (g[Q] > 0 ? ("_" + g[0]) : "");
        return D
    }, aq = function(g) {
        return R == g || "-" == g || "" == g
    }, V = function(l) {
        var g = 1, s = 0, p;
        if (!aq(l)) {
            g = 0;
            for (p = l[Q] - 1; p >= 0; p--) {
                s = l.charCodeAt(p), g = (g << 6 & 268435455) + s + (s << 14), s = g & 266338304, g = s != 0 ? g ^ s >> 21 : g
            }
        }
        return g
    }, al = function() {
        var p = aI();
        for (var l = p, g = ac.navigator, l = g.appName + g.version + l.language + g.platform + g.userAgent + l.javaEnabled + l.D + l.C + (an.cookie ? an.cookie : "") + (an.referrer ? an.referrer : ""), g = l.length, s = ac.history.length; s > 0; ) {
            l += s-- ^ g++
        }
        return V(l)
    }, ab = function() {
        var g = {name: "other",version: "0"}, s = navigator.userAgent.toLowerCase();
        browserRegExp = {se360: /360se/,se360_2x: /qihu/,ie: /msie[ ]([\w.]+)/,firefox: /firefox[|\/]([\w.]+)/,chrome: /chrome[|\/]([\w.]+)/,safari: /version[|\/]([\w.]+)(\s\w.+)?\s?safari/,opera: /opera[|\/]([\w.]+)/};
        for (var p in browserRegExp) {
            var l = browserRegExp[p].exec(s);
            if (l) {
                g.name = p;
                g.version = l[1] || "0";
                break
            }
        }
        return g
    }, G = function() {
        var g = /(win|android|linux|nokia|ipad|iphone|ipod|mac|sunos|solaris)/.exec(navigator.platform.toLowerCase());
        return g == null ? "other" : g[0]
    }, aG = function() {
        var p = "", y = ["jwotest_product", "jwotest_list", "jwotest_cart", "jwotest_orderinfo", "jwotest_homepage", "jwotest_other1", "jwotest_other2", "jwotest_other3"];
        for (var t = 0, g = y.length; t < g; t++) {
            var s = r.util.Vv(y[t]);
            if (s[Q] == 0) {
                continue
            }
            var C = ad(s[0]).match(/=(.*?)&/gi), l = [];
            if (C == null) {
                continue
            }
            $.each(C, function(K, D) {
                l.push(K == 0 ? "T" + D.substring(1, D.length - 1) : D.substring(1, D.length - 1))
            });
            p += l[F]("-") + ";"
        }
        return p
    }, aE = function(t) {
        t.set(af, an.location.hostname);
        t.set(ai, an.title.replace(/\$/g, ""));
        t.set(ay, an.location.pathname);
        t.set(ax, an.referrer.replace(/\$/g, ""));
        t.set(aF, an.location.href);
        var g = r.util.Vv("__jda"), L = g[Q] > 0 ? g[0][J](".") : null;
        t.set(ae, L ? L[1] : Y());
        t.set(i, L ? L[2] : t.get(d));
        t.set(a, L ? L[3] : t.get(d));
        t.set(aM, L ? L[4] : t.get(d));
        t.set(av, L ? L[5] : 1);
        var C = r.util.Vv("__jdv"), y = C[Q] > 0 ? C[0][J]("|") : null, l = y && y.length == 5 ? 1 : 0;
        t.set(f, y ? y[0 + l] : "direct");
        t.set(at, y ? y[1 + l] : "-");
        t.set(c, y ? y[2 + l] : "none");
        t.set(ap, y ? y[3 + l] : "-");
        var K = r.util.Vv("__jdb"), D = K[Q] > 0 ? K[0][J](".") : null, l = D && D.length == 4 ? 1 : 0;
        t.set(P, D ? D[0 + l] : 0);
        t.set(b, aG() || "-");
        var s = JA.util.Vv("clickid"), p = s[Q] && s[0];
        t.set(aw, p);
        return !0
    }, aC = function() {
        var l = r.util.Vv("__jdb"), g = l[Q] > 0 ? l[0][J](".") : null;
        if (g && g.length == 1) {
            return g[0] * 1
        } else {
            if (g && g.length == 4) {
                return g[1] * 1
            } else {
                return 0
            }
        }
    }, aD = function(aX) {
        var s = an.location.href, C = an.referrer, aU = aX.get(I), y = r.util.getParameter(s, "utm_source"), t = [], O = aX.get(f), L = aX.get(at), K = aX.get(c), g = r.util.Vv("__jdb")[Q] == 0;
        if (y) {
            var l = r.util.getParameter(s, "utm_campaign"), aW = r.util.getParameter(s, "utm_medium"), W = r.util.getParameter(s, "utm_term");
            t[N](y);
            t[N](l || "-");
            t[N](aW || "-");
            t[N](W || "not set");
            aX.set(ap, t[3])
        } else {
            var p = C && C[J]("/")[2], aV = false;
            if (p && p[w](aU) < 0) {
                for (var aa = aX.get(aK), ak = 0; ak < aa.length; ak++) {
                    var aT = aa[ak][J](":");
                    if (p[w](aT[0][H]()) > -1 && C[w]((aT[1] + "=")[H]()) > -1) {
                        var D = r.util.getParameter(C, aT[1]);
                        t[N](aT[0]);
                        t[N]("-");
                        t[N]("organic");
                        t[N](D || "not set");
                        aX.set(ap, t[3]);
                        aV = true;
                        break
                    }
                }
                if (!aV) {
                    if (p[w]("zol.com.cn") > -1) {
                        t[N]("zol.com.cn");
                        t[N]("-");
                        t[N]("cpc");
                        t[N]("not set")
                    } else {
                        t[N](p);
                        t[N]("-");
                        t[N]("referral");
                        t[N]("-")
                    }
                }
            }
        }
        if (g || (!g && t[Q] > 0 && (t[0] !== O || t[1] !== L || t[2] !== K) && t[2] !== "referral")) {
            aX.set(f, t[0] || aX.get(f));
            aX.set(at, t[1] || aX.get(at));
            aX.set(c, t[2] || aX.get(c));
            aX.set(ap, t[3] || aX.get(ap));
            ar(aX)
        } else {
            h(aX)
        }
    }, j = function(l, g) {
    	debug.log('g:'+g);
        var p = g.split(".");
        l.set(i, p[2]);
        l.set(a, p[4]);
        l.set(aM, aR());
        l.m(av);
        l.set(P, 1)
    }, E = function(l) {
        var g = l.get(d);
        l.set(ae, Y());
        l.set(i, g);
        l.set(a, g);
        l.set(aM, g);
        l.set(av, 1);
        l.set(P, 1)
    }, h = function(g) {
        g.m(P)
    }, u = function(g) {
        return [g.get(ag), g.get(ae) || "-", g.get(i) || "-", g.get(a) || "-", g.get(aM) || "-", g.get(av) || 1][F](".")
    }, e = function(g) {
        return [g.get(ag), g.get(P) || 1, g.get(ae) + "|" + g.get(av) || 1, g.get(aM) || g.get(d)][F](".")
    }, x = function(g) {
        return [g.get(ag), g.get(f) || an.domain, g.get(at) || "(direct)", g.get(c) || "direct", g.get(ap) || "-"][F]("|")
    }, ar = function(g) {
        var l = r.util.Vv("__jda");
        l.length == 0 ? E(g) : j(g, l[0])
    };
    var q = new aN(), au = function() {
        this.a = {};
        this.add = function(g, l) {
            this.a[g] = l
        };
        this.get = function(g) {
            return this.a[g]
        };
        this.toString = function() {
            return this.a[F]("&")
        }
    }, o = function(l, g) {
        g.add("jdac", l.get(aj)), g.add("jduid", l.get(ae)), g.add("jdsid", l.get(ae) + "|" + l.get(av)), g.add("jdje", l.get(n)), g.add("jdsc", l.get(M)), g.add("jdsr", l.get(z)), g.add("jdul", l.get(aA)), g.add("jdcs", l.get(A)), g.add("jddt", l.get(ai) || "-"), g.add("jdmr", aB(l.get(ax))), g.add("jdhn", l.get(af) || "-"), g.add("jdfl", l.get(aO)), g.add("jdos", l.get(B)), g.add("jdbr", l.get(Z)), g.add("jdbv", l.get(U)), g.add("jdwb", l.get(i)), g.add("jdxb", l.get(a)), g.add("jdyb", l.get(aM)), g.add("jdzb", l.get(av)), g.add("jdcb", l.get(P)), g.add("jdusc", l.get(f) || "direct"), g.add("jducp", l.get(at) || "-"), g.add("jdumd", l.get(c) || "-"), g.add("jduct", l.get(ap) || "-"), g.add("jdlt", typeof jdpts != "object" ? 0 : jdpts._st == undefined ? 0 : l.get(aS) - jdpts._st), g.add("jdtad", l.get(b)), g.add("jdak", l.get(aw))
    }, aQ = function(l, g, p, s) {
        g.add("jdac", l.get(aj)), g.add("jduid", l.get(ae)), g.add("jdsid", l.get(ae) + "|" + l.get(av)), g.add("jdje", "-"), g.add("jdsc", "-"), g.add("jdsr", "-"), g.add("jdul", "-"), g.add("jdcs", "-"), g.add("jddt", "-"), g.add("jdmr", aB(l.get(ax))), g.add("jdhn", "-"), g.add("jdfl", "-"), g.add("jdos", "-"), g.add("jdbr", "-"), g.add("jdbv", "-"), g.add("jdwb", "-"), g.add("jdxb", "-"), g.add("jdyb", "-"), g.add("jdzb", l.get(av)), g.add("jdcb", s ? (aC() + s) : l.get(P)), g.add("jdusc", "-"), g.add("jducp", "-"), g.add("jdumd", "-"), g.add("jduct", "-"), g.add("jdlt", 0), g.add("jdtad", p), g.add("jdak", l.get(aw))
    }, aP = function() {
        aE(q) && aD(q);
        var l = new au(), g = q.get(I);
        o(q, l);
        r.util.Wv("__jda", u(q), g, q.get(aJ));
        r.util.Wv("__jdb", e(q), g, q.get(ao));
        r.util.Wv("__jdc", q.get(ag), g);
        r.util.Wv("__jdv", x(q), g, q.get(X));
        r.util.Wv("clickid", "0", g, -84600000);
        return l.a
    }, az = function() {
        var g = new au();
        o(q, g);
        return g.a
    }, aL = function(g, l) {
        var p = new au();
        aQ(q, p, g, l);
        return p.a
    };
    r.tracker = {loading: function(t, s, p, y) {
            var C = p && (p.jdac + "||" + p.jdje + "||" + p.jdsc + "||" + p.jdsr + "||" + p.jdul + "||" + p.jdcs + "||" + aB(p.jddt) + "||" + p.jdhn + "||" + p.jdfl + "||" + p.jdos + "||" + p.jdbr + "||" + p.jdbv + "||" + p.jdwb + "||" + p.jdxb + "||" + p.jdyb + "||" + p.jdzb + "||" + p.jdcb + "||" + p.jdusc + "||" + p.jducp + "||" + p.jdumd + "||" + p.jduct + "||" + p.jdlt + "||" + p.jdtad), g = r.util.Vv("pin");
            var l = ("https:" == document.location.protocol ? "https://cscssl" : "http://csc") + ".jd.com/log.ashx?type1=" + aB(t) + "&type2=" + aB(s) + "&pin=" + aB(g[Q] > 0 ? g[0] : "-") + "&uuid=" + p.jduid + "&sid=" + p.jdsid + (p.jdak ? ("&utmp=" + document.location.href + aB("&clickid=" + p.jdak)) : "") + "&referrer=" + aB(p.jdmr || "-") + "&jinfo=" + C + "&data=" + aB(JA.util.join(y)) + "&callback=?";
            $.getJSON(l, function() {
            })
        },ngloader: function(s, K) {
            var C = "";
            for (var g in K) {
                C += ((g + "=" + K[g]) + "$")
            }
            C = C.substring(0, C.length - 1);
            var l = r.util.Vv("pin"), y = az();
            var p = ("https:" == document.location.protocol ? "https://mercuryssl" : "http://mercury") + ".jd.com/log.gif?t=" + s + "&m=" + q.get(aj) + "&pin=" + aB(l) + "&uid=" + y.jduid + "&sid=" + y.jdsid + (y.jdak ? ("&cul=" + document.location.href + aB("&clickid=" + y.jdak)) : "") + "&v=" + aB(C) + "&ref=" + aB(an.referrer) + "&rm=" + (new Date).getTime();
            var D = new Image(1, 1);
            D.src = p;
            D.onload = function() {
                D.onload = null;
                D.onerror = null
            };
            D.onerror = function() {
                D.onload = null;
                D.onerror = null
            }
        },bloading: function(p, g, s) {
            var l = aP();
            this.loading(p, g, l, s);
            var t = {je: l.jdje,sc: l.jdsc,sr: l.jdsr,ul: l.jdul,cs: l.jdcs,dt: l.jddt,hn: l.jdhn,fl: l.jdfl,os: l.jdos,br: l.jdbr,bv: l.jdbv,wb: l.jdwb,xb: l.jdxb,yb: l.jdyb,zb: l.jdzb,cb: l.jdcb,usc: l.jdusc,ucp: l.jducp,umd: l.jdumd,uct: l.jduct,lt: l.jdlt,ct: s,tad: l.jdtad};
            r.tracker.ngloader("www.100000", t)
        },aloading: function(p, l, s) {
            var g = az();
            this.loading(p, l, g, s)
        },adshow: function(l) {
            var g = aL(l);
            this.loading("AD", "IM", g, "")
        },adclick: function(l) {
            var g = aL(l, 1);
            this.loading("AD", "CL", g, "")
        }};
    window.JA = r;
    r.tracker.bloading("J", "A", new Date().getTime());
    var S = $(".w .crumb a").length === 5 && /e.jd.com\/products\/(\d*)-(\d*)-(\d*).html[\w\W]*?e.jd.com\/(\d*).html/.exec($(".w .crumb").html());
    if ((window.pageConfig && window.pageConfig.product && window.pageConfig.product.cat) || S) {
        r.tracker.ngloader("item.010001", {sku: S[4] || window.pageConfig.product.skuid,cid1: S[1] || window.pageConfig.product.cat[0],cid2: S[2] || window.pageConfig.product.cat[1],cid3: S[3] || window.pageConfig.product.cat[2],brand: S ? "0" : window.pageConfig.product.brand})
    }
    (function() {
        if (isChecked()) {
            SucInfoMethod.Init();
            var t = getCookie("_distM");
            if (t && t == SucInfo_OrderId) {
                return true
            }
            var g = ["p000", "p100", "np000", "np100"];
            for (var s = 0; s < g.length; s++) {
                var C = getCookie(g[s]);
                if (C != null && C != "") {
                    log("HomePageOrder", g[s])
                }
            }
            var p = "1:2:3:4:5:1a:1b:BR1:BR2:BR3:BR4:BR5:DDR:GR1:GR2:GR3:GR4:VR1:VR2:VR3:VR4:VR5:NR:CR1:CR2:CR3:SR1:SR2:SR3:SR4:Indiv&Simi:Indiv&OthC:Indiv&AllC:Zd";
            simpleMold(p.split(":"), "R", "reWids", "4");
            var y = "Club,ThirdRec,AttRec,OCRec,SORec,EBRec,BookSpecial,BookTrack,BookHis,Coupon,GlobalTrack,GlobalHis,History,historyreco_s,historyreco_c";
            complexMold(y.split(","), "R", "reWids", "4");
            var l = ["v", "TrackRec", "TrackHis", "CouDan", "CarAcc", "Zd", "Tc", "g", "s", "Book", "BookSpecial", "BookTrack", "BookHis", "GlobalTrack", "GlobalHis", "History", "Hiss", "Hisc", "simi", "GThirdRec", "PtoAccy", "AtoAccy"];
            complexMold(l, "o", "rod", "d", true);
            RecommendTrans("reHome2012,_rtbook", "N", "4");
            complexMold(["_rdCube"], "Cube", "", "4");
            simpleMold(["SEO"], "S", "seWids", "4");
            setCookieMills("_distM", SucInfo_OrderId, 86400000);
            setCookieMills("_ghis", "", -1);
            log("7", "2", SucInfo_OrderId, SucInfo_OrderType, SucInfo_OrderDetail);
            JA && JA.tracker.ngloader("order.100000", {orderid: SucInfo_OrderId,ordertype: SucInfo_OrderType,orderdetail: SucInfo_OrderDetail})
        }
    })()
})();
function log(c, b) {
    var a = Array.prototype.slice.call(arguments);
    a = a && a.slice(2);
    JA && JA.tracker.aloading(c, b, a);
    JA && JA.tracker.ngloader("other.000000", {t1: c,t2: b,p0: JA.util.join(a)})
}
(function() {
    if (typeof jdpts != "object") {
        return
    }
    if (jdpts._cls) {
        log(jdpts._cls.split(".")[0], jdpts._cls.split(".")[1])
    }
})();
Clublog();