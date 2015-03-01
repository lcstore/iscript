var w = window, zj = w.ZhangYueJS;
function judgeEnable(a) {
    var b = {Action: "setisEnableThreeScreen",isenable: a};
    var c = JSON.stringify(b);
    if (zj && zj.do_command) {
        zj.do_command(c);
    } else {
        console.log(c);
        console.table(c);
    }
}
function focusFn(a, b) {
    a.className += " " + b;
}
function blurFn(b, d) {
    var a = new RegExp(d);
    b.className = b.className.replace(a, "");
}
$.fn.touchFn = function(a) {
    var c = {obj: $(this),v3_class: "v3_cur"};
    var b = $.extend({}, c, a);
    return $(this).each(function() {
        $(this).bind("touchstart", function() {
            setTimeout(focusFn(this, b.v3_class), 100);
        });
        $(this).bind("touchmove", function() {
            blurFn(this, b.v3_class);
        });
        $(this).bind("touchend", function() {
            blurFn(this, b.v3_class);
        });
    });
};
$(function() {
    $(".v3_ybtn").touchFn();
    $(".v3_wbtn").touchFn();
    $(".v3_bk_list li").touchFn();
    $(".v3_user_con div").touchFn();
    $(".v3_classic li").touchFn();
    $(".v3_tab3 li").touchFn();
    $(".v3_tag_other li a").touchFn();
    if ($(".v3_ad_txt").get(0) === undefined) {
        if ($(".v3_wrap0").get(0) !== undefined) {
            $(".v3_wrap0").get(0).className += " bt0";
        }
    }
});
function visitBook(a) {
    window.location.href = detailUrl + a;
}
function replaceElement(eId, html) {
    var e = document.getElementById(eId), div = document.createElement("div");
    e.innerHTML = "";
    div.id = eId;
    div.innerHTML = html;
    var elements = div.childNodes;
    var parent = e.parentNode, ns = e.nextSibling;
    parent.removeChild(e);
    for (var i = 0; i < elements.length; i++) {
        if (elements[i].nodeType != 1) {
            continue;
        }
        if (elements[i].nodeName == "SCRIPT" && (elements[i].type == "" || elements[i].type == "text/javascript")) {
            eval.call(window, elements[i].text);
        } else {
            if (ns != null) {
                parent.insertBefore(elements[i], ns);
            } else {
                parent.appendChild(elements[i]);
            }
        }
    }
}



var zy = {}, w = window, zj = w.ZhangYueJS, j = 0;
zy.cm = function(a) {
    if (typeof a == "object") {
        a = JSON.stringify(a);
    }
    if (zj && zj.do_command) {
        zj.do_command(a);
    } else {
        zy.debug(a);
    }
};
zy.alert = function(a) {
    if (zj && zj.do_alert) {
        zj.do_alert(a);
    } else {
        zy.debug(a);
    }
};
zy.expList = function(a) {
    zy.cm(a);
};
zy.debug = function(b, a) {
    if (a) {
        alert(b);
    }
    console.log(b);
};
zy.later = function(d, b, c, a) {
    var e = setInterval(function() {
        d--;
        j++;
        if (d <= 0) {
            clearInterval(e);
            if (b) {
                zy.goUrl(b);
            }
            return;
        } else {
            alR = 1;
            if (a) {
                alR = 0;
                if (j > a) {
                    alR = 1;
                } else {
                    console.log(j);
                }
            }
            if (alR == 1) {
                if (!c) {
                    c = "正在跳转中...，请耐心等待！";
                }
                zy.alert(c);
            }
        }
    }, 1000);
};
zy.removeVariableFromURL = function(d, b) {
    var a = String(d), c = new RegExp("\\?" + b + "=[^&]*&?", "gi");
    a = a.replace(c, "?");
    c = new RegExp("\\&" + b + "=[^&]*&?", "gi");
    a = a.replace(c, "&");
    c = new RegExp("\\&" + b + "=[^&]*", "gi");
    a = a.replace(c, "");
    a = a.replace(/(\?|&)$/, "");
    c = null;
    return a;
};
zy.strpos = function(b, c, d) {
    var a = (b + "").indexOf(c, (d || 0));
    return a === -1 ? false : a;
};
zy.sign = function(c) {
    c = zy.removeVariableFromURL(c, "zysid");
    c = zy.removeVariableFromURL(c, "zysign");
    var a = w.location.href;
    var b = a.split("?");
    if (zy.strpos(c, "http://") === 0) {
    } else {
        if (zy.strpos(c, "/") === 0) {
            var d = b[0].split("/");
            c = "http://" + d[2] + c;
        } else {
            if (zy.strpos(c, "?") === 0) {
                c = b[0] + c;
            } else {
                var d = b[0].split("/");
                d.pop();
                c = d.join("/") + "/" + c;
            }
        }
    }
    if (zj && zj.do_sign) {
        c = zj.do_sign(c);
    }
    return c;
};
zy.goUrl = function(a, b) {
    if (typeof b == "undefined" || b == "" || b == null) {
        a = zy.sign(a);
    }
    return w.location.href = a;
};
zy.noAutoOrder = function() {
    if (zj && zj.cancelAllAutoOrder) {
        return zj.cancelAllAutoOrder();
    }
};
