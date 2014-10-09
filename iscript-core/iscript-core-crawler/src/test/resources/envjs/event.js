document.addEventListener('onload', function(event) {
	ilog("document.call:" + event.eventType);
}, false);
var divElement = document.createElement("div");
divElement.id = "divId";
divElement.addEventListener('onload', function(event) {
	ilog("divElement.call:" + event.eventType);
}, false);
document.getElementsByTagName('body')[0].appendChild(divElement);
var event = document.createEvent('HTMLEvents');
event.initEvent("onload", true, true);
event.eventType = 'message,send event.';

//document.dispatchEvent(event);
divElement.dispatchEvent(event);
var idEle = document.getElementById('divId');
idEle.dispatchEvent(event);