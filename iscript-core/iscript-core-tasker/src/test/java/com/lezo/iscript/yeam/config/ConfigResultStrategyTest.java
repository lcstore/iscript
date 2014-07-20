package com.lezo.iscript.yeam.config;

import org.json.JSONObject;
import org.junit.Test;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.config.strategy.BarCodeStrategy;
import com.lezo.iscript.yeam.writable.ResultWritable;

public class ConfigResultStrategyTest {

	@Test
	public void test() throws Exception {
		String[] configs = new String[] { "classpath:spring-config-ds.xml" };
		ApplicationContext cx = new ClassPathXmlApplicationContext(configs);
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
		Thread.currentThread().join();
	}
}
