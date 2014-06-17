package com.lezo.iscript.yeam.ua;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.commons.io.IOUtils;

public class LoginUaFactory extends UaFactory {

	@Override
	protected void loadUajs(StringBuilder sb) {
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
			}
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(bReader);
		}
	}

}
