package com.lezo.iscript.yeam.ua;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.io.IOUtils;

public class LoadUaFactory extends UaFactory {

	protected String loadUajs() {
		StringBuilder sb = new StringBuilder();
		InputStream in = LoadUaFactory.class.getClassLoader().getResourceAsStream("js/deua_master.js");
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
		return sb.toString();
	}

}
