package com.lezo.iscript.yeam.ua;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.io.IOUtils;

public class LoginUaFactory extends UaFactory {

	@Override
	protected String loadUajs() {
		StringBuilder sb = new StringBuilder();
		InputStream in = LoginUaFactory.class.getClassLoader().getResourceAsStream("js/ua_authcenter_login_master.js");
		BufferedReader bReader = null;
		try {
			bReader = new BufferedReader(new InputStreamReader(in, "UTF-8"));
			while (bReader.ready()) {
				String line = bReader.readLine();
				if (line == null) {
					break;
				}
				sb.append(line);
				sb.append("\n");
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(bReader);
		}
		return sb.toString();
	}

	@Override
	public String getUaOpt() {
		// form_tk,json_ua define before this
		StringBuilder sb = new StringBuilder();
		sb.append("window.location.href = 'http://www.etao.com/?tbpm=20140614';");
		sb.append("document.referer = 'http://www.etao.com/?tbpm=20140614';");
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
		return sb.toString();
	}

}
