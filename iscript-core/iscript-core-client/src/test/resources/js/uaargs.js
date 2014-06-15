window.location.href = 'http://www.etao.com/?tbpm=20140614';
document.referer = 'http://www.etao.com/?tbpm=20140614';
ilog('start.........');

//var json_ua = null,form_tk = (document.getElementsByName('rds_form_token')[0] && document.getElementsByName('rds_form_token')[0].value) || '';
//var UA_Opt = new Object;
//UA_Opt.ExTarget = ['password','password_input'];
//UA_Opt.FormId = "login";
//UA_Opt.GetAttrs = ['href', 'src'];
//UA_Opt.Token = form_tk;
//UA_Opt.LogVal = "json_ua";
//UA_Opt.MaxMCLog = 100;
//UA_Opt.MaxKSLog = 100;
//UA_Opt.MaxMPLog = 100;
//UA_Opt.MaxFocusLog = 100;
//UA_Opt.SendMethod = 9;
//UA_Opt.Flag = 32766;

var ua = "";
var UA_Opt = new Object();
UA_Opt.LogVal = "ua";
UA_Opt.MaxMCLog=3;
UA_Opt.MaxMPLog=3;
UA_Opt.MaxKSLog=3;
UA_Opt.Token=new Date().getTime()+":"+Math.random();
UA_Opt.SendMethod=8;
UA_Opt.Flag=14222;
