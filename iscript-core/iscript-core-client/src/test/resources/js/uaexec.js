//UA_Opt['reload'] = hrt;
//window.UA_Opt.reload();
var event = document.createEvent('HTMLEvents');
event.initEvent("DOMContentLoaded", true, true);
document.dispatchEvent(event);
var newUa = eval(UA_Opt.LogVal);
ilog('@@@@['+UA_Opt.LogVal+']='+newUa);