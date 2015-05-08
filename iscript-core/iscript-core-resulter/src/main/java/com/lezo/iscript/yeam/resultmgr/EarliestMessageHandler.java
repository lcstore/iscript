package com.lezo.iscript.yeam.resultmgr;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

import com.lezo.iscript.service.crawler.dto.MessageDto;
import com.lezo.iscript.service.crawler.service.MessageService;
import com.lezo.iscript.utils.JSONUtils;
import com.lezo.iscript.yeam.resultmgr.directory.DirMeta;
import com.lezo.iscript.yeam.resultmgr.directory.DirSummaryCacher;

public class EarliestMessageHandler {
	private static Logger logger = LoggerFactory.getLogger(EarliestMessageHandler.class);
	private static AtomicBoolean running = new AtomicBoolean(false);
	@Value("#{settings['msg_handle_names'].split(',')}")
	private List<String> nameList;
	@Autowired
	private MessageService messageService;

	public void run() {
		if (running.get()) {
			logger.warn("EarliestMessageHandler is running..");
			return;
		}
		long start = System.currentTimeMillis();
		try {
			running.set(true);
			if (CollectionUtils.isEmpty(nameList)) {
				logger.warn("nameList is empty.check the config..");
				return;
			}
			logger.info("start to do EarliestMessageHandler,name size:" + nameList.size());
			List<MessageDto> dtoList = messageService.getEarlyMessageDtoByNameList(nameList, 0);
			List<DirMeta> dirBeans = new ArrayList<DirMeta>();
			for (MessageDto dto : dtoList) {
				if (StringUtils.isEmpty(dto.getDataBucket()) || StringUtils.isEmpty(dto.getDataDomain())) {
					continue;
				}
				JSONObject mObject = JSONUtils.getJSONObject(dto.getMessage());
				if (mObject == null) {
					continue;
				}
				Iterator<?> it = mObject.keys();
				while (it.hasNext()) {
					DirMeta dirBean = new DirMeta();
					dirBean.setBucket(dto.getDataBucket());
					dirBean.setCreateTime(dto.getCreateTime());
					dirBean.setDomain(dto.getDataDomain());
					dirBean.setType(dto.getName());
					dirBean.setPid(it.next().toString());
					dirBeans.add(dirBean);
				}
			}
			DirSummaryCacher cacher = DirSummaryCacher.getInstance();
			for (DirMeta dirBean : dirBeans) {
				cacher.fireEvent(dirBean);
			}
			long cost = System.currentTimeMillis() - start;
			logger.info("add earliest message:" + dirBeans.size() + ",nameCount:" + nameList.size() + ",cost:" + cost);
		} catch (Exception e) {
			logger.warn("", e);
		} finally {
			running.set(false);
		}
	}
}
