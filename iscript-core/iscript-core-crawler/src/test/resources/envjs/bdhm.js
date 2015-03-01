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
var	QHDomain=document.QHDomain;
var QHPass = window.QHPass || {};
QHPass.Browser = function() {
    var e = window.navigator, t = e.userAgent.toLowerCase(), n = /(msie|webkit|gecko|presto|opera|safari|firefox|chrome|maxthon|android|ipad|iphone|webos|hpwos)[ \/os]*([\d_.]+)/ig, r = {platform: e.platform};
    t.replace(n, function(e, t, n) {
        var i = t.toLowerCase();
        r[i] || (r[i] = n)
    }), r.opera && t.replace(/opera.*version\/([\d.]+)/, function(e, t) {
        r.opera = t
    });
    if (r.msie) {
        r.ie = r.msie;
        var i = parseInt(r.msie, 10);
        r["ie" + i] = !0
    }
    return r
}(), function(e) {
    var t = e.$, n = QHDomain.captchaUrl, r = "i360", i, s = 0;
    e.addPop = function(n, r, o) {
        function p() {
            var n = '<div id="pop_mask_bg" unselectable="on"  class="mod-qiuser-pop-bg"></div>';
            return e.Browser.ie6 && (n = '<div id="pop_mask_bg" unselectable="on"  class="mod-qiuser-pop-bg"><div class="pop-bg-inner" unselectable="on"></div><iframe src="about:blank"  border="0" frameborder="0"></iframe></div>'), t("body").append(n), t("#pop_mask_bg")[0]
        }
        function d() {
            var e = i.offsetParent, t = document.documentElement, n = i.style;
            try {
                if (parseInt(n.top) != e.scrollTop || parseInt(n.left) != e.scrollLeft)
                    n.top = e.scrollTop, n.left = e.scrollLeft;
                t.clientHeight != i.offsetHeight && (n.height = t.clientHeight), t.clientWidth != i.offsetWidth && (n.width = t.clientWidth)
            } catch (r) {
            }
        }
        var u = e.getDocRect(), a = '<div id="mod_quc_pop" ></div>', f = '<div class="mod-quc-top quc-clearfix"><div id="qucPanelTitle" class="top-title">\u6b22\u8fce\u767b\u5f55360</div><a href="#" class="pop-dia-close" title="\u5173\u95ed"></a></div>', l = '<div class="quc-clearfix " ><div class="reg-nav"><ul  id="regWays"></ul><div class="has-account">\u5df2\u6709\u5e10\u53f7\uff0c<a href="###" class="fac f14 clk-quc-login">\u7acb\u5373\u767b\u5f55</a></div></div><div class="reg-quc-content" id="modQiUserPop"></div>', c = '<div id="modQiUserPop"></div>';
        i = p(), t("body").append(a), o == "reg" ? t("#mod_quc_pop").append(f).append(l).css({width: r + "px"}) : t("#mod_quc_pop").append(f).append(c).css({width: r + "px"}), e.Browser.ie6 && (d(i), clearInterval(s), s = setInterval(d, 1e3));
        try {
            QHPASS_MONITOR.log({cId: "\u5f39\u51fa\u5c42\u7684\u5c55\u73b0\u91cf",c: e.resConfig.src + "_" + o + "\u7c7b\u578b\u5c42\u7684\u5c55\u73b0\u91cf",p: "360_psp_pop"}, "click"), t(".pop-dia-close")[0].setAttribute("text", e.resConfig.src + "_" + o + "\u7c7b\u578b\u5c42\u7684\u5173\u95ed\u91cf")
        } catch (h) {
        }
        e.resConfig.afterRenderPanel && e.resConfig.afterRenderPanel()
    }, e.resizeAndshow = function(n, r) {
        t(window).on("resize", function() {
            i()
        });
        var i = function() {
            var n = t("#mod_quc_pop"), i = e.getDocRect();
            n[0] && (n.css("left", i.scrollX + (i.width - r) / 2 + "px").css("top", i.scrollY + (i.height - n[0].offsetHeight) / 2 + "px"), t("#pop_mask_bg").css("width", i.scrollWidth + "px").css("height", i.scrollHeight + "px")), t("#mod_quc_pop").show()
        };
        t("#mod_quc_pop").css("position") == "absolute" ? i() : setTimeout(i, 200)
    }, e.closePop = function() {
        clearInterval(s), e.removeNode(t("#pop_mask_bg")[0]), e.removeNode(t("#mod_quc_pop")[0]), e.resConfig.regPopOnce = !0, e.resConfig.afterClosePanel && e.resConfig.afterClosePanel()
    }
}(QHPass);
(function(e) {
    var t = e.$;
    e.tmpl = function(e, t) {
        e = String(e);
        var n = Array.prototype.slice.call(arguments, 1), r = Object.prototype.toString;
        return n.length ? (n = n.length == 1 ? t !== null && /\[object Array\]|\[object Object\]/.test(r.call(t)) ? t : n : n, e.replace(/#\{(.+?)\}/g, function(e, t) {
            var i = n[t];
            return "[object Function]" == r.call(i) && (i = i(t)), "undefined" == typeof i ? "" : i
        })) : e
    }, e.showLogin = function(n, r) {
        r = r || {}, e.cssLoadOnce && e.cssLoadOnce(), e.Adapter.globalCallback = n, e.mix(r, e.resConfig.loginOpts || {}), e.Adapter.ssoOpts = e.resConfig.ssoOpts;
        var i = r.title || e.resConfig && e.resConfig.loginTitle || "\u6b22\u8fce\u767b\u5f55360", s = r.width || 460, o = e.Adapter.ssoOpts.wrap = r.wrap || "", u = r.css || e.resConfig && e.resConfig.css || "", a = r.thirdLogin || [], f = e.Adapter.ssoOpts.type = r.type || "pop", l = r.cookie_domains || e.resConfig.cookie_domains || {}, c = r.captFlag || !1, h = r.checkPharse || !1, p = r.customHtml || !1, d = r.afterRender || e.resConfig.loginOpts && e.resConfig.loginOpts.afterRender || "", v = r.height || 335, m = r.loginType || "normal";
        r.cookie_domains = l, u && e.loadCss && e.loadCss(u);
        var g = [], y = [], b = "", w = "", E = "", S = ['<dd class="other"><span class="title">\u5176\u4ed6\u5e10\u53f7\u767b\u5f55\uff1a</span><span class="login-ways">'], x = ["formStart", "userName", "password", "find", "submit", "formEnd"];
        c || h ? (x.splice(4, 0, "phrase"), x.splice(6, 0, "thirdLogin")) : x.splice(5, 0, "thirdLogin"), a.length > 0 && (y = a[0].split("|"), b = a[1] || "", w = a[2] || "", E = a[3] || "", e.forEach(y, function(t) {
            S.push(e.tmpl(e.tpl.pspFriend[t], {from: b,type: w,destUrl: E}))
        }), S.push("</span></dd>"), e.tpl.loginTpl.thirdLogin = S.join("")), e.forEach(x, function(t) {
            g.push(e.tpl.loginTpl[t])
        });
        if (!p)
            if (f == "pop") {
                e.closePop && e.closePop(), e.addPop(v, s, "login"), t("#modQiUserPop").html(g.join(""));
                if (m == "normal")
                    var T = '<div class="hd"><ul class="js-tab"><li id="other-login" data-target="mod-other-id-login"><span id="qucPanelTitle">\u6b22\u8fce\u60a8\u767b\u5f55360</span></li></ul><a href="#" class="pop-dia-close" title="\u5173\u95ed"></a></div>';
                else
                    var T = '<div class="hd"><ul class="js-tab"><li id="quick-login" data-target="mod-quick-login" ><a href="#">\u5feb\u901f\u767b\u5f55</a></li><li id="other-login" data-target="mod-other-id-login" class="cur"><a href="#">\u5176\u4ed6\u5e10\u53f7\u767b\u5f55</a></li></ul><a href="#" class="pop-dia-close" title="\u5173\u95ed"></a></div>';
                t(".mod-quc-top").html(T).css("height", "56px"), t("#quick-login").on("click", function(r) {
                    t("#other-login").removeClass("cur"), t(this).addClass("cur"), e.showSso(n, {loginType: "quick"}), e.preventDefault(r)
                }), e.resizeAndshow(v, s)
            } else {
                if (!o)
                    return;
                t("#" + o).html(g.join(""))
            }
        h && (t("#phraseLoginwarp").show(), e.loginUtils.doGetCaptcha()), i && t("#qucPanelTitle").html(i);
        try {
            t(".reg-new-account").click(e._btn_reg_new_account_handler)
        } catch (N) {
        }
        d && d();
        try {
            t(".pop-dia-close").on("click", function(t) {
                e.resConfig.loginAfterSetName = !1, e.closePop && e.closePop(), e.preventDefault(t)
            })
        } catch (C) {
        }
        e.loginUtils.initEvent(e.Adapter.globalCallback, r)
    }, e._btn_reg_new_account_handler = function(t) {
        e.showReg && e.showReg(e.Adapter.globalCallback), e.preventDefault(t)
    }, e.showSso = function(n, r) {
        function p() {
            e.closePop && e.closePop(), e.addPop(o, s, "login");
            var n = "http://axlogin.passport.360.cn/ptlogin.php", r = a[0].replace(/\|/ig, ","), i = n + "?nextUrl=http://" + location.host + "/psp_jump.html&domain_list=" + r + "&t=" + Math.random(), f = '<iframe src="' + i + '" frameborder="0" width="100%" scrolling="no" height="100%" id="pspPtloginIframe"  ></iframe>';
            t("#modQiUserPop").html(f).css("text-indent", "-1000000px").show(), t(".mod-quc-top").html('<div class="hd"><ul class="login-tab" ><li id="quick-login" data-target="mod-quick-login" class="cur"><a href="#">\u5feb\u901f\u767b\u5f55</a></li><li id="other-login" data-target="mod-other-id-login"><a href="#">\u5176\u4ed6\u5e10\u53f7\u767b\u5f55</a></li></ul><a href="#" class="pop-dia-close" title="\u5173\u95ed"></a></div>').css("height", "56px"), u == "normal" && t(".login-tab").hide(), t("#modQiUserPop").css("height", "258px"), e.resizeAndshow(o, s)
        }
        r = r || {}, e.cssLoadOnce && e.cssLoadOnce(), e.Adapter.globalCallback = n, e.mix(r, e.resConfig.ssoOpts || {}), e.Adapter.loginOpts = e.resConfig.loginOpts;
        var i = r.title || e.resConfig && e.resConfig.loginTitle || "", s = r.width || 460, o = r.height || 170, u = r.loginType || "normal", a = r.cookie_domains || e.resConfig.cookie_domains || [e._hostShort], f = r.css || e.resConfig && e.resConfig.css || "", l = e.Adapter.loginOpts.wrap = r.wrap || "", c = r.afterRender || e.resConfig.ssoOpts && e.resConfig.ssoOpts.afterRender || "";
        e.Adapter.loginOpts.type = r.type || "pop", f && e.loadCss(f), p(), t("#mod_quc_pop").css("height", "314px"), t("#modQiUserPop").show(), c && c();
        try {
            t(".pop-dia-close").on("click", function(t) {
                e.resConfig.loginAfterSetName = !1, e.closePop && e.closePop(), e.preventDefault(t)
            })
        } catch (h) {
        }
    }, e.getUserStatus = function(n) {
        if (!n)
            return;
        t("#pspPtusIframe") && e.removeNode(t("#pspPtusIframe")[0]);
        var r = "http://axlogin.passport.360.cn/ptlogin.php", i = r + "?nextUrl=http://" + location.host + "/psp_jump.html&us=1&func=parent." + n + "&t=" + Math.random(), s = '<iframe id="pspPtusIframe" src="' + i + '" style="display:none"  ></iframe>';
        t(document.body).append(s)
    }, e.ptLogin = function(n) {
        if (n.s == "0")
            e.getUserInfo(function(t) {
                e.execCallback(e.Adapter.globalCallback, t)
            }), e.closePop && e.closePop();
        else if (n.s == "1") {
            t("#modQiUserPop").css("text-indent", "0px").show(), t(".login-tab").show(), t("#other-login").on("click", function(n) {
                t("#quick-login").removeClass("cur"), t(this).addClass("cur"), e.showLogin(e.Adapter.globalCallback, {loginType: "quick"}), e.preventDefault(n)
            });
            try {
                t(".pop-dia-close").on("click", function(t) {
                    e.resConfig.loginAfterSetName = !1, e.closePop && e.closePop(), e.preventDefault(t)
                })
            } catch (r) {
            }
        } else
            e.showLogin(e.Adapter.globalCallback, {loginType: "normal"})
    }, e.initPlaceHolders = function() {
        function t(e) {
            var e = e || window.event;
            return e.target || e.srcElement
        }
        function n(e) {
            var t = e.hintEl;
            return t && g(t)
        }
        function r(e) {
            var n = t(e), r = n.__emptyHintEl;
            if (!n || n.tagName != "INPUT" && n.tagName != "TEXTAREA")
                return;
            r && (n.value ? r.style.display = "none" : r.style.display = "")
        }
        function i(e) {
            var n = t(e), r = n.__emptyHintEl;
            if (!n || n.tagName != "INPUT" && n.tagName != "TEXTAREA")
                return;
            r && (r.style.display = "none")
        }
        if ("placeholder" in document.createElement("input"))
            return;
        document.addEventListener ? (document.addEventListener("focus", i, !0), document.addEventListener("blur", r, !0)) : (document.attachEvent("onfocusin", i), document.attachEvent("onfocusout", r));
        var s = document.getElementById("mod_quc_pop");
        !s && e.Adapter.ssoOpts.wrap && (s = document.getElementById(e.Adapter.ssoOpts.wrap));
        if (!s)
            return;
        var o = [s.getElementsByTagName("input"), s.getElementsByTagName("textarea")], u = 0, a = 0, f, l, c;
        for (u = 0; u < 2; u++) {
            l = o[u];
            for (a = 0; a < l.length; a++) {
                c = l[a], f = c.getAttribute("placeholder"), emptyHintEl = c.__emptyHintEl;
                if (!f) {
                    var h = c.getAttributeNode("placeholder");
                    h && (f = f.nodeValue)
                }
                if (f && !emptyHintEl) {
                    emptyHintEl = document.createElement("span"), emptyHintEl.innerHTML = f, emptyHintEl.className = "emptyhint", emptyHintEl.onclick = function(e) {
                        return function() {
                            try {
                                e.focus()
                            } catch (t) {
                            }
                        }
                    }(c);
                    if (c.value || c == document.activeElement)
                        emptyHintEl.style.display = "none";
                    c.parentNode.insertBefore(emptyHintEl, c), c.__emptyHintEl = emptyHintEl
                }
            }
        }
    }
})(QHPass), function(e) {
    var t = e.$, n = QHDomain.login_https + "/?o=sso&m=getToken", r = "", i = [], s = 0, o = QHDomain.captchaUrl, u = "", a = "i360", f = "", l = !1, c = null, h = 1;
    prevUsername = "", captFlag = !1, type = "pop", checkPharse = !1, t0 = "", t1 = "", callBack = null, onLoading = null, auth_info = {}, successfun = null, errorfun = null, t.prototype.parent || (t.prototype.parent = t.prototype.parentNode);
    var p = {win: null,showErrs: function(n, r) {
            return clearTimeout(c), t("#loginSubmit").removeClass("btn-login-loading").addClass("btn-login"), !e.resConfig.loginOpts.err2Global && n && type != "pop" ? (r && t("#tips-" + n).addClass("tips-msg").html(r).show(), n && t(".icon-" + n).addClass("icon-wrong").show()) : t("#error_tips").html(r).removeClass("login-loading").addClass("login-error").show(), !1
        },getLoginUrl: function() {
            return QHDomain.login_https + "/?o=sso&m=login&from=" + e.resConfig.src + "&rtype=data&func=QHPass.loginUtils.loginCallback"
        },tokenCallback: function(n) {
            var r = e.trim(t("#loginAccount").val()), i = t("#lpassword").val(), s = t("#iskeepalive").attr("checked") ? 1 : 0, o = t("#phraseLogin").val();
            e.resConfig.passLevelCookieName && e.getPassLevel && e.Cookie.set(e.resConfig.passLevelCookieName, e.getPassLevel(i), {expires: 5184e6,path: "/"});
            if (n.errno != 0)
                return clearTimeout(c), t("#loginSubmit")[0].disabled = !1, p.showErrs("", decodeURIComponent(n.errmsg)), !1;
            this.doLogin(r, i, s, n.token, o)
        },doLogin: function(t, n, i, s, o) {
            var u = [];
            u.push("userName=" + encodeURIComponent(t)), u.push("pwdmethod=1"), u.push("password=" + hex_md5(n)), u.push("isKeepAlive=" + i), u.push("token=" + s), captFlag ? (u.push("captFlag=1"), u.push("captId=" + a), u.push("captCode=" + encodeURIComponent(o))) : u.push("captFlag="), u.push("r=" + (new Date).getTime()), t0 = (new Date).getTime(), r = this.getLoginUrl(), e.loadJsonp(r + "&" + u.join("&"))
        },loginSuccess: function() {
            successfun && successfun(), clearTimeout(c), t("#error_tips").removeClass("login-loading").addClass("login-success"), t("#error_tips").html(""), t1 = (new Date).getTime(), e.Cookie.set("i360loginName", t("#loginAccount").val(), {expires: 5184e6,path: "/"}), u.ldtime = t1 - t0;
            if (e.resConfig.loginAfterSetName)
                if (u.type != "bind" && (!u.userName || u.userName.indexOf("360U") > -1))
                    e.showSetName && e.showSetName(callBack, {notRequest: !0,crumb: u.crumb});
                else if (u.type == "bind")
                    e.showBind && e.showBind(callBack, {notRequest: !0,crumb: u.crumb});
                else {
                    try {
                        e.setloginStatus(u)
                    } catch (n) {
                    }
                    e.execCallback(callBack, u)
                }
            else {
                try {
                    e.setloginStatus(u)
                } catch (n) {
                }
                e.execCallback(callBack, u)
            }
        },setCookieCallback: function(n) {
            if (n.errno > 0) {
                t("#loginSubmit")[0].disabled = !1;
                return
            }
            var r = i[s++];
            r ? e.loadJsonp(r + "/?o=sso&m=setcookie&func=QHPass.loginUtils.setCookieCallback&" + "s=" + f) : p.loginSuccess()
        },loginuse: function(t, n, r, i) {
            var s = (new Date).getTime(), o = "";
            if (n == "pop") {
                var u = e.getDocRect();
                toleft = u.scrollX + (u.width - 875) / 2, totop = Math.max(u.scrollY + (u.height - 645) / 2, 0), o = "left=" + toleft + ",top=" + totop + ",alwaysRaised=yes,height=645,width=875"
            }
            this.win = window.open(QHDomain.i360 + "/oauth/loginByOauth?c=" + t + "&type=" + n + "&destUrl=" + encodeURIComponent(i) + "&f=" + r + "&r=" + s, "oauthlogin", o)
        },closeOpenHander: function() {
            return this.win.close()
        },loginCallback: function(n) {
            if (n.errno == 0)
                f = n.s, u = n.userinfo, l ? p.loginSuccess() : (s = 0, p.setCookieCallback(n));
            else {
                clearTimeout(c), t("#loginSubmit")[0].disabled = !1, t("#loginSubmit")[0].className = "btn-login", errorfun && errorfun(), t("#error_tips").html("");
                switch (n.errno) {
                    case 1036:
                        t("#loginAccount").focus(), n.capturl && (e.loginUtils.doGetCaptcha(n.capturl), t("#phraseLogin").val("")), p.showErrs("loginAccount", "\u5e10\u53f7\u6216\u5bc6\u7801\u9519\u8bef\uff0c\u8bf7\u91cd\u65b0\u8f93\u5165"), t("#lpassword").val("");
                        break;
                    case 221:
                        t("#loginAccount").focus(), n.capturl && (e.loginUtils.doGetCaptcha(n.capturl), t("#phraseLogin").val(""));
                        var r = n.errmsg + ',<a target="_blank" href="http://www.360.cn/about/contactus.html">\u8bf7\u8054\u7cfb\u5ba2\u670d</a>';
                        p.showErrs("loginAccount", r), t("#lpassword").val("");
                        break;
                    case 219:
                    case 220:
                        t("#lpassword").val(""), t("#lpassword").focus(), n.capturl && (e.loginUtils.doGetCaptcha(n.capturl), t("#phraseLogin").val("")), p.showErrs("lpassword", "\u5e10\u53f7\u6216\u5bc6\u7801\u9519\u8bef\uff0c\u8bf7\u91cd\u65b0\u8f93\u5165");
                        break;
                    case 2e4:
                        p.showErrs("loginAccount", n.errmsg);
                        break;
                    case "login_captcha_001":
                    case "login_captcha_002":
                    case "login_captcha_003":
                        checkPharse = !0, t("#phraseLoginwarp").show(), t("#phraseLogin").focus(), e.loginUtils.doGetCaptcha(n.capturl), p.showErrs("phraseLogin", n.errmsg);
                        break;
                    default:
                        p.showErrs("", n.errmsg)
                }
            }
        },submit: function() {
            function a(e) {
                t("#tips-" + e).hide(), t(".icon-" + e).hide(), t("#error_tips").hide()
            }
            var r = e.trim(t("#loginAccount").val()), i = /^(13|14|15|18)\d{9}$/, s = t("#lpassword").val(), o = e.trim(t("#phraseLogin").val()), u = n + "&func=QHPass.loginUtils.tokenCallback&" + "userName=" + encodeURIComponent(r) + "&rand=" + Math.random();
            if (r == "")
                return t("#loginAccount").val("").focus(), p.showErrs("loginAccount", "\u8bf7\u8f93\u5165\u60a8\u7684\u5e10\u53f7");
            a("loginAccount");
            if (s == "")
                return t("#lpassword").val("").focus(), p.showErrs("lpassword", "\u8bf7\u8f93\u5165\u60a8\u7684\u5bc6\u7801");
            if (e.byteLen(s) < 6)
                return t("#lpassword").val("").focus(), p.showErrs("lpassword", "\u5e10\u53f7\u6216\u5bc6\u7801\u9519\u8bef\uff0c\u8bf7\u91cd\u65b0\u8f93\u5165");
            a("lpassword");
            if (checkPharse && o.length != 4)
                return t("#phraseLogin").val("").focus(), p.showErrs("phraseLogin", "\u8bf7\u8f93\u5165\u6b63\u786e\u7684\u9a8c\u8bc1\u7801");
            a("phraseLogin"), t("#error_tips").removeClass("login-error").addClass("login-loading"), onLoading && onLoading(), c = setTimeout(function() {
                t("#loginSubmit").removeClass("btn-login-loading").addClass("btn-login"), t("#error_tips").html("\u8bf7\u6c42\u8d85\u65f6").removeClass("login-loading").addClass("login-error").show(), t("#loginSubmit")[0].disabled = !1
            }, 2e4), t("#loginSubmit").removeClass("btn-login").addClass("btn-login-loading"), t("#loginSubmit")[0].disabled = !0, e.loadJsonp(u)
        },initEvent: function(n, r) {
            function u() {
                setTimeout(function() {
                    var n = e.Cookie.get("i360loginName");
                    n && n != "undefined" && t("#loginAccount").val(n), e.initPlaceHolders()
                }, 50)
            }
            callBack = n, errorfun = r.errorfun || "", successfun = r.successfun || "", onLoading = r.onLoading || "", captFlag = r.captFlag || !1, type = r.type || "pop", checkPharse = r.checkPharse || !1, i = [];
            if (r.cookie_domains.length == 2 && r.cookie_domains[1] == "nc" || r.cookie_domains[0] == "360")
                l = !0;
            else {
                var s = r.cookie_domains || [], o = s.length && s || [e._hostShort];
                e.forEach(o[0].split("|"), function(t) {
                    var n = e._hostShort2long[t];
                    n && i.push("http://login." + n)
                })
            }
            t(".tipinput1").focus(function() {
                var e = t(this).attr("id");
                e == "phraseLogin" ? t(this).parent().removeClass("verify-code").addClass("verify-code-focus") : t(this).parent().removeClass("input-bg").addClass("input-bg-focus")
            }).blur(function() {
                var e = t(this).attr("id"), n = t(this).val();
                e == "phraseLogin" ? t(this).parent().removeClass("verify-code-focus").addClass("verify-code") : t(this).parent().removeClass("input-bg-focus").addClass("input-bg"), n || t("#tips-" + e).show()
            }), t("#loginAccount").blur(function() {
                var n = e.trim(t("#loginAccount").val());
                prevUsername = n
            }), t("#lwm,#refreshCaptchaLogin").click(function() {
                return p.doGetCaptcha(), t("#phraseLogin").val("").focus(), !1
            }), type == "pop" ? u() : (u(), t(window).on("load", function() {
                u()
            }))
        },doGetCaptcha: function(n) {
            n || (e.queryUrl(location.href, "captcha") == "b360" && (o = QHDomain.i360 + "/reg/doGetCap?app="), n = o + a), t("#lwm").attr("src", n + "&r=" + Math.random())
        },userAuth: function(e) {
            auth_info = e;
            var n = '<label for="qucqlogin"><input type="radio" value="ss" checked="checked" id="qucqlogin" name="qucqlogin">' + e.userName + "</label>";
            e.userName && t("#qhAccount").html(n)
        },quickLogin: function(t) {
            e.autoAuth(t, auth_info)
        }};
    e.loginUtils = p
}(QHPass), QHPass.tpl.loginTpl = {formStart: ['<div id="modLoginWrap" class="mod-qiuser-pop">', '<iframe style="display:none" name="loginiframe"></iframe><form id="loginForm" method="post" target="loginiframe" onsubmit="QHPass.loginUtils.submit();return false;">', ' <dl class="login-wrap">', '<dt><span id="loginTitle"></span></dt>'].join(""),userName: ["<dd >", '<div class="quc-clearfix login-item">', '<label for="loginAccount" >\u5e10\u53f7</label><span class="input-bg"><input placeholder="\u624b\u673a\u53f7/\u7528\u6237\u540d/\u90ae\u7bb1" type="text" tabindex="1" id="loginAccount" name="username" autocomplete="off" maxlength="100"  class="ipt tipinput1"/></span>', '<b class="tips-wrong icon-loginAccount"></b>', '<span id="tips-loginAccount" class="tips-msg "></span>', "</div>", "</dd>"].join(""),password: ['<dd class="password">', '<div class="quc-clearfix login-item">', '<label for="lpassword" >\u5bc6\u7801</label><span class="input-bg"><input placeholder="\u8bf7\u8f93\u5165\u60a8\u7684\u5bc6\u7801" type="password" tabindex="2" id="lpassword" name="password" maxlength="20"  autocomplete="off" class="ipt tipinput1"/></span>', '<b class="tips-wrong icon-lpassword"></b>', '<span id="tips-lpassword" class="tips-msg"></span>', "</div>", "</dd>"].join(""),phrase: ['<dd class="rem" id="phraseLoginwarp" style="display:none" >', '<label for="phraseLogin">\u9a8c\u8bc1\u7801</label>', '<span class="verify-code"><input type="text" tabindex="3" maxlength="4" id="phraseLogin" name="phrase" class="ipt1 tipinput1" autocomplete="off"></span>', '<span class="yz"><img width="99" height="35" id="lwm">', '<a  class="ml8 fac" href="#nogo" id="refreshCaptchaLogin">\u6362\u4e00\u5f20</a>', "</span>", '<p><b class="tips-wrong  icon-phraseLogin"></b><span id="tips-phraseLogin" class="tips-phrase">\u8bf7\u8f93\u5165\u56fe\u4e2d\u7684\u5b57\u6bcd\u6216\u6570\u5b57\uff0c\u4e0d\u533a\u5206\u5927\u5c0f\u5199</span></p>', "</dd>"].join(""),find: ['<dd class="find">', '<label for="iskeepalive"><input type="checkbox" tabindex="4" name="iskeepalive" id="iskeepalive" checked="checked"> \u4e0b\u6b21\u81ea\u52a8\u767b\u5f55</label>', "<a href=" + QHDomain.i360 + '/findpwd/  target="_blank" class="fac" id="findPwd">\u5fd8\u8bb0\u5bc6\u7801\uff1f</a>', "</dd>"].join(""),thirdLogin: "",submit: ['<dd class="submit">', '<span><input type="submit" onfocus="this.blur()"  id="loginSubmit" value="" class="btn-login quc-psp-gstat"><a href="###" class="fac reg-new-account" >\u6ce8\u518c\u65b0\u5e10\u53f7</a></span>', "</dd>"].join(""),formEnd: ['<dd class="global-tips">', '<div id="error_tips" class=""></div>', "</dd>", "</dl>", "</form>", "</div>"].join("")}, QHPass.tpl.pspFriend = {sina: "<a href=\"#\" onclick=\"QHPass.loginUtils.loginuse('Sina','#{type}','#{from}','#{destUrl}'); return false;\"  class=\"loginbtn_sina\"  title=\"\u65b0\u6d6a\u5fae\u535a\u767b\u5f55\"></a>",renren: "<a href=\"#\"  onclick=\"QHPass.loginUtils.loginuse('RenRen','#{type}','#{from}','#{destUrl}'); return false;\" class=\"loginbtn_rr\"  title=\"\u4eba\u4eba\u767b\u5f55\"></a>",fetion: "<a href=\"#\" onclick=\"QHPass.loginUtils.loginuse('Fetion','#{type}','#{from}','#{destUrl}'); return false;\" class=\"loginbtn_fx\"  title=\"\u98de\u4fe1\"></a>",msn: "<a href=\"#\"  onclick=\"QHPass.loginUtils.loginuse('Msn','#{type}','#{from}','#{destUrl}'); return false;\"  class=\"loginbtn_msn\" title=\"Msn\u767b\u5f55\"></a>",Telecom: "<a href=\"#\"  onclick=\"QHPass.loginUtils.loginuse('Telecom','#{type}','#{from}','#{destUrl}'); return false;\"  class=\"loginbtn_ty\" title=\"\u5929\u7ffc\u767b\u5f55\"></a>"};
(function(e) {
    var t = e.$, n = QHDomain.captchaUrl, r = "i360";
    t.prototype.getAttr = t.prototype.getAttr || t.prototype.attr, t.prototype.ancestorNode = t.prototype.ancestorNode || t.prototype.closest, e.removeNode = function(e) {
        e && e.parentNode.removeChild(e)
    }, e.removeFromArray = function(e, t) {
        var n = [], r = 0, i = t.length;
        for (r = 0; r < i; r++)
            t[r] != e && n.push(t[r]);
        return n
    }, e.bindGetcode = function(n, r) {
        e.forEach(t(n), function(n) {
            var r = null, i = 120, s = /^(13|14|15|18)\d{9}$/;
            obj = t(n), clearInterval(r), obj.click(function(n) {
                return function() {
                    var o = t("#phoneReg").val();
                    o ? s.test(o) ? (n[0].disabled = !0, e.loadJsonp(QHDomain.i360 + "/smsApi/sendsmscode?account=" + o + "&condition=2&r=" + Math.random(), function(e) {
                        if (e.errno == "0") {
                            n.removeClass("auth-code").addClass("msging"), n.val(i + "\u79d2\u540e\u70b9\u6b64\u53ef\u91cd\u53d1");
                            var s = i;
                            r = setInterval(function() {
                                s < 1 ? (n[0].disabled = !1, n.removeClass("msging").addClass("auth-code"), s = i, n.val("\u70b9\u6b64\u91cd\u53d1\u6821\u9a8c\u7801"), clearInterval(r)) : (s -= 1, n.val(s + "\u79d2\u540e\u70b9\u6b64\u53ef\u91cd\u53d1"))
                            }, 1e3)
                        } else if (e.errno == 1106) {
                            var o = '\u5e10\u53f7\u5df2\u5b58\u5728\uff0c<a target="_blank" class="fac clk-quc-login" href="###">\u7acb\u5373\u767b\u5f55</a>';
                            t(".icon-phoneReg").removeClass("icon-success").removeClass("icon-loading").addClass("icon-wrong").show(), t("#tips-phoneReg").html(o).addClass("reg-tips-wrong").show(), n[0].disabled = !1
                        } else
                            t(".icon-phoneReg").removeClass("icon-success").removeClass("icon-loading").addClass("icon-wrong").show(), t("#tips-phoneReg").html(e.errmsg).addClass("reg-tips-wrong").show(), n[0].disabled = !1
                    })) : e.regUtils.showErrs("phoneReg", "\u624b\u673a\u53f7\u683c\u5f0f\u9519\u8bef") : e.regUtils.showErrs("phoneReg", "\u624b\u673a\u53f7\u4e0d\u80fd\u4e3a\u7a7a")
                }
            }(obj))
        })
    }, e.showBind = function(t, n) {
        n = n || {}, e.mix(n, e.resConfig.bindOpts), e.showReg(t, n)
    }, e.showReg = function(n, r) {
        function y(n, r, s) {
            var o = {email: n ? '<li data-type="email" class= "ncur"  ><a  href="###"><span class="email-icon">\u90ae\u7bb1\u6ce8\u518c</span></a></li>' : "",phone: r ? '<li data-type="phone" class= "ncur"  ><a  href="###"><span class="tel-icon">\u624b\u673a\u6ce8\u518c</span></a></li>' : "",name: s ? '<li data-type="name" class= "ncur"  ><a  href="###"><span class="uname-icon">\u7528\u6237\u540d\u6ce8\u518c</span></a></li>' : ""}, u = "";
            if (i.length > 1)
                for (var a = 0, f = i.length; a < f; a++)
                    i[a] == "normal" ? i[a] = "email" : "", u += o[i[a]];
            else
                u = o.email + o.phone + o.name;
            t("#regWays").html(u);
            var l = t("#regWays li");
            for (var a = 0, f = l.length; a < f; a++) {
                var c = t(l[a]);
                if (c.getAttr("data-type") == i[0]) {
                    c.removeClass("ncur").addClass("cur");
                    break
                }
            }
            t("#regWays").click(function(n) {
                var r = n.target, i = t(r).ancestorNode("li"), s = t(i)[0].getAttribute("data-type");
                if (t(i).hasClass("cur"))
                    return;
                t("li[data-type]").removeClass("cur").addClass("ncur"), t(i).removeClass("ncur").addClass("cur"), b(s), e.preventDefault(n)
            })
        }
        function b(n) {
            switch (n) {
                case "email":
                    e.showReg(e.Adapter.globalCallback, {regway: "email"});
                    break;
                case "phone":
                    e.showReg(e.Adapter.globalCallback, {regway: "phone"});
                    break;
                case "name":
                    e.showReg(e.Adapter.globalCallback, {regway: "name"})
            }
            t("a,button,label").focus(function() {
                this.blur()
            })
        }
        r = r || {}, r.regway = r.regway || e.resConfig.regway || "normal", e.cssLoadOnce && e.cssLoadOnce(), e.Adapter.globalCallback = n;
        var i = e.resConfig.regType || "normal";
        i = i.split("_"), regway = r.regway, regway == "phone" ? e.mix(r, e.resConfig.regPhoneOpts || {}) : e.mix(r, e.resConfig.regOpts || {});
        var s = r.title || e.resConfig && e.resConfig.regTitle || "\u6b22\u8fce\u6ce8\u518c360", o = r.width || 531, u = r.height || 390, a = r.css || e.resConfig && e.resConfig.css || "", f = r.field || ["loginEmail", "password", "rePassword", "phrase"], l = r.wrap || "", c = r.cookie_domains || e.resConfig.cookie_domains || [], h = r.afterRender || "", p = r.captFlag == null ? !0 : r.captFlag;
        customBtn = r.customBtn || !1, notMustField = r.notMustField || [], nickname = r.nickname || !1, needName = r.needName || !1, needEmailActive = regway == "name" ? !1 : r.needEmailActive || !1, type = r.type || l ? "wrap" : "pop", r.cookie_domains = c, regway == "email" || regway == "normal" ? regway = "email" : regway == "phone" ? f = ["phone", "password", "rePassword", "authCode"] : regway == "name" ? f = ["regUsername", "password", "rePassword", "phrase"] : regway == "bind" && (f = ["regUsername", "password", "rePassword"] || e.resConfig && e.resConfig.bindOpts.field), needName && regway != "name" && regway != "bind" && needName && f.splice(1, 0, "regUsername");
        if (nickname && regway != "name" && regway != "bind")
            for (var d = 0, v = f.length; d < v; d++)
                if (f[d] == "password") {
                    f.splice(d, 0, "nickname");
                    break
                }
        a && e.loadCss && e.loadCss(a);
        var m = [];
        f = regway != "bind" ? "formStart#" + f.join("#") + "#formEnd" : "formStart#" + f.join("#") + "#formBindEnd", f = f.split("#"), e.tpl.temp[regway + "Reg"] == "" && (e.forEach(f, function(t) {
            m.push(e.tpl.regTpl[t])
        }), regway == "email" || regway == "normal" ? e.tpl.temp.emailReg = m.join("") : e.tpl.temp[regway + "Reg"] = m.join(""));
        if (type == "pop")
            e.resConfig.regPopOnce && (e.closePop && e.closePop(), e.addPop(u, o, "reg"), regway != "bind" ? y("email", "tel", "name") : (t(".reg-nav").hide(), t(".reg-quc-content").css({"padding-left": "10px"})), e.resConfig.regPopOnce = !1), t("#modQiUserPop").html(e.tpl.temp[regway + "Reg"]), regway == "name" ? t(".has-account").css({"margin-top": 165 + 52 * (3 - i.length) + "px"}) : t(".has-account").css({"margin-top": 227 + 52 * (3 - i.length) + "px"}), e.resizeAndshow(u, o);
        else {
            if (!l)
                return;
            t("#" + l).html(e.tpl.temp[regway + "Reg"])
        }
        e.forEach(t(".tipinput"), function(e, n) {
            t(e).attr("tabindex", n + 1)
        }), s && t("#qucPanelTitle").html(s);
        try {
            t(document.body).delegate(".clk-quc-login", "click", function(t) {
                e.login(e.Adapter.globalCallback), e.preventDefault(t)
            })
        } catch (g) {
        }
        p && e.refreshCaptcha(), regway == "bind" && (t("#qucpspregForm").attr("action", QHDomain.i360 + "/profile/dotraninfo"), t("#qucPopcrumb").val(r.crumb || ""), t("#loginEmail").attr("name", "loginEmail")), e.resConfig && e.resConfig.postCharset && t("#pageType").val(e.resConfig.postCharset), needEmailActive && t("#loginEmailActiveFlag").val(1), h && h();
        try {
            t(".pop-dia-close").click(function(t) {
                e.closePop && e.closePop(), e.preventDefault(t)
            })
        } catch (g) {
        }
        e.regUtils.initEvent(e.Adapter.globalCallback, r)
    }, e.initFormByUrl = function(n) {
        var r = e.queryUrl(location.href, "src") || "";
        r && t("#src" + n).val(r)
    }, e.refreshCaptcha = function() {
        e.queryUrl(location.href, "captcha") == "b360" && (n = QHDomain.i360 + "/reg/doGetCap?app="), t("#wm").attr("src", n + r + "&r=" + Math.random())
    }
})(QHPass), function(e) {
    var t = {reg_default_loginEmail: '\u8bf7\u8f93\u5165\u60a8\u7684\u5e38\u7528\u90ae\u7bb1\uff0c<a target="_blank" class="fac" href="http://reg.email.163.com/mailregAll/reg0.jsp"> \u6ca1\u6709\u90ae\u7bb1\uff1f</a>',reg_default_regUsername: "2-14\u4e2a\u5b57\u7b26\uff1a\u82f1\u6587\u3001\u6570\u5b57\u6216\u4e2d\u6587",reg_default_nickname: "2-14\u4e2a\u5b57\u7b26\uff1a\u82f1\u6587\u3001\u6570\u5b57\u6216\u4e2d\u6587",reg_default_password: "6-20\u4e2a\u5b57\u7b26\uff0c\uff08\u533a\u5206\u5927\u5c0f\u5199\uff09",reg_default_rePassword: "\u8bf7\u518d\u6b21\u8f93\u5165\u5bc6\u7801",reg_default_phrase: "\u8bf7\u8f93\u5165\u56fe\u4e2d\u7684\u5b57\u6bcd\u6216\u6570\u5b57\uff0c\u4e0d\u533a\u5206\u5927\u5c0f\u5199",reg_default_phoneReg: "\u8bf7\u8f93\u5165\u60a8\u7684\u624b\u673a\u53f7",reg_default_authCode: "\u8bf7\u8f93\u5165\u77ed\u4fe1\u4e2d6\u4f4d\u6570\u5b57\u6821\u9a8c\u7801",reg_default_realName: "\u8bf7\u8f93\u5165\u6709\u6548\u7684\u771f\u5b9e\u59d3\u540d",reg_default_idCardNum: "\u8bf7\u8f93\u5165\u6709\u6548\u7684\u8eab\u4efd\u8bc1\u53f7\u7801",reg_wrong_default_loginEmail: '\u8bf7\u8f93\u5165\u60a8\u7684\u5e38\u7528\u90ae\u7bb1\uff0c<a target="_blank" class="fac" href="http://reg.email.163.com/mailregAll/reg0.jsp"> \u6ca1\u6709\u90ae\u7bb1\uff1f</a>',reg_wrong_default_regUsername: "\u8bf7\u8f93\u5165\u60a8\u7684\u7528\u6237\u540d",reg_wrong_default_phoneReg: "\u8bf7\u8f93\u5165\u60a8\u7684\u624b\u673a\u53f7",reg_wrong_default_nickname: "\u8bf7\u8f93\u5165\u60a8\u7684\u6635\u79f0",reg_wrong_default_password: "\u5bc6\u7801\u5e94\u4e3a6-20\u4e2a\u5b57\u7b26\uff0c\uff08\u533a\u5206\u5927\u5c0f\u5199\uff09",reg_wrong_default_rePassword: "\u4e24\u6b21\u8f93\u5165\u7684\u5bc6\u7801\u4e0d\u4e00\u6837\uff0c\u8bf7\u91cd\u65b0\u8f93\u5165",reg_wrong_default_phrase: "\u8bf7\u6b63\u786e\u586b\u5199\u9a8c\u8bc1\u7801",reg_wrong_default_authCode: "\u8bf7\u8f93\u5165\u77ed\u4fe1\u4e2d6\u4f4d\u6570\u5b57\u6821\u9a8c\u7801",reg_wrong_loginEmail_empty: "\u8bf7\u8f93\u5165\u60a8\u7684\u5e38\u7528\u90ae\u7bb1",reg_wrong_loginEmail_format: "\u8bf7\u8f93\u5165\u6709\u6548\u7684\u90ae\u7bb1\u5730\u5740",reg_wrong_password_chinese: "\u5bc6\u7801\u4e0d\u80fd\u542b\u6709\u4e2d\u6587\u6216\u5168\u89d2\u5b57\u7b26",reg_wrong_password_same_chars: "\u5bc6\u7801\u4e0d\u80fd\u5168\u4e3a\u76f8\u540c\u5b57\u7b26",reg_wrong_password_empty: "\u5bc6\u7801\u4e0d\u80fd\u4e3a\u7a7a",reg_wrong_password_emptychars: "\u5bc6\u7801\u4e0d\u80fd\u5168\u90e8\u4e3a\u7a7a\u683c",reg_wrong_password_weaklevel: "\u5bc6\u7801\u5f31\uff0c\u6709\u98ce\u9669\uff0c\u8bf7\u91cd\u65b0\u8f93\u5165",reg_wrong_password_lx_chars: "\u5bc6\u7801\u4e0d\u80fd\u4e3a\u8fde\u7eed\u5b57\u7b26",reg_wrong_repassword_empty: "\u786e\u8ba4\u5bc6\u7801\u4e0d\u80fd\u4e3a\u7a7a",reg_wrong_repassword_emptychars: "\u786e\u8ba4\u5bc6\u7801\u4e0d\u80fd\u5168\u90e8\u4e3a\u7a7a\u683c",reg_wrong_phrase_input: "\u9a8c\u8bc1\u7801\u8f93\u5165\u9519\u8bef\uff0c\u8bf7\u91cd\u65b0\u8f93\u5165",reg_wrong_phrase_ban: "\u9a8c\u8bc1\u7801\u8fde\u7eed\u9519\u8bef\u6b21\u6570\u8fc7\u591a\uff0c\u8bf7\u6539\u5929\u518d\u6765",reg_wrong_isagree: "\u8bf7\u5148\u9605\u8bfb\u5e76\u540c\u610f\u300a360\u7528\u6237\u670d\u52a1\u6761\u6b3e\u300b",reg_wrong_process_error: "\u6ce8\u518c\u8fc7\u7a0b\u4e2d\u53d1\u751f\u610f\u5916\uff0c\u8bf7\u5237\u65b0\u540e\u91cd\u8bd5",net_check: "\u68c0\u67e5\u4e2d\uff0c\u8bf7\u7a0d\u540e...",reg_wrong_username_short: "\u7528\u6237\u540d\u6700\u5c11\u4f7f\u75282\u4e2a\u5b57\u7b26\u6216\u6c49\u5b57",reg_wrong_username_long: "\u7528\u6237\u540d\u4e0d\u8d85\u8fc77\u4e2a\u6c49\u5b57\u621614\u4e2a\u5b57\u7b26",reg_wrong_username_chars: "\u7528\u6237\u540d\u4e0d\u80fd\u5305\u542b\u7279\u6b8a\u5b57\u7b26",reg_wrong_username_empty: "\u8bf7\u8f93\u5165\u6709\u6548\u7684\u7528\u6237\u540d",reg_wrong_nickname_short: "\u6635\u79f0\u6700\u5c11\u4f7f\u75282\u4e2a\u5b57\u7b26\u6216\u6c49\u5b57",reg_wrong_nickname_long: "\u6635\u79f0\u4e0d\u8d85\u8fc77\u4e2a\u6c49\u5b57\u621614\u4e2a\u5b57\u7b26",reg_wrong_nickname_chars: "\u6635\u79f0\u4e0d\u80fd\u5305\u542b\u7279\u6b8a\u5b57\u7b26",reg_wrong_nickname_empty: "\u8bf7\u8f93\u5165\u6709\u6548\u7684\u6635\u79f0",reg_wrong_authCode_empty: "\u6821\u9a8c\u7801\u4e0d\u80fd\u4e3a\u7a7a",reg_wrong_phoneNumber_empty: "\u8bf7\u8f93\u5165\u60a8\u7684\u624b\u673a\u53f7",reg_wrong_phoneNumber_format: "\u624b\u673a\u53f7\u683c\u5f0f\u9519\u8bef",reg_wrong_format_authCode: "\u6821\u9a8c\u7801\u683c\u5f0f\u9519\u8bef",reg_wrong_idCardNum_empty: "\u8eab\u4efd\u8bc1\u53f7\u4e0d\u80fd\u4e3a\u7a7a",reg_wrong_realName_empty: "\u771f\u5b9e\u59d3\u540d\u4e0d\u80fd\u4e3a\u7a7a",reg_wrong_realName: "\u59d3\u540d\u4e0d\u6b63\u786e\uff0c\u8bf7\u8f93\u5165\u6b63\u786e\u7684\u59d3\u540d",reg_wrong_idCardNum: "\u8bf7\u68c0\u67e5\u4f60\u7684\u8eab\u4efd\u8bc1\u53f7\u7801\uff0c\u652f\u630115\u4f4d\u6216\u800518\u4f4d",reg_wrong_age_lt18: "\u60a8\u672a\u6ee118\u5c81\uff0c\u4e0d\u80fd\u901a\u8fc7\u7533\u8bf7\u6ce8\u518c"}, n = e.$, r = [], i = [], s = !0, o = !1, u = [], a = null, f = "", l = !1, c = !1, h = "", p = [], d = "\u6b63\u5728\u6ce8\u518c\u4e2d\uff0c\u8bf7\u7a0d\u540e", v = null, m = null, g = null, y = null, b = [], w = "1", E = "", S = {prevloginEmail: "",prevuserName: "",prevnickName: ""}, x = ["asdasd", "asdfgh", "asdfghjkl", "Iloveyou", "qwerty", "Password", "Passwd", "Woaini", "Wodemima", "Woaiwojia", "zxcvbn", "tamade", "nimade", "123abc", "0123456", "0123456789", "100200", "102030", "121212", "111222", "115415", "123000", "123123", "123789", "12301230", "123321", "123456", "1234560", "123465", "1234567", "12345678", "123456789", "1234567890", "123123123", "1314520", "1314521", "147258369", "147852369", "159357", "168168", "201314", "211314", "321321", "456456", "4655321", "521521", "5201314", "520520", "741852", "741852963", "7758258", "7758521", "654321", "852963", "987654", "963852741", "000000", "111111", "11111111", "112233", "666666", "888888", "abcdef", "abcabc", "abc123", "a1b2c3", "aaa111", "123qwe", "qweasd", "admin", "password", "p@ssword", "passwd", "iloveyou", "1qaz2wsx", "qwertyuiop", "qq123456", "1q2w3e4r", "123456abc", "abc123456", "qazwsxedc", "1q2w3e4r5t"];
    n.prototype.parent = n.prototype.parent || n.prototype.parentNode, n.prototype.bind = n.prototype.bind || n.prototype.on;
    var T = {showErrs: function(e, t) {
            return clearTimeout(m), this.inArray(u, e) == -1 && u.push(e), n(".icon-" + e).removeClass("icon-success").removeClass("icon-loading").addClass("icon-wrong").show(), n("#tips-" + e).html(t).removeClass("reg-tips-success").addClass("reg-tips-wrong").show(), !1
        },showCorrect: function(e, r) {
            return this.reset(e), n(".icon-" + e).removeClass("icon-wrong").removeClass("icon-loading").addClass("icon-success").show(), E == "pop" ? n("#tips-" + e).html(t["reg_default_" + e]).removeClass("reg-tips-wrong").addClass("reg-tips-success").show() : n("#tips-" + e).hide(), !0
        },reset: function(t) {
            u = e.removeFromArray(t, u), n(".tips-wrong-" + t).hide()
        },loadingFun: function(e) {
            n(".icon-" + e).removeClass("icon-info").addClass("icon-loading"), n("#tips-" + e).hide()
        },loginEmail: function(r) {
            r = r || "";
            var i = e.trim(n("#qucpspregForm")[0].loginEmail.value), s = QHDomain.login_http + "/index.php?op=checkemail&loginEmail=" + i + "&r=" + Math.random();
            S.prevloginEmail = i;
            if (!i)
                return this.showErrs("loginEmail", t.reg_wrong_loginEmail_empty);
            if (i && !this.isEmail(i))
                return this.showErrs("loginEmail", t.reg_wrong_loginEmail_format);
            i && (r || (this.loadingFun("loginEmail"), e.loadJsonp(s, e.regUtils.loginEmailCallback)))
        },realName: function() {
            var r = e.trim(n("#qucpspregForm")[0].realName.value), i = /^[\u4e00-\u9fa5]{2,5}$/;
            return r ? r && !i.test(r) ? this.showErrs("realName", t.reg_wrong_realName) : this.showCorrect("realName", t.reg_default_realName) : this.showErrs("realName", t.reg_wrong_realName_empty)
        },idCardNum: function() {
            function v(e, t) {
                for (var n = 0; n < t.length; n++)
                    e = e.replace(t[n][0], t[n][1]);
                return e
            }
            function m(e) {
                return v(e, [[/[\uff01-\uff5e]/g, function(e) {
                            return String.fromCharCode(e.charCodeAt(0) - 65248)
                        }], [/\u3000/g, " "], [/\u3002/g, "."]])
            }
            var r = e.trim(n("#qucpspregForm")[0].idCardNum.value);
            r = m(r);
            var i = r == "";
            if (!!i)
                return this.showErrs("idCardNum", t.reg_wrong_idCardNum_empty);
            if (/^\d{15}$/.test(r))
                i = !0;
            else if (/^\d{17}[0-9xX]$/.test(r)) {
                var s = "1,0,x,9,8,7,6,5,4,3,2".split(","), o = "7,9,10,5,8,4,2,1,6,3,7,9,10,5,8,4,2".split(","), u = r.toLowerCase().split(""), a = 0;
                for (var f = 0; f < 17; f++)
                    a += o[f] * u[f];
                i = s[a % 11] == u[17]
            }
            if (!i)
                return this.showErrs("idCardNum", t.reg_wrong_idCardNum);
            var l = r.substring(6, 14), c = new Date, h = new Date(l.substring(0, 4), l.substring(4, 6), l.substring(6, 8)), p = c - h, d = Math.round(p / 31536e6);
            d < 18 ? (i = !1, this.showErrs("idCardNum", t.reg_wrong_age_lt18)) : i = !0, i && this.showCorrect("idCardNum", t.reg_default_idCardNum)
        },regUsername: function(r) {
            r = r || "";
            var i = e.trim(n("#qucpspregForm")[0].regUsername.value), s = e.byteLen(i), o = /^[0-9a-zA-Z\u4e00-\u9fa5_\.]*$/i, u = QHDomain.login_http + "/index.php?op=checkuser&userName=" + encodeURIComponent(i) + "&r=" + Math.random();
            S.prevUserName = i;
            if (!i)
                return this.showErrs("regUsername", t.reg_wrong_username_empty);
            if (0 == o.test(i))
                return this.showErrs("regUsername", t.reg_wrong_username_chars);
            if (i && s < 2)
                return this.showErrs("regUsername", t.reg_wrong_username_short);
            if (i && s > 14)
                return this.showErrs("regUsername", t.reg_wrong_username_long);
            i && (r || (this.loadingFun("regUsername"), e.loadJsonp(u, e.regUtils.checkUsernameCallback)))
        },nickname: function(r) {
            r = r || "";
            var i = e.trim(n("#qucpspregForm")[0].nickname.value), s = e.byteLen(i), o = /^[0-9a-zA-Z\u4e00-\u9fa5_\.]*$/i, u = QHDomain.login_http + "/index.php?op=checknickname&nickName=" + encodeURIComponent(i) + "&r=" + Math.random();
            S.prevnickName = i;
            if (!i)
                return this.showErrs("nickname", t.reg_wrong_nickname_empty);
            if (0 == o.test(i))
                return this.showErrs("nickname", t.reg_wrong_nickname_chars);
            if (i && s < 2)
                return this.showErrs("nickname", t.reg_wrong_nickname_short);
            if (i && s > 14)
                return this.showErrs("nickname", t.reg_wrong_nickname_long);
            i && (r || (this.loadingFun("nickname"), e.loadJsonp(u, e.regUtils.checkNicknameCallback)))
        },phoneReg: function(r) {
            r = r || "";
            var i = n("#qucpspregForm")[0].phoneReg.value, s = QHDomain.login_http + "/index.php?op=checkmobile&mobile=" + encodeURIComponent(i) + "&r=" + Math.random();
            regTel = /^(13|14|15|18)\d{9}$/;
            if (!i)
                return this.showErrs("phoneReg", t.reg_wrong_phoneNumber_empty);
            if (!regTel.test(i))
                return this.showErrs("phoneReg", t.reg_wrong_phoneNumber_format);
            i && (r || (this.loadingFun("phoneReg"), e.loadJsonp(s, e.regUtils.checkPhoneCallback)))
        },checkPhoneCallback: function(t) {
            if (0 == t.errno)
                e.regUtils.showCorrect("phoneReg", t.errmsg), n("#getAuthcode")[0].disabled = !1;
            else if (t.errno == 1037) {
                var r = t.errmsg + '\uff0c<a target="_blank" class="fac clk-quc-login" href="##">\u7acb\u5373\u767b\u5f55</a>';
                n("#getAuthcode")[0].disabled = !0, e.regUtils.showErrs("phoneReg", r)
            } else
                e.regUtils.showErrs("phoneReg", t.errmsg)
        },authCode: function() {
            var e = n("#qucpspregForm")[0].authCode.value, r = /^\d{6}$/i;
            if (!e)
                return this.showErrs("authCode", t.reg_wrong_authCode_empty);
            if (!r.test(e))
                return this.showErrs("authCode", t.reg_wrong_format_authCode);
            if (e.length != 6)
                return this.showErrs("authCode", t.reg_wrong_default_authCode);
            this.reset("authCode"), n(".icon-authCode").removeClass("icon-wrong").removeClass("icon-success").addClass("icon-info"), n("#tips-authCode").html(t.reg_default_authCode).removeClass("reg-tips-wrong").addClass("reg-tips-success")
        },isEmail: function(e) {
            return e.length > 100 ? !1 : /^([\w\-\.]+)@(([0-9a-zA-Z\-]+\.)+[a-zA-Z]{2,4})$/i.test(e) ? !0 : !1
        },loginEmailCallback: function(t) {
            if (0 == t.res)
                e.regUtils.showCorrect("loginEmail", t.msg);
            else if (t.errno == 2e4)
                e.regUtils.showErrs("loginEmail", t.msg);
            else if (1 == t.res) {
                var n = t.msg + (w == "bind" ? "" : '\uff0c<a target="_blank" class="fac clk-quc-login" href="###">\u7acb\u5373\u767b\u5f55</a>');
                e.regUtils.showErrs("loginEmail", n)
            } else
                e.regUtils.showErrs("loginEmail", t.msg)
        },checkUsernameCallback: function(t) {
            if (0 == t.res)
                e.regUtils.showCorrect("regUsername", t.msg);
            else if (1 == t.res && t.errno == 213) {
                var n = t.msg + (w == "bind" ? "" : '\uff0c<a target="_blank" class="fac clk-quc-login" href="###">\u7acb\u5373\u767b\u5f55</a>');
                e.regUtils.showErrs("regUsername", n)
            } else
                e.regUtils.showErrs("regUsername", t.msg)
        },checkNicknameCallback: function(t) {
            0 == t.res ? e.regUtils.showCorrect("nickname", t.msg) : e.regUtils.showErrs("nickname", t.msg)
        },doRegSuccess: function() {
            g && g();
            if (c) {
                e.showSetName(a);
                return
            }
            if (l)
                e.getUserInfo(function(t) {
                    t = t || {}, t.realName = n("#realName").val(), t.idCardNum = n("#idCardNum").val();
                    if (t.qid > 0) {
                        try {
                            e.setloginStatus(t)
                        } catch (r) {
                        }
                        e.execCallback(a, t)
                    }
                });
            else {
                var t = {};
                t.realName = n("#realName").val(), t.idCardNum = n("#idCardNum").val();
                try {
                    e.setloginStatus()
                } catch (r) {
                }
                e.execCallback(a, t)
            }
        },rdCallback: function(t) {
            if (t.errno > 0) {
                this.doRegSuccess();
                return
            }
            var n = i.length, s = "";
            i[n++] = t.domain, n == r.length ? (i = [], this.doRegSuccess()) : (s = r[n] + "?o=sso&m=setcookie&func=QHPass.regUtils.rdCallback&s=" + f, e.loadJsonp(s))
        },unique: function(e) {
            var t = e.length, n = e.slice(0), r, i;
            while (--t > 0) {
                i = n[t], r = t;
                while (r--)
                    if (i === n[r]) {
                        n.splice(t, 1);
                        break
                    }
            }
            return n
        },inArray: function(e, t, n) {
            var r = e.length, i = t;
            n |= 0, n < 0 && (n = Math.max(0, r + n));
            for (; n < r; n++)
                if (n in e && e[n] === t)
                    return n;
            return -1
        },getPassLevel: function(t) {
            function a(e) {
                e += "";
                var t = e.length, n = !0, r = e.charCodeAt(t - 1) - e.charCodeAt(0) > 0 ? 1 : -1;
                for (var i = 0; i < t - 1; i++)
                    if (r !== e.charCodeAt(i + 1) - e.charCodeAt(i))
                        return n = !1, n;
                return n
            }
            t += "";
            var n = t.length, r = t.split(""), i = T.unique(r), s = i.length, o = -1;
            cflag = a(t);
            if (n < 6 || n > 20)
                o = -1;
            else if (s == 1)
                o = 0;
            else if (cflag)
                o = 1;
            else if (x.join("#").indexOf(t) > -1)
                o = 2;
            else {
                var u = {d: 0,c: 0,o: 0};
                e.forEach(i, function(e, t) {
                    /\d/.test(e) ? u.d = 1 : /[a-zA-Z]/.test(e) ? u.c = 1 : u.o = 1
                }), o = u.d + u.c + u.o + (n > 9 ? 2 : 1), o = o == 2 ? o + 1 : o
            }
            return o
        },password: function() {
            var e = "password", r = n("#qucpspregForm")[0].password[0].value, i = T.getPassLevel(r), s = /[^\x00-\xff]/, o = /^\s*$/, u = n("#qucpspregForm")[0].rePassword[0].value;
            if (!(r && r.length >= 6 && r.length <= 20))
                return this.showErrs(e, t.reg_wrong_default_password);
            if (s.test(r))
                return u && r != u && this.showErrs("rePassword", t.reg_wrong_default_rePassword), this.showErrs(e, t.reg_wrong_password_chinese);
            if (i < 3) {
                if (!r)
                    return this.showErrs("password", t.reg_wrong_password_empty);
                if (o.test(r))
                    return this.showErrs(e, t.reg_wrong_password_emptychars);
                i == 0 ? this.showErrs(e, t.reg_wrong_password_same_chars) : i == 1 ? this.showErrs(e, t.reg_wrong_password_lx_chars) : i == 2 ? this.showErrs(e, t.reg_wrong_password_weaklevel) : this.showErrs(e, t.reg_wrong_password_weaklevel), u && r != u && this.showErrs("rePassword", t.reg_wrong_default_rePassword)
            } else
                this.showCorrect(e, t.reg_default_password), u && r == u && this.showCorrect("rePassword", t.reg_wrong_default_rePassword)
        },rePassword: function() {
            var e = n("#qucpspregForm")[0].password[0].value, r = /^\s*$/, i = n("#qucpspregForm")[0].rePassword[0].value;
            return i ? r.test(i) ? this.showErrs("rePassword", t.reg_wrong_repassword_emptyChars) : e != i ? this.showErrs("rePassword", t.reg_wrong_default_rePassword) : this.showCorrect("rePassword", t.reg_default_rePassword) : this.showErrs("rePassword", t.reg_wrong_repassword_empty)
        },phrase: function() {
            if (!s)
                return;
            var r = e.trim(n("#qucpspregForm")[0].phrase.value);
            if (r.length != 4)
                return this.showErrs("phrase", t.reg_wrong_default_phrase);
            this.reset("phrase"), E == "pop" ? (n(".icon-phrase").hide(), n("#tips-phrase").hide()) : (n(".icon-phrase").removeClass("icon-wrong").removeClass("icon-success").addClass("icon-info"), n("#tips-phrase").html(t.reg_default_phrase).removeClass("reg-tips-wrong").addClass("reg-tips-success"))
        },beforeSubmit: function() {
            if (!n("#srcreg").val() && w != "bind")
                return !1;
            e.forEach(n(".tipinput"), function(e) {
                var t = e.getAttribute("id");
                T[t]("beck")
            });
            if (u.length > 0)
                return !1;
            if (!n("#is_agree").attr("checked") && w != "bind")
                return T.showGlobalTips(t.reg_wrong_isagree), !1;
            m = setTimeout(function() {
                T.showGlobalTips("\u8bf7\u6c42\u8d85\u65f6\uff0c\u8bf7\u5237\u65b0\u9875\u9762\u91cd\u8bd5\uff01")
            }, 2e4), T.beforeSetValue()
        },initEvent: function(i, f) {
            a = i, b = f.field || [], y = f.errorfun || "", g = f.successfun || "", p = f.notMustField || [], w = f.regway || "normal", E = f.type || "pop", n("#accoutType").val(w == "email" || w == "normal" ? 1 : w == "phone" ? 2 : 4), u.length = 0, s = f.captFlag == null ? !0 : f.captFlag, l = f.getInfoAfterReg || e.resConfig && e.resConfig.getInfoAfterReg || !1, c = f.isSetname || !1, r = [];
            var h = f.src || e.resConfig && e.resConfig.src || "";
            if (f.cookie_domains.length == 2 && f.cookie_domains[1] == "nc" || f.cookie_domains[0] == "360")
                o = !0;
            else {
                var d = f.cookie_domains.length && f.cookie_domains || [e._hostShort];
                e.forEach(d[0].split("|"), function(t) {
                    var n = e._hostShort2long[t];
                    n && r.push("http://login." + n)
                })
            }
            e.initFormByUrl("reg"), h && n("#srcreg").val(h), n(".tipinput").focus(function() {
                var e = n(this).attr("id"), r = "";
                e == "phrase" ? n(this).parent().removeClass("verify-code").addClass("verify-code-focus") : e == "phoneReg" ? n(this).parent().removeClass("input-phone-bg").addClass("input-phone-bg-focus") : n(this).parent().removeClass("input-bg").addClass("input-bg-focus");
                if (e == "nickname" || e == "regUsername" || e == "loginEmail")
                    S["prev" + e] = n("#" + e).val();
                r = e == "password" || e == "rePassword" ? n("#qucpspregForm")[0][e][0].value : n("#qucpspregForm")[0][e].value, r || (E != "pop" ? n(".icon-" + e).removeClass("icon-wrong").addClass("icon-info").show() : n(".icon-" + e).removeClass("icon-wrong").addClass("icon-info").hide(), n("#tips-" + e).html(t["reg_default_" + e]).show().removeClass("reg-tips-wrong"))
            }).blur(function() {
                var e = n(this).attr("id"), r = e == "password" || e == "rePassword" ? n("#qucpspregForm")[0][e][0].value : n("#qucpspregForm")[0][e].value;
                empty = /^\s*$/, e == "phrase" ? n(this).parent().removeClass("verify-code-focus").addClass("verify-code") : e == "phoneReg" ? n(this).parent().removeClass("input-phone-bg-focus").addClass("input-phone-bg") : n(this).parent().removeClass("input-bg-focus").addClass("input-bg");
                if (!r) {
                    e == "loginEmail" ? n("#tips-" + e).addClass("reg-tips-wrong").show() : n("#tips-" + e).html(t["reg_wrong_default_" + e]).addClass("reg-tips-wrong").show(), n(".icon-" + e).removeClass("icon-info").removeClass("icon-success").addClass("icon-wrong").show();
                    return
                }
                if ((e == "nickname" || e == "regUsername" || e == "loginEmail") && S["prev" + e] == n("#" + e).val())
                    return;
                T[e]()
            }), n("#password").bind("keyup", function(e) {
                n("#tips-password").html(t.reg_default_password).removeClass("reg-tips-wrong");
                if (n(this).val()) {
                    var r = parseInt(T.getPassLevel(n(this).val()));
                    if (r == -1)
                        return T.showErrs("password", t.reg_wrong_default_password);
                    r < 3 ? (r = 2, n("#tips-password").html('<span class="level2">\u5f31\uff1a</span>\u8bd5\u8bd5\u5b57\u6bcd\u3001\u6570\u5b57\u548c\u6807\u7b7e\u7b26\u53f7'), E != "pop" && n(".icon-password").removeClass("icon-wrong").addClass("icon-info")) : r == 3 ? (n("#tips-password").html('<span class="level3">\u4e2d\uff1a</span>\u8bd5\u8bd5\u5b57\u6bcd\u3001\u6570\u5b57\u548c\u6807\u7b7e\u7b26\u53f7'), E != "pop" && n(".icon-password").removeClass("icon-wrong").addClass("icon-info")) : r >= 4 && (n("#tips-password").html('<span class="level4">\u5f3a\uff1a</span>\u8bf7\u7262\u8bb0\u4f60\u7684\u5bc6\u7801'), E != "pop" && n(".icon-password").removeClass("icon-wrong").addClass("icon-info"))
                }
            }), n("#phraseLi")[s ? "show" : "hide"](), n("#captchaFlag").val(+s), n("#wm,#refreshCaptcha").click(function(t) {
                e.preventDefault(t), e.refreshCaptcha(), n("#phrase").val("").focus()
            }), e.bindGetcode(".btn-auth-code")
        },beforeSetValue: function() {
            if (w == "normal" || w == "email") {
                var t = n("#loginEmail");
                n("#loginEmail").val(e.trim(t.val()))
            } else if (w == "name") {
                var r = n("#regUsername");
                r.val(e.trim(r.val()))
            }
            var i = n("#topassword"), s = n("#torePassword");
            i.val(hex_md5(n("#password").val())), s.val(hex_md5(n("#rePassword").val())), typeof vcTime != "undefined" && n("#vc").val(vcTime), n("#regGlobal_tips").hide(), n("#regSubmitBtn").removeClass("btn-register").addClass("btn-register-loading").val("\u63d0\u4ea4\u4e2d...")
        },showGlobalTips: function(e) {
            clearTimeout(m);
            var t = w == "bind" ? "\u63d0\u4ea4" : "\u7acb\u5373\u6ce8\u518c";
            n("#regSubmitBtn").removeClass("btn-register-loading").addClass("btn-register").val(t), n("#regGlobal_tips").html(e).show()
        },submitCallback: function(i) {
            try {
                if (i.errno == 0)
                    if (i.activeurl)
                        if (E == "pop") {
                            var u = decodeURIComponent(i.activeurl) + "&destUrl=" + encodeURIComponent(location.href), l = n("#qucpspregForm")[0].loginEmail.value, c = l.substring(0, l.indexOf("@")), p = l.substring(l.indexOf("@"));
                            h = u, e.loadJsonp(h, function(t) {
                                e.closePop && e.closePop(), e.addPop("100", "408", "login"), e.resizeAndshow("100", "408"), n("#qucPanelTitle").html("\u6d88\u606f\u63d0\u793a");
                                try {
                                    n(".pop-dia-close").click(function(t) {
                                        e.closePop && e.closePop(), e.preventDefault(t)
                                    })
                                } catch (r) {
                                }
                                t.errno == 0 ? (n("#modQiUserPop").html(e.tpl.activeTpl), n(".set-active-result").show(), e.byteLen(l) > 20 ? n("#secactiveverify").html(c.replace(/(\w)\w+(\w)/ig, "$1******$2") + p) : l && n("#secactiveverify").html(l), t.goToMail ? n("#qucgoactive").attr("href", t.goToMail) : n(".go-sec-mail").hide(), n("#click_resendMail").on("click", function(t) {
                                    e.loadJsonp(h, function(e) {
                                        e.errno == 0 ? (n("#resendmail_result").show(), setTimeout(function() {
                                            n("#resendmail_result").hide()
                                        }, 3e3)) : n("#modQiUserPop").html('<div style="padding:40px;font-size:14px"><p>' + e.errmsg + ',<a href="#"  class="fac"  id="click_resendMail" target="_blank">\u70b9\u6b64\u91cd\u53d1\u4e00\u5c01</a></p></div>')
                                    }), e.preventDefault(t)
                                })) : (n("#modQiUserPop").html('<div style="padding:40px;font-size:14px"><p>' + t.errmsg + ',<a href="#"  class="fac"  id="click_resendMail" target="_blank">\u70b9\u6b64\u91cd\u53d1\u4e00\u5c01</a></p></div>'), n("#click_resendMail").on("click", function(t) {
                                    e.loadJsonp(h, function(e) {
                                        e.errno == 0 ? (n("#resendmail_result").show(), setTimeout(function() {
                                            n("#resendmail_result").hide()
                                        }, 3e3)) : n("#modQiUserPop").html('<div style="padding:40px;font-size:14px"><p>' + e.errmsg + ',<a href="#"  class="fac"  id="click_resendMail" target="_blank">\u70b9\u6b64\u91cd\u53d1\u4e00\u5c01</a></p></div>')
                                    }), e.preventDefault(t)
                                }))
                            })
                        } else
                            location.href = decodeURIComponent(i.activeurl) + "&destUrl=" + encodeURIComponent(location.href);
                    else {
                        var d = "", v = n("#qucpspregForm")[0];
                        if (w != "email" && w != "normal" || !v.loginEmail) {
                            if (w == "phone" && v.phoneReg)
                                d = e.trim(v.phoneReg.value);
                            else if (w == "name" && v.regUsername)
                                d = e.trim(v.regUsername.value);
                            else if (w == "bind") {
                                e.autoLogin(a), e.closePop && e.closePop();
                                return
                            }
                        } else
                            d = e.trim(v.loginEmail.value);
                        d == "" && v.regUsername && (d = e.trim(v.regUsername.value)), e.Cookie.set("i360loginName", d, {expires: 5184e6,path: "/"}), f = i.rd;
                        if (f && !o) {
                            var g = r.length, b = 0, S = r[b] + "?o=sso&m=setcookie&func=QHPass.regUtils.rdCallback&s=" + f;
                            e.loadJsonp(S)
                        } else
                            this.doRegSuccess()
                    }
                else {
                    y && y(i), clearTimeout(m);
                    var x = w == "bind" ? "\u63d0\u4ea4" : "\u7acb\u5373\u6ce8\u518c";
                    n("#regSubmitBtn").removeClass("btn-register-loading").addClass("btn-register").val(x), n("#regGlobal_tips").hide();
                    if (i.errno == -2)
                        return n("#phrase").focus(), e.refreshCaptcha(), this.showErrs("phrase", t.reg_wrong_phrase_input);
                    if (i.errno == -3)
                        return n("#phrase").focus(), e.refreshCaptcha(), this.showErrs("phrase", t.reg_wrong_phrase_ban);
                    if (i.errno == 1670)
                        s = !0, n("#phraseLi").show(), e.refreshCaptcha(), n("#phrase").focus();
                    else {
                        if (i.errno == 1351)
                            return n("#authCode").focus(), e.refreshCaptcha(), this.showErrs("authCode", decodeURIComponent(i.errmsg));
                        if (i.errmsg.indexOf("\u5bc6\u7801") != -1)
                            return this.showErrs("password", decodeURIComponent(i.errmsg));
                        T.showGlobalTips(decodeURIComponent(i.errmsg));
                        try {
                            e.refreshCaptcha(), n("#phrase").val("")
                        } catch (N) {
                        }
                    }
                }
            } catch (N) {
                T.showGlobalTips(t.reg_wrong_process_error)
            }
        }};
    e.getPassLevel = function(e) {
        return T.getPassLevel(e)
    }, e.regUtils = T
}(QHPass), QHPass.tpl.regTpl = {formStart: ['<div id="modRegWrap" class="mod-qiuser-pop">', '<iframe src="" name ="qucpspregIframe" style="display:none"></iframe>', '<form id="qucpspregForm" method="post" name="qucpspregForm" target="qucpspregIframe" onsubmit="return QHPass.regUtils.beforeSubmit()" action=' + QHDomain.i360 + "/reg/doregAccount >", '<dl class="reg-wrap">', '<dt><span id="qucRegGuide"></span><div id="regGlobal_tips" class="reg-global-error reg-global-success reg-global-loading"></div></dt>'].join(""),loginEmail: ["<dd>", '<div class="quc-clearfix reg-item" >', '<label for="loginEmail">\u90ae\u7bb1</label>', '<span class="input-bg"><input type="text"  id="loginEmail"  name="account" maxlength="100" autocomplete="off" class="ipt tipinput"/></span>', '<b class="icon-loginEmail"></b>', "</div>", '<span id="tips-loginEmail" class="text-tips tips-loginEmail">\u8bf7\u8f93\u5165\u60a8\u7684\u5e38\u7528\u90ae\u7bb1\uff0c<a target="_blank" class="fac" href="http://reg.email.163.com/mailregAll/reg0.jsp"> \u6ca1\u6709\u90ae\u7bb1\uff1f</a></span>', "</dd>"].join(""),nickname: ["<dd>", '<div class="quc-clearfix reg-item" >', '<label for="nickname">\u6635\u79f0</label>', '<span class="input-bg"><input type="text"  id="nickname"  name="nickName" maxlength="14" autocomplete="off" class="ipt tipinput"/></span>', '<b class="icon-nickname"></b>', "</div>", '<span id="tips-nickname" class="text-tips tips-nickname">2-14\u4e2a\u5b57\u7b26\uff1a\u82f1\u6587\u3001\u6570\u5b57\u6216\u4e2d\u6587</span>', "</dd>"].join(""),idCardNum: ["<dd>", '<div class="quc-clearfix reg-item" >', '<label for="idCardNum">\u8eab\u4efd\u8bc1\u53f7</label>', '<span class="input-bg"><input type="text"  id="idCardNum"   maxlength="100" autocomplete="off" class="ipt tipinput"/></span>', '<b class="icon-idCardNum"></b>', "</div>", '<span id="tips-idCardNum" class="text-tips tips-idCardNum">\u8bf7\u8f93\u5165\u6709\u6548\u7684\u8eab\u4efd\u8bc1\u53f7\u7801</span>', "</dd>"].join(""),realName: ["<dd>", '<div class="quc-clearfix reg-item" >', '<label for="realName">\u771f\u5b9e\u59d3\u540d</label>', '<span class="input-bg"><input type="text"  id="realName"   maxlength="100" autocomplete="off" class="ipt tipinput"/></span>', '<b class="icon-realName"></b>', "</div>", '<span id="tips-realName" class="text-tips tips-realName">\u8bf7\u8f93\u5165\u6709\u6548\u7684\u771f\u5b9e\u59d3\u540d</span>', "</dd>"].join(""),phone: ['<dd class="phone">', '<div class="quc-clearfix reg-item" >', '<label for="phoneReg">\u624b\u673a\u53f7</label>', '<span class="input-phone-bg"><input type="text"  id="phoneReg"  name="account" maxlength="11" autocomplete="off" class="ipt tipinput"/></span>', '<input id="getAuthcode" onfocus="this.blur()" class="auth-code btn-auth-code" value="\u514d\u8d39\u83b7\u53d6\u6821\u9a8c\u7801" />', '<b class="icon-phoneReg"></b>', "</div>", '<span id="tips-phoneReg" class="text-tips tips-phoneReg">\u8bf7\u8f93\u5165\u60a8\u7684\u624b\u673a\u53f7</span>', '<div class="btn-authcode">', '<a class="ques-link" target="_blank" href="http://i.360.cn/help/smscode">\u6821\u9a8c\u7801\u5e38\u89c1\u95ee\u9898</a>', "</div>", "</dd>"].join(""),authCode: ["<dd>", '<div class="quc-clearfix reg-item" >', '<label for="authCode">\u6821\u9a8c\u7801</label>', '<span class="input-bg"><input type="text" id="authCode" name="smscode" maxlength="6" autocomplete="off"  class="ipt tipinput "></span>', '<b class="icon-authCode"></b>', "</div>", '<span id="tips-authCode" class="text-tips tips-authCode">\u8bf7\u8f93\u5165\u77ed\u4fe1\u4e2d6\u4f4d\u6570\u5b57\u6821\u9a8c\u7801</span>', "</dd>"].join(""),regUsername: ["<dd>", '<div class="quc-clearfix reg-item">', '<label for="regUsername">\u7528\u6237\u540d</label>', '<span class="input-bg"><input type="text"  maxlength="14" id="regUsername" name="userName" autocomplete="off" class="ipt tipinput"/></span>', '<b class="icon-regUsername"></b>', "</div>", '<span id="tips-regUsername" class="text-tips tips-regUsername">2-14\u4e2a\u5b57\u7b26\uff1a\u82f1\u6587\u3001\u6570\u5b57\u6216\u4e2d\u6587</span>', "</dd>"].join(""),password: ["<dd>", '<div class="quc-clearfix reg-item">', '<label for="password">\u5bc6\u7801</label>', '<span class="input-bg"><input type="password"   id="password"  autocomplete="off" class="ipt tipinput"/></span>', '<b class="icon-password"></b>', "</div>", '<span id="tips-password" class="text-tips tips-password">6-20\u4e2a\u5b57\u7b26\uff0c\uff08\u533a\u5206\u5927\u5c0f\u5199\uff09</span>', "</dd>"].join(""),rePassword: ["<dd>", '<div class="quc-clearfix reg-item">', '<label for="rePassword">\u786e\u8ba4\u5bc6\u7801</label>', '<span class="input-bg"><input type="password" id="rePassword" autocomplete="off" class="ipt tipinput"/></span>', '<b class="icon-rePassword "></b>', "</div>", '<span id="tips-rePassword" class="text-tips tips-rePassword">\u8bf7\u518d\u6b21\u8f93\u5165\u5bc6\u7801</span>', "</dd>"].join(""),phrase: ['<dd class="rem" id="phraseLi" >', '<label for="phrase">\u9a8c\u8bc1\u7801</label>', '<span class="verify-code"><input type="text"  maxlength="4" id="phrase" name="phrase" class="ipt1 tipinput" autocomplete="off"></span>', '<span class="yz"><img width="99" height="35" style="cursor: pointer;" id="wm"><a  class="fac ml8" href="#nogo" id="refreshCaptcha">\u6362\u4e00\u5f20</a>', '</span><b class="icon-phrase"></b>', '<p class="phrase-tips"><span id="tips-phrase" class="tips-phrase">\u8bf7\u8f93\u5165\u56fe\u4e2d\u7684\u5b57\u6bcd\u6216\u6570\u5b57\uff0c\u4e0d\u533a\u5206\u5927\u5c0f\u5199</span></p>', "</dd>"].join(""),formEnd: ['<dd class="submit">', '<input type="submit" onfocus="this.blur()" text="\u7acb\u5373\u6ce8\u518c" id="regSubmitBtn" value="\u7acb\u5373\u6ce8\u518c" class="btn-register quc-psp-gstat">', "</dd>", '<dd class="rules">', '<label style="" for="is_agree">', '<input type="checkbox" name="is_agree" id="is_agree" tabindex="5" checked="checked" value="1">\u6211\u5df2\u7ecf\u9605\u8bfb\u5e76\u540c\u610f</label>', '<a href="http://i.360.cn/pub/protocol.html" class="fac" target="_blank">\u300a360\u7528\u6237\u670d\u52a1\u6761\u6b3e\u300b</a>', "</dd>", "</dl>", '<input id="srcreg" type="hidden" value="" name="src">', '<input id="loginEmailActiveFlag" type="hidden" value="0" name="loginEmailActiveFlag">', '<input id="pageType" type="hidden" value="utf-8" name="charset">', '<input id="accoutType" type="hidden" value="" name="acctype">', '<input id="pwdmethod" type="hidden" value="1" name="pwdmethod">', '<input id="proxy" type="hidden" value="http://' + location.host + '/psp_jump.html" name="proxy">', '<input id="topassword" type="hidden" value="" name="password">', '<input id="torePassword" type="hidden" value="" name="rePassword">', '<input id="callback" type="hidden" value="parent.QHPass.regUtils.submitCallback" name="callback">', '<input id="captchaFlag" type="hidden" value="1" name="captchaFlag">', '<input id="captchaAppId" type="hidden" value="i360" name="captchaAppId">', "</form></div>"].join(""),formBindEnd: ['<dd class="submit">', '<input type="submit" onfocus="this.blur()" text="bind\u63d0\u4ea4" id="regSubmitBtn" value="\u63d0\u4ea4" class="btn-register quc-psp-gstat">', "</dd>", '<dd class="rules">', '<input id="proxy" type="hidden" value="http://' + location.host + '/psp_jump.html" name="proxy">', '<input id="topassword" type="hidden" value="" name="password">', '<input id="torePassword" type="hidden" value="" name="rePassword">', '<input id="callback" type="hidden" value="parent.QHPass.regUtils.submitCallback" name="callback">', '<input id="qucPopcrumb" type="hidden" value="" name="crumb"></dd></dl>', "</form></div>"].join("")}, QHPass.tpl.activeTpl = '<div class="set-active-result" id="setactiveResult"><p>\u9a8c\u8bc1\u90ae\u4ef6\u5df2\u7ecf\u53d1\u9001\u5230<span class="active-tips" id="secactiveverify"></span></p><p>\u4f60\u9700\u8981\u70b9\u51fb\u90ae\u7bb1\u4e2d\u7684\u786e\u8ba4\u94fe\u63a5\u6765\u5b8c\u6210</p><p class="go-sec-mail"><a  href="#" class="fac"  id="qucgoactive" target="_blank">\u7acb\u5373\u8fdb\u5165\u90ae\u7bb1</a></p><p class="result-title">\u6ca1\u6709\u6536\u5230\u786e\u8ba4\u94fe\u63a5\u600e\u4e48\u529e\uff1f</p><p>1.\u770b\u770b\u662f\u5426\u5728\u90ae\u7bb1\u7684\u56de\u6536\u7ad9\u4e2d\uff0c\u5783\u573e\u90ae\u7bb1\u4e2d</p><p>2.\u786e\u8ba4\u6ca1\u6709\u6536\u5230\uff0c<a href="#"  class="fac"  id="click_resendMail" target="_blank">\u70b9\u6b64\u91cd\u53d1\u4e00\u5c01</a><span class="resend-result" id="resendmail_result">\u53d1\u9001\u6210\u529f\uff01</span></p></div>', QHPass.tpl.temp = {normalLogin: "",emailReg: "",nameReg: "",phoneReg: "",bindReg: ""};
(function(e) {
    var t = e.$, n = QHDomain.captchaUrl;
    e.showSetName = function(n, r) {
        r = r || {}, e.cssLoadOnce && e.cssLoadOnce(), e.mix(r, e.resConfig.namedOpts || {});
        var i = r.title || e.resConfig.setNameTitle || "\u8bbe\u7f6e\u7528\u6237\u540d", s = r.width || 408, o = r.height || 200, u = r.type || "pop", a = r.wrap, f = r.css || e.resConfig && e.resConfig.css || "", l = r.afterRender || e.resConfig && e.resConfig.namedOpts && e.resConfig.namedOpts.afterRender, c = r.notRequest || !1;
        f && e.loadCss(f);
        if (c) {
            if (u == "pop")
                e.closePop && e.closePop(), e.addPop(o, s, "username"), t("#modQiUserPop").html(e.tpl.unameTpl), e.resizeAndshow(o, s);
            else {
                if (!a)
                    return;
                t("#" + a).append(e.tpl.unameTpl)
            }
            i && t("#qucPanelTitle").html(i), e.resConfig && e.resConfig.postCharset && t("#pageType").val(e.resConfig.postCharset), l && l(), e.userNameUtils.initEvent(n, r)
        } else
            e.getUserInfo(function(f) {
                r.crumb = f.crumb;
                if (f.type == "bind" || !(!f.userName || f.userName.indexOf("360U") > -1)) {
                    e.execCallback(n, f);
                    return
                }
                u == "pop" ? (e.closePop && e.closePop(), e.addPop && e.addPop(o, s, "username"), t("#modQiUserPop").html(e.tpl.unameTpl), t(".mod-qiuser-pop").css({height: o + "px"}), e.resizeAndshow(o, s)) : t("#" + a).append(e.tpl.unameTpl), e.resConfig && e.resConfig.postCharset && t("#pageType").val(e.resConfig.postCharset), i && t("#qucPanelTitle").html(i), l && l(), e.userNameUtils.initEvent(n, r)
            })
    }
})(QHPass), function(e) {
    e.userNameUtils = function() {
        function b(e, n) {
            return n && t("#qucUnameTips").hide(), t("#userNameSuggest").addClass("uname-tips-wrong").html(e).show(), !1
        }
        function w() {
            var n = t("#pspUserName").val(), o = e.byteLen(n);
            return o < 2 ? b(c, !0) : o > 14 ? b(p, !0) : i.test(n) ? b(h, !0) : r.test(n) ? s.test(n) ? b(l, !0) : !0 : b(d, !0)
        }
        function E(e) {
            t("#pspUserName").val(e.value)
        }
        function S(t) {
            t.errno == 0 ? (u && u(), e.resConfig.loginAfterSetName = !1, e.clearUinfo(), typeof o == "string" ? location.href = o : typeof o == "boolean" ? window.location.reload(o) : e.getUserInfo(function(t) {
                try {
                    e.setloginStatus(t)
                } catch (n) {
                }
                t.type != "bind" && t.userName && t.userName.indexOf("360U") == -1 && o && o(t)
            })) : (a && a(), e.userNameUtils.showErr(decodeURIComponent(t.errmsg)), setTimeout(function() {
                window.parent.location.reload(!0)
            }, 2e3))
        }
        function x() {
            e.resConfig.loginAfterSetName = !1, typeof o == "string" ? location.href = o : typeof o == "boolean" ? window.location.reload(o) : e.getUserInfo(function(t) {
                try {
                    e.setloginStatus(t)
                } catch (n) {
                }
            })
        }
        function T(n) {
            if (n.errno == 0)
                if (e._hostCurr) {
                    var r = "http://login." + e._hostCurr;
                    e.loadJsonp(r + "/?o=sso&m=updateCookie&key=" + n.key, e.userNameUtils.setUserNameSuccess)
                } else
                    o && o();
            else
                n.errno == 1051 || n.errno == 197 || n.errno == 1050 ? (e.userNameUtils.showErr(decodeURIComponent(n.errmsg)), setTimeout(function() {
                    window.parent.location.reload(!0)
                }, 2e3)) : (t("#btn-submitName")[0].disabled = !0, e.userNameUtils.showErr(decodeURIComponent(n.errmsg)));
            t("#btn-submitName")[0].disabled = !1
        }
        function N(e) {
            var n = t("#pspUserName").val();
            if (1 == e.res) {
                t("#qucUnameTips").hide();
                var r = e.userinfo ? e.userinfo.length : 0;
                if (r >= 1) {
                    var i = '<h4 class="recommend-username">\u7528\u6237\u540d\u5df2\u7ecf\u88ab\u5360\u7528\uff0c\u6211\u4eec\u4e3a\u60a8\u63a8\u8350\u4ee5\u4e0b\u7528\u6237\u540d\uff1a</h4>';
                    for (var s = 0; s < r; s++)
                        i += '<label for="user' + s + '"><input type="radio" name="usernames" id="user' + s + '" value="' + e.userinfo[s] + '" onclick="QHPass.userNameUtils.getUserName(this)" >' + e.userinfo[s] + "</label>", s % 2 && (i += "<br/>");
                    t("#userNameSuggest").html(i).show()
                } else
                    t("#userNameSuggest").html(decodeURIComponent(e.msg)).addClass("recommend-username");
                t("#btn-submitName")[0].disabled = !1
            } else
                document.qucPspSetUserName.submit()
        }
        function C(n, r) {
            r = r || {}, a = r.errorfun || "", u = r.successfun || "", o = n, t("#qucCrumb").val(r.crumb), t("#pspUserName").focus(function() {
                t(this).parent().addClass("input-bg-focus")
            }).blur(function() {
                t(this).parent().removeClass("input-bg-focus")
            }), t("#pspUserName").on("keyup", function() {
                var n = t(this).val(), r = e.byteLen(n);
                r > 14 ? (t(this).val(n.substring(0, 13)), b(p, !0)) : t("#userNameSuggest").removeClass("recommend-username").html("")
            }), t(".pop-dia-close").click(function(t) {
                e._hostCurr ? x() : e.closePop && e.closePop(), e.preventDefault(t)
            }), e.initPlaceHolders()
        }
        function k() {
            if (w()) {
                t("#btn-submitName")[0].disabled = !0;
                var n = t("#pspUserName").val();
                e.loadJsonp(f + "&userName=" + encodeURIComponent(n) + "&time=" + Math.random(), e.userNameUtils.checkUsernameBack)
            }
        }
        var t = e.$, n = [], r = /^[0-9a-zA-Z\u4e00-\u9fa5_\.]*$/i, i = /^\d*$/i, s = /^360U/i, o = null, u = null, a = null, f = QHDomain.login_http + "/index.php?op=checkuser", l = "\u7528\u6237\u540d\u4e0d\u80fd\u4ee5 360U \u5f00\u5934", c = "\u7528\u6237\u540d\u6700\u5c11\u4f7f\u75282\u4e2a\u5b57\u7b26", h = "\u7528\u6237\u540d\u4e0d\u80fd\u5168\u90e8\u662f\u6570\u5b57", p = "\u7528\u6237\u540d\u4e0d\u8d85\u8fc77\u4e2a\u6c49\u5b57\u621614\u4e2a\u5b57\u7b26", d = "\u7528\u6237\u540d\u4e0d\u80fd\u5305\u542b\u7279\u6b8a\u5b57\u7b26", v = "\u90ae\u7bb1\u4e0d\u80fd\u4e3a\u7a7a", m = "\u90ae\u7bb1\u683c\u5f0f\u4e0d\u5408\u6cd5", g = "\u786e\u8ba4\u5bc6\u7801\u4e0d\u80fd\u4e3a\u7a7a", y = "\u4e24\u6b21\u8f93\u5165\u5bc6\u7801\u4e0d\u4e00\u81f4\uff0c\u8bf7\u91cd\u65b0\u8f93\u5165";
        return {initEvent: C,showErr: b,checkUsernameBack: N,submitCallback: T,getUserName: E,submit: k,setUserNameSuccess: S}
    }()
}(QHPass), QHPass.tpl.unameTpl = '<div class="mod-set-username mod-qiuser-pop"><iframe style="display:none" name="setUsernameiframe"></iframe><form  method="post" onsubmit="QHPass.userNameUtils.submit();return false;" action=' + QHDomain.i360 + '/profile/dochusername id="qucPspSetUserName" name="qucPspSetUserName" target="setUsernameiframe" >' + "<ul>" + '<li class="msg-tit-tips">' + '<span class="uname-tips global-tips"  id="userNameResultTips"></span>' + "</li>" + '<li class="uname-input">' + '<span class="input-bg"><input type="text" name="userName" maxlength="14" class="ipt" placeholder="\u8bf7\u8f93\u5165\u60a8\u7684\u7528\u6237\u540d" id="pspUserName" /></span><input type="submit" value="" id="btn-submitName" class="submit-setuname-btn"/>' + "</li>" + '<li id="qucUnameTips" class="uname-tips">\u7528\u6237\u540d\u662f360\u8eab\u4efd\u5c55\u793a\uff0c\u4e00\u7ecf\u786e\u8ba4\uff0c\u4e0d\u53ef\u4fee\u6539</li>' + '<li class="suggest-username">' + '<div  class="uname-tips" id="userNameSuggest"></div></li>' + "<li>" + '<input id="qucCrumb" name="crumb"  type="hidden" />' + '<input id="pageType" name="charset" value="gbk"  type="hidden" />' + '<input id="jump_url" name="proxy" type="hidden"  value="http://' + location.host + '/psp_jump.html" />' + '<input id="callback" name="callback" value="parent.QHPass.userNameUtils.submitCallback"  type="hidden"  />' + "</li>" + "</ul>" + "</form>" + "</div> ";
(function(e) {
    var t = e.$, n = QHDomain.captchaUrl;
    e.showSecMail = function(n, r) {
        r = r || {}, e.cssLoadOnce && e.cssLoadOnce(), e.mix(r, e.resConfig.mailOpts || {});
        var i = r.title || e.resConfig.setMailTitle || "\u8bbe\u7f6e\u5bc6\u4fdd\u90ae\u7bb1", s = r.width || 408, o = r.height || 200, u = r.type || "pop", a = r.css || e.resConfig && e.resConfig.css || "", f = r.afterRender || e.resConfig && e.resConfig.mailOpts && e.resConfig.mailOpts.afterRender;
        a && e.loadCss(a), e.closePop && e.closePop(), e.addPop && e.addPop(o, s, "username"), t("#modQiUserPop").html(e.tpl.mailTpl), e.resizeAndshow(o, s), e.resConfig && e.resConfig.postCharset && t("#pageType").val(e.resConfig.postCharset);
        try {
            t(".pop-dia-close").click(function(t) {
                e.closePop && e.closePop(), e.preventDefault(t)
            })
        } catch (l) {
        }
        i && t("#qucPanelTitle").html(i), f && f(), e.emailUtils.initEvent(n, r)
    }
})(QHPass), function(e) {
    e.emailUtils = function() {
        function a(n, o) {
            o = o || {}, s = o.emailtype || "\u5bc6\u4fdd\u90ae\u7bb1", i = o.errorfun || "", r = o.successfun || "", n = n, t(".tipinput").focus(function() {
                t(this).parent().removeClass("input-bg").addClass("input-bg-focus")
            }).blur(function() {
                t(this).parent().removeClass("input-bg-focus").addClass("input-bg")
            }), o.crumb ? t("#qucCrumb").val(o.crumb) : e.getUserInfo(function(e) {
                e.qid && t("#qucCrumb").val(e.crumb)
            }), t("#qucsecSrc").val(e.resConfig.src), e.initPlaceHolders(), t("#click_resendMail").click(function(t) {
                reSendMail(), e.preventDefault(t)
            })
        }
        function f(e) {
            return e.length > 100 ? !1 : /^([\w\-\.]+)@(([0-9a-zA-Z\-]+\.)+[a-zA-Z]{2,4})$/i.test(e) ? !0 : !1
        }
        function l() {
            return e.trim(t("#qucPspSetSecMail")[0].qucsecemail.value) ? f(t("#qucPspSetSecMail")[0].qucsecemail.value) ? t("#qucPspSetSecMail")[0].qucsecpassword.value ? e.byteLen(t("#qucPspSetSecMail")[0].qucsecpassword.value) < 6 ? (c("qucsecpassword", u.sec_pwd_default), !1) : (t("#qucsecPassword").val(hex_md5(t("#qucPspSetSecMail")[0].qucsecpassword.value)), t("#secMailTips").html(""), !0) : (c("qucsecpassword", u.sec_pwd_empty), !1) : (c("qucsecmail", s + u.sec_mail_format), !1) : (c("qucsecmail", s + u.sec_mail_empty), !1)
        }
        function c(e, n) {
            t("#secMailTips").html(n).addClass("set-mail-wrong"), e && t("#" + e).focus()
        }
        function h(e) {
            t("#secMailTips").html(e).addClass("set-mail-right")
        }
        function p() {
            e._hostCurr && e.loadJsonp("http://login." + e._hostCurr + "/s=sso&m=getRd&t=" + Math.random(), function(t) {
                t.rd && e.loadJsonp("http://rd.360.cn/s=sso&m=setcookie&t=" + Math.random(), function(e) {
                    e.errno != 0 && c("", "\u8bf7\u5148\u767b\u5f55\u540e\u64cd\u4f5c")
                })
            })
        }
        function d(e) {
            e.errno == 0 ? r && r(e) : i && i(e)
        }
        var t = e.$, n = !1, r = null, i = null, s = "\u5bc6\u4fdd\u90ae\u7bb1", o = null, u = {sec_mail_empty: "\u4e0d\u80fd\u4e3a\u7a7a",sec_mail_format: "\u683c\u5f0f\u4e0d\u6b63\u786e",sec_pwd_empty: "\u5bc6\u7801\u4e0d\u80fd\u4e3a\u7a7a",sec_pwd_default: "\u5bc6\u7801\u957f\u5ea66\u523020\u4f4d,\u533a\u5206\u5927\u5c0f\u5199"};
        return {initEvent: a,submitCallback: d,submit: l}
    }()
}(QHPass), QHPass.tpl.mailTpl = '<div class="mod-set-secmail mod-set-username mod-qiuser-pop"><iframe style="display:none" name="setEmailiframe"></iframe><form  method="post" onsubmit="return QHPass.emailUtils.submit();" action=' + QHDomain.i360 + '/profile/dosetsecemail id="qucPspSetSecMail" name="qucPspSetSecMail" target="setEmailiframe" >' + '<div id="secmailWrap">' + '<ul id="secmailList">' + '<li class="msg-tit-tips">' + '<span class="global-tips"  id="secMailTips"></span>' + "</li>" + "<li >" + '<span class="input-bg"><input type="text"  name="secemail"  maxlength="100" autocomplete="off" class="ipt tipinput" placeholder="\u8bf7\u8f93\u5165\u60a8\u7684\u5bc6\u4fdd\u90ae\u7bb1" id="qucsecemail" /></span>' + "</li>" + "<li >" + '<span class="input-bg"><input type="password"  maxlength="20" autocomplete="off" class="ipt tipinput" placeholder="\u8bf7\u8f93\u5165\u60a8\u7684360\u5e10\u53f7\u5bc6\u7801" id="qucsecpassword" /></span>' + "</li>" + '<li class="secmail-btn" >' + '<input type="submit" value="" id="btnQucSecMail" class="submit-setuname-btn"/>' + "</li>" + '<li style="display:none">' + '<input id="qucCrumb" name="crumb"  type="hidden" />' + '<input id="qucsecPassword" name="password"   type="hidden" />' + '<input id="qucsecSrc"  name="src"   type="hidden" />' + '<input id="qucsecDestUrl" name="destUrl" value=' + location.href + '  type="hidden" />' + '<input id="pageType" name="charset" value="gbk"  type="hidden" />' + '<input id="jump_url" name="proxy" type="hidden"  value="http://' + location.host + '/psp_jump.html" />' + '<input id="callback" name="callback" value="parent.QHPass.emailUtils.submitCallback"  type="hidden"  />' + "</li>" + "</ul>" + "</div>" + "</form>" + "</div> ";
(function(e) {
    var t = e.$;
    e.CrossDomainRequest = function(e, t, n, r) {
        var i = +(new Date), s = "";
        typeof n == "string" ? s = n : (s = "_CrossDomainCallback" + i, window[s] = function() {
            var e = decodeURIComponent(arguments[0]);
            return o.parentNode.removeChild(o), n(e)
        });
        var o = document.createElement("div");
        o.innerHTML = '<iframe style="display:none" id="_CrossDomainiframe' + i + '" name="' + "_CrossDomainiframe" + i + '" src=""></iframe>', document.body.appendChild(o);
        var u = document.createElement("FORM");
        u.style.display = "none", u.method = r || "post", u.target = "_CrossDomainiframe" + i, u.action = e;
        var a = [];
        a.push('<input type="hidden" name="callback" value="' + s + '" />'), a.push('<input type="hidden" name="proxy" value="http://' + location.host + '/psp_jump.html" />'), u.innerHTML = a.join("");
        for (var f in t) {
            var l = document.createElement("input");
            l.setAttribute("type", "hidden"), l.setAttribute("name", f), l.setAttribute("value", t[f]), u.appendChild(l)
        }
        document.body.appendChild(u), u.submit(), u.parentNode.removeChild(u)
    }, e.showLoginEmail = function(n, r) {
        r = r || {}, e.cssLoadOnce && e.cssLoadOnce(), e.mix(r, e.resConfig.mailOpts || {});
        var i = '<div class="mod-isLoginEmail mod-qiuser-pop"><h2>\u4f7f\u7528\u771f\u5b9e\u7684\u90ae\u7bb1\u4f5c\u4e3a\u767b\u5f55\u90ae\u7bb1\uff0c\u53ef\u5728\u60a8\u5fd8\u8bb0\u5e10\u53f7\u5bc6\u7801\u65f6\u5feb\u901f\u8bbe\u7f6e\u65b0\u5bc6\u7801</h2><div class="global-loginEmail-text"></div><ul><li><span class="loginemail-tag">\u60a8\u7533\u8bf7\u9a8c\u8bc1\u7684\u767b\u5f55\u90ae\u7bb1\u4e3a\uff1a</span></li><li><span class="loginemail-text" id="loginEmail_text"></span></li><li>\u662f\u5426\u5411\u6b64\u90ae\u7bb1\u53d1\u9001\u9a8c\u8bc1\u90ae\u4ef6\uff1f</li><li>\u6211\u4eec\u4f1a\u5411\u60a8\u7684\u90ae\u7bb1\u53d1\u9001\u4e00\u5c01\u9a8c\u8bc1\u90ae\u4ef6\uff0c\u8bf7\u6309\u7167\u90ae\u4ef6\u5185\u6307\u793a\u5b8c\u6210\u9a8c\u8bc1</li><li class="btn-console"><a href="#" data-type="le" id="reSendLoginEmail" class="btn-loginemail">\u53d1\u9001\u9a8c\u8bc1\u90ae\u4ef6</a><span class="send-result">\u53d1\u9001\u6210\u529f\uff01</span></li></ul><div class="loginEmail-tips" id="modifyLoginEmail_warp"><p>\u8fd9\u4e2a\u90ae\u7bb1\u6211\u4e0d\u518d\u4f7f\u7528\u600e\u4e48\u529e\uff1f<a href="#" id="modifyLoginEmail" class="modify-loginEmail">\u4fee\u6539\u767b\u5f55\u90ae\u7bb1</a></p></div></div>', s = '<div class="mod-setLoginEmail mod-qiuser-pop"><h2>\u4f7f\u7528\u771f\u5b9e\u7684\u90ae\u7bb1\u4f5c\u4e3a\u767b\u5f55\u90ae\u7bb1\uff0c\u53ef\u5728\u60a8\u5fd8\u8bb0\u5e10\u53f7\u5bc6\u7801\u65f6\u5feb\u901f\u8bbe\u7f6e\u65b0\u5bc6\u7801</h2><div class="global-loginEmail-text"></div><ul id="loginEmailWrap"><li ><label for="qucLoginEmail">\u767b\u5f55\u90ae\u7bb1</label><span class="input-bg"><input id="qucLoginEmail" type="text"  name="loginEmail"  maxlength="100" autocomplete="off" class="ipt tipinput" /></span><b id="qucLoginEmail_ico" class="icon-wrong error-ico"></b></li><li class="loginemail-ipt-tips"><span id="qucLoginEmail_text" >\u8bf7\u586b\u5199\u60a8\u7684\u767b\u5f55\u90ae\u7bb1</span></li><li ><label for="qucPassword">\u5bc6\u7801</label><span class="input-bg"><input id="qucPassword" type="password"  maxlength="20" autocomplete="off" class="ipt tipinput"  /></span><b id="qucPassword_ico" class="icon-wrong error-ico"></b></li><li class="loginemail-ipt-tips"><span id="qucPassword_text" >\u8bf7\u586b\u5199\u60a8\u7684360\u5e10\u53f7\u767b\u5f55\u5bc6\u7801</span></li><li class="btn-console" ><a  href="#"  id="sendLoginEmail" class="btn-loginemail send-loginemail">\u53d1\u9001\u9a8c\u8bc1\u90ae\u4ef6</a></li></ul><div class="loginEmail-tips"><h2>\u5982\u4f55\u5b8c\u6210\u9a8c\u8bc1\uff1f</h2><p>\u70b9\u51fb\u201c\u53d1\u9001\u90ae\u4ef6\u6309\u94ae\u201d\u540e\uff0c\u6211\u4eec\u4f1a\u5411\u60a8\u7684\u90ae\u4ef6\u53d1\u9001\u4e00\u5c01\u9a8c\u8bc1\u90ae\u4ef6</p><p>\u8bf7\u6309\u7167\u90ae\u4ef6\u4e2d\u7684\u63d0\u793a\u5b8c\u6210\u64cd\u4f5c</p></div></div>', o = '<div class="mod-loginEmail-result mod-qiuser-pop"><div class="global-loginEmail-text"></div><ul><li><span class="loginemail-tag">\u9a8c\u8bc1\u90ae\u4ef6\u5df2\u7ecf\u53d1\u9001\u5230\u60a8\u7684\u767b\u5f55\u90ae\u7bb1:</span></li><li><span class="loginemail-text" id="loginSafeEmail_text"></span></li><li>\u8bf7\u60a8\u572848\u5c0f\u65f6\u5185\u767b\u5f55\u90ae\u7bb1\uff0c\u6309\u7167\u90ae\u7bb1\u5185\u7684\u6307\u793a\u5b8c\u6210\u9a8c\u8bc1</li><li class="btn-console"><a href="#" id="qucgoMail" target="_blank" class="btn-loginemail">\u53bb\u90ae\u7bb1\u6536\u4fe1</a></li></ul><div class="loginEmail-tips"><h2>\u6ca1\u6536\u5230\u90ae\u4ef6\u600e\u4e48\u529e\uff1f</h2><p>\u8bf7\u68c0\u67e5\u60a8\u7684\u5783\u573e\u90ae\u4ef6\u548c\u5e7f\u544a\u90ae\u4ef6</p><p><a href="#" id="reSendLoginEmail2" class="btn-resend-loginemail">\u70b9\u51fb\u8fd9\u91cc</a> \u91cd\u53d1\u4e00\u5c01\u90ae\u4ef6<span class="send-result">\u53d1\u9001\u6210\u529f\uff01</span></p><p>\u5982\u60a8\u6536\u4e0d\u5230\u90ae\u4ef6\uff0c\u8bf7\u8054\u7cfb\u5ba2\u670d<a href="mailto:kefu@360.cn">kefu@360.cn</a></p></div></div>', u = r.title || e.resConfig.setMailTitle || "\u9a8c\u8bc1\u767b\u5f55\u90ae\u7bb1", a = r.width || 425, f = r.height || 200, l = r.type || "pop", c = r.css || e.resConfig && e.resConfig.css || "", h = r.afterRender || e.resConfig.mailOpts.afterRender;
        c && e.loadCss(c), e.closePop && e.closePop(), u && t("#qucPanelTitle").html(u), e.loadJsonp(QHDomain.i360 + "/active/checkLoginEmailStatus?crumb=" + r.crumb, function(u) {
            if (!u.needActive) {
                if (u.errno == 1050) {
                    e.getUserInfo(function(t) {
                        t.crumb ? e.showLoginEmail(n, {crumb: t.crumb}) : alert(u.errmsg)
                    }, r);
                    return
                }
                if (u.errno == 1051) {
                    e.showLogin(function(t) {
                        e.showLoginEmail(n, {crumb: t.crumb})
                    }, r);
                    return
                }
                n && n();
                return
            }
            var l = u.loginEmailUnactivated || u.loginEmail || "", c = u.safeLoginEmailUnactivated || u.safeLoginEmail || "";
            l ? (e.addPop && e.addPop(f, a, "loginEmail"), t("#modQiUserPop").html(i + s + o), u.isOverLimit && t("#modifyLoginEmail_warp").hide(), t("#loginEmail_text").html(l.length > 40 ? c : l), t(".mod-isLoginEmail").show(), u.loginEmailUnactivated && t("#reSendLoginEmail")[0].setAttribute("data-type", "la")) : (e.addPop && e.addPop(f, a, "loginEmail"), t("#modQiUserPop").html(s + o), t(".mod-setLoginEmail").show()), t(".global-loginEmail-text").hide(), t(".send-result").hide(), e.resizeAndshow(f, a);
            try {
                t(".pop-dia-close").click(function(t) {
                    e.closePop && e.closePop(), e.preventDefault(t)
                })
            } catch (p) {
            }
            h && h(), e.loginEmailUtils.initEvent(n, r)
        })
    }
})(QHPass), function(e) {
    e.loginEmailUtils = function() {
        function d(n, r) {
            r = r || {}, s = r.errorfun || "", i = r.successfun || "", u = r.crumb, n = n, t(".tipinput").focus(function() {
                t(this).parent().removeClass("input-bg").addClass("input-bg-focus")
            }).blur(function() {
                t(this).parent().removeClass("input-bg-focus").addClass("input-bg")
            }), t("#sendLoginEmail").click(function(t) {
                e.loginEmailUtils.sendLoginEmail(), e.preventDefault(t)
            }), t("#reSendLoginEmail").click(function(n) {
                t("#reSendLoginEmail")[0].getAttribute("data-type") == "le" ? e.loginEmailUtils.reSendLoginEmail(QHDomain.i360 + "/active/doSetLoginEmail") : e.loginEmailUtils.reSendLoginEmail(), e.preventDefault(n)
            }), t("#modifyLoginEmail").click(function(t) {
                e.loginEmailUtils.modifyLoginEmail(), e.preventDefault(t)
            }), t("#reSendLoginEmail2").click(function(t) {
                e.loginEmailUtils.reSendLoginEmail("", "QHPass.loginEmailUtils.reSendCB2"), e.preventDefault(t)
            }), t("#qucLoginEmail").blur(function() {
                m()
            }), t("#qucPassword").blur(function() {
                g()
            })
        }
        function v(e) {
            return e.length > 100 ? !1 : /^([\w\-\.]+)@(([0-9a-zA-Z\-]+\.)+[a-zA-Z]{2,4})$/i.test(e) ? !0 : !1
        }
        function m() {
            return t("#qucLoginEmail").val() ? v(t("#qucLoginEmail").val()) ? (T("qucLoginEmail"), !0) : (x("qucLoginEmail", p.sec_mail_format), !1) : (x("qucLoginEmail", p.sec_mail_empty), !1)
        }
        function g() {
            return t("#qucPassword").val() ? e.byteLen(t("#qucPassword").val()) < 6 ? (x("qucPassword", p.sec_pwd_default), !1) : (T("qucPassword"), !0) : (x("qucPassword", p.sec_pwd_empty), !1)
        }
        function y() {
            b(), c = !0, t(".global-loginEmail-text").html("").hide(), a = setTimeout(function() {
                l = !0, c = !1, t(".global-loginEmail-text").html("\u8bf7\u6c42\u8d85\u65f6\uff0c\u8bf7\u7a0d\u540e\u91cd\u8bd5").show()
            }, 1e4)
        }
        function b() {
            clearTimeout(a), l = !1, c = !1, t(".global-loginEmail-text").html("").hide(), t(".send-result").hide()
        }
        function w() {
            if (c)
                return;
            m() && g() && (y(), e.CrossDomainRequest(QHDomain.i360 + "/active/doSetLoginEmail", {loginEmail: t("#qucLoginEmail").val(),password: hex_md5(t("#qucPassword").val()),crumb: u,src: e.resConfig.src,destUrl: location.href,charset: e.resConfig.postCharset,proxy: "http://" + location.host + "/psp_jump.html",callback: "parent.QHPass.loginEmailUtils.sendCB"}))
        }
        function E() {
            b(), t(".mod-isLoginEmail").hide(), t(".mod-setLoginEmail").show()
        }
        function S(t, n) {
            if (c)
                return;
            y(), e.CrossDomainRequest(t || QHDomain.i360 + "/active/doSendActiveEmail", {crumb: u,src: e.resConfig.src,destUrl: location.href,charset: e.resConfig.postCharset,proxy: "http://" + location.host + "/psp_jump.html",callback: n || "QHPass.loginEmailUtils.reSendCB"})
        }
        function x(e, n) {
            t("#" + e + "_ico").show(), t("#" + e + "_text").html(n).addClass("tip-text-wrong").show()
        }
        function T(e) {
            t("#" + e + "_text").hide(), t("#" + e + "_ico").hide()
        }
        function N() {
            n && e.loadJsonp("http://login." + n + "/?o=sso&m=getRd&t=" + Math.random(), function(t) {
                t.rd && e.loadJsonp("http://rd.login.360.cn/?o=sso&m=setcookie&t=" + Math.random(), function(e) {
                    e.errno != 0 && x("", "\u8bf7\u5148\u767b\u5f55\u540e\u64cd\u4f5c")
                })
            })
        }
        function C(e) {
            (e.loginEmailUnactivated || "").length > 40 ? t("#loginSafeEmail_text").html(e.safeLoginEmailUnactivated) : t("#loginSafeEmail_text").html(e.loginEmailUnactivated), e.mailHostUrl ? t("#qucgoMail").attr("href", e.mailHostUrl) : t("#qucgoMail").hide()
        }
        function k(n) {
            if (l)
                return;
            b(), n.errno == 0 ? (C(n), t(".mod-setLoginEmail").hide(), t(".mod-loginEmail-result").show()) : n.errno == "220" ? e.loginEmailUtils.showError("qucPassword", decodeURIComponent(n.errmsg)) : (s && s(), t(".global-loginEmail-text").html(decodeURIComponent(n.errmsg)).show())
        }
        function L(e) {
            if (l)
                return;
            b(), e.errno == 0 ? (C(e), t(".mod-isLoginEmail").hide(), t(".mod-loginEmail-result").show()) : (c = !1, t(".send-result").html(decodeURIComponent(e.errmsg)).show(), clearTimeout(f), f = setTimeout(function() {
                t(".send-result").hide()
            }, 2e3))
        }
        function A(e) {
            if (l)
                return;
            b(), e.errno == 0 ? (t(".send-result").show(), clearTimeout(f), f = setTimeout(function() {
                t(".send-result").hide()
            }, 2e3)) : (t(".send-result").html(decodeURIComponent(e.errmsg)).show(), clearTimeout(f), f = setTimeout(function() {
                t(".send-result").hide()
            }, 2e3))
        }
        var t = e.$, n = e._hostCurr, r = !1, i = null, s = null, o = "\u767b\u5f55\u90ae\u7bb1", u = "", a = null, f = null, l = !1, c = !1, h = null, p = {sec_mail_empty: "\u767b\u5f55\u90ae\u7bb1\u4e0d\u80fd\u4e3a\u7a7a",sec_mail_format: "\u767b\u5f55\u90ae\u7bb1\u683c\u5f0f\u4e0d\u6b63\u786e",sec_pwd_empty: "\u5bc6\u7801\u4e0d\u80fd\u4e3a\u7a7a",sec_pwd_default: "\u5bc6\u7801\u957f\u5ea66\u523020\u4f4d,\u533a\u5206\u5927\u5c0f\u5199"};
        return {initEvent: d,showError: x,sendLoginEmail: w,reSendLoginEmail: S,modifyLoginEmail: E,sendCB: k,reSendCB: L,reSendCB2: A}
    }()
}(QHPass);
function hex_md5(e) {
    return binl2hex(core_md5(str2binl(e), e.length * chrsz))
}
function b64_md5(e) {
    return binl2b64(core_md5(str2binl(e), e.length * chrsz))
}
function str_md5(e) {
    return binl2str(core_md5(str2binl(e), e.length * chrsz))
}
function hex_hmac_md5(e, t) {
    return binl2hex(core_hmac_md5(e, t))
}
function b64_hmac_md5(e, t) {
    return binl2b64(core_hmac_md5(e, t))
}
function str_hmac_md5(e, t) {
    return binl2str(core_hmac_md5(e, t))
}
function md5_vm_test() {
    return hex_md5("abc") == "900150983cd24fb0d6963f7d28e17f72"
}
function core_md5(e, t) {
    e[t >> 5] |= 128 << t % 32, e[(t + 64 >>> 9 << 4) + 14] = t;
    var n = 1732584193, r = -271733879, i = -1732584194, s = 271733878;
    for (var o = 0; o < e.length; o += 16) {
        var u = n, a = r, f = i, l = s;
        n = md5_ff(n, r, i, s, e[o + 0], 7, -680876936), s = md5_ff(s, n, r, i, e[o + 1], 12, -389564586), i = md5_ff(i, s, n, r, e[o + 2], 17, 606105819), r = md5_ff(r, i, s, n, e[o + 3], 22, -1044525330), n = md5_ff(n, r, i, s, e[o + 4], 7, -176418897), s = md5_ff(s, n, r, i, e[o + 5], 12, 1200080426), i = md5_ff(i, s, n, r, e[o + 6], 17, -1473231341), r = md5_ff(r, i, s, n, e[o + 7], 22, -45705983), n = md5_ff(n, r, i, s, e[o + 8], 7, 1770035416), s = md5_ff(s, n, r, i, e[o + 9], 12, -1958414417), i = md5_ff(i, s, n, r, e[o + 10], 17, -42063), r = md5_ff(r, i, s, n, e[o + 11], 22, -1990404162), n = md5_ff(n, r, i, s, e[o + 12], 7, 1804603682), s = md5_ff(s, n, r, i, e[o + 13], 12, -40341101), i = md5_ff(i, s, n, r, e[o + 14], 17, -1502002290), r = md5_ff(r, i, s, n, e[o + 15], 22, 1236535329), n = md5_gg(n, r, i, s, e[o + 1], 5, -165796510), s = md5_gg(s, n, r, i, e[o + 6], 9, -1069501632), i = md5_gg(i, s, n, r, e[o + 11], 14, 643717713), r = md5_gg(r, i, s, n, e[o + 0], 20, -373897302), n = md5_gg(n, r, i, s, e[o + 5], 5, -701558691), s = md5_gg(s, n, r, i, e[o + 10], 9, 38016083), i = md5_gg(i, s, n, r, e[o + 15], 14, -660478335), r = md5_gg(r, i, s, n, e[o + 4], 20, -405537848), n = md5_gg(n, r, i, s, e[o + 9], 5, 568446438), s = md5_gg(s, n, r, i, e[o + 14], 9, -1019803690), i = md5_gg(i, s, n, r, e[o + 3], 14, -187363961), r = md5_gg(r, i, s, n, e[o + 8], 20, 1163531501), n = md5_gg(n, r, i, s, e[o + 13], 5, -1444681467), s = md5_gg(s, n, r, i, e[o + 2], 9, -51403784), i = md5_gg(i, s, n, r, e[o + 7], 14, 1735328473), r = md5_gg(r, i, s, n, e[o + 12], 20, -1926607734), n = md5_hh(n, r, i, s, e[o + 5], 4, -378558), s = md5_hh(s, n, r, i, e[o + 8], 11, -2022574463), i = md5_hh(i, s, n, r, e[o + 11], 16, 1839030562), r = md5_hh(r, i, s, n, e[o + 14], 23, -35309556), n = md5_hh(n, r, i, s, e[o + 1], 4, -1530992060), s = md5_hh(s, n, r, i, e[o + 4], 11, 1272893353), i = md5_hh(i, s, n, r, e[o + 7], 16, -155497632), r = md5_hh(r, i, s, n, e[o + 10], 23, -1094730640), n = md5_hh(n, r, i, s, e[o + 13], 4, 681279174), s = md5_hh(s, n, r, i, e[o + 0], 11, -358537222), i = md5_hh(i, s, n, r, e[o + 3], 16, -722521979), r = md5_hh(r, i, s, n, e[o + 6], 23, 76029189), n = md5_hh(n, r, i, s, e[o + 9], 4, -640364487), s = md5_hh(s, n, r, i, e[o + 12], 11, -421815835), i = md5_hh(i, s, n, r, e[o + 15], 16, 530742520), r = md5_hh(r, i, s, n, e[o + 2], 23, -995338651), n = md5_ii(n, r, i, s, e[o + 0], 6, -198630844), s = md5_ii(s, n, r, i, e[o + 7], 10, 1126891415), i = md5_ii(i, s, n, r, e[o + 14], 15, -1416354905), r = md5_ii(r, i, s, n, e[o + 5], 21, -57434055), n = md5_ii(n, r, i, s, e[o + 12], 6, 1700485571), s = md5_ii(s, n, r, i, e[o + 3], 10, -1894986606), i = md5_ii(i, s, n, r, e[o + 10], 15, -1051523), r = md5_ii(r, i, s, n, e[o + 1], 21, -2054922799), n = md5_ii(n, r, i, s, e[o + 8], 6, 1873313359), s = md5_ii(s, n, r, i, e[o + 15], 10, -30611744), i = md5_ii(i, s, n, r, e[o + 6], 15, -1560198380), r = md5_ii(r, i, s, n, e[o + 13], 21, 1309151649), n = md5_ii(n, r, i, s, e[o + 4], 6, -145523070), s = md5_ii(s, n, r, i, e[o + 11], 10, -1120210379), i = md5_ii(i, s, n, r, e[o + 2], 15, 718787259), r = md5_ii(r, i, s, n, e[o + 9], 21, -343485551), n = safe_add(n, u), r = safe_add(r, a), i = safe_add(i, f), s = safe_add(s, l)
    }
    return Array(n, r, i, s)
}
function md5_cmn(e, t, n, r, i, s) {
    return safe_add(bit_rol(safe_add(safe_add(t, e), safe_add(r, s)), i), n)
}
function md5_ff(e, t, n, r, i, s, o) {
    return md5_cmn(t & n | ~t & r, e, t, i, s, o)
}
function md5_gg(e, t, n, r, i, s, o) {
    return md5_cmn(t & r | n & ~r, e, t, i, s, o)
}
function md5_hh(e, t, n, r, i, s, o) {
    return md5_cmn(t ^ n ^ r, e, t, i, s, o)
}
function md5_ii(e, t, n, r, i, s, o) {
    return md5_cmn(n ^ (t | ~r), e, t, i, s, o)
}
function core_hmac_md5(e, t) {
    var n = str2binl(e);
    n.length > 16 && (n = core_md5(n, e.length * chrsz));
    var r = Array(16), i = Array(16);
    for (var s = 0; s < 16; s++)
        r[s] = n[s] ^ 909522486, i[s] = n[s] ^ 1549556828;
    var o = core_md5(r.concat(str2binl(t)), 512 + t.length * chrsz);
    return core_md5(i.concat(o), 640)
}
function safe_add(e, t) {
    var n = (e & 65535) + (t & 65535), r = (e >> 16) + (t >> 16) + (n >> 16);
    return r << 16 | n & 65535
}
function bit_rol(e, t) {
    return e << t | e >>> 32 - t
}
function str2binl(e) {
    var t = Array(), n = (1 << chrsz) - 1;
    for (var r = 0; r < e.length * chrsz; r += chrsz)
        t[r >> 5] |= (e.charCodeAt(r / chrsz) & n) << r % 32;
    return t
}
function binl2str(e) {
    var t = "", n = (1 << chrsz) - 1;
    for (var r = 0; r < e.length * 32; r += chrsz)
        t += String.fromCharCode(e[r >> 5] >>> r % 32 & n);
    return t
}
function binl2hex(e) {
    var t = hexcase ? "0123456789ABCDEF" : "0123456789abcdef", n = "";
    for (var r = 0; r < e.length * 4; r++)
        n += t.charAt(e[r >> 2] >> r % 4 * 8 + 4 & 15) + t.charAt(e[r >> 2] >> r % 4 * 8 & 15);
    return n
}
function binl2b64(e) {
    var t = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789+/", n = "";
    for (var r = 0; r < e.length * 4; r += 3) {
        var i = (e[r >> 2] >> 8 * (r % 4) & 255) << 16 | (e[r + 1 >> 2] >> 8 * ((r + 1) % 4) & 255) << 8 | e[r + 2 >> 2] >> 8 * ((r + 2) % 4) & 255;
        for (var s = 0; s < 4; s++)
            r * 8 + s * 6 > e.length * 32 ? n += b64pad : n += t.charAt(i >> 6 * (3 - s) & 63)
    }
    return n
}
var hexcase = 0, b64pad = "", chrsz = 8;
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
(function(e) {
    var t = e.$, n = QHDomain.captchaUrl;
    e.showBindMobile = function(n, r) {
        r = r || {}, e.cssLoadOnce && e.cssLoadOnce(), e.mix(r, e.resConfig.bindMobileOpts || {});
        var i = r.title || e.resConfig.bindMobileTitle || "\u7ed1\u5b9a\u624b\u673a\u53f7\u7801", s = r.width || 408, o = r.height || 300, u = r.type || "pop", a = r.wrap, f = r.css || e.resConfig && e.resConfig.css || "", l = r.afterRender || e.resConfig.bindMobileOpts && e.resConfig.bindMobileOpts.afterRender;
        f && e.loadCss(f);
        if (u == "pop")
            e.closePop && e.closePop(), e.addPop(o, s, "bindMobile"), t("#modQiUserPop").html(e.tpl.mobileTpl), e.resizeAndshow(o, s);
        else {
            if (!a)
                return;
            t("#" + a).append(e.tpl.mobileTpl)
        }
        i && t("#qucPanelTitle").html(i), e.resConfig && e.resConfig.postCharset && t("#pageType").val(e.resConfig.postCharset), l && l(), e.bindMobileUtils.initEvent(n, r)
    }, e.bindMobileUtils = function() {
        var n = {mobile: /^1(3|5|8|4)\d{9}$/,smscode: /^\d{6}$/,password: /^[\s\S]{6,20}$/}, r = "/smsApi/sendSmsCode", i = "/security/dobindMobile", s = !1, o = null, u = null, a = null, f = null, l = null, c = null, h = ["icon-wrong", "icon-success"], p = ["psp-tips-wrong", "psp-tips-success"];
        return {initEvent: function(r, i) {
                i = i || {}, f = i.submitBindErrorFun, a = i.submitBindSuccessFun, u = r, l = i.sendSmsCodeSuccessFun, c = i.sendSmsCodeErrorFun, i.crumb ? t("#qucCrumb").val(i.crumb) : e.getUserInfo(function(e) {
                    e.qid && t("#qucCrumb").val(e.crumb)
                }), t("#quc_psp_bind_mobile input.ipt").focus(function() {
                    t(this).parent().addClass("input-bg-focus")
                }).blur(function() {
                    var r = this;
                    t(r).parent().removeClass("input-bg-focus"), r.name && r.name in n && e.bindMobileUtils.validIpt(r)
                }), t("#quc_bm_mobile").on("keyup", function() {
                    var t = this, n = e.trim(t.value);
                    e.byteLen(n) > 11 && (t.value = n.substring(0, 11))
                }), t("#quc_psp_get_authcode").click(function() {
                    s = !0
                }), t("#qucsecSrc").val(e.resConfig.src), t(".pop-dia-close").click(function(t) {
                    e.preventDefault(t), e._hostCurr && e.bindMobileUtils.cancelBindMobile("close"), e.closePop && e.closePop()
                }), e.initPlaceHolders && e.initPlaceHolders()
            },showErr: function(e, n) {
                var r = e.id, i = +n, s = +!n;
                e.__psp_icon || (e.__psp_icon = t("#icon-" + r)), e.__psp_icon.addClass(h[i]).removeClass(h[s]).show();
                if (!e.__psp_tips) {
                    var o = e.__psp_tips = t("#tips-" + r);
                    e.__psp_tips_html = o.html()
                }
                e.__psp_tips.addClass(p[i]).removeClass(p[s]).html((n ? e.__psp_tips_html : t(e).attr("psp-err-str")) || "")
            },validIpt: function(t) {
                var r = n[t.name].test(e.trim(t.value));
                return this.showErr(t, r), r
            },validAll: function(e) {
                var e = e || t("#quc_psp_bind_mobile")[0], r, i = !0;
                for (r in n)
                    i = i && this.validIpt(e[r]);
                return i
            },showGlobalErr: function(e, n) {
                var r = +e, i = +!e;
                t("#quc_bind_mobile_guide").addClass(h[i]).removeClass(h[i])[n ? "show" : "hide"](), t("#quc_bind_mobile_global_tips").html(n)
            },bindMobileSuccess: function(e) {
                e.errno == 0 ? (showGlobalErr(!0, "\u624b\u673a\u53f7\u7ed1\u5b9a\u6210\u529f\u3002"), a && a(e.errno), this.cancelBindMobile("success")) : (t("#quc_psp_bind_mobile")[0].password.value = o, this.showGlobalErr(!1, e.errmsg ? decodeURIComponent(e.errmsg) : "\u7ed1\u5b9a\u624b\u673a\u53f7\u7801\u5931\u8d25\uff01"), f && f(e.errno))
            },sendSmsCodeSuccess: function(e) {
                var n = t("#quc_psp_get_authcode");
                if (e.errno != "0") {
                    n.removeAttr("disabled"), this.showGlobalErr(!1, decodeURIComponent(e.errmsg) || "\u83b7\u53d6\u6821\u9a8c\u7801\u5931\u8d25\uff01"), c && c(e.errno);
                    return
                }
                this.showGlobalErr(!0, "\u624b\u673a\u6821\u9a8c\u7801\u5df2\u53d1\u9001"), l && l(e.errno), n.attr("disabled", "disabled").val("120\u79d2\u540e\u53ef\u91cd\u53d1");
                var r = 120, i = setInterval(function() {
                    r < 1 ? (clearInterval(i), n.removeAttr("disabled").val("\u53d1\u9001\u6821\u9a8c\u7801")) : n.val(r-- + "\u79d2\u540e\u53ef\u91cd\u53d1")
                }, 1e3)
            },cancelBindMobile: function(e) {
                var t = typeof u;
                t == "string" ? location.href = u : t == "boolean" ? window.location.reload(u) : t == "function" && u(e)
            },submit: function(e) {
                var n;
                return s ? (s = !1, n = this.validIpt(e.mobile), n && (e.account.value = e.mobile.value, e.callback.value = "parent.QHPass.bindMobileUtils.sendSmsCodeSuccess", e.action = QHDomain.i360 + r, t("#quc_psp_get_authcode").attr("disabled", "disabled"))) : (n = this.validAll(e), n && (e.callback.value = "parent.QHPass.bindMobileUtils.bindMobileSuccess", o = e.password.value, e.password.value = hex_md5(o), e.action = QHDomain.i360 + i)), n && this.showGlobalErr(!0, "\u6570\u636e\u53d1\u9001\u4e2d\u2026"), n
            }}
    }()
})(QHPass), QHPass.tpl.mobileTpl = '<div class="mod-bind-mobile mod-qiuser-pop"><iframe style="display:none" name="ifr_psp_bind_mobile"></iframe><form method="post" onsubmit="return QHPass.bindMobileUtils.submit(this);" action="' + QHDomain.i360 + '/security/dobindMobile" id="quc_psp_bind_mobile" name="qucPspBindMobile" target="ifr_psp_bind_mobile">' + '<dl class="reg-wrap">' + '<dt><span id="quc_bind_mobile_guide"></span><div id="quc_bind_mobile_global_tips" class="reg-global-error reg-global-success reg-global-loading"></div></dt>' + '<dd class="phone">' + '<div class="quc-clearfix reg-item">' + '<label for="quc_bm_mobile">\u624b\u673a\u53f7</label>' + '<span class="input-phone-bg"><input type="text" id="quc_bm_mobile" name="mobile" psp-err-str="\u624b\u673a\u53f7\u683c\u5f0f\u9519\u8bef" maxlength="11" autocomplete="off" class="ipt tipinput" tabindex="1"></span>' + '<input type="submit" id="quc_psp_get_authcode" onfocus="this.blur()" class="auth-code btn-auth-code" value="\u514d\u8d39\u83b7\u53d6\u6821\u9a8c\u7801">' + '<b id="icon-quc_bm_mobile"></b>' + "</div>" + '<span id="tips-quc_bm_mobile" class="text-tips">\u8bf7\u8f93\u5165\u60a8\u7684\u624b\u673a\u53f7</span>' + '<div class="btn-authcode"><a class="ques-link" target="_blank" href="http://i.360.cn/help/smscode">\u6821\u9a8c\u7801\u5e38\u89c1\u95ee\u9898</a></div>' + "</dd>" + "<dd>" + '<div class="quc-clearfix reg-item">' + '<label for="quc_bm_smscode">\u6821\u9a8c\u7801</label>' + '<span class="input-bg"><input type="text" id="quc_bm_smscode" name="smscode" psp-err-str="\u6821\u9a8c\u7801\u683c\u5f0f\u9519\u8bef" maxlength="6" autocomplete="off" class="ipt tipinput " tabindex="2"></span>' + '<b id="icon-quc_bm_smscode"></b>' + "</div>" + '<span id="tips-quc_bm_smscode" class="text-tips">\u8bf7\u8f93\u5165\u77ed\u4fe1\u4e2d6\u4f4d\u6570\u5b57\u6821\u9a8c\u7801</span>' + "</dd>" + "<dd>" + '<div class="quc-clearfix reg-item">' + '<label for="quc_bm_password">\u767b\u5f55\u5bc6\u7801</label>' + '<span class="input-bg"><input type="password" id="quc_bm_password" name="password" psp-err-str="\u5bc6\u7801\u4e3a6-20\u4e2a\u5b57\u7b26" autocomplete="off" class="ipt tipinput" tabindex="3"></span>' + '<b id="icon-quc_bm_password"></b>' + "</div>" + '<span id="tips-quc_bm_password" class="text-tips">6-20\u4e2a\u5b57\u7b26\uff0c\uff08\u533a\u5206\u5927\u5c0f\u5199\uff09</span>' + "</dd>" + '<dd class="submit">' + '<input type="submit" onfocus="this.blur()" value="\u63d0 \u4ea4" class="btn-register quc-psp-gstat">' + "</dd>" + "</dl>" + '<input name="account" value="" type="hidden" />' + '<input name="acctype" value="2" type="hidden" />' + '<input name="condition" value="2" type="hidden" />' + '<input id="qucsecSrc" name="src" value="" type="hidden" />' + '<input id="qucCrumb" name="crumb" type="hidden" />' + '<input id="pageType" name="charset" value="gbk" type="hidden" />' + '<input id="jump_url" name="proxy" type="hidden" value="http://' + location.host + '/psp_jump.html" />' + '<input id="callback" name="callback" value="parent.QHPass.bindMobileUtils.bindMobileSuccess" type="hidden" />' + "</form>" + "</div>";
