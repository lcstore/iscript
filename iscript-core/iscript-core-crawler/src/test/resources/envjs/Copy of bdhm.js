var log = function(msg){
	if(msg){
		java.lang.System.out.println(msg);
	}else {
		java.lang.System.err.println("null");
	}
}
location.href='http://jifen.wan.360.cn/';
	var hm = document.createElement("script");
	hm.src = "http://s0.qhimg.com/i360/;js;pass_api_/seed,log/v3202.js";
	var s = document.getElementsByTagName("script")[0];
	s.parentNode.appendChild(hm);

var QHPass = window.QHPass || {};
(function(e, t, n) {
    e.tpl = e.tpl || {};
    t.art = '12344';
    if (!t.QHDomain) {
    	log('t.QHDomain');
        var r = n.getElementsByTagName("script"), i = /^.*\/(\w+)\.js.*$/.exec(r[r.length - 1].src);
    }
    t.QHDomain =32432;
    var s = t.QHDomain;
    log('t.QHDomain'+t.QHDomain);
    s.captchaUrl = location.host.indexOf("360.cn") == -1 ? s.i360 + "/reg/doGetCap?app=" : s.captcha + "/image.php?app=";
    var o = e.$ = t.jQuery || t.qui || t.QW && t.QW.NodeW;
    o.prototype.append || (o.prototype.append = o.prototype.appendChild), o.prototype.un || (o.prototype.un = o.prototype.unbind);
    var u = "1360.com|360.cn|qihoo.com|woxihuan.com|yunpan.cn|360pay.cn|360kan.com|so.com|leidian.com", a = u.split("|"), f = [], l = {}, c = 0, h, p, d = a.length;
    for (; c < d; c++)
        h = a[c], p = h.split(".")[0], f.push(p), l[p] = h;
    e._hostConf = u, e._hostShort = f.join("|"), e._hostShort2long = l;
    var v = location.host.match(new RegExp(e._hostConf.replace(/\./, "\\."), "ig"));
    e._hostCurr = v && v[0], e.uinfo = {}, e.resConfig = {reloadAfterLogout: !0,jumpUrl: "",charset: "gbk",useType: "pop",loginStatus: !1,userInfoParas: "",postCharset: "gbk",isAutoLogin: !1,loginTitle: "",regTitle: "",setNameTitle: "",setMailTitle: "",afterRenderPanel: null,needDefaultSkin: !0,afterClosePanel: null,loginAfterSetName: !0,regType: "email_phone",isByqt: !0,thirdFun: function(e) {
        },callback: "",regPopOnce: !0,logoutCallback: null,regCallback: function() {
        },loginCallback: function() {
        },getInfoAfterReg: !0,src: "pcw_i360",initModule: function() {
        },regOpts: {regway: "email",needEmailActive: !0,needName: !0,nickname: !1},regPhoneOpts: {regway: "phone",needName: !0,nickname: !1},loginOpts: {thirdLogin: ["sina|renren|msn|fetion|Telecom", "", "", ""],captFlag: !0},ssoOpts: {},bindOpts: {width: 400,title: "\u5b8c\u5584\u5e10\u53f7\u8d44\u6599"},namedOpts: {},mailOpts: {}}, e.thirdLoginSuccess = function() {
        t.loginThirdCallback = function() {
            e.getUserInfo(function(t) {
                if (t.qid)
                    try {
                        e.loginUtils.closeOpenHander(), e.setloginStatus(t), e.resConfig.thirdFun && e.resConfig.thirdFun(t)
                    } catch (n) {
                    }
            })
        };
        if (e._hostCurr && e._hostCurr !== "360.cn") {
            var n = "http://login." + e._hostCurr + "/?o=sso&m=setcookie&func=loginThirdCallback&s=", r = "http://login.360.cn/?o=sso&m=getRd&func=rdCallBack&s=";
            e.loadJs(r + Math.random()), t.rdCallBack = function(t) {
                var r = t.rd;
                r != "" ? e.loadJs(n + r + "&t=" + Math.random()) : loginThirdCallback()
            }
        } else
            loginThirdCallback()
    }, e.getDocRect = function(e) {
        e = e || n;
        var t = e.defaultView || e.parentWindow, r = navigator.userAgent.match(/opera/i), i = e.compatMode, s = e.documentElement, o = t.innerHeight || 0, u = t.innerWidth || 0, a = t.pageXOffset || 0, f = t.pageYOffset || 0, l = s.scrollWidth, c = s.scrollHeight;
        return i != "CSS1Compat" && (s = e.body, l = s.scrollWidth, c = s.scrollHeight), i && !r && (u = s.clientWidth, o = s.clientHeight), l = Math.max(l, u), c = Math.max(c, o), a = Math.max(a, s.scrollLeft, e.body.scrollLeft), f = Math.max(f, s.scrollTop, e.body.scrollTop), {width: u,height: o,scrollWidth: l,scrollHeight: c,scrollX: a,scrollY: f}
    }, e.logoutOtherDomain = function(t) {
        function n() {
            try {
                e.setloginStatus({})
            } catch (t) {
            }
            e.resConfig.passLevelCookieName && e.Cookie.del(e.resConfig.passLevelCookieName), e.resConfig.logoutCallback && e.resConfig.logoutCallback()
        }
        t.errno == 0 && (e._hostCurr && e._hostCurr !== "360.cn" ? e.loadJsonp("http://login." + e._hostCurr + "/?o=sso&m=logout&func=%callback%", n) : n())
    }, e.logout = function(t) {
        t && (e.resConfig.logoutCallback = t);
        if (e.Cookie.get("Q"))
            if (e.resConfig.reloadAfterLogout) {
                var n = e.resConfig.jumpUrl || location.href;
                location.href = s.login_http + "/?op=logout&destUrl=" + encodeURIComponent(n)
            } else
                e.loadJsonp(s.login_http + "/?o=sso&m=logout&func=%callback%", e.logoutOtherDomain);
        else
            t && t()
    }, e._btn_logout_handler = function(t) {
        try {
            e.logout(), e.preventDefault(t)
        } catch (n) {
        }
    }, e.setloginStatus = function(t) {
        e.closePop && e.closePop();
        try {
            if (t.qid) {
                e.uinfo = t;
                var n = t.userName && t.userName.indexOf("360U") == -1 ? t.userName : t.login_email || t.loginEmail || t.userName;
                o(".loginWrap").show(), o(".nloginWrap").hide(), o(".popUsername").html(n), o(".btn-logout-pop").click(e._btn_logout_handler)
            } else
                e.uinfo = {}, o(".loginWrap").hide(), o(".popUsername").html(""), o(".nloginWrap").show(), o(".btn-logout-pop").un("click", e._btn_logout_handler)
        } catch (r) {
        }
    }, e.mix = function(e, t, n) {
        for (var r in t)
            if (n || !(r in e))
                e[r] = t[r];
        return e
    }, e.loadJs = function(e, t, r) {
        r = r || {};
        var i = n.getElementsByTagName("head")[0] || n.documentElement, s = n.createElement("script"), o = !1;
        s.src = e, r.charset && (s.charset = r.charset), s.onerror = s.onload = s.onreadystatechange = function() {
            !o && (!this.readyState || this.readyState == "loaded" || this.readyState == "complete") && (o = !0, t && t(), s.onerror = s.onload = s.onreadystatechange = null, i.removeChild(s))
        }, i.insertBefore(s, i.firstChild)
    }, e.loadJsonp = function() {
        var n = new Date * 1;
        return function(r, i, s) {
            s = s || {};
            var o = "QiUserJsonP" + n++, u = s.callbackReplacer || /%callback%/ig;
            t[o] = function(e) {
                i && i(e), t[o] = null
            }, u.test(r) ? r = r.replace(u, o) : r += (/\?/.test(r) ? "&" : "?") + "callback=" + o, e.loadJs(r, null, s)
        }
    }(), e.trim = function(e) {
        return e && e.replace(/^\s+|\s+$/g, "")
    }, e.byteLen = function(e) {
        return e.replace(/[^\x00-\xff]/g, "--").length
    }, e.forEach = function(e, t, n) {
        for (var r = 0, i = e.length; r < i; r++)
            r in e && t.call(n, e[r], r, e)
    }, e.queryUrl = function(e, t) {
        e = e.replace(/^[^?=]*\?/ig, "").split("#")[0];
        var n = {};
        return e.replace(/(^|&)([^&=]+)=([^&]*)/g, function(e, t, r, i) {
            try {
                r = decodeURIComponent(r), i = decodeURIComponent(i)
            } catch (s) {
            }
            r in n ? n[r] instanceof Array ? n[r].push(i) : n[r] = [n[r], i] : n[r] = /\[\]$/.test(r) ? [i] : i
        }), t ? n[t] : n
    }, e.Cookie = e.Cookie || {}, e.Cookie.get = function(t) {
        if (e.Cookie._isValidKey(t)) {
            var r = new RegExp("(^| )" + t + "=([^;]*)(;|$)"), i = r.exec(n.cookie);
            if (i)
                try {
                    return decodeURIComponent(i[2]) || null
                } catch (s) {
                    return i[2] || null
                }
        }
        return null
    }, e.Cookie._isValidKey = function(e) {
        return (new RegExp('^[^\\x00-\\x20\\x7f\\(\\)<>@,;:\\\\\\"\\[\\]\\?=\\{\\}\\/\\u0080-\\uffff]+$')).test(e)
    }, e.Cookie.set = function(t, r, i) {
        if (!e.Cookie._isValidKey(t))
            return;
        i = i || {};
        var s = i.expires;
        "number" == typeof i.expires && (s = new Date, s.setTime(s.getTime() + i.expires)), n.cookie = t + "=" + encodeURIComponent(r) + "; path=" + (i.path ? i.path : "/") + (s ? "; expires=" + s.toGMTString() : "") + (i.domain ? "; domain=" + i.domain : "") + (i.secure ? "; secure" : "")
    }, e.Cookie.del = function(t) {
        e.Cookie.set(t, "")
    }, e.getUserStatus = function() {
        return !!e.uinfo.qid
    }, e.clearUinfo = function() {
        e.uinfo = {}
    }, e.getUserInfo = function(t, n, r) {
        r = r || e.resConfig.userInfoParas;
        if (e.Cookie.get("Q") || !e.resConfig.isByqt)
            if (e.getUserStatus())
                t && t(e.uinfo);
            else {
                var i = r || e._hostCurr && e._hostCurr && "http://login." + e._hostCurr + "/?o=sso&m=info&func=%callback%&show_name_flag=1" || "http://login.360.cn/?o=sso&m=info&func=%callback%&show_name_flag=1";
                i && e.loadJsonp(i, function(e) {
                    e.qid ? t && t(e) : n && n()
                })
            }
        else
            n && n(), o(".loginWrap").hide(), o(".nloginWrap").show()
    }, e.execCallback = function(n, r) {
        e.resConfig.loginAfterSetName = !1, typeof n == "string" ? t.location.href = n : typeof n == "boolean" ? t.location.reload(n) : n && n(r)
    }, e.login = function(t, n) {
        e.getUserInfo(function(n) {
            e.execCallback(t, n)
        }, function() {
            e[e.resConfig.loginOpts.loginType == "quick" ? "showSso" : "showLogin"](t, n)
        })
    }, e.reg = function(t, n) {
        e.getUserInfo(function(n) {
            e.execCallback(t, n)
        }, function() {
            e.showReg(t, n)
        })
    }, e.autoLogin = function(t) {
        e.loadJsonp(s.login_https + "/?o=sso&m=info&func=%callback%&need_ck=1", function(n) {
            n.auth && n.auth.length > 0 && e.autoAuth(t, n)
        })
    }, e.autoAuth = function(t, n) {
        n = n || {};
        var r = (new Date).getTime(), i = n.auth, s = s || {};
        if (!e._hostCurr)
            return;
        for (var o = 0, u = i.length; o < u; o++)
            i[o].t && (s.expires = i[o].t - r), s.path = "/", s.domain = e._hostCurr, e.Cookie.set(i[o].k, i[o].v, s);
        if (e.resConfig.loginAfterSetName && n.type != "bind" && (!n.userName || n.userName.indexOf("360U") > -1))
            e.showSetName(t, {notRequest: !0,crumb: n.crumb});
        else if (e.resConfig.loginAfterSetName && n.type == "bind")
            e.showBind(t, {regway: "bind",crumb: n.crumb});
        else {
            e.execCallback(t, n);
            try {
                e.setloginStatus(n)
            } catch (a) {
            }
        }
    }, e.sso = function(t, n) {
        e.getUserInfo(function(n) {
            e.execCallback(t, n)
        }, function() {
            e.showSso(t, n)
        })
    };
    var m = ["showSso", "showLogin", "showReg", "showSetName", "showBind", "showBindMobile", "showSecMail", "showLoginEmail"], g, y = 0, b = m.length, w = {};
    for (; y < b; y++)
        g = m[y], e[g] = w[g] = function(t) {
            return function(n, r) {
                e.loadJs(s.popJsPath, function() {
                    if (w[t] == e[t])
                        throw new Error("QUC JSAPI [" + s.popJsPath + "] loading exception. Run the [QHPass." + t + "] methods fail.");
                    e[t](n, r), delete w[t]
                }, {charset: e.resConfig.charset})
            }
        }(g);
    e.namedUser = function(t, n) {
        e.resConfig.loginAfterSetName = !0, e.getUserInfo(function(n) {
            n.type != "bind" && (!n.userName || n.userName.indexOf("360U") > -1) ? e.showSetName(t, {notRequest: !0,crumb: n.crumb}) : n.type == "bind" ? e.showBind(t, {regway: "bind",crumb: n.crumb}) : e.execCallback(t, n)
        }, function() {
            e.showLogin(t)
        })
    }, e.secMail = function(t, n) {
        e.getUserInfo(function(n) {
            e.showSecMail(t, {crumb: n.crumb})
        }, function() {
            e.showLogin(function(n) {
                e.showSecMail(t, {crumb: n.crumb})
            }, n)
        })
    }, e.preventDefault = function(e) {
        e = e || t.event, e && e.preventDefault ? e.preventDefault() : e.returnValue = !1
    }, e.Adapter = {globalCallback: null,regOpts: {},loginOpts: {},ssoOpts: {},namedOpts: {}}, e.log = function() {
        o(n.body).delegate("#mod_quc_pop a, #mod_quc_pop .quc-psp-gstat", "mousedown", function(n) {
            if (!t.QHPASS_MONITOR)
                return;
            var r = n.target;
            try {
                var i = {cId: QHPASS_MONITOR.util.getContainerId(r),c: QHPASS_MONITOR.util.getText(r),p: "360_psp_" + e.resConfig.useType};
                QHPASS_MONITOR.log(i, "click")
            } catch (s) {
            }
        })
    }, e._btn_login_handler = function(t) {
        e.preventDefault(t), e.login(e.resConfig.callback || e.resConfig.loginCallback)
    }, e._btn_reg_handler = function(t) {
        e.preventDefault(t), e.reg(e.resConfig.callback || e.resConfig.regCallback)
    }, e._checkLoginStatus = function() {
        e.getUserInfo(function(t) {
            e.setloginStatus(t), e.resConfig.initModule && e.resConfig.initModule(t)
        })
    }, e._autoLogin = function(t) {
        if (!t && location.host.indexOf("360.cn") > -1)
            return;
        e.getUserInfo(e.setloginStatus, function() {
            e.autoLogin(e.resConfig.callback)
        })
    }, (t.QW && Dom.ready || o)(function() {
        try {
            o(".btn-login-pop").click(e._btn_login_handler), o(".btn-reg-pop").click(e._btn_reg_handler), e.resConfig.isAutoLogin ? e._autoLogin() : e.resConfig.loginStatus && e._checkLoginStatus(), e.log()
        } catch (t) {
        }
    })
})(QHPass, window, document);
(function(e, t) {
    if (typeof e.QHPASS_MONITOR != "undefined")
        return;
    var n = "v1.2.7 (2013.04.27)", r = "360.cn", i = function(e, s) {
        var o;
        (function() {
            o = !0;
            try {
                var e = location.protocol.toLowerCase();
                if (e == "http:" || e == "https:")
                    o = !1
            } catch (t) {
            }
        })();
        var u = t, a = navigator, f = e.screen, l = o ? "" : t.domain.toLowerCase(), c = a.userAgent.toLowerCase(), h = {trim: function(e) {
                return e.replace(/^[\s\xa0\u3000]+|[\u3000\xa0\s]+$/g, "")
            }}, p = {on: function(e, t, n) {
                e.addEventListener ? e && e.addEventListener(t, n, !1) : e && e.attachEvent("on" + t, n)
            },parentNode: function(e, t, n) {
                n = n || 5, t = t.toUpperCase();
                while (e && n-- > 0) {
                    if (e.tagName === t)
                        return e;
                    e = e.parentNode
                }
                return null
            }}, d = {fix: function(e) {
                if (!("target" in e)) {
                    var t = e.srcElement || e.target;
                    t && t.nodeType == 3 && (t = t.parentNode), e.target = t
                }
                return e
            }}, v = function() {
            function e(e) {
                return e != null && e.constructor != null ? Object.prototype.toString.call(e).slice(8, -1) : ""
            }
            return {isArray: function(t) {
                    return e(t) == "Array"
                },isObject: function(e) {
                    return e !== null && typeof e == "object"
                },mix: function(e, t, n) {
                    for (var r in t)
                        if (n || !(e[r] || r in e))
                            e[r] = t[r];
                    return e
                },encodeURIJson: function(e) {
                    var t = [];
                    for (var n in e) {
                        if (e[n] == null)
                            continue;
                        t.push(encodeURIComponent(n) + "=" + encodeURIComponent(e[n]))
                    }
                    return t.join("&")
                }}
        }(), m = {get: function(e) {
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
            }}, g = {getProject: function() {
                return ""
            },getReferrer: function() {
                return u.referrer
            },getBrowser: function() {
                var t = {"360se-ua": "360se",TT: "tencenttraveler",Maxthon: "maxthon",GreenBrowser: "greenbrowser",Sogou: "se 1.x / se 2.x",TheWorld: "theworld"};
                for (var n in t)
                    if (c.indexOf(t[n]) > -1)
                        return n;
                var r = !1;
                try {
                    +external.twGetVersion(external.twGetSecurityID(e)).replace(/\./g, "") > 1013 && (r = !0)
                } catch (i) {
                }
                if (r)
                    return "360se-noua";
                var s = c.match(/(msie|chrome|safari|firefox|opera)/);
                return s = s ? s[0] : "", s == "msie" && (s = c.match(/msie[^;]+/)), s
            },getLocation: function() {
                var e = "";
                try {
                    e = location.href
                } catch (t) {
                    e = u.createElement("a"), e.href = "", e = e.href
                }
                return e = e.replace(/[?#].*$/, ""), e = /\.(s?htm|php)/.test(e) ? e : e.replace(/\/$/, "") + "/", e
            },getGuid: function() {
                function t(e) {
                    var t = 0, n = 0, r = e.length - 1;
                    for (r; r >= 0; r--) {
                        var i = parseInt(e.charCodeAt(r), 10);
                        t = (t << 6 & 268435455) + i + (i << 14), (n = t & 266338304) != 0 && (t ^= n >> 21)
                    }
                    return t
                }
                function n() {
                    var n = [a.appName, a.version, a.language || a.browserLanguage, a.platform, a.userAgent, f.width, "x", f.height, f.colorDepth, u.referrer].join(""), r = n.length, i = e.history.length;
                    while (i)
                        n += i-- ^ r++;
                    return (Math.round(Math.random() * 2147483647) ^ t(n)) * 2147483647
                }
                var i = "__guid", s = m.get(i);
                if (!s) {
                    s = [t(o ? "" : u.domain), n(), +(new Date) + Math.random() + Math.random()].join(".");
                    var c = {expires: 2592e7,path: "/"};
                    if (r) {
                        var h = "." + r;
                        if (l.indexOf(h) > 0 && l.lastIndexOf(h) == l.length - h.length || l == h)
                            c.domain = h
                    }
                    m.set(i, s, c)
                }
                return function() {
                    return s
                }
            }(),getCount: function() {
                var e = "count", t = m.get(e);
                return t = (parseInt(t) || 0) + 1, m.set(e, t, {expires: 864e5,path: "/"}), function() {
                    return t
                }
            }(),getFlashVer: function() {
                var t = -1;
                if (a.plugins && a.mimeTypes.length) {
                    var n = a.plugins["Shockwave Flash"];
                    n && n.description && (t = n.description.replace(/([a-zA-Z]|\s)+/, "").replace(/(\s)+r/, ".") + ".0")
                } else if (e.ActiveXObject && !e.opera)
                    try {
                        var r = new ActiveXObject("ShockwaveFlash.ShockwaveFlash");
                        if (r) {
                            var i = r.GetVariable("$version");
                            t = i.replace(/WIN/g, "").replace(/,/g, ".")
                        }
                    } catch (s) {
                    }
                return t = parseInt(t, 10), t
            },getContainerId: function(e) {
                var t = b.areaIds;
                if (t) {
                    var n, r = new RegExp("^(" + t.join("|") + ")$", "ig");
                    while (e) {
                        if (e.id && r.test(e.id))
                            return (e.getAttribute("data-desc") || e.id).substr(0, 100);
                        e = e.parentNode
                    }
                }
                return ""
            },getText: function(e) {
                var t = "";
                return e.tagName.toLowerCase() == "input" ? t = e.getAttribute("text") || e.getAttribute("data-text") || e.value || e.title || "" : t = e.getAttribute("text") || e.getAttribute("data-text") || e.innerText || e.textContent || e.title || "", h.trim(t).substr(0, 100)
            },getHref: function(e) {
                try {
                    return e.getAttribute("data-href") || e.href || ""
                } catch (t) {
                    return ""
                }
            }}, y = {getBase: function() {
                return {p: g.getProject(),u: g.getLocation(),id: g.getGuid(),guid: g.getGuid()}
            },getTrack: function() {
                return {b: g.getBrowser(),c: g.getCount(),r: g.getReferrer(),fl: g.getFlashVer()}
            },getClick: function(e) {
                e = d.fix(e || event);
                var t = e.target, n = t.tagName, r = g.getContainerId(t);
                if (!t.type || t.type != "submit" && t.type != "button") {
                    if (n == "AREA")
                        return {f: g.getHref(t),c: "area:" + t.parentNode.name,cId: r};
                    var f, l;
                    return n == "IMG" && (f = t), t = p.parentNode(t, "A"), t ? (l = g.getText(t), {f: g.getHref(t),c: l ? l : f ? f.src.match(/[^\/]+$/) : "",cId: r}) : !1
                }
                var i = p.parentNode(t, "FORM"), s = {};
                if (i) {
                    var o = i.id || "", u = t.id;
                    s = {f: i.action,c: "form:" + (i.name || o),cId: r};
                    if ((o == "search-form" || o == "searchForm") && (u == "searchBtn" || u == "search-btn")) {
                        var a = w("kw") || w("search-kw") || w("kw1");
                        s.w = a ? a.value : ""
                    }
                } else
                    s = {f: g.getHref(t),c: g.getText(t),cId: r};
                return s
            },getKeydown: function(e) {
                e = d.fix(e || event);
                if (e.keyCode != 13)
                    return !1;
                var t = e.target, n = t.tagName, r = g.getContainerId(t);
                if (n == "INPUT") {
                    var i = p.parentNode(t, "FORM");
                    if (i) {
                        var s = i.id || "", o = t.id, u = {f: i.action,c: "form:" + (i.name || s),cId: r};
                        if (o == "kw" || o == "search-kw" || o == "kw1")
                            u.w = t.value;
                        return u
                    }
                }
                return !1
            }}, b = {trackUrl: null,clickUrl: null,areaIds: null}, w = function(e) {
            return t.getElementById(e)
        };
        return {version: n,util: g,data: y,config: b,sendLog: function() {
                return e.__qihoo_monitor_imgs = {}, function(t) {
                    var n = "log_" + +(new Date), r = e.__qihoo_monitor_imgs[n] = new Image;
                    r.onload = r.onerror = function() {
                        e.__qihoo_monitor_imgs && e.__qihoo_monitor_imgs[n] && (e.__qihoo_monitor_imgs[n] = null, delete e.__qihoo_monitor_imgs[n])
                    }, r.src = t
                }
            }(),buildLog: function() {
                var e = "";
                return function(t, n) {
                    if (t === !1)
                        return;
                    t = t || {};
                    var r = y.getBase();
                    t = v.mix(r, t, !0);
                    var i = n + v.encodeURIJson(t);
                    if (i == e)
                        return;
                    e = i, setTimeout(function() {
                        e = ""
                    }, 500);
                    var s = v.encodeURIJson(t);
                    s += "&t=" + +(new Date), n = n.indexOf("?") > -1 ? n + "&" + s : n + "?" + s, this.sendLog(n)
                }
            }(),log: function(e, t) {
                t = t || "click";
                var n = b[t + "Url"];
                n || alert("Error : the " + t + "url does not exist!"), this.buildLog(e, n)
            },setConf: function(e, t) {
                var n = {};
                return v.isObject(e) ? n = e : n[e] = t, this.config = v.mix(this.config, n, !0), this
            },setUrl: function(e) {
                return e && (this.util.getLocation = function() {
                    return e
                }), this
            },setProject: function(e) {
                return e && (this.util.getProject = function() {
                    return e
                }), this
            },setId: function() {
                var e = [], t = 0, n;
                while (n = arguments[t++])
                    v.isArray(n) ? e = e.concat(n) : e.push(n);
                return this.setConf("areaIds", e), this
            },getTrack: function() {
                var e = this.data.getTrack();
                return this.log(e, "track"), this
            },getClickAndKeydown: function() {
                var e = this;
                return p.on(u, "mousedown", function(t) {
                    var n = e.data.getClick(t);
                    e.log(n, "click")
                }), p.on(u, "keydown", function(t) {
                    var n = e.data.getKeydown(t);
                    e.log(n, "click")
                }), i.getClickAndKeydown = function() {
                    return e
                }, this
            }}
    }(e);
    i.setConf({trackUrl: "http://s.360.cn/w360/s.htm",clickUrl: "http://s.360.cn/w360/c.htm",wpoUrl: "http://s.360.cn/w360/p.htm"}), e.QHPASS_MONITOR = i
})(window, document);
log('document.cookie:'+document.cookie);