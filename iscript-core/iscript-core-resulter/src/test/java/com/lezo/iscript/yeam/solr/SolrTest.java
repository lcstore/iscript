package com.lezo.iscript.yeam.solr;

import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.core.CoreContainer;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class SolrTest {

	public void testSolr() {
		String[] locations = new String[] { "classpath:spring-config-ds.xml" };
		ApplicationContext cx = new ClassPathXmlApplicationContext(locations);
		System.setProperty("solr.solr.home", "E:/lezo/codes/solr_home");
		CoreContainer coreContainer = new CoreContainer("E:/lezo/codes/solr_home");
		EmbeddedSolrServer server = new EmbeddedSolrServer(coreContainer, "");
	}
}
