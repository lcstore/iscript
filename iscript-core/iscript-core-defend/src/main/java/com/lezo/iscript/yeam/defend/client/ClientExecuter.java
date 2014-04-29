package com.lezo.iscript.yeam.defend.client;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.io.IOUtils;
import org.apache.log4j.Logger;

import com.lezo.iscript.yeam.ClientConstant;
import com.lezo.iscript.yeam.ObjectBuilder;
import com.lezo.iscript.yeam.writable.ClientWritable;

public class ClientExecuter {
	private static Logger log = Logger.getLogger(ClientExecuter.class);
	private long lastActive = System.currentTimeMillis();
	private ClientController controller;

	private ClientExecuter() {
		String clientPath = (String) ObjectBuilder.findObject(ClientConstant.CLIENT_PATH);
		File parent = new File(clientPath, ClientConstant.CLIENT_WORK_SPACE);
		controller = new ClientController(new ClientLocker(parent));
	}

	private static final class InstanceHolder {
		private static final ClientExecuter INSTANCE = new ClientExecuter();
	}

	public static ClientExecuter getClientExecuter() {
		return InstanceHolder.INSTANCE;
	}

	public void doExecute() {
		long delay = 0;
		long period = 30 * 1000;
		new Timer("Excute.Timer").schedule(new TimerTask() {
			private volatile boolean running = false;

			@Override
			public void run() {
				if (running) {
					return;
				}
				try {
					running = true;
					String name = (String) ObjectBuilder.findObject(ClientConstant.CLIENT_NAME);
					if (controller.getProcess() != null && !controller.getLocker().tryLock(name)) {
						return;
					}
					if (!hasVersion()) {
						log.warn("Can not found version in ClientWritable.");
						return;
					}
					if (!hasProperties()) {
						log.warn("Can not found [client.properties].fail to start client.");
						return;
					} else {
						loadPropeties();
						assertPropeties();
					}
					boolean status = controller.start();
					log.info("start Client[" + controller.getName() + "].status:" + status);
				} catch (Exception e) {
					log.warn("", e);
				} finally {
					running = false;
				}
			}
		}, delay, period);
		period = 1000;
		new Timer(true).schedule(new TimerTask() {
			private volatile boolean running = false;

			@Override
			public void run() {
				if (running) {
					return;
				}
				try {
					running = true;
					Process process = controller.getProcess();
					if (process == null) {
						return;
					}
					InputStream in = process.getInputStream();
					if (in.available() > 0) {
						IOUtils.readLines(in, ClientConstant.CLIENT_CHARSET);
					}
					InputStream err = process.getErrorStream();
					if (err.available() > 0) {
						IOUtils.readLines(err, ClientConstant.CLIENT_CHARSET);
					}
				} catch (Exception e) {
					log.warn("", e);
				} finally {
					running = false;
				}
			}
		}, 0, period);
	}

	protected void assertPropeties() {
		assertArgument(ClientConstant.CLIENT_NAME);
		assertArgument(ClientConstant.CLIENT_PATH);
		assertArgument(ClientConstant.CLIENT_TASKER_HOST);
	}

	private void assertArgument(String name) {
		if (ObjectBuilder.findObject(name) == null) {
			throw new IllegalArgumentException("Can not found[" + name + "],please set <-D" + name + "=something>");
		}
	}

	protected void loadPropeties() {
		String proName = "client.properties";
		InputStream in = null;
		BufferedInputStream inStream = null;
		try {
			Object pathObj = ObjectBuilder.findObject(ClientConstant.CLIENT_PATH);
			if (pathObj != null) {
				File workFile = new File(pathObj.toString(), ClientConstant.CLIENT_WORK_SPACE);
				File properity = new File(workFile, proName);
				if (properity.exists()) {
					in = new FileInputStream(properity);
				}
			}
			if (in == null) {
				throw new FileNotFoundException("Can not found [" + proName + "] in work space.");
			}
			Properties pro = new Properties();
			inStream = new BufferedInputStream(in);
			pro.load(inStream);
			for (Entry<Object, Object> entry : pro.entrySet()) {
				String key = entry.getKey().toString();
				if (ObjectBuilder.findObject(key) != null) {
					ObjectBuilder.deleteObject(key);
				}
				ObjectBuilder.newObject(key, entry.getValue().toString());
			}
		} catch (IOException e) {
			log.warn("", e);
		} finally {
			IOUtils.closeQuietly(in);
			IOUtils.closeQuietly(inStream);
		}

	}

	protected boolean hasProperties() {
		String proName = "client.properties";
		String clientPath = (String) ObjectBuilder.findObject(ClientConstant.CLIENT_PATH);
		File clientProperity = new File(clientPath, ClientConstant.CLIENT_WORK_SPACE + File.separator + proName);
		return clientProperity.exists();
	}

	protected boolean hasVersion() {
		Object clientObject = ObjectBuilder.findObject(ClientWritable.class.getName());
		if (clientObject == null) {
			return false;
		}
		ClientWritable clientWritable = (ClientWritable) clientObject;
		return clientWritable.getVersion() != null;
	}

	public long getLastActive() {
		return lastActive;
	}

	public void setLastActive(long lastActive) {
		this.lastActive = lastActive;
	}

	public ClientController getController() {
		return controller;
	}

}
