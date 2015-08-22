package com.lezo.iscript.io;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.lezo.iscript.IoSeed;
import com.lezo.iscript.cache.SeedCacher;
import com.lezo.iscript.rest.http.HttpClientManager;
import com.lezo.rest.data.BaiduPcsRester;
import com.lezo.rest.data.ClientRest;
import com.lezo.rest.data.ClientRestFactory;

public class FetchParserMain {

	public static void main(String[] args) {
		String bucket = "idocs";
		String accessToken =
				"21.dcaf9aca78a0dcb555ff2e76ccf3b21d.2592000.1440400567.4026763474-2920106";
		String domain = "baidu.com";
		IoDispatcher.getInstance().start();
		IoSeed element = new IoSeed();
		element.setBucket(bucket);
		element.setDomain(domain);
		element.setDataPath("/apps/idocs/iscript/20150815/ConfigJdProduct/28493621-7b4f-4a44-90e0-ae3fcda8e055");
		element.setFetcher(new PathFetcher());
		Map<String, String> params = new HashMap<String, String>();
		params.put("limit", "0-5");
		element.setParams(params);
		ClientRest clientRest = new ClientRest();
		clientRest.setBucket(bucket);
		clientRest.setDomain(domain);
		BaiduPcsRester rester = new BaiduPcsRester();
		rester.setAccessToken(accessToken);
		rester.setBucket(bucket);
		rester.setDomain(domain);
		rester.setClient(HttpClientManager.getDefaultHttpClient());
		clientRest.setRester(rester);
		ClientRestFactory.getInstance().put(clientRest);

		ClassPathXmlApplicationContext cx = new ClassPathXmlApplicationContext(
				new String[] { "classpath:spring-config-ds.xml" });
		SeedCacher.getInstance().getQueue().offer(IoConstants.LEVEL_PATH, element);

	}

}
