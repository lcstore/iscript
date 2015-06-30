package com.lezo.iscript.resulter.cluster;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.lezo.iscript.service.crawler.service.ProductService;
import com.lezo.iscript.service.crawler.service.SimilarService;
import com.lezo.iscript.service.crawler.service.SynonymBrandService;

public class ClusterSimilarJobTest {

	public static void main(String[] args) {
		String[] configs = new String[] { "classpath:spring-config-ds.xml" };
		ApplicationContext cx = new ClassPathXmlApplicationContext(configs);
		ClusterSimilarJob job = new ClusterSimilarJob();
		job.setProductService(cx.getBean(ProductService.class));
		job.setSynonymBrandService(cx.getBean(SynonymBrandService.class));
		job.setSimilarService(cx.getBean(SimilarService.class));
		job.run();
	}
}
