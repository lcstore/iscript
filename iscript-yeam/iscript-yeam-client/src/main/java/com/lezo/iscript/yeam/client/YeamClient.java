package com.lezo.iscript.yeam.client;

import java.util.Map.Entry;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.Logger;

import com.lezo.iscript.yeam.ClassBuilder;
import com.lezo.iscript.yeam.ClientConstant;
import com.lezo.iscript.yeam.ObjectBuilder;
import com.lezo.iscript.yeam.writable.ClientWritable;

public class YeamClient {
	private int corePoolSize = 3;
	private int maximumPoolSize = 4;
	private long keepAliveTime = 1;
	private TimeUnit unit = TimeUnit.SECONDS;
	private int capacity = 5;
	private BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<Runnable>(capacity);
	private ThreadPoolExecutor executor = new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, unit,
			workQueue);
	private static Logger log = Logger.getLogger(YeamClient.class);

	private static final class InstanceHolder {
		private static final YeamClient INSTANCE = new YeamClient();
	}

	private YeamClient() {
	}

	public static YeamClient getClient() {
		return InstanceHolder.INSTANCE;
	}

	public static void main(String[] args) throws Exception {
		log.info("client starting");
		YeamClient.getClient().startup(args);
		log.info("client startup success..");
	}

	public void startup(String[] args) {
		initArgs();
		assertArgs();
		addClientVersion();
		ExecutorService exec = executor;
		exec.execute(new DataFetcher());
		exec.execute(new TaskExecuter());
		exec.execute(new DataSubmiter());
		exec.shutdown();
	}

	private void addClientVersion() {
		ClientWritable clientWritable = new ClientWritable();
		String name = (String) ObjectBuilder.findObject(ClientConstant.CLIENT_NAME);
		String version = (String) ObjectBuilder.findObject(ClientConstant.CLIENT_VERSION);
		clientWritable.setName(name);
		clientWritable.setVersion(version);
		ObjectBuilder.newObject(ClientWritable.class.getName(), clientWritable);
	}

	private void assertArgs() {
		assertArgument(ClientConstant.CLIENT_NAME);
		assertArgument(ClientConstant.CLIENT_PATH);
		assertArgument(ClientConstant.CLIENT_TASKER_HOST);
	}

	protected void initArgs() {
		initSystemArgs();
		ClassBuilder.addClass(getClass());
	}

	protected void initSystemArgs() {
		String envKeyMark = ClientConstant.CLIENT_ENV_HEAD;
		// load System.getenv
		for (Entry<String, String> entry : System.getenv().entrySet()) {
			if (entry.getKey().startsWith(envKeyMark)) {
				ObjectBuilder.newObject(entry.getKey(), entry.getValue());
			}
		}
		// load System.getProperties
		for (Entry<Object, Object> entry : System.getProperties().entrySet()) {
			String key = entry.getKey().toString();
			if (key.startsWith(envKeyMark)) {
				if (null != ObjectBuilder.findObject(key)) {
					ObjectBuilder.deleteObject(key);
				}
				ObjectBuilder.newObject(key, entry.getValue().toString());
			}
		}

	}

	private void assertArgument(String name) {
		if (ObjectBuilder.findObject(name) == null) {
			throw new IllegalArgumentException("Can not found[" + name + "],please set <-D" + name + "=something>");
		}
	}

	public int shutdown(long timeout) throws Exception {
		executor.awaitTermination(timeout, TimeUnit.MILLISECONDS);
		if (!executor.isTerminated()) {
			return executor.shutdownNow().size();
		}
		return -1;
	}

	public ThreadPoolExecutor getExecutor() {
		return executor;
	}

	public void setExecutor(ThreadPoolExecutor executor) {
		this.executor = executor;
	}
}
