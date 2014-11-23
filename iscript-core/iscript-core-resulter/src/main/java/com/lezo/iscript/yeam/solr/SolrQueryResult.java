package com.lezo.iscript.yeam.solr;

import org.apache.solr.common.SolrDocumentList;

public class SolrQueryResult {
	private long numFound = 0;
	private long start = 0;
	private Float maxScore = null;
	private SolrDocumentList docs;

	public long getNumFound() {
		return numFound;
	}

	public void setNumFound(long numFound) {
		this.numFound = numFound;
	}

	public long getStart() {
		return start;
	}

	public void setStart(long start) {
		this.start = start;
	}

	public SolrDocumentList getDocs() {
		return docs;
	}

	public void setDocs(SolrDocumentList docs) {
		this.docs = docs;
	}

	public Float getMaxScore() {
		return maxScore;
	}

	public void setMaxScore(Float maxScore) {
		this.maxScore = maxScore;
	}

}
