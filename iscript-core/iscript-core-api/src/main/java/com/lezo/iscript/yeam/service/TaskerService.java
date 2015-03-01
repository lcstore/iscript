package com.lezo.iscript.yeam.service;

import java.io.IOException;

import com.lezo.iscript.yeam.writable.ClientWritable;
import com.lezo.iscript.yeam.writable.RemoteWritable;
import com.lezo.iscript.yeam.writable.StorageBufferWritable;
import com.lezo.iscript.yeam.writable.StorageHeaderWritable;

public interface TaskerService {
	StorageBufferWritable getStorageBuffer(StorageHeaderWritable header) throws IOException;

	RemoteWritable<?> getClient(String version) throws IOException;

	RemoteWritable<?> getMore(ClientWritable client) throws IOException;

}
