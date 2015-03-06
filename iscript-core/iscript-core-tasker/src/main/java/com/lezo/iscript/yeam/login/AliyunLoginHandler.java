package com.lezo.iscript.yeam.login;

import java.io.IOException;
import java.util.List;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.FrameWindow;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlInput;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

/**
 * @author lezo
 * @email lcstore@126.com
 * @since 2015年3月6日
 */
public class AliyunLoginHandler {

	public static void main(String[] args) throws Exception, IOException {
		final WebClient webClient = new WebClient(BrowserVersion.FIREFOX_24);
		webClient.getOptions().setCssEnabled(false);
		webClient.getOptions().setAppletEnabled(false);
		// Get the first page
		HtmlPage loginPage = webClient.getPage("https://account.aliyun.com/login/login.htm?oauth_callback=http%3A%2F%2Fwww.aliyun.com%2F%3Fspm%3D5176.3047821.1.1.tJ1w28");
//		final HtmlPage loginPage = webClient.getPage("https://account.aliyun.com/login/login.htm?oauth_callback=http://bbs.aliyun.com/u.php");
		List<FrameWindow> iframeEleList = loginPage.getFrames();
		FrameWindow iframeEle = iframeEleList.get(0);

		HtmlPage loginFramePage = (HtmlPage) iframeEle.getEnclosedPage();
		final HtmlForm form = loginFramePage.getFormByName("login-form");

		HtmlInput loginInput = form.getInputByName("loginId");
		loginInput.focus();
		loginInput.setValueAttribute("lcstore@126.com");
		loginInput.blur();
		HtmlInput pwdInput = form.getInputByName("password");
		pwdInput.focus();
		pwdInput.setValueAttribute("net@9Lezo");
		pwdInput.blur();
//		List<HtmlInput> checkboxList = loginPage.getElementsByAttribute("input", "type", "checkbox");
//		HtmlElement loginModule = loginPage.getHtmlElementById("login-module");
//		HtmlInput checkboxEle = loginModule.getOneHtmlElementByAttribute("input", "type", "checkbox");
//		checkboxEle.setAttribute("checked", "checked");
//		checkboxEle.setValueAttribute("on");
		final HtmlPage homePage = form.getInputByName("submit-btn").click();
		System.err.println(homePage.asText());
		webClient.closeAllWindows();
	}
}
