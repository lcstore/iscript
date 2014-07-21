package com.lezo.iscript.yeam.simple.event.woker;

import java.util.ArrayList;
import java.util.List;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.LoggerFactory;

import com.lezo.iscript.utils.InetAddressUtils;
import com.lezo.iscript.yeam.io.IoRespone;
import com.lezo.iscript.yeam.proxy.ProxyBuffer;
import com.lezo.iscript.yeam.writable.ProxyWritable;

public class ProxyResponeWorker implements Runnable {
	private static org.slf4j.Logger logger = LoggerFactory.getLogger(ProxyResponeWorker.class);
	private static final Object WRITE_LOCK = new Object();
	private IoRespone ioRespone;

	public ProxyResponeWorker(IoRespone ioRespone) {
		super();
		this.ioRespone = ioRespone;
	}

	@Override
	public void run() {
		doProxyUpdate();
	}

	private void doProxyUpdate() {
		List<ProxyWritable> configList = getConfigList();
		if (CollectionUtils.isEmpty(configList)) {
			return;
		}
		synchronized (WRITE_LOCK) {
			ProxyBuffer proxyBuffer = ProxyBuffer.getInstance();
			int size = configList.size();
			for (int i = 0; i < size; i++) {
				ProxyWritable proxyWritable = configList.get(i);
				String host = getHost(proxyWritable);
				proxyBuffer.addProxy(proxyWritable.getId(), proxyBuffer.createProxy(host, proxyWritable.getPort()));
			}
			String msg = String.format("proxyManager add:%d,use:%d,error:%d", configList.size(), proxyBuffer
					.getProxys().size(), proxyBuffer.getErrors().size());
			logger.info(msg);
			proxyBuffer.clearErrors();
		}
	}

	private String getHost(ProxyWritable proxyWritable) {
		return InetAddressUtils.inet_ntoa(proxyWritable.getIp());
	}

	@SuppressWarnings("unchecked")
	private List<ProxyWritable> getConfigList() {
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
