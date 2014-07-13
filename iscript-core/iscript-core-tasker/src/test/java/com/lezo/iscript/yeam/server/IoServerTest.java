package com.lezo.iscript.yeam.server;

import java.io.IOException;

import com.lezo.iscript.yeam.tasker.buffer.ConfigBuffer;
import com.lezo.iscript.yeam.writable.ConfigWritable;

public class IoServerTest {
	public static void main(String[] args) throws IOException {
		int port = 1111;
		ConfigWritable configWritable = new ConfigWritable();
		configWritable.setContent("test config".getBytes());
		configWritable.setName("test.name");
		configWritable.setStamp(System.currentTimeMillis());
		configWritable.setType(ConfigWritable.CONFIG_TYPE_SCRIPT);
		ConfigBuffer.getInstance().addConfig(configWritable.getName(), configWritable);
		IoServer ioServer = new IoServer(port);
	}
}
