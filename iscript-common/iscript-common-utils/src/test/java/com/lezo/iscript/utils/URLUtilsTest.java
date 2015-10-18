package com.lezo.iscript.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;

import org.junit.Assert;
import org.junit.Test;

public class URLUtilsTest {
	@Test
	public void testUrl() {
		String url = "http://e.wochacha.com/redirect.html?url=http%3A%2F%2Fitem.yixun.com%2Fitem-652046.html&version=3.2.1&vendor=chrome";
		url = "http://e.wochacha.com/redirect.html?url=http%3A%2F%2Fclick.union.jd.com%2FJdClick%2F%3FunionId%3D13228%26t%3D1%26to%3Dhttp%3A%2F%2Fitem.jd.com%2F855739.html&version=3.2.1&vendor=chrome";
		url = "http://e.wochacha.com/redirect.html?url=http%3A%2F%2Fcps.gome.com.cn%2Fhome%2FJoinUnion%3Fsid%3D1496%26feedback%3D%26to%3Dhttp%3A%2F%2Fwww.gome.com.cn%2Fproduct%2FA0004735064.html&version=3.2.1&vendor=chrome";
		url = "http://e.wochacha.com/redirect.html?url=http%3A%2F%2Fwww.amazon.cn%2Fdp%2FB00CAI7VUW%2F%3Ftag%3Dwcc-23&version=3.2.1&vendor=chrome";
		// url =
		// "http://e.wochacha.com/redirect.html?url=http%3A%2F%2Fwww.newegg.cn%2FProduct%2Fa28-032-5vf.htm&version=3.2.1&vendor=chrome";
		// url =
		// "http://e.wochacha.com/redirect.html?url=http%3A%2F%2Fitem.yhd.com%2Fitem%2F10880806%3Ftracker_u%3D7550426%26union_ref%3D7&version=3.2.1&vendor=chrome";
		// url =
		// "http://e.wochacha.com/redirect.html?url=http%3A%2F%2Fwww.winxuan.com%2Fproduct%2F1200835879&version=3.2.1&vendor=chrome";
		// url =
		// "http://e.wochacha.com/redirect.html?url=http%3A%2F%2Funion.suning.com%2Faas%2Fopen%2FvistorAd.action%3FuserId%3D129271%26webSiteId%3D0%26adInfoId%3D00%26adBookId%3D0%26vistURL%3Dhttp%3A%2F%2Fproduct.suning.com%2F109397112.html&version=3.2.1&vendor=chrome";
		// url =
		// "http://e.wochacha.com/redirect.html?url=http%3A%2F%2Fwww.tiantian.com%2Fcosmetic%2F45736.html&version=3.2.1&vendor=chrome";
		// url =
		// "http://e.wochacha.com/redirect.html?url=http%3A%2F%2Fwww.womai.com%2FProduct-200-517646.htm%3FwomaiSource%3Dwochacha&version=3.2.1&vendor=chrome";
		// url = "http://www.amazon.cn/mn/detailApp?asin=b006dvda2a";
		url = "http://item.jd.com/1091109951.html";
		// url = "";
		url = toDestUrl(url);
		System.out.println(url);
		System.out.println(URLUtils.getCodeFromUrl(url));
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

	@Test
    public void testRootHost() {
		String url = "http://xx.gome.com.cn/product/A0004331780.html&version=3.2.1&vendor=chrome";
		Assert.assertEquals("gome.com.cn", URLUtils.getRootHost(url));
		url = "http://abc.xxxx.gome.com.cn/product/A0004331780.html&version=3.2.1&vendor=chrome";
		Assert.assertEquals("gome.com.cn", URLUtils.getRootHost(url));
		url = "http://bbs.csdn.net/topics/390082054";
		Assert.assertEquals("csdn.net", URLUtils.getRootHost(url));
		url = "http://java-er.com/blog/java-url-join/";
		Assert.assertEquals("java-er.com", URLUtils.getRootHost(url));
		url = "http://www.ceca.org.cn/";
		Assert.assertEquals("ceca.org.cn", URLUtils.getRootHost(url));
        url = "http://detail.tmall.com/item.htm?id=13510626287&&frm=yiyao";
        Assert.assertEquals("tmall.com", URLUtils.getRootHost(url));
	}
}
