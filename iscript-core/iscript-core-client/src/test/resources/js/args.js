function emptyArgs() {
	ilog(ilogger('emptyArgs.arguments:' + JSON.stringify(arguments)));
	ilog('emptyArgs.arguments:' + JSON.stringify(arguments));
	ilog('emptyArgs.length:' + JSON.stringify(arguments.length));
}
function oneArgs(a) {
	ilog('arguments.callee:' + JSON.stringify(arguments.callee));
	ilog('oneArgs.length:' + JSON.stringify(arguments.length));
}
function sum(a, b) {
	var kont  = new  Continuation();
	var iSum = a + b;
	ilog('a+b:' + iSum);
	ilog('con:' + kont);
	ilog('sum.arguments:' + JSON.stringify(arguments));
	ilog('sum.length:' + JSON.stringify(arguments.length));
}
emptyArgs();
oneArgs('one..');
sum(5, 10);