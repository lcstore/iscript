package com.lezo.iscript.yeam.ua;

import org.junit.Test;

public class UABuilder {
	private static final UaFactory UA_FACTORY = new UaFactory();
	private static final LoginUaFactory LOGIN_UA_FACTORY = new LoginUaFactory();

	public static StringBuilder newLogUaOpt() {
		StringBuilder sb = new StringBuilder();
		sb.append("if(typeof(UA_Opt) == 'undefined'){ UA_Opt = new Object()};");
		sb.append("UA_Opt.ExTarget = [ 'TPL_password_1','J_Pwd1','J_PwdV'];");
		sb.append("UA_Opt.ResHost = 'acjstb.aliyun.com';");
		sb.append("UA_Opt.FormId = 'J_StaticForm';");
		sb.append("UA_Opt.LogVal = 'log'; ");
		sb.append("UA_Opt.Token = new Date().getTime()+':'+Math.random();  ");
		sb.append("UA_Opt.ImgUrl = '';");
		sb.append("UA_Opt.GetAttrs = ['href', 'src']; ");
		sb.append("UA_Opt.MaxMCLog = 150;  ");
		sb.append("UA_Opt.MaxKSLog = 150;  ");
		sb.append("UA_Opt.MaxMPLog = 150;  ");
		sb.append("UA_Opt.MPInterval = 150;  ");
		sb.append("UA_Opt.SendInterval = 10;  ");
		sb.append("UA_Opt.SendMethod = 1;");
		sb.append("UA_Opt.Flag = 131071;");
		return sb;
	}

	public static StringBuilder newLoadUaOpt() {
		StringBuilder sb = new StringBuilder();
		sb.append("var ua = '';");
		sb.append("if(typeof(UA_Opt) == 'undefined'){ UA_Opt = new Object()};");
		sb.append("UA_Opt.LogVal = 'ua';");
		sb.append("UA_Opt.SendMethod = 8;");
		sb.append("UA_Opt.MaxMCLog=3;");
		sb.append("UA_Opt.MaxMPLog=3;");
		sb.append("UA_Opt.MaxKSLog=3;");
		sb.append("UA_Opt.Token=new Date().getTime()+':'+Math.random();");
		sb.append("UA_Opt.SendMethod=8;");
		sb.append("UA_Opt.Flag=14222;");
		return sb;
	}

	public static StringBuilder newLoginUaOpt() {
		// form_tk,json_ua define before this
		StringBuilder sb = new StringBuilder();
		sb.append("var json_ua = null;");
		sb.append("var form_tk = (document.getElementsByName('rds_form_token')[0] && document.getElementsByName('rds_form_token')[0].value) || '';");
		sb.append("if(typeof(UA_Opt) == 'undefined'){ UA_Opt = new Object()};");
		sb.append("UA_Opt.ExTarget = ['password','password_input'];");
		sb.append("UA_Opt.FormId = 'login';");
		sb.append("UA_Opt.GetAttrs = ['href', 'src'];");
		sb.append("UA_Opt.Token = form_tk;");
		sb.append("UA_Opt.LogVal = 'json_ua';");
		sb.append("UA_Opt.MaxMCLog = 100;");
		sb.append("UA_Opt.MaxKSLog = 100;");
		sb.append("UA_Opt.MaxMPLog = 100;");
		sb.append("UA_Opt.MaxFocusLog = 100;");
		sb.append("UA_Opt.SendMethod = 9;");
		sb.append("UA_Opt.Flag = 32766;");
		return sb;
	}

	public static String newUa(String opt) {
		return UA_FACTORY.createUa(opt);
	}

	public static String newLoginUa(String opt) {
		return LOGIN_UA_FACTORY.createUa(opt);
	}

	@Test
	public void test() throws Exception {
//		System.out.println("#1:" + UABuilder.newUa(newLoadUaOpt().toString()));
		System.out.println("#2:" + UABuilder.newLoginUa(newLoginUaOpt().toString()));
	}
}
