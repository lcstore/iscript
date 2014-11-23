package com.lezo.iscript.yeam.solr;

import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;

public class EmbeddedSolrServerHolder {
	private static final EmbeddedSolrServerHolder INSTANCE = new EmbeddedSolrServerHolder();
	private EmbeddedSolrServer embeddedSolrServer;

	public static EmbeddedSolrServerHolder getInstance() {
		return INSTANCE;
	}

	public EmbeddedSolrServer getEmbeddedSolrServer() {
		return embeddedSolrServer;
	}

	public void setEmbeddedSolrServer(EmbeddedSolrServer embeddedSolrServer) {
		this.embeddedSolrServer = embeddedSolrServer;
	}
}
