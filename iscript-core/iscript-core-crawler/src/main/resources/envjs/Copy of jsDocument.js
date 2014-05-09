document.location = location;
var ga = document.createElement('script');
ga.type = 'text/javascript';
ga.async = true;
ga.src = ('https:' == document.location.protocol ? 'https://ssl' : 'http://www')
		+ '.google-analytics.com/ga.js';
var s = document.getElementsByTagName('script')[0];
s.parentNode.insertBefore(ga, s);
var ele = document.getElementsByTagName('script')[0];
java.lang.System.out.println('argsList:' + ele.type);
//SimpleClass.age = 100;
//java.lang.System.out.println('SimpleClass.age:' + SimpleClass.age);