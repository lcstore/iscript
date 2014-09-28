package com.lezo.iscript.yeam.ua;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.io.IOUtils;

public class LogUaFactory extends UaFactory {

	protected String loadUajs() {
		StringBuilder sb = new StringBuilder();
		InputStream in = LogUaFactory.class.getClassLoader().getResourceAsStream("js/ua_action_log_master.js");
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
		StringBuilder sb = new StringBuilder();
		sb.append("window.location.href = 'http://www.etao.com/?tbpm=20140614';");
		sb.append("document.referer = 'http://www.etao.com/?tbpm=20140614';");
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
		return sb.toString();
	}

}
