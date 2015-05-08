package com.lezo.iscript.yeam.resultmgr;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.lezo.iscript.service.crawler.dto.MessageDto;
import com.lezo.iscript.service.crawler.service.MessageService;
import com.lezo.iscript.yeam.resultmgr.directory.DirMeta;
import com.lezo.iscript.yeam.resultmgr.directory.DirSummary;
import com.lezo.iscript.yeam.resultmgr.directory.DirSummaryCacher;

public class DataMessageHandler {
	private static Logger logger = LoggerFactory.getLogger(DataMessageHandler.class);
	private static AtomicBoolean running = new AtomicBoolean(false);
	@Autowired
	private MessageService messageService;

	public void run() {
		if (running.get()) {
			logger.warn("DataMessageHandler is running..");
			return;
		}
		long start = System.currentTimeMillis();
		try {
			running.set(true);
			logger.info("start to do DataMessageHandler ..");
			Iterator<Entry<String, DirSummary>> it = DirSummaryCacher.getInstance().iterator();
			int total = 0;
			int count = 0;
			List<DirSummary> notChangeList = new ArrayList<DirSummary>();
			while (it.hasNext()) {
				total++;
				Entry<String, DirSummary> entry = it.next();
				DirSummary dirSummary = entry.getValue();
				if (dirSummary.getToStamp() > dirSummary.getFromStamp()) {
					count++;
					DirMeta dirBean = dirSummary.getDirBean();
					List<String> nameList = new ArrayList<String>();
					nameList.add(dirBean.getType());
					Date beforeTime = new Date(dirSummary.getToStamp());
					Calendar c = Calendar.getInstance();
					c.setTime(beforeTime);
					c.add(Calendar.MINUTE, 1);
					beforeTime = c.getTime();
					messageService.updateStatusByCreateTime(nameList, dirBean.getBucket(), dirBean.getDomain(),
							beforeTime, MessageDto.STATUS_NEW, MessageDto.STATUS_DONE);
					dirSummary.setFromStamp(dirSummary.getToStamp());
				} else {
					notChangeList.add(dirSummary);
				}
			}
			long cost = System.currentTimeMillis() - start;
			int keepCount = notChangeList.size();
			notChangeList = checkDoneDirSummary(notChangeList);
			int waitCount = notChangeList.size();
			int doneCount = keepCount - waitCount;
			logger.info("handle DirMeta,total:" + total + ",handleCount:" + count + ",doneCount:" + doneCount
					+ ",waitCount:" + waitCount + ",cost:" + cost);
			for (DirSummary summary : notChangeList) {
				logger.warn("no change.key:" + summary.getDirBean().toDirKey() + ",count:" + summary.getCount()
						+ ",toStamp:" + summary.getToStamp());
			}
		} catch (Exception e) {
			long cost = System.currentTimeMillis() - start;
			logger.warn("handle DirMeta,cost:" + cost + ",cause:", e);
		} finally {
			running.set(false);
		}
	}

	private List<DirSummary> checkDoneDirSummary(List<DirSummary> notChangeList) {
		if (CollectionUtils.isEmpty(notChangeList)) {
			return java.util.Collections.emptyList();
		}
		Map<String, DirSummary> typeSummaryMap = new HashMap<String, DirSummary>();
		DirSummaryCacher summaryCacher = DirSummaryCacher.getInstance();
		for (DirSummary summary : notChangeList) {
			DirMeta meta = summary.getDirBean();
			StringBuilder sb = new StringBuilder();
			sb.append(meta.getType());
			sb.append(".");
			sb.append(meta.getDomain());
			sb.append(".");
			sb.append(meta.getBucket());
			String key = sb.toString();
			DirSummary doneSummary = summary;
			DirSummary hasSummary = typeSummaryMap.get(key);
			if (hasSummary == null || hasSummary.getDirBean().getCreateTime().before(meta.getCreateTime())) {
				doneSummary = typeSummaryMap.put(key, summary);
			}
			if (doneSummary != null) {
				String dirKey = doneSummary.getDirBean().toDirKey();
				summaryCacher.remove(dirKey);
				logger.info("done dirKey:" + dirKey + ",count:" + doneSummary.getCount() + ",toStamp:"
						+ doneSummary.getToStamp());
			}
		}
		return new ArrayList<DirSummary>(typeSummaryMap.values());
	}
}
