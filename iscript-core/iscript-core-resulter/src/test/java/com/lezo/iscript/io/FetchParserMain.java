package com.lezo.iscript.io;

import java.util.HashMap;
import java.util.Map;

import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.lezo.iscript.IoSeed;
import com.lezo.iscript.rest.http.HttpClientManager;
import com.lezo.rest.data.BaiduPcsRester;
import com.lezo.rest.data.ClientRest;
import com.lezo.rest.data.ClientRestFactory;

public class FetchParserMain {

    public static void main(String[] args) {
        String bucket = "idocs";
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

        addResters();

        ClassPathXmlApplicationContext cx = new ClassPathXmlApplicationContext(
                new String[] { "classpath:spring-config-ds.xml" });
        // SeedCacher.getInstance().getQueue().offer(IoConstants.LEVEL_PATH, element);

    }

    private static void addResters() {
        String bucket = "idocs";
        String accessToken =
                "21.28d93026d0d1b6be4bbc21864e95fa1f.2592000.1442897384.4026763474-2920106";
        String domain = "baidu.com";
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

        bucket = "istore_doc";
        accessToken =
                "21.5e071f36ed7fef36db71e6019df41e30.2592000.1443014085.4026763474-1856205";
        domain = "baidu.com";
        clientRest = new ClientRest();
        clientRest.setBucket(bucket);
        clientRest.setDomain(domain);
        rester = new BaiduPcsRester();
        rester.setAccessToken(accessToken);
        rester.setBucket(bucket);
        rester.setDomain(domain);
        rester.setClient(HttpClientManager.getDefaultHttpClient());
        clientRest.setRester(rester);
        ClientRestFactory.getInstance().put(clientRest);
    }

}
