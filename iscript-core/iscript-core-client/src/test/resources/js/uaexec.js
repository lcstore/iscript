//UA_InputId
var staticFormEle = document['createElement']("form");
staticFormEle['action'] = 'https://login.taobao.com/member/login.jhtml';
staticFormEle['method'] = 'post';
staticFormEle['id'] = 'J_StaticForm';
var userEle = document['createElement']("input");
userEle['type'] = 'text';
userEle['name'] = 'TPL_username';
userEle['id'] = 'TPL_username_1';
userEle['class'] = 'login-text J_UserName';
userEle['value'] = '';
userEle['maxlength'] = '32';
userEle['tabindex'] = '1';

staticFormEle.appendChild(userEle);
document.body.appendChild(staticFormEle);
// UA_Opt['reload'] = hrt;
window.UA_Opt.reload();
 var event = document.createEvent('HTMLEvents');
 event.initEvent("mousedown", true, true);
 document.dispatchEvent(event);
var newUa = eval(UA_Opt.LogVal);
ilog('@@@@[' + UA_Opt.LogVal + ']=' + newUa);
