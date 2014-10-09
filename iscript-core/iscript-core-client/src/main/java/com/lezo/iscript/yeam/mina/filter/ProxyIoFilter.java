package com.lezo.iscript.yeam.mina.filter;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.apache.mina.core.filterchain.IoFilterAdapter;
import org.apache.mina.core.session.IoSession;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.utils.InetAddressUtils;
import com.lezo.iscript.yeam.io.IoConstant;
import com.lezo.iscript.yeam.io.IoRespone;
import com.lezo.iscript.yeam.proxy.ProxyBuffer;
import com.lezo.iscript.yeam.writable.ProxyWritable;

public class ProxyIoFilter extends IoFilterAdapter {
	private static org.slf4j.Logger logger = LoggerFactory.getLogger(ProxyIoFilter.class);

	@Override
	public void messageReceived(NextFilter nextFilter, IoSession session, Object message) throws Exception {
		IoRespone ioRespone = (IoRespone) message;
		if (IoConstant.EVENT_TYPE_PROXY == ioRespone.getType()) {
			addProxys(ioRespone);
		} else {
			nextFilter.messageReceived(session, message);
		}
	}

	private void addProxys(IoRespone ioRespone) {
		List<ProxyWritable> configList = getConfigList(ioRespone);
		if (CollectionUtils.isEmpty(configList)) {
			return;
		}
		ProxyBuffer proxyBuffer = ProxyBuffer.getInstance();
		int size = configList.size();
		for (int i = 0; i < size; i++) {
			ProxyWritable proxyWritable = configList.get(i);
			String host = getHost(proxyWritable);
			proxyBuffer.addProxy(proxyWritable.getId(), proxyBuffer.createProxy(host, proxyWritable.getPort()));
		}
		String msg = String.format("proxyManager add:%d,use:%d,error:%d", configList.size(), proxyBuffer.getProxys()
				.size(), proxyBuffer.getErrors().size());
		logger.info(msg);
		proxyBuffer.clearErrors();
	}

	private String getHost(ProxyWritable proxyWritable) {
		return InetAddressUtils.inet_ntoa(proxyWritable.getIp());
	}

	@SuppressWarnings("unchecked")
	private List<ProxyWritable> getConfigList(IoRespone ioRespone) {
		List<ProxyWritable> configList = new ArrayList<ProxyWritable>();
		try {
			Object dataObject = ioRespone.getData();
			if (dataObject instanceof ProxyWritable) {
				ProxyWritable ProxyWritable = (ProxyWritable) dataObject;
				configList.add(ProxyWritable);
			} else if (dataObject instanceof List) {
				configList = (List<ProxyWritable>) dataObject;
			}
		} catch (Exception e) {
			logger.warn("can not cast data to ProxyWritable.", e);
		}
		return configList;
	}

}
