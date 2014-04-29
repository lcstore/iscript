//package com.yihaodian.pis.config.session;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.InputStream;
//import java.io.InputStreamReader;
//import java.util.List;
//
//import org.apache.commons.io.FileUtils;
//import org.json.JSONObject;
//import org.junit.Test;
//
//import com.yihaodian.pis.config.xml.XMLConfig2Script;
//import com.yihaodian.pis.javascriptcrawler.JavaScriptCrawler;
//import com.yihaodian.pis.services.distributed.taskprovider.CrawlerTask;
//
//public class SessionKeyTest {
//
//	@Test
//	public void test() throws Exception {
//		String path = "D:/pis-code/pis-local-center/pis-crawler-taobao/src/main/resources/appConfig.txt";
//		List<String> keyList = FileUtils.readLines(new File(path));
//		String jsConfig = getConfig();
//		for (String keyString : keyList) {
//			String[] keyArr = keyString.split("\t");
//			if (keyArr == null || keyArr.length < 2) {
//				System.err.println("error key:" + keyString);
//				continue;
//			}
//			String key = keyArr[0];
//			JavaScriptCrawler jsc = new JavaScriptCrawler();
//			String params = "";
//			params = "appkey:\""
//					+ key
//					+ "\",pcodes:\"15370-6182881841-10061847261-8039660017-9784478474-9238306193-8757410187-2223024525-2604498551-10374682347-10770988759-4512594186-8148253693-4968281572-3829291979-8616299168-12391471415-7698464961-10346600398-8833715897,15328-5246595060\",uuid:\"fa6ba16c-05eb-415e-a99f-cbfb212708a9\"";
//			params = "," + params;
//			CrawlerTask task = new CrawlerTask();
//			task.setJsonArgs(String.format("{url:%s, tid:%s, level:%s", JSONObject.quote("http://taobao"),
//					JSONObject.quote(String.valueOf(1L)), JSONObject.quote(String.valueOf(3)))
//					+ params + "}");
//
//			String args = task.getJsonArgs();
//			try {
//				String sConfig = String.format("(function(args){%s})(%s);", jsConfig, args);
//				Object result = jsc.executeScriptToJSONString(sConfig);
//			} catch (Exception e) {
//				System.err.println(args + ":error:" + e.getMessage());
//			}
//		}
//		System.out.println("#### finish:" + keyList.size());
//
//	}
//
//	public String getConfig() throws Exception {
//		int len = 0;
//		char[] buffer = new char[128];
//		String filePath = "D:/pis-code/config/crawler-config/taobao-items.xml";
//		InputStream inputStream = new FileInputStream(filePath);
//		InputStreamReader reader = new InputStreamReader(inputStream, "utf-8");
//		StringBuilder sb = new StringBuilder();
//		while ((len = reader.read(buffer)) > 0) {
//			sb.append(buffer, 0, len);
//		}
//		reader.close();
//		inputStream.close();
//		String jsConfig = XMLConfig2Script.convertConfig(sb.toString());
//		return jsConfig;
//	}
//}
