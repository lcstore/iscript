package com.lezo.iscript.yeam.crawler;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.lezo.iscript.spring.context.SpringBeanUtils;

public class WccSimilarSearchTimerTest {

	@Test
	public void testDoSimilarSearch() throws Exception {
		String[] configs = new String[] { "classpath:spring-config-ds.xml","classpath:spring/spring-bean-resulter.xml" };
		ApplicationContext cx = new ClassPathXmlApplicationContext(configs);
		WccSimilarSearchTimer timer = SpringBeanUtils.getBean(WccSimilarSearchTimer.class);
//		timer.run();
		Thread.currentThread().join();
	}

	@Test
	public void testUrl() {
		String url = "http://e.wochacha.com/redirect.html?url=http%3A%2F%2Fitem.yixun.com%2Fitem-652046.html&version=3.2.1&vendor=chrome";
		url = "http://e.wochacha.com/redirect.html?url=http%3A%2F%2Fclick.union.jd.com%2FJdClick%2F%3FunionId%3D13228%26t%3D1%26to%3Dhttp%3A%2F%2Fitem.jd.com%2F855739.html&version=3.2.1&vendor=chrome";
		url = "http://e.wochacha.com/redirect.html?url=http%3A%2F%2Fcps.gome.com.cn%2Fhome%2FJoinUnion%3Fsid%3D1496%26feedback%3D%26to%3Dhttp%3A%2F%2Fwww.gome.com.cn%2Fproduct%2FA0004735064.html&version=3.2.1&vendor=chrome";
		url = "http://e.wochacha.com/redirect.html?url=http%3A%2F%2Fwww.amazon.cn%2Fdp%2FB00CAI7VUW%2F%3Ftag%3Dwcc-23&version=3.2.1&vendor=chrome";
		url = "http://e.wochacha.com/redirect.html?url=http%3A%2F%2Fwww.newegg.cn%2FProduct%2Fa28-032-5vf.htm&version=3.2.1&vendor=chrome";
		url = "http://e.wochacha.com/redirect.html?url=http%3A%2F%2Fitem.yhd.com%2Fitem%2F10880806%3Ftracker_u%3D7550426%26union_ref%3D7&version=3.2.1&vendor=chrome";
		url = "http://e.wochacha.com/redirect.html?url=http%3A%2F%2Fwww.winxuan.com%2Fproduct%2F1200835879&version=3.2.1&vendor=chrome";
		url = "http://e.wochacha.com/redirect.html?url=http%3A%2F%2Funion.suning.com%2Faas%2Fopen%2FvistorAd.action%3FuserId%3D129271%26webSiteId%3D0%26adInfoId%3D00%26adBookId%3D0%26vistURL%3Dhttp%3A%2F%2Fproduct.suning.com%2F109397112.html&version=3.2.1&vendor=chrome";
		url = "http://e.wochacha.com/redirect.html?url=http%3A%2F%2Fwww.tiantian.com%2Fcosmetic%2F45736.html&version=3.2.1&vendor=chrome";
		url = "http://e.wochacha.com/redirect.html?url=http%3A%2F%2Fwww.womai.com%2FProduct-200-517646.htm%3FwomaiSource%3Dwochacha&version=3.2.1&vendor=chrome";
//		url = "";
		url = toDestUrl(url);
		System.out.println(url);
		url = "http://item.jd.com/856886.html";
		System.out.println(getCodeFromUrl(url));
	}

	private String getCodeFromUrl(String productUrl) {
		if (StringUtils.isEmpty(productUrl)) {
			return null;
		}
		//jd,yixun,suning,tiantian,jumei,dangdang
		Pattern oReg = Pattern.compile(".*?[/-]{1}([0-9]+).html");
		Matcher matcher = oReg.matcher(productUrl);
		if (matcher.find()) {
			return matcher.group(1);
		}
		//gome,newegg,winxuan,womai
		oReg = Pattern.compile("product[/-]{1}([0-9a-zA-Z-]{6,})",Pattern.CASE_INSENSITIVE);
		matcher = oReg.matcher(productUrl);
		if (matcher.find()) {
			return matcher.group(1);
		}
		//amazon,
		oReg = Pattern.compile("dp/([0-9a-zA-Z]{8,})/");
		matcher = oReg.matcher(productUrl);
		if (matcher.find()) {
			return matcher.group(1);
		}
		//yhd
		oReg = Pattern.compile("item/([0-9a-zA-Z]{8,})");
		matcher = oReg.matcher(productUrl);
		if (matcher.find()) {
			return matcher.group(1);
		}
		return null;
	}

	private String toDestUrl(String url) {
		// http://e.wochacha.com/redirect.html?url=http%3A%2F%2Fitem.yixun.com%2Fitem-652046.html&version=3.2.1&vendor=chrome
		String mark = "http:";
		try {
			url = URLDecoder.decode(url, "UTF-8");
			int index = url.lastIndexOf(mark);
			return url.substring(index);
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
}
