package com.lezo.iscript.yeam.resultmgr;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicBoolean;

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
			int doneCount = 0;
			int count = 0;
			int waitCount = 0;
			List<DirSummary> notChangeList = new ArrayList<DirSummary>();
			while (it.hasNext()) {
				total++;
				Entry<String, DirSummary> entry = it.next();
				DirSummary dirSummary = entry.getValue();
				if (dirSummary.getToStamp() != dirSummary.getFromStamp() || dirSummary.isDone()) {
					count++;
					DirMeta dirBean = dirSummary.getDirBean();
					List<String> nameList = new ArrayList<String>();
					nameList.add(dirBean.getType());
					long destStamp = dirSummary.getFromStamp() > dirSummary.getToStamp() ? dirSummary.getFromStamp()
							: dirSummary.getToStamp();
					Date beforeTime = new Date(destStamp);
					Calendar c = Calendar.getInstance();
					c.setTime(beforeTime);
					c.add(Calendar.MINUTE, 1);
					beforeTime = c.getTime();
					messageService.updateStatusByCreateTime(nameList, dirBean.getBucket(), dirBean.getDomain(),
							beforeTime, MessageDto.STATUS_NEW, MessageDto.STATUS_DONE);
					if (dirSummary.isDone()) {
						doneCount++;
						it.remove();
						logger.info("done,dirKey:" + dirBean.toDirKey());
					} else {
						waitCount++;
						// dirSummary.setFromStamp(dirSummary.getToStamp());
					}
				} else {
					notChangeList.add(dirSummary);
				}
			}
			long cost = System.currentTimeMillis() - start;
			logger.info("handle DirMeta,total:" + total + ",handleCount:" + count + ",doneCount:" + doneCount
					+ ",waitCount:" + waitCount + ",noChange:" + notChangeList.size() + ",cost:" + cost);
			for (DirSummary summary : notChangeList) {
				logger.warn("no change.key:" + summary.getDirBean().toDirKey() + ",count:" + summary.getCount()
						+ ",toStamp:" + summary.getToStamp() + ",fromStamp:" + summary.getFromStamp());
			}
		} catch (Exception e) {
			long cost = System.currentTimeMillis() - start;
			logger.warn("handle DirMeta,cost:" + cost + ",cause:", e);
		} finally {
			running.set(false);
		}
	}
}
