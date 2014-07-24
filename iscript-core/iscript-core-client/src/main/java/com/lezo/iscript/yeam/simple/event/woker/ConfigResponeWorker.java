package com.lezo.iscript.yeam.simple.event.woker;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.yeam.config.ConfigParserBuffer;
import com.lezo.iscript.yeam.io.IoRespone;
import com.lezo.iscript.yeam.writable.ConfigWritable;

public class ConfigResponeWorker implements Runnable {
	private static org.slf4j.Logger logger = LoggerFactory.getLogger(ConfigResponeWorker.class);
	private static final Object CONFIG_WRITE_LOCK = new Object();
	private IoRespone ioRespone;

	public ConfigResponeWorker(IoRespone ioRespone) {
		super();
		this.ioRespone = ioRespone;
	}

	@Override
	public void run() {
		doConfigUpdate();
	}

	private void doConfigUpdate() {
		List<ConfigWritable> configList = getConfigList();
		if (CollectionUtils.isEmpty(configList)) {
			return;
		}
		// keep ConfigResponeWorker working in the lineS
		synchronized (CONFIG_WRITE_LOCK) {
			ConfigParserBuffer configBuffer = ConfigParserBuffer.getInstance();
			// doStampAsc(configList);
			int size = configList.size();
			// use index to foreach the list，keep the queue.
			for (int i = 0; i < size; i++) {
				ConfigWritable configWritable = configList.get(i);
				if (configBuffer.addConfig(configWritable.getName(), configWritable)) {
					logger.info("update.config[" + configWritable.getName() + "].stamp:" + configWritable.getStamp());
				} else {
					logger.warn("fail load config[" + configWritable.getName() + "].stamp:" + configWritable.getStamp());
				}
			}
		}
	}

	/**
	 * 按照stamp升序排列
	 * 
	 * @param configList
	 */
	private void doStampAsc(List<ConfigWritable> configList) {
		Collections.sort(configList, new Comparator<ConfigWritable>() {
			@Override
			public int compare(ConfigWritable o1, ConfigWritable o2) {
				return new Long(o1.getStamp()).compareTo(o2.getStamp());
			}
		});
	}

	@SuppressWarnings("unchecked")
	private List<ConfigWritable> getConfigList() {
		List<ConfigWritable> configList = new ArrayList<ConfigWritable>();
		try {
			Object dataObject = ioRespone.getData();
			if (dataObject instanceof ConfigWritable) {
				ConfigWritable configWritable = (ConfigWritable) dataObject;
				configList.add(configWritable);
			} else if (dataObject instanceof List) {
				configList = (List<ConfigWritable>) dataObject;
			}
		} catch (Exception e) {
			logger.warn("can not cast data to config.", e);
		}
		return configList;
	}
}
