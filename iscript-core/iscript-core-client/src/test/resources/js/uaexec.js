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
var phEle = document['createElement']("span");
phEle['class'] = "ph-label";
staticFormEle.appendChild(phEle);
staticFormEle.appendChild(userEle);
document.body.appendChild(staticFormEle);
// UA_Opt['reload'] = hrt;
window.UA_Opt.reload();

//mousemove
staticFormEle.dispatchEvent(createMousemoveEvent());
//fouse
userEle.dispatchEvent(createFocusEvent());
//mousedown
phEle.dispatchEvent(createMousedownEvent());
//keydown
var uname = 'lcstore99';
 
var charCode = uname.charCodeAt(0);
userEle.dispatchEvent(createKeydownEvent(charCode));
var newUa = eval(UA_Opt.LogVal);
ilog('@@@@[' + UA_Opt.LogVal + ']=' + newUa);
ilog('@@@@[' + UA_Opt.LogVal + ']=' + document['getElementById']('UA_InputId')['value']);


/**
 * target=form#J_StaticForm;
 */
function createMousemoveEvent(){
	var cancelBubble=false;
	var cancelable=true;
	var event = document.createEvent('HTMLEvents');
	event.initEvent("mousemove", cancelBubble, cancelable);
	event.bubbles=true;
	event.button=0;
	event.charCode=0;
	event.clientX=17;
	event.clientY=121;
	event.keyCode=0;
	event.layerX=17;
	event.layerY=121;
	event.offsetX=16;
	event.offsetY=69;
	event.pageX=17;
	event.pageY=121;
	event.screenX=806;
	event.screenY=225;
	return event;
}
/**
 * target=span.ph-label;
 */
function createMousedownEvent(){
	var cancelBubble=false;
	var cancelable=true;
	var event = document.createEvent('HTMLEvents');
	event.initEvent("mousedown", cancelBubble, cancelable);
	event.bubbles=true;
	event.button=0;
	event.charCode=0;
	event.clientX=84;
	event.clientY=84;
	event.clipboardData=undefined;
	event.ctrlKey=false;
	event.dataTransfer=null;
	event.defaultPrevented=false;
	event.detail=1;
	event.eventPhase=3;
	event.keyCode=0;
	event.layerX=38;
	event.layerY=11;
	event.metaKey=false;
	event.offsetX=38;
	event.offsetY=11;
	event.pageX=84;
	event.pageY=84;
	event.screenX=873;
	event.screenY=288;
	event.shiftKey=false;
	event.webkitMovementX=0;
	event.webkitMovementY=0;
	event.which=1;
	event.x=84;
	event.y=84;
	return event;
}
/**
 * input#TPL_username_1.login-text.J_UserName;
 */
function createKeydownEvent(charCode){
	if(!charCode){
		return;
	}
	var keyCode = charCode;
	if (keyCode < 48 || keyCode > 57) {
		keyCode = keyCode - 32;
	}
	var cancelBubble=false;
	var cancelable=true;
	var event = document.createEvent('HTMLEvents');
	event.initEvent("keydown", cancelBubble, cancelable);
	event.altGraphKey=false;
	event.altKey=false;
	event.bubbles=true;
	event.charCode=charCode;
	event.ctrlKey=false;
	event.defaultPrevented=false;
	event.detail=0;
	event.eventPhase=3;
	event.keyCode=keyCode;
	event.which=keyCode;
	event.keyIdentifier="U+0031";
	event.keyLocation=0;
	event.layerX=0;
	event.layerY=0;
	event.location=0;
	event.metaKey=false;
	event.pageX=0;
	event.pageY=0;
	event.repeat=false;
	event.returnValue=true;
	event.shiftKey=false;
	return event;
}
/**
 * input#TPL_username_1.login-text.J_UserName;
 */
function createFocusEvent(){
	var cancelBubble=false;
	var cancelable=true;
	var event = document.createEvent('HTMLEvents');
	event.initEvent("focus", cancelBubble, cancelable);
	bubbles=false;
	event.cancelBubble=false;
	event.cancelable=false;
	event.charCode=0;
	event.clipboardData=undefined;
	event.defaultPrevented=false;
	event.detail=0;
	event.eventPhase=1;
	event.keyCode=0;
	event.layerX=0;
	event.layerY=0;
	event.pageX=0;
	event.pageY=0;
	event.relatedTarget=null;
	event.returnValue=true;
	event.which=0;
	return event;
}
