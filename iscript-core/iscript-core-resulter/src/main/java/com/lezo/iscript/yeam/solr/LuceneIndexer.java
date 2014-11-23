package com.lezo.iscript.yeam.solr;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.lang3.time.DateUtils;
import org.apache.solr.client.solrj.embedded.EmbeddedSolrServer;
import org.apache.solr.common.SolrInputDocument;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.lezo.iscript.service.crawler.dto.LuceneIndexDto;
import com.lezo.iscript.service.crawler.dto.ProductDto;
import com.lezo.iscript.service.crawler.service.LuceneIndexService;
import com.lezo.iscript.service.crawler.service.ProductService;
import com.lezo.iscript.utils.JSONUtils;

public class LuceneIndexer {
	private static AtomicBoolean running = new AtomicBoolean(false);
	private static Logger logger = LoggerFactory.getLogger(LuceneIndexer.class);
	@Autowired
	private LuceneIndexService luceneIndexService;
	@Autowired
	private ProductService productService;

	public void run() {
		if (running.get()) {
			logger.warn("doing.LuceneIndexer");
			return;
		}
		long start = System.currentTimeMillis();
		EmbeddedSolrServer server = EmbeddedSolrServerHolder.getInstance().getEmbeddedSolrServer();
		try {
			logger.info("start.lucene indexer...");
			running.set(true);
			LuceneIndexDto lastDto = luceneIndexService.getLatestLuceneIndexDto(null);
			Long nextQueryId = 0L;
			Long fromId = 0L;
			Long toId = 0L;
			int count = 0;
			if (lastDto != null) {
				JSONObject mObject = JSONUtils.getJSONObject(lastDto.getMessage());
				toId = JSONUtils.getLong(mObject, "toId");
				toId = toId == null ? 0L : toId;
				fromId = JSONUtils.getLong(mObject, "fromId");
				fromId = fromId == null ? 0L : fromId;
				nextQueryId = toId > 0 ? toId : fromId;
				count = lastDto.getDataCount() == null ? 0 : lastDto.getDataCount();
				if (lastDto.getStatus() != LuceneIndexDto.INDEX_DONE) {
					lastDto.setRetry(lastDto.getRetry() + 1);
					lastDto.setStatus(LuceneIndexDto.INDEX_DOING);
					List<LuceneIndexDto> updateList = new ArrayList<LuceneIndexDto>();
					updateList.add(lastDto);
					luceneIndexService.batchUpdateDtos(updateList);
				}
			}
			LuceneIndexDto indexDto = lastDto;
			int limit = 500;
			List<SolrInputDocument> docList = new ArrayList<SolrInputDocument>();
			while (true) {
				List<ProductDto> dtoList = productService.getProductDtosFromId(nextQueryId, limit, null);
				for (ProductDto dto : dtoList) {
					if (nextQueryId < dto.getId()) {
						nextQueryId = dto.getId();
					}
					if (indexDto == null || !DateUtils.isSameDay(indexDto.getDataDay(), dto.getCreateTime())) {
						List<LuceneIndexDto> indexList = new ArrayList<LuceneIndexDto>();
						if (indexDto != null) {
							if (!docList.isEmpty()) {
								server.add(docList);
								server.commit();
							}
							docList = new ArrayList<SolrInputDocument>();
							JSONObject mObject = new JSONObject();
							JSONUtils.put(mObject, "toId", toId);
							logger.info("query.dataDay:" + indexDto.getDataDay() + ",msg:" + indexDto.getMessage());
							LuceneIndexDto indexingDto = luceneIndexService.getLuceneIndexDtoByDay(indexDto.getDataDay());
							indexingDto.setMessage(mObject.toString());
							indexingDto.setDataCount(count);
							indexingDto.setStatus(LuceneIndexDto.INDEX_DONE);
							logger.info("done.dataDay:" + indexDto.getDataDay() + ",msg:" + indexDto.getMessage());
							indexList.add(indexingDto);
						}
						fromId = dto.getId();
						count = 0;
						indexDto = createIndexDto(dto);
						indexList.add(indexDto);
						luceneIndexService.batchSaveDtos(indexList);
					}
					toId = dto.getId();
					count++;
					SolrInputDocument doc = new SolrInputDocument();
					for (Field field : dto.getClass().getDeclaredFields()) {
						field.setAccessible(true);
						doc.addField(field.getName(), field.get(dto));
					}
					docList.add(doc);
				}
				if (!docList.isEmpty()) {
					server.add(docList);
					server.commit();
				}
				if (dtoList.size() < limit) {
					break;
				}
			}
			if (!docList.isEmpty()) {
				server.add(docList);
				server.commit();
				List<LuceneIndexDto> indexList = new ArrayList<LuceneIndexDto>();
				if (indexDto != null) {
					if (!docList.isEmpty()) {
						server.add(docList);
						server.commit();
					}
					docList = new ArrayList<SolrInputDocument>();
					JSONObject mObject = new JSONObject();
					JSONUtils.put(mObject, "fromId", fromId);
					JSONUtils.put(mObject, "toId", toId);
					LuceneIndexDto indexingDto = luceneIndexService.getLuceneIndexDtoByDay(indexDto.getDataDay());
					indexingDto.setMessage(mObject.toString());
					indexingDto.setDataCount(count);
					logger.info("last.dataDay:" + indexDto.getDataDay() + ",msg:" + indexDto.getMessage());
					indexList.add(indexingDto);
				}
				luceneIndexService.batchSaveDtos(indexList);
			}
			server.optimize();
			long cost = System.currentTimeMillis() - start;
			String msg = String.format("done.cost:%s", cost);
			logger.info(msg);
		} catch (Exception e) {
			long cost = System.currentTimeMillis() - start;
			String msg = String.format("abort.cost:%s", cost);
			logger.warn(msg, e);
		} finally {
			running.set(false);
		}
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
