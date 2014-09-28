package com.yihaodian.pis.config.session;

public class VerifyUserConfig {
	private String tbUserName;
	private String tbPassword;
	private String emailAddr;
	private String emailPwd;

	public String getTbUserName() {
		return tbUserName;
	}

	public void setTbUserName(String tbUserName) {
		this.tbUserName = tbUserName;
	}

	public String getTbPassword() {
		return tbPassword;
	}

	public void setTbPassword(String tbPassword) {
		this.tbPassword = tbPassword;
	}

	public String getEmailAddr() {
		return emailAddr;
	}

	public void setEmailAddr(String emailAddr) {
		this.emailAddr = emailAddr;
	}

	public String getEmailPwd() {
		return emailPwd;
	}

	public void setEmailPwd(String emailPwd) {
		this.emailPwd = emailPwd;
	}
}
