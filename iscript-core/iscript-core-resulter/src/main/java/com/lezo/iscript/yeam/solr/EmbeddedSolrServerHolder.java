package com.lezo.iscript.yeam.solr;

import java.util.concurrent.TimeUnit;

import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.slf4j.Logger;

public class EmbeddedSolrServerHolder {
	private static Logger logger = org.slf4j.LoggerFactory.getLogger(EmbeddedSolrServerHolder.class);
	private static final EmbeddedSolrServerHolder INSTANCE = new EmbeddedSolrServerHolder();
	private EmbeddedSolrServer embeddedSolrServer;

	public static EmbeddedSolrServerHolder getInstance() {
		return INSTANCE;
	}

	public EmbeddedSolrServer getEmbeddedSolrServer() {
		while (embeddedSolrServer == null) {
			logger.warn("wait for embedded server init..");
			try {
				TimeUnit.MILLISECONDS.sleep(5);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		return embeddedSolrServer;
	}

	public void setEmbeddedSolrServer(EmbeddedSolrServer embeddedSolrServer) {
		this.embeddedSolrServer = embeddedSolrServer;
	}
}
