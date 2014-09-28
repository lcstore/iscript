window.location.href = 'http://login.etao.com/?spm=0.0.0.0.NTyQZP&redirect_url=http%3A%2F%2Fjf.etao.com%2F%3F';
document.referer = 'http://jf.etao.com/?';
(function(h, C) {
    var k = {version: "0.8.1",enabled: true,debug: false,ratio: 1,timeout: 3000,timestamp: "-",token: "",serviceUrl: "/service/um.json",enableMod: true,containers: {flash: null,dcp: null},appName: ""};
    var j = "string", b = "function", D = "https://s.tbcdn.cn/g/security/umflash/fp.swf?v1=2", l = 6, w = [10, 8, 7, 3, 2];
    var A = window, p = A.document, d = A.navigator, s = 0, n = null, i = false, f = null, r = null, y = false, g, t = null, x = {};
    var B = function() {
        f = p.getElementById("umData");
        n = p.getElementById("umFlash");
        r = p.getElementById("umDcp");
        try {
            if (r && typeof r.getHardVersion != "undefined") {
                t.mod = parseInt(r.getHardVersion().replace(/\./g, ""), 10) || 1
            }
        } catch (u) {
        }
        try {
            if (!i) {
                i = n && n.md5
            }
        } catch (u) {
        }
    };
    var e = function(u, H) {
        var F = u.split(".");
        var I = H.split(".");
        if (F.length > I.length) {
            for (var G = 0; G < F.length - I.length; G++) {
                I.push("0")
            }
        } else {
            if (F.length < I.length) {
                for (var G = 0; G < I.length - F.length; G++) {
                    F.push("0")
                }
            }
        }
        for (var G = 0; G < F.length; G++) {
            if (F[G] < I[G]) {
                return -1
            } else {
                if (F[G] > I[G]) {
                    return 1
                }
            }
        }
        return 0
    };
    var a = function() {
        var S = p.getElementById != C && p.getElementsByTagName != C && p.createElement != C, F = d.userAgent.toLowerCase(), R = d.platform.toLowerCase(), W = R ? (/win/).test(R) : (/win/).test(F), J = R ? (/mac/).test(R) : (/mac/).test(F), u = /webkit/.test(F) ? parseFloat(F.replace(/^.*webkit\/(\d+(\.\d+)?).*$/, "$1")) : false, Q = /msie/.test(F), U = /opera/.test(F), N = !u && (/gecko/).test(F), T = function(X, Y) {
            return d.plugins && d.plugins[X] && d.mimeTypes && d.mimeTypes[Y] && d.mimeTypes[Y].enabledPlugin ? d.plugins[X] : false
        }, G = function(Z) {
            var Y = false;
            try {
                Y = new ActiveXObject(Z)
            } catch (X) {
            }
            return Y
        };
        var O, H, M = [0, 0, 0], K = 0, L = 0;
        if (!!(A.ActiveXObject instanceof Function)) {
            Q = true;
            O = G("ShockwaveFlash.ShockwaveFlash");
            if (O) {
                try {
                    if ((H = O.GetVariable("$version"))) {
                        H = H.split(" ")[1].split(",");
                        M = [parseInt(H[0], 10), parseInt(H[1], 10), parseInt(H[2], 10)]
                    }
                } catch (V) {
                }
                O = null
            }
            O = x.enableMod && G("Alim.webmod");
            if (O) {
                try {
                    L = 1;
                    if (typeof O.getHardVersion != "undefined") {
                        L = parseInt(O.getHardVersion().replace(/\./g, ""), 10) || 1
                    }
                } catch (V) {
                }
                O = null
            }
        } else {
            Q = false;
            O = T("Shockwave Flash", "application/x-shockwave-flash");
            if (O && O.description) {
                H = O.description.replace(/^.*\s+(\S+\s+\S+$)/, "$1");
                M[0] = parseInt(H.replace(/^(.*)\..*$/, "$1"), 10);
                M[1] = parseInt(H.replace(/^.*\.(.*)\s.*$/, "$1"), 10);
                M[2] = /[a-zA-Z]/.test(H) ? parseInt(H.replace(/^.*[a-zA-Z]+(.*)$/, "$1"), 10) : 0;
                O = null
            }
            try {
                if (x.enableMod) {
                    var P = F.match(/chrome\/([\d.]+) safari\/([\d.]+)$/);
                    if (P && P[1]) {
                        var I = P[1];
                        x.enableMod = e(I, "32.0.1700.0") < 1
                    }
                }
            } catch (V) {
            }
            O = x.enableMod && T("Alipay webmod control", "application/alidcp");
            if (O) {
                L = 1;
                O = null
            }
        }
        return {w3: S,flash: M,edit: K,mod: L,wk: u,gk: N,ie: Q,win: W,mac: J}
    }, E = function() {
        if (!x.debug) {
            return
        }
        var F = new Date(), u = F.getSeconds() + "." + F.getMilliseconds() + ": " + Array.prototype.slice.call(arguments).join(" | ");
        if (window.console && console.log) {
            console.log(u)
        } else {
            if (!E.messages) {
                E.messages = []
            }
            E.messages.push(u)
        }
    }, q = function() {
        var u = [];
        return function(F) {
            if (!x.debug) {
                return
            }
            if (window.Tracker) {
                Tracker.click("um-" + F)
            } else {
                if (F) {
                    u.push(F);
                    setTimeout(function() {
                        q(u.shift())
                    }, 100)
                }
            }
        }
    }(), v = function(H) {
        for (var G = 1, F = arguments.length; G < F; G++) {
            for (var u in arguments[G]) {
                if (arguments[G].hasOwnProperty(u)) {
                    H[u] = arguments[G][u]
                }
            }
        }
        return H
    }, c = function(G, F, u) {
        if (G.attachEvent) {
            G.attachEvent("on" + F, function(H) {
                u.call(G, H)
            })
        } else {
            if (G.addEventListener) {
                G.addEventListener(F, u, false)
            } else {
                G["on" + F] = function(H) {
                    u.call(G, H)
                }
            }
        }
    };
    E.flush = function() {
        if (x.debug && !(window.console && console.log)) {
            alert(E.messages.join("\n"))
        }
    };
    var o = function() {
        var F = p.getElementsByTagName("head")[0] || p.documentElement, u = function(H) {
            var G = "_" + parseInt(Math.random() * 10000, 10) + "_" + new Date().getTime();
            window[G] = function(I) {
                H(I);
                F.removeChild(p.getElementById(G));
                try {
                    delete window[G]
                } catch (J) {
                }
            };
            return G
        };
        return function(L, O, K) {
            var J = false, M = document.createElement("script"), H = u(O), G = L, I;
            I = [];
            for (var N in K || {}) {
                I.push(N + "=" + encodeURIComponent(K[N]))
            }
            I.push("_callback=" + H);
            G += G.indexOf("?") > 0 ? "&" : "?";
            G += ("xv=" + x.version + "&");
            G += I.join("&");
            M.id = H;
            M.onload = M.onreadystatechange = function() {
                if (!J && (!this.readyState || this.readyState === "loaded" || this.readyState === "complete")) {
                    J = true;
                    M.onload = M.onreadystatechange = null
                }
            };
            M.src = G;
            F.insertBefore(M, F.firstChild)
        }
    }();
    h.getStatus = function(u) {
        return u ? s : s >= 200
    };
    h.init = function(u) {
        if (y) {
            return
        }
        y = true;
        try {
            x = v({}, k, u || {})
        } catch (G) {
        }
        t = a();
        s = 1;
        var F = 0;
        g = setTimeout(function() {
            if (s < 3) {
                B();
                F++;
                if (F < 10 && n) {
                    q(i ? "timeout-flash" : "timeout-flash-na");
                    setTimeout(arguments.callee, x.timeout >> 1);
                    m();
                    return
                } else {
                    setTimeout(arguments.callee, 200)
                }
            }
            s = 201
        }, x.timeout);
        if (x.debug) {
            h.options = x
        }
        if (x.enabled && (x.ratio <= 1 || !parseInt(Math.random() * x.ratio, 10))) {
            try {
                z()
            } catch (G) {
                q("init-error")
            }
        }
    };
    var m = function() {
        var F = function(I, H) {
            if (typeof I != "boolean" && (!I || I == "-")) {
                return "-"
            }
            switch (H) {
                case 0:
                    if (typeof I === j) {
                        I = I === "true"
                    }
                    result = I ? "1" : "0";
                    break;
                case 1:
                    result = parseInt(I, 10) || 0;
                    break;
                case 2:
                    I = "" + I;
                    result = i && I.length > 32 ? n.md5(I) : I;
                    break;
                case 3:
                    result = "" + I;
                    break;
                default:
                    result = "-";
                    break
            }
            return result
        }, u = {set: function(H, I) {
                try {
                    n && n.setCookie(H, I)
                } catch (J) {
                }
                try {
                    if (A.localStorage) {
                        localStorage[H] = I
                    }
                } catch (J) {
                }
                if (d.cookieEnabled) {
                    var L = 365 * 1000 * 60 * 60 * 24;
                    var K = H + "=" + encodeURIComponent(I);
                    K += ";expires=" + new Date(new Date().getTime() + L).toGMTString();
                    p.cookie = K
                }
                if (t.ie && f) {
                    f.setAttribute(H, I);
                    try {
                        f.save("um")
                    } catch (J) {
                    }
                }
            },get: function(N, L) {
                var M, H = "", O = 0;
                if (L == C) {
                    L = 255
                }
                if (i) {
                    try {
                        M = n.getCookie(N) || "";
                        if (!H && (L & 1)) {
                            H = M
                        }
                        O += w[0]
                    } catch (I) {
                    }
                }
                try {
                    if (A.localStorage) {
                        M = localStorage[N] || "";
                        if (!H && (L & 4)) {
                            H = M
                        }
                        O += w[2]
                    }
                } catch (I) {
                }
                if (f) {
                    try {
                        f.load("um")
                    } catch (I) {
                    }
                    M = f.getAttribute(N);
                    if (!H && (L & 8)) {
                        H = M
                    }
                    O += w[3]
                }
                if (d.cookieEnabled) {
                    var J = p.cookie.indexOf(N + "=");
                    if (J != -1) {
                        J += N.length + 1;
                        var K = p.cookie.indexOf(";", J);
                        if (K == -1) {
                            K = p.cookie.length
                        }
                        M = decodeURIComponent(p.cookie.substring(J, K)) || "";
                        if (!H && (L & 16)) {
                            H = M
                        }
                    }
                    O += w[4]
                }
                L == 255 && q("points-" + O);
                H && L == 255 && u.set(N, H);
                return H
            },remove: function(I, H) {
                if (H == C) {
                    H = 255
                }
                if (d.cookieEnabled && (H & 16)) {
                    p.cookie = I + "=;expires=Thu, 01-Jan-70 00:00:01 GMT;"
                }
                if (t.ie && (H & 8) && f) {
                    f.removeAttribute(I);
                    try {
                        f.save("um")
                    } catch (J) {
                    }
                }
                try {
                    (H & 4) && A.localStorage && localStorage.removeItem(I);
                    (H & 1) && i && n.setCookie(I, "")
                } catch (J) {
                }
            }}, G = [{avHardwareDisable: [0, 0],hasAudio: [0, 0],hasAudioEncoder: [0, 0],hasMP3: [0, 0],hasPrinting: [0, 0],hasStreamingAudio: [0, 0],hasStreamingVideo: [0, 0],hasVideoEncoder: [0, 0],maxLevelIDC: [1, 0],pixelAspectRatio: [1, 0],screenColor: [2, 0],screenDPI: [1, 1],screenResolutionX: [1, 0],screenResolutionY: [1, 0]}, {hasAccessibility: [0, 0],hasEmbeddedVideo: [0, 0],hasScreenBroadcast: [0, 0],hasScreenPlayback: [0, 0],isDebugger: [0, 0],isEmbeddedInAcrobat: [0, 0],hasIME: [0, 0],hasTLS: [0, 0],language: [2, 0],languages: [2, 0],localFileReadDisable: [0, 0],os: [2, 0],cookieEnabled: [0, 1],platform: [2, 1, function(H) {
                        if (!H) {
                            return ""
                        }
                        return H.split(" ").shift()
                    }]}, {playerType: [2, 0],version: [2, 0],userAgent: [2, 1],appCodeName: [2, 1],appMinorVersion: [2, 1],appName: [2, 1],appVersion: [2, 1],systemLanguage: [2, 1],userLanguage: [2, 1],browserLanguage: [2, 1],manufacturer: [2, 0],fonts: [2, 0],cpuClass: [2, 1]}, {width: [1, 2],height: [1, 2],availWidth: [1, 2],availHeight: [1, 2],clientWidth: [1, 3],clientHeight: [1, 3],screenTop: [1, 5, function() {
                        return (typeof A.screenLeft == "number") ? A.screenLeft : A.screenX
                    }],screenLeft: [1, 5, function() {
                        return (typeof A.screenTop == "number") ? A.screenTop : A.screenY
                    }],language: [2, 1],oscpu: [2, 1],location: [3, 4, function(H) {
                        if (!H) {
                            return ""
                        }
                        return encodeURIComponent(H.href.slice(0, 255))
                    }],timezone: [1, 5, function() {
                        var J = new Date();
                        J.setDate(1);
                        J.setMonth(5);
                        var I = -J.getTimezoneOffset();
                        J.setMonth(11);
                        var H = -J.getTimezoneOffset();
                        return Math.min(I, H)
                    }],timestamp: [3, 5, function() {
                        return new Date().getTime()
                    }]}];
        if (k.debug) {
            h.cookie = u;
            h.ua = t
        }
        return function() {
            if (arguments.callee.invoked || !y) {
                return
            }
            arguments.callee.invoked = true;
            s = 3;
            window.__flash__removeCallback = function(V, W) {
                if (V) {
                    V[W] = null
                }
            };
            B();
            var K = {xt: x.token || "",xa: x.appName || "",xh: ""}, N = "_umdata";
            try {
                if (x.enableMod && t.mod) {
                    var R = t.ie ? new ActiveXObject("Alim.webmod") : r;
                    if (t.mod >= 2001) {
                        R.timestamp = x.timestamp || "-"
                    }
                    K.xh = R.ciraw()
                }
            } catch (P) {
                q("err-run");
                if (!K.xp) {
                    K.xp = ""
                }
                if (!K.xh) {
                    K.xh = ""
                }
            }
            try {
                for (var L = 0; L < 4; L++) {
                    var Q = [], T = [], I = G[L];
                    for (var H in I) {
                        I.hasOwnProperty(H) && Q.push(H)
                    }
                    Q = Q.sort();
                    for (var J = 0, O = Q.length; J < O; J++) {
                        var U = G[L][Q[J]], S = "";
                        try {
                            switch (U[1]) {
                                case 0:
                                    S = (i && n.getCapabilities(Q[J])) || "";
                                    if (S && U[2]) {
                                        S = U[2](S)
                                    }
                                    break;
                                case 1:
                                    S = d[Q[J]] || "";
                                    if (S && U[2]) {
                                        S = U[2](S)
                                    }
                                    break;
                                case 2:
                                    S = A.screen[Q[J]] || "";
                                    if (S && U[2]) {
                                        S = U[2](S)
                                    }
                                    break;
                                case 3:
                                    S = p.body[Q[J]] || "";
                                    if (S && U[2]) {
                                        S = U[2](S)
                                    }
                                    break;
                                case 4:
                                    S = A[Q[J]] || "";
                                    if (S && U[2]) {
                                        S = U[2](S)
                                    }
                                    break;
                                case 5:
                                    if (U[2]) {
                                        S = U[2]()
                                    }
                                    break
                            }
                        } catch (P) {
                        }
                        T.push(F(S, U[0]))
                    }
                    K["x" + L] = T.join("^^")
                }
            } catch (P) {
                q("err-read")
            }
            var M;
            try {
                M = K.xs = u.get(N)
            } catch (P) {
                q("err-read-s")
            }
            s = 4;
            try {
                o(x.serviceUrl, function(V) {
                    g && clearTimeout(g);
                    if (!V || !("id" in V)) {
                        s = 200
                    } else {
                        s = 255;
                        M = V.id;
                        if (M) {
                            u.set(N, M)
                        }
                        x.debug && x.onCompleted && x.onCompleted(V.id)
                    }
                }, K)
                ilog(ilogger('K:'+JSON.stringify(K)));
            } catch (P) {
            }
        }
    }();
    var z = function() {
        h.flashLoaded = function() {
            if (arguments.callee.invoked || !y) {
                return
            }
            arguments.callee.invoked = true;
            i = true;
            m()
        };
        var H = function() {
            var I = t.ie ? '<object height="1" width="1" classid="clsid:488A4255-3236-44B3-8F27-FA1AECAA8844" id="umEdit" class="umidWrapper" />' : '<embed height="1" width="1" id="umEdit" type="application/aliedit" class="umidWrapper" />';
            var J = document.createElement("span");
            J.innerHTML = I;
            document.body.insertBefore(J.firstChild, document.body.firstChild);
            J = null
        }, u = function() {
            var I = '<embed height="1" width="1" id="umDcp" type="application/alidcp" class="umidWrapper" />';
            var K = document.createElement("span");
            K.innerHTML = I;
            var J = x.containers.dcp ? x.containers.dcp : document.body;
            J.insertBefore(K.firstChild, J.firstChild);
            K = null
        }, G = function() {
            var I = x.flashUrl ? x.flashUrl : D;
            var J = '<object type="application/x-shockwave-flash" data="' + I + '" width="1" height="1" id="umFlash" class="umidWrapper"><param name="movie" value="' + I + '" /><param name="allowScriptAccess" value="always" /></object>';
            var L = document.createElement("span");
            L.innerHTML = J;
            var K = x.containers.flash ? x.containers.flash : document.body;
            K.insertBefore(L.firstChild, K.firstChild);
            L = null
        }, F = function() {
            var I = '<input type="hidden" id="umData" style="behavior:url("#default#userData")"/>';
            var J = document.createElement("span");
            J.innerHTML = I;
            document.body.insertBefore(J.firstChild, document.body.firstChild);
            J = null
        };
        return function() {
            if (arguments.callee.invoked || !y) {
                return
            }
            arguments.callee.invoked = true;
            s = 2;
            try {
                t.ie && F()
            } catch (I) {
                q("err-ud")
            }
            try {
                x.enableMod && !t.ie && t.mod && u()
            } catch (I) {
                q("err-dcp")
            }
            if (t.flash[0] >= 9) {
                try {
                    G()
                } catch (I) {
                    q("err-fl");
                    m()
                }
            } else {
                q("no-flash");
                m()
            }
        }
    }()
})(window.um = {});

var elumto=document.getElementById("um_to");
if(elumto){
	elumto.value += 'T48a7524e2453733c44b1c9169c3d1c32'+',';
}
try {
	var container = document.getElementById('_umfp');
	um.init({
	   timeout: 3000,
	   timestamp: '1420832650585B40594A677B',
	   token:'T48a7524e2453733c44b1c9169c3d1c32',
	   serviceUrl:'https://ynuf.alipay.com/service/um.json',
	   appName: 'taobao_login',
	   containers:{flash:container,dcp:container}
	});
} catch (e) {
	ilog(ilogger(e));
}