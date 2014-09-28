function() {
    var b = this,
    c = b.get("uajs"),
    d = a.Defer();
    return window.ua && window.UA_Opt ? (window.UA_Opt.Token = (new Date).getTime() + ":" + Math.random(), window.UA_Opt.reload(), d.resolve()) : (window.ua = "", window.UA_Opt = {
        LogVal: "ua",
        MaxMCLog: 5,
        MaxMPLog: 5,
        MaxKSLog: 5,
        Token: (new Date).getTime() + ":" + Math.random(),
        SendMethod: 8,
        Flag: 14222
    },
    a.getScript(c,
    function() {
        try {
            window.UA_Opt.reload(),
            d.resolve()
        } catch(a) {}
    },
    "gbk")),
    d.promise
}