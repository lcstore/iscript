package com.lezo.iscript.yeam.solr;

import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang3.math.NumberUtils;
import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.lezo.iscript.service.crawler.dto.LuceneIndexDto;
import com.lezo.iscript.service.crawler.dto.ProductDto;
import com.lezo.iscript.service.crawler.dto.SearchHisDto;
import com.lezo.iscript.service.crawler.service.SearchHisService;
import com.lezo.iscript.utils.JSONUtils;

public class SorlSearchHandler {
	private static AtomicBoolean running = new AtomicBoolean(false);
	private static Logger logger = LoggerFactory.getLogger(SorlSearchHandler.class);
	@Autowired
	private SearchHisService searchHisService;

	public void run() {
		if (running.get()) {
			logger.warn("doing.SorlSearchHandler");
			return;
		}
		long start = System.currentTimeMillis();
		EmbeddedSolrServer server = EmbeddedSolrServerHolder.getInstance().getEmbeddedSolrServer();
		try {
			running.set(true);
			List<SearchHisDto> dtoList = searchHisService.getSearchHisDtoByStatus(SearchHisDto.STATUS_NEW);
			if (dtoList.isEmpty()) {
				long cost = System.currentTimeMillis() - start;
				logger.info("done.no search to do.cost:{}", cost);
				return;
			}
			List<Long> idList = new ArrayList<Long>(dtoList.size());
			for (SearchHisDto dto : dtoList) {
				idList.add(dto.getId());
			}
			searchHisService.batchUpdateSearchHisDtoStatus(idList, SearchHisDto.STATUS_SEARCHING);
			logger.info("start.do search size:" + idList.size());
			ObjectMapper mapper = new ObjectMapper();
			for (SearchHisDto dto : dtoList) {
				try {
					doSearch(dto, server, mapper);
				} catch (Exception e) {
					e.printStackTrace();
					dto.setStatus(SearchHisDto.STATUS_ABORT);
					logger.warn("query:" + dto.getQuerySolr() + ",cause:", e);
				}
			}
			searchHisService.batchUpdateDtos(dtoList);
			long cost = System.currentTimeMillis() - start;
			String msg = String.format("done.querySize:%s,cost:%s", dtoList.size(), cost);
			logger.info(msg);
		} catch (Exception e) {
			long cost = System.currentTimeMillis() - start;
			String msg = String.format("abort.cost:%s", cost);
			logger.warn(msg, e);
		} finally {
			running.set(false);
		}
	}

	private void doSearch(SearchHisDto dto, SolrServer server, ObjectMapper mapper) throws Exception {
		SolrQuery solrQuery = getSolrQuery(dto);
		QueryResponse respone = server.query(solrQuery);
		SolrQueryResult sqr = new SolrQueryResult();
		SolrDocumentList docs = respone.getResults();
		sqr.setDocs(docs);
		sqr.setNumFound(docs.getNumFound());
		sqr.setStart(docs.getStart());
		sqr.setMaxScore(docs.getMaxScore());
		StringWriter writer = new StringWriter();
		mapper.writeValue(writer, sqr);
		dto.setQueryResult(writer.toString());
		dto.setStatus(SearchHisDto.STATUS_DONE);
		dto.setQueryCost((long) respone.getQTime());
	}

	private SolrQuery getSolrQuery(SearchHisDto dto) throws UnsupportedEncodingException {
		Map<String, String> keyValueMap = toKeyValueMap(dto.getQuerySolr());
		SolrQuery solrQuery = new SolrQuery();
		solrQuery.setQuery(getQuery(keyValueMap));
		solrQuery.setStart(getIntValue(keyValueMap.get("start"), 0));
		solrQuery.setRows(getIntValue(keyValueMap.get("rows"), 10));
		String[] fields = getFields(keyValueMap);
		if (fields != null && fields.length > 0) {
			solrQuery.setFields(fields);
		}
		return solrQuery;
	}

	private String getQuery(Map<String, String> keyValueMap) throws UnsupportedEncodingException {
		return URLDecoder.decode(keyValueMap.get("q"), "UTF-8");
	}

	private String[] getFields(Map<String, String> keyValueMap) throws UnsupportedEncodingException {
		String flString = keyValueMap.get("fl");
		flString = URLDecoder.decode(flString, "UTF-8");
		return flString.split(",");
	}

	private Integer getIntValue(String value, int defaultValue) {
		if (NumberUtils.isNumber(value)) {
			return Integer.valueOf(value);
		}
		return defaultValue;
	}

	private Map<String, String> toKeyValueMap(String querySolr) {
		String[] kvArray = querySolr.split("&");
		Map<String, String> kvMap = new HashMap<String, String>();
		for (int i = 0; i < kvArray.length; i++) {
			String[] unitArr = kvArray[i].split("=");
			if (unitArr != null && unitArr.length == 2) {
				kvMap.put(unitArr[0], unitArr[1]);
			}
		}
		return kvMap;
	}

	private LuceneIndexDto createIndexDto(ProductDto dto) {
		Calendar c = Calendar.getInstance();
		c.setTime(dto.getCreateTime());
		c.set(Calendar.HOUR_OF_DAY, 0);
		c.set(Calendar.MINUTE, 0);
		c.set(Calendar.SECOND, 0);
		LuceneIndexDto indexDto = new LuceneIndexDto();
		indexDto.setDataDay(c.getTime());
		indexDto.setCreateTime(new Date());
		indexDto.setUpdateTime(indexDto.getCreateTime());
		indexDto.setStatus(LuceneIndexDto.INDEX_DOING);
		JSONObject mObject = new JSONObject();
		JSONUtils.put(mObject, "fromId", dto.getId());
		indexDto.setMessage(mObject.toString());
		return indexDto;
	}
}
