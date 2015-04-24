package com.lezo.iscript.yeam.mina.filter;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.session.IoSession;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.yeam.config.ConfigParserBuffer;
import com.lezo.iscript.yeam.io.IoConstant;
import com.lezo.iscript.yeam.io.IoRequest;
import com.lezo.iscript.yeam.io.IoRespone;
import com.lezo.iscript.yeam.mina.SessionSender;
import com.lezo.iscript.yeam.mina.utils.HeaderUtils;
import com.lezo.iscript.yeam.writable.ConfigWritable;

public class ConfigIoFilter extends IoFilterAdapter {
	private static org.slf4j.Logger logger = LoggerFactory.getLogger(ConfigIoFilter.class);

	@Override
	public void messageReceived(NextFilter nextFilter, IoSession session, Object message) throws Exception {
		IoRespone ioRespone = (IoRespone) message;
		if (IoConstant.EVENT_TYPE_CONFIG == ioRespone.getType()) {
			updateConfig(ioRespone);
			IoRequest ioRequest = new IoRequest();
			ioRequest.setType(IoRequest.REQUEST_REPORT);
			ioRequest.setHeader(HeaderUtils.getHeader().toString());
			SessionSender.getInstance().send(ioRequest);
		} else {
			nextFilter.messageReceived(session, message);
		}
	}

	private void updateConfig(IoRespone ioRespone) {
		List<ConfigWritable> configList = getConfigList(ioRespone);
		if (CollectionUtils.isEmpty(configList)) {
			return;
		}
		ConfigParserBuffer configBuffer = ConfigParserBuffer.getInstance();
		int size = configList.size();
		for (int i = 0; i < size; i++) {
			ConfigWritable configWritable = configList.get(i);
			if (configBuffer.addConfig(configWritable.getName(), configWritable)) {
				logger.info("update.config[" + configWritable.getName() + "].stamp:" + configWritable.getStamp());
			} else {
				logger.warn("fail load config[" + configWritable.getName() + "].stamp:" + configWritable.getStamp());
			}
		}
	}

	@SuppressWarnings("unchecked")
	private List<ConfigWritable> getConfigList(IoRespone ioRespone) {
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
