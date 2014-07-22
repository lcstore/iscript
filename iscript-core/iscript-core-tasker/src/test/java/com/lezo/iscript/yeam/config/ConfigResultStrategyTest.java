package com.lezo.iscript.yeam.config;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.config.strategy.BarCodeStrategy;
import com.lezo.iscript.yeam.config.strategy.ProxyCollectorStrategy;
import com.lezo.iscript.yeam.writable.ResultWritable;

public class ConfigResultStrategyTest {

	public static void main(String[] args) throws Exception {
		String[] configs = new String[] { "classpath:spring-config-ds.xml" };
		ApplicationContext cx = new ClassPathXmlApplicationContext(configs);
		ConfigResultStrategyTest strategyTest = new ConfigResultStrategyTest();
//		strategyTest.testProxyCollectorStrategy();
	}

	public void testBarCodeStrategy() throws Exception {

		String type = "Config1688Product";
		BarCodeStrategy barCodeStrategy = new BarCodeStrategy();
		ResultWritable rWritable = new ResultWritable();
		rWritable.setStatus(ResultWritable.RESULT_SUCCESS);
		String result = "{\"特产\":\"是\",\"地方风味\":\"其他风味\",\"生产日期\":\"最新\",\"保质期\":\"365（天）\",\"原料与配料\":\"芒果干\",\"有无中文标签\":\"有\",\"售卖方式\":\"包装\",\"生产厂家\":\"Mangoes,Sugar and Sodium Me\",\"原产地\":\"菲律宾\",\"包装规格\":\"芒果干100g/包\",\"name\":\"进口食品菲律宾芒果干7D芒果干100克防伪新包装*100包一箱\",\"barCode\":\"4809010272010\",\"等级\":\"A\",\"brand\":\"7D\",\"产品类别\":\"果干\",\"是否进口\":\"是\",\"净含量（规格）\":\"100（g）\",\"储藏方法\":\"避光干燥\",\"加工工艺\":\"果干类\"}";
		JSONObject rsObject = new JSONObject();
		JSONObject argsObject = new JSONObject();
		JSONUtils.put(argsObject, "url", "http://detail.1688.com/offer/37687586366.html");
		JSONUtils.put(argsObject, "level", 0);
		JSONUtils.put(argsObject, "type", type);
		JSONUtils.put(rsObject, "rs", result);
		JSONUtils.put(rsObject, "args", argsObject);
		rWritable.setResult(rsObject.toString());
		rWritable.setType(type);
		barCodeStrategy.handleResult(rWritable);
	}

	public void testProxyCollectorStrategy() throws Exception {

		String type = "ConfigProxyCollector";
		ProxyCollectorStrategy proxyCollectorStrategy = new ProxyCollectorStrategy();
		ResultWritable rWritable = new ResultWritable();
		rWritable.setStatus(ResultWritable.RESULT_SUCCESS);
		String result = "{\"proxys\":[{\"port\":\"7808\",\"ip\":\"185.49.15.25\"},{\"port\":\"7808\",\"ip\":\"92.222.153.153\"},{\"port\":\"3127\",\"ip\":\"188.241.141.112\"},{\"port\":\"7808\",\"ip\":\"23.89.198.161\"},{\"port\":\"7808\",\"ip\":\"62.244.31.16\"},{\"port\":\"7808\",\"ip\":\"107.182.16.221\"},{\"port\":\"7808\",\"ip\":\"93.115.8.229\"},{\"port\":\"8089\",\"ip\":\"198.52.199.152\"},{\"port\":\"3127\",\"ip\":\"198.52.217.44\"},{\"port\":\"8080\",\"ip\":\"195.175.201.170\"},{\"port\":\"3127\",\"ip\":\"192.227.139.227\"},{\"port\":\"3127\",\"ip\":\"107.182.135.43\"},{\"port\":\"3127\",\"ip\":\"199.200.120.140\"},{\"port\":\"3128\",\"ip\":\"41.74.79.136\"},{\"port\":\"3128\",\"ip\":\"210.212.97.179\"},{\"port\":\"3128\",\"ip\":\"202.29.243.36\"},{\"port\":\"7808\",\"ip\":\"199.241.137.180\"},{\"port\":\"3127\",\"ip\":\"184.105.18.60\"},{\"port\":\"3128\",\"ip\":\"200.135.250.130\"},{\"port\":\"80\",\"ip\":\"196.29.140.130\"}],\"nexts\":[\"http://www.cool-proxy.net/proxies/http_proxy_list/sort:score/direction:desc/page:2\"]}";
		JSONObject rsObject = new JSONObject();
		JSONObject argsObject = new JSONObject();
		JSONUtils.put(argsObject, "url", "http://detail.1688.com/offer/37687586366.html");
		JSONUtils.put(argsObject, "level", 0);
		JSONUtils.put(argsObject, "type", type);
		JSONUtils.put(rsObject, "rs", result);
		JSONUtils.put(rsObject, "args", argsObject);
		rWritable.setResult(rsObject.toString());
		rWritable.setType(type);
		proxyCollectorStrategy.handleResult(rWritable);
	}
}
