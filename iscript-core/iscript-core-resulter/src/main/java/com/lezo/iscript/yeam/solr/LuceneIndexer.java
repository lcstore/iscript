package com.lezo.iscript.yeam.solr;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.lezo.iscript.service.crawler.dto.LuceneIndexDto;
import com.lezo.iscript.service.crawler.dto.ProductDto;
import com.lezo.iscript.service.crawler.service.LuceneIndexService;
import com.lezo.iscript.service.crawler.service.ProductService;

public class LuceneIndexer {
	private static AtomicBoolean running = new AtomicBoolean(false);
	private static Logger logger = LoggerFactory.getLogger(LuceneIndexer.class);
	@Autowired
	private LuceneIndexService luceneIndexService;
	@Autowired
	private EmbeddedSolrServer server;
	@Autowired
	private ProductService productService;

	public void run() {
		if (running.get()) {
			logger.warn("doing.LuceneIndexer");
			return;
		}
		long start = System.currentTimeMillis();
		try {
			logger.info("start.");
			running.set(true);
			LuceneIndexDto lastDto = luceneIndexService.getLatestLuceneIndexDto(LuceneIndexDto.INDEX_DONE);
			if (lastDto == null) {
				lastDto = new LuceneIndexDto();
			}
			Long fromId = 0L;
			int limit = 500;
			while (true) {
				List<ProductDto> dtoList = productService.getProductDtosFromId(fromId, limit, null);
				Long maxId = fromId;
				Map<Integer, Set<String>> siteCodeMap = new HashMap<Integer, Set<String>>();
				for (ProductDto dto : dtoList) {
					if (maxId < dto.getId()) {
						maxId = dto.getId();
					}
					Set<String> codeSet = siteCodeMap.get(dto.getSiteId());
					if (codeSet == null) {
						codeSet = new HashSet<String>();
						siteCodeMap.put(dto.getSiteId(), codeSet);
					}
					codeSet.add(dto.getProductCode());
				}
				if (dtoList.size() < limit) {
					break;
				}
			}
			long cost = System.currentTimeMillis() - start;
			String msg = String.format("done.");
			logger.info(msg);
		} catch (Exception e) {
			long cost = System.currentTimeMillis() - start;
			String msg = String.format("abort");
			logger.warn(msg, e);
		} finally {
			running.set(false);
		}
	}
}
