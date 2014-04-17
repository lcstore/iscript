package com.lezo.iscript.yeam.tasker.handle;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map.Entry;

import com.lezo.iscript.yeam.ClientConstant;
import com.lezo.iscript.yeam.tasker.buffer.ConfigBuffer;
import com.lezo.iscript.yeam.writable.ClientWritable;
import com.lezo.iscript.yeam.writable.ConfigWritable;
import com.lezo.iscript.yeam.writable.RemoteWritable;

public class GetConfigHandler implements GetMoreHandle {

	@Override
	public boolean isAccept(ClientWritable client) {
		if (client == null || client.getParam() == null) {
			return true;
		}
		Object configStampObject = client.getParam().get(ClientConstant.CLIENT_CONFIG_STAMP);
		if (configStampObject == null) {
			return true;
		}
		long configStamp = (Long) configStampObject;
		long currentConfigStamp = ConfigBuffer.getInstance().getStamp();
		return configStamp < currentConfigStamp;
	}

	@Override
	public RemoteWritable<?> createWritable(ClientWritable client) throws IOException {
		RemoteWritable<ConfigWritable> remoteWritable = new RemoteWritable<ConfigWritable>();
		if (!isAccept(client)) {
			remoteWritable.setStatus(ClientConstant.GET_NONE);
			return remoteWritable;
		}
		List<ConfigWritable> configWritables = new ArrayList<ConfigWritable>();
		remoteWritable.setStorageList(configWritables);
		remoteWritable.setStatus(getStatus());
		long configStamp = 0;
		if (client != null && client.getParam() != null) {
			Object configStampObject = client.getParam().get(ClientConstant.CLIENT_CONFIG_STAMP);
			if (configStampObject != null) {
				configStamp = (Long) configStampObject;
			}
		}
		Iterator<Entry<String, ConfigWritable>> it = ConfigBuffer.getInstance().unmodifyIterator();
		while (it.hasNext()) {
			ConfigWritable config = it.next().getValue();
			if (config.getStamp() > configStamp) {
				configWritables.add(config);
			}
		}
		return remoteWritable;
	}

	@Override
	public Integer getStatus() {
		return ClientConstant.GET_CONFIG;
	}

}
