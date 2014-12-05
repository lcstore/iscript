package com.lezo.iscript.yeam.resultmgr.writer;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;

import com.lezo.iscript.common.BufferObjectWriter;
import com.lezo.iscript.service.crawler.dto.CrawlerWarnHisDto;

public class WriteNotifyer {
	private static final WriteNotifyer INSTANCE_NOTIFYER = new WriteNotifyer();
	private Logger logger = org.slf4j.LoggerFactory.getLogger(WriteNotifyer.class);
	private Map<String, BufferObjectWriter<?>> bufferWriterMap = new HashMap<String, BufferObjectWriter<?>>();

	private WriteNotifyer() {
		addWriter(CrawlerWarnHisDto.class, new BufferObjectWriter<CrawlerWarnHisDto>(
				new CrawlerWarnHisWriter(), 200));
	}

	public static WriteNotifyer getInstance() {
		return INSTANCE_NOTIFYER;
	}
	
	public <T> void addWriter(Class<T> dtoClass,BufferObjectWriter<T> bufferObjectWriter){
		addWriter(dtoClass.getSimpleName(), bufferObjectWriter);
	}
	public <T> void addWriter(String dtoName,BufferObjectWriter<T> bufferObjectWriter){
		bufferWriterMap.put(dtoName, bufferObjectWriter);
	}

	@SuppressWarnings("unchecked")
	public void doNotify(List<Object> dataList) {
		if (CollectionUtils.isEmpty(dataList)) {
			return;
		}
		String name = dataList.get(0).getClass().getSimpleName();
		BufferObjectWriter<Object> writer = (BufferObjectWriter<Object>) bufferWriterMap.get(name);
		writer.write(dataList);
	}

	public void flush() {
		for (Entry<String, BufferObjectWriter<?>> entry : bufferWriterMap.entrySet()) {
			logger.info("start to flush.BufferObjectWriter,Object:"+entry.getKey());
			entry.getValue().flush();
		}
	}
}
