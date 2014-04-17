package com.lezo.iscript.yeam.tasker.handle;

import java.io.IOException;

import com.lezo.iscript.yeam.writable.ClientWritable;
import com.lezo.iscript.yeam.writable.RemoteWritable;

public interface GetMoreHandle {
	boolean isAccept(ClientWritable client);

	RemoteWritable<?> createWritable(ClientWritable client) throws IOException;

	Integer getStatus();
}
