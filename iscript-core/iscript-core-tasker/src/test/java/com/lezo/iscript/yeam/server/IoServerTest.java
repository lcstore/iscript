package com.lezo.iscript.yeam.server;

import java.io.IOException;
import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import com.lezo.iscript.yeam.tasker.buffer.ConfigBuffer;
import com.lezo.iscript.yeam.tasker.cache.TaskCacher;
import com.lezo.iscript.yeam.writable.ConfigWritable;
import com.lezo.iscript.yeam.writable.TaskWritable;

public class IoServerTest {
	public static void main(String[] args) throws IOException {
		int port = 8209;
		final ConfigWritable configWritable = new ConfigWritable();
		String script = "var sum =args.x + args.y; return sum*100;";
		configWritable.setContent(script.getBytes());
		configWritable.setName("test.name");
		configWritable.setStamp(System.currentTimeMillis());
		configWritable.setType(ConfigWritable.CONFIG_TYPE_SCRIPT);
		ConfigBuffer.getInstance().addConfig(configWritable.getName(), configWritable);
		IoServer ioServer = new IoServer(port);
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {

			@Override
			public void run() {
				TaskWritable tWritable = new TaskWritable();
				tWritable.setId(new Random().nextLong());
				tWritable.put("x", new Random().nextInt(100));
				tWritable.put("y", new Random().nextInt(10));
				tWritable.put("type", configWritable.getName());
				TaskCacher.getInstance().getQueue(configWritable.getName()).offer(tWritable, 0);
				System.err.println("offer task:" + tWritable.getId());
			}
		}, 1000, 10000);
	}
}
